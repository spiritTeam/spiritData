package com.spiritdata.dataanal.task.run.mem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.spiritdata.dataanal.common.model.Owner;
import com.spiritdata.dataanal.exceptionC.Dtal0403CException;
import com.spiritdata.dataanal.task.core.enumeration.StatusType;
import com.spiritdata.dataanal.task.core.model.PreTask;
import com.spiritdata.dataanal.task.core.model.TaskGroup;
import com.spiritdata.dataanal.task.core.model.TaskInfo;
import com.spiritdata.dataanal.task.core.persistence.pojo.TaskGroupPo;
import com.spiritdata.dataanal.task.core.persistence.pojo.TaskInfoPo;
import com.spiritdata.dataanal.task.core.persistence.pojo.TaskRelPo;
import com.spiritdata.dataanal.task.core.service.TaskManageService;
import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.SystemCache;

/**
 * <p>任务内存的操作服务类。
 * <pre>
 * 包括:
 * 1-增加、删除、更新；
 * 2-内存的加载；
 * 3-所有者内存的修改；
 * </pre>
 * @author wh
 */
public class TaskMemoryService {
    //java的占位单例模式===begin
    private static class InstanceHolder {
        public static TaskMemoryService instance = new TaskMemoryService();
    }
    public static TaskMemoryService getInstance() {
        TaskMemoryService tms = InstanceHolder.instance;
        tms.setTadskMemory();
        return tms;
    }
    //java的占位单例模式===end

    /**
     * 任务内存数据
     */
    protected TaskMemory tm = null;
    protected void setTadskMemory() {
        this.tm = TaskMemory.getInstance();
    }

