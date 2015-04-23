package com.spiritdata.dataanal.task.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.dataanal.exceptionC.Dtal0402CException;
import com.spiritdata.dataanal.exceptionC.Dtal0403CException;
import com.spiritdata.dataanal.task.core.model.TaskGroup;
import com.spiritdata.dataanal.task.core.model.TaskInfo;
import com.spiritdata.dataanal.task.core.persistence.pojo.TaskGroupPo;
import com.spiritdata.dataanal.task.core.persistence.pojo.TaskInfoPo;
import com.spiritdata.dataanal.task.core.persistence.pojo.TaskRelPo;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;

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
     * 调整所有者Id，为登录成功后，切换所有者所准备的方法
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
        taskGroupDao.excute("changeOwner", param);
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
            //1-先存储数据库
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

            //保存到内存
            //String ownerId = (tg.getOwnerId()).trim();
            //if (ownerId==null||ownerId.trim().length()==0) { //抛出异常
            //}
            //保存到数据库
        } catch(Exception e) {
            //删除
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
}