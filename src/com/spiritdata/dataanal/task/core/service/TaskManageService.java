package com.spiritdata.dataanal.task.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.dataanal.exceptionC.Dtal0402CException;
import com.spiritdata.dataanal.exceptionC.Dtal0403CException;
import com.spiritdata.dataanal.task.core.enumeration.StatusType;
import com.spiritdata.dataanal.task.core.model.TaskGroup;
import com.spiritdata.dataanal.task.core.model.TaskInfo;
import com.spiritdata.dataanal.task.core.persistence.pojo.TaskGroupPo;
import com.spiritdata.dataanal.task.core.persistence.pojo.TaskInfoPo;
import com.spiritdata.dataanal.task.core.persistence.pojo.TaskRelPo;
import com.spiritdata.dataanal.task.run.mem.TaskMemory;
import com.spiritdata.dataanal.task.run.mem.TaskMemoryService;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.util.StringUtils;

/**
 * 任务管理服务类，包括持久化部分和应用缓存部分，但不包括任务执行及调度部分。
 * @author wh
 */
public class TaskManageService {
    @Resource(name="defaultDAO")
    private MybatisDAO<TaskGroupPo> taskGroupDao; //任务组
    @Resource(name="defaultDAO")
    private MybatisDAO<TaskInfoPo> taskInfoDao; //任务信息，单个的任务
    @Resource(name="defaultDAO")
    private MybatisDAO<TaskRelPo> taskRelDao; //任务关系，标明前置任务

    @PostConstruct
    public void initParam() {
        taskGroupDao.setNamespace("taskGroup");
        taskInfoDao.setNamespace("taskInfo");
        taskRelDao.setNamespace("taskRel");
    }

    /**
     * 调整所有者Id。登录成功后，切换所有者时所调用的方法
     * @param oldOwnerId 旧所有者Id，目前是SessionId
     * @param newOwnerId 新所有者Id，目前是用户的Id
     * @return 调整成功，返回true，否则，返回false
     */
    public boolean changeOwnerId(String oldOwnerId, String newOwnerId) {
        //调整缓存中的数据
        //调整数据库中的数据
        Map<String, String> param = new HashMap<String, String>();
        param.put("oldOwnerId", oldOwnerId);
        param.put("newOwnerId", newOwnerId);
        taskGroupDao.execute("changeOwner", param);
        TaskMemoryService tms = TaskMemoryService.getInstance();
        tms.changeOwnerId(oldOwnerId, newOwnerId);
        return true;
    }

    /**
     * 保存任务组信息
     * @param tg 任务组信息
     * @return 若保存成功，返回true，否则返回false
     */
    public void save(TaskGroup tg) {
        if (!tg.getTaskGraph().checkGraph()) throw new Dtal0403CException(new Dtal0402CException("任务图不封闭或是空任务组！"));

        int flag = -1;
        try {
            tg.setSubCount(tg.getTaskInfoSize()); //设置所属子任务个数
            flag = taskGroupDao.insert(tg.convert2Po());
            if (flag==0) throw new Dtal0403CException("存储任务组信息(主表)到数据库失败，没有存储任何数据！");
            if (flag!=1) throw new Dtal0403CException("存储任务组信息(主表)到数据库失败，存入了多于1条的信息！");
            Map<String, TaskInfo> taskMap = tg.getTaskGraph().getTaskMap();
            for (String taskId: taskMap.keySet()) {
                TaskInfo ti = taskMap.get(taskId);
                flag = taskInfoDao.insert(ti.convert2Po());
                if (flag==0) throw new Dtal0403CException("存储任务描述信息(子表)到数据库失败，没有存储任何数据！");
                if (flag!=1) throw new Dtal0403CException("存储任务描述信息(子表)到数据库失败，存入了多于1条的信息！");
                List<TaskRelPo> trl = ti.convertProTasks2PoList();
                if (trl!=null&&trl.size()>0) {
                    for (TaskRelPo trp: trl) {
                        flag = taskRelDao.insert(trp);
                        if (flag==0) throw new Dtal0403CException("存储任务关联信息到数据库失败，没有存储任何数据！");
                        if (flag!=1) throw new Dtal0403CException("存储任务关联信息到数据库失败，存入了多于1条的信息！");
                    }
                }
            }
        } catch(Exception e) {
            try {
                taskRelDao.delete("deleteByGroupId", tg.getId());
                taskInfoDao.delete("deleteByGroupId", tg.getId());
                taskGroupDao.delete("delete", tg.getId());
            } catch(Exception e1) {
                e1.addSuppressed(e);
                throw new Dtal0403CException("删除已处理数据异常", e1);
            }
            throw e;
        }
    }

    /**
     * 获得能够执行的任务组信息。先查看可执行的具体任务，再以此任务的组ID为依据，得到这个列表
     * @return 任务组信息列表
     */
    public List<TaskGroupPo> getCanExecuteTaskGroups() {
        return taskGroupDao.queryForList("getCanExecuteTaskGroups");
    }

    /**
     * 获得能够执行的任务信息。
     * @return 任务信息列表
     */
    public List<TaskInfoPo> getCanExecuteTaskInfos() {
        return taskInfoDao.queryForList("getCanExecuteTaskInfos");
    }

    /**
     * 获得能够执行的任务的任务关系信息。
     * @return 任务关系信息列表
     */
    public List<TaskRelPo> getCanExecuteTaskRels() {
        return taskRelDao.queryForList("getCanExecuteTaskRels");
    }

    /**
     * 完成某一任务的执行，并写入数据库。包括其任务组信息的修改。
     * @param ti 任务信息
     * @param status 任务完成时的状态值
     */
    public void completeTaskInfo(TaskInfo ti) {
        StatusType status = ti.getStatus();
        TaskMemory tm = TaskMemory.getInstance();
        if (status!=StatusType.SUCCESS||status!=StatusType.FAILD) new Dtal0403CException(new IllegalArgumentException("status(任务完成状态)只能是4或5，而当前status="+status));
        if (StringUtils.isNullOrEmptyOrSpace(ti.getId())) new Dtal0403CException(new IllegalArgumentException("任务信息中的任务id字段不能是空"));

        Map<String, Object> param = new HashMap<String, Object>();

        //任务失效的判断及处理
        ti.setExecuteCount(ti.getExecuteCount()+1);
        if (status==StatusType.FAILD) {//失败
            int executeLimit = tm.getEXECUTECOUNT_LIMIT();
            if (ti.getTaskGroup()!=null&&ti.getTaskGroup().getDefaultExecuteCountLimit()>0) {
                executeLimit = ti.getTaskGroup().getDefaultExecuteCountLimit();
            }
            //执行失败的次数过多，这样的任务是无效的
            if (executeLimit<=ti.getExecuteCount()) ti.setAbatement();
        }

        //修改任务表
        param.put("id", ti.getId());
        param.put("executeCount", ti.getExecuteCount());
        param.put("status", ti.getStatus().getValue());
        param.put("beginTime", ti.getBeginTime());
        param.put("endTime", ti.getEndTime());
        taskInfoDao.update(param);

        //检查任务组是否也需要更新
        TaskGroup tg = ti.getTaskGroup();
        if (tg==null) return;
        Map<String, TaskInfo> tiMap = tg.getTaskGraph().getTaskMap();
        if (tiMap!=null&&tiMap.size()>0) {
            tg.adjustStatus();
            if (tg.getStatus()!=StatusType.PREPARE&&tg.getStatus()!=StatusType.PROCESSING) {//更新任务组
                param.clear();
                param.put("id", tg.getId());
                param.put("status", tg.getStatus().getValue());
                taskGroupDao.update(param);
            }
        }
    }

    /**
     * 更新任务组状态
     * @param ti 任务组信息
     */
    public void updateTaskGroupStatus(TaskGroup tg) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("id", tg.getId());
        param.put("status", tg.getStatus().getValue());
        taskGroupDao.update(param);
    }
}