    /**
     * 把任务组加入任务内存，同时构造内存中对应的任务信息
     * @param tg 预加入的任务组
     * @return 加入成功返回true，否则返回false
     */
    public boolean addTaskGroup(TaskGroup tg) {
        if (tm.taskGroupMap.size()>tm.MEMORY_MAXSIZE_TASKGROUP) return false; //已经不能插入任务组了
        if (tg.getStatus()!=StatusType.PREPARE&&tg.getStatus()!=StatusType.FAILD) {
            throw new Dtal0403CException("只有为[准备执行]/[执行失败]状态的任务组才能加入任务内存，当前任务组[id="+tg.getId()+"]的状态为["+tg.getStatus().getName()+"]");
        }
        if (tg.getTaskInfoSize()>0) {
            boolean inserted = false;
            Map<String, TaskInfo> _m = tg.getTaskGraph().getTaskMap();
            for (String tiId: _m.keySet()) {
                TaskInfo ti = _m.get(tiId);
                try {
                    inserted = addTaskInfo(ti);
                } catch(Exception e) {
                    e.printStackTrace();
                }
                if (!inserted) break;
            }
            if (inserted) {
                if (tm.taskGroupMap.get(tg.getId())==null) {
                    tg.setProcessing();//设置为正在执行
                    tm.taskGroupMap.put(tg.getId(), tg);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 把任务信息加入内存
     * @param ti 预加入的任务
     * @return 加入成功返回true，否则返回false
     */
    public boolean addTaskInfo(TaskInfo ti) {
        if ((tm.taskInfoMap.size())>=tm.MEMORY_MAXSIZE_TASKINFO) return false; //任务信息数量已经达到了上限，不能再加入了
        if (ti.getStatus()!=StatusType.PREPARE&&ti.getStatus()!=StatusType.FAILD) {
            throw new Dtal0403CException("只有为[准备执行]/[执行失败]状态的任务才能加入任务内存，当前任务[id="+ti.getId()+"]的状态为["+ti.getStatus().getName()+"]");
        }
        if (tm.taskInfoMap.get(ti.getId())==null) {
            tm.taskInfoSortList.add(getInsertIndex(ti), ti.getId());
            ti.setWaiting();
            tm.taskInfoMap.put(ti.getId(), ti);
        }
        return true;
    }

    /**
     * 把执行失败的任务加入内存，以便再次执行
     * @param ti 预加入的任务
     * @return 加入成功返回true，否则返回false
     */
    public boolean addFaildTaskInfo(TaskInfo ti) {
        if (ti.getStatus()==StatusType.FAILD) {
            ti.setWaiting();
            if (tm.taskInfoMap.get(ti.getId())==null) tm.taskInfoMap.put(ti.getId(), ti);
            if (!_find(ti)) tm.taskInfoSortList.add(getInsertIndex(ti), ti.getId());
            return true;
        }
        return false;
    }

    //以下函数是需要在线程中调用的
    /**
     * 装载数据库信息到任务内存
     */
    public void loadData() {
        System.out.println("[开始装载可执行任务到TaskMemory]"+(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSSS", Locale.CHINESE)).format(new Date()));
        //这里需要用到Spring的容器
        ServletContext sc = (ServletContext)SystemCache.getCache(FConstants.SERVLET_CONTEXT).getContent();
        TaskManageService tmService = (TaskManageService)WebApplicationContextUtils.getWebApplicationContext(sc).getBean("taskManageService");

        TaskInfo selfTi, preTi;

        //1-构造任务组结构，主表
        List<TaskGroupPo> tgL = tmService.getCanExecuteTaskGroups();
        Map<String, TaskGroup> tempTGm = new HashMap<String, TaskGroup>();
        for (TaskGroupPo tgp: tgL) {
            tempTGm.put(tgp.getId(), new TaskGroup(tgp));
        }

        //2-处理任务信息结构
        List<TaskInfoPo> tiL = tmService.getCanExecuteTaskInfos();
        Map<String, TaskInfo> tempTIm = new HashMap<String, TaskInfo>();
        for (TaskInfoPo tip: tiL) {
            selfTi = new TaskInfo(tip);
            selfTi.setTaskGroup(tempTGm.get(tip.getTaskGId()));
            if (tempTGm.get(tip.getTaskGId())!=null) selfTi.getTaskGroup().addTask2Graph(selfTi);
            tempTIm.put(tip.getId(), selfTi);
        }

        //3-补充任务中的前置任务
        List<TaskRelPo> trL = tmService.getCanExecuteTaskRels();
        if (trL!=null&&trL.size()>0) {
            for (TaskRelPo trp: trL) {
                selfTi = tempTIm.get(trp.getTaskId());
                preTi = tempTIm.get(trp.getPreTaskId());
                if (selfTi!=null&&preTi!=null) {
                    selfTi.addPreTask(preTi, trp.getUsedPreData()==1);
                }
            }
        }

        //4-加入内存结构
        for (String tgId: tempTGm.keySet()) {//任务组
            try {
                addTaskGroup(tempTGm.get(tgId));
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        for (String tiId: tempTIm.keySet()) {//不属于任何任务组的任务
            selfTi = tempTIm.get(tiId);
            if (selfTi.getTaskGroup()==null) {
                try {
                    addTaskInfo(selfTi);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获得下一个可执行的具体任务。<br/>
     * 注意，这里是完全按照时间顺序来处理的，没有按照优先级及其他处理。
     * @return 下一个可执行的具体任务
     */
    public TaskInfo getNextCanProcessTaskInfo() {
        TaskInfo ret = null;
        String taskInfoId = null;
        int i=0;
        for (; i<tm.taskInfoSortList.size(); i++) {
            taskInfoId = tm.taskInfoSortList.get(i);
            ret = tm.taskInfoMap.get(taskInfoId);
            if (ret.getPreTasks()!=null&&ret.getPreTasks().size()>0) {
                for (PreTask pt: ret.getPreTasks()) {
                    if (pt.getPreTask().getStatus()==StatusType.PREPARE
                        ||pt.getPreTask().getStatus()==StatusType.WAITING
                        ||pt.getPreTask().getStatus()==StatusType.PROCESSING) {
                        ret = null;
                        continue;
                    }
                }
            }
            if (ret!=null) break;
        }
        if (ret!=null) tm.taskInfoSortList.remove(i);
        return ret;
    }

    /**
     * 清除已经完成的任务或任务组
     */
    public void cleanTaskMemory() {
        Map<String, TaskGroup> taskGroupMap = tm.taskGroupMap;
        Map<String, TaskInfo> taskInfoMap = tm.taskInfoMap;
        int cleanLimitSize = tm.MEMORY_CLEANSIZE_TASK; //
        int cleanTG_Count = 0;
        int cleanTI_Count = 0;

        TaskGroup tg = null;
        TaskInfo ti = null;
        //清除任务组
        boolean canClean = false;
        if (taskGroupMap!=null&&taskGroupMap.size()>0) {
            for (String tgId: taskGroupMap.keySet()) {
                canClean = true;
                tg = taskGroupMap.get(tgId);
                for (String tiId: tg.getTaskGraph().getTaskMap().keySet()) {
                    ti = taskInfoMap.get(tiId);
                    if (ti.getStatus()==StatusType.PREPARE||ti.getStatus()==StatusType.PROCESSING||ti.getStatus()==StatusType.WAITING) {
                        canClean = false;
                        break;
                    }
                }
                if (canClean) {
                    _removeCompeteTaskGroup(tgId);
                    cleanLimitSize--;
                    cleanTG_Count++;
                }
                if (cleanLimitSize==0) break;
            }
        }
        //清除任务
        if (cleanLimitSize>0) {
            for (String tiId: tm.taskInfoSortList) {
                ti = taskInfoMap.get(tiId);
                if (ti.getTaskGroup()==null&&(ti.getStatus()==StatusType.SUCCESS||ti.getStatus()==StatusType.FAILD||ti.getStatus()==StatusType.ABATE)) {//可删除
                    taskInfoMap.remove(tiId);
                    cleanLimitSize--;
                    cleanTI_Count++;
                    if (cleanLimitSize==0) break;
                }
            }
        }
        if ((cleanTI_Count+cleanTG_Count)>0)
        System.out.println("本次清除已完成的任务组["+cleanTG_Count+"]个、任务["+cleanTI_Count+"]个。");
    }

    /**
     * 得到可执行任务队列的长度，用于判断是否需要分发任务
     * @return 可执行任务队列的长度
     */
    public int getExecuterListSize() {
        return tm.taskInfoSortList.size();
    }
    /**
     * 调整所有者Id。登录成功后，切换所有者时所调用的方法
     * @param oldOwnerId 旧所有者Id，目前是SessionId
     * @param newOwnerId 新所有者Id，目前是用户的Id
     * @return 调整成功，返回true，否则，返回false
     */
    public boolean changeOwnerId(String oldOwnerId, String newOwnerId) {
        Map<String, TaskGroup> taskGroupMap = tm.taskGroupMap;
        Owner owner = new Owner();
        TaskGroup tg = null;
        if (taskGroupMap!=null&&taskGroupMap.size()>0) {
            for (String tgId: taskGroupMap.keySet()) {
                tg = taskGroupMap.get(tgId);
                if (tg.getOwner().getOwnerId().equals(oldOwnerId)) {
                    owner.setOwnerId(newOwnerId);
                    owner.setOwnerType(1);
                    tg.setOwner(owner);
                }
            }
        }
        return true;
    }

    /*
     * 删除指定的任务组，注意这里不判断任务组是否已执行完毕
     */
    private void _removeCompeteTaskGroup(String tgId) {
        TaskGroup tg = tm.taskGroupMap.get(tgId);
        if (tg!=null) {
            for (String tiId: tg.getTaskGraph().getTaskMap().keySet()) {
                //删除Map中内容
                tm.taskInfoMap.remove(tiId);
            }
        }
        tm.taskGroupMap.remove(tgId);
    }

    /*
     * 从任务信息列表中找到插入的位置，按时间排序，越靠前的时间序号越小
     * @param ti 要插入的任务
     * @return 插入的索引号
     */
    private int getInsertIndex(TaskInfo ti) {
        if (tm.taskInfoSortList.size()==0) return 0;
        return _getInsertIndex(ti);
    }
    /*
     * 从后查找法
     * @param ti 要插入的任务
     * @return 插入的索引号
     */
    private int _getInsertIndex(TaskInfo ti) {
        TaskInfo tempTi = null;
        for (int i=tm.taskInfoSortList.size()-1; i>=0; i--) {
            tempTi = tm.taskInfoMap.get(tm.taskInfoSortList.get(i));
            if (tempTi!=null) {
                if (!tempTi.getFirstTime().after(ti.getFirstTime())) {
                    return i; 
                }
            }
        }
        return 0;
    }

    private boolean _find(TaskInfo ti) {
        for (String id: tm.taskInfoSortList) {
            if (id.endsWith(ti.getId())) return true;
        }
        return false;
    }
}