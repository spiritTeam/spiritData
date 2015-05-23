package com.spiritdata.dataanal.task.run.mem;

import java.sql.Timestamp;
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
import com.spiritdata.filemanage.category.ANAL.model.AnalResultFile;
import com.spiritdata.filemanage.core.model.FileInfo;
import com.spiritdata.filemanage.core.persistence.pojo.FileCategoryPo;
import com.spiritdata.filemanage.core.service.FileManageService;
import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.jsonD.util.JsonUtils;

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
        if (tm==null||tm.taskGroupMap==null) return false;
        if (tm.taskGroupMap.size()>tm.MEMORY_MAXSIZE_TASKGROUP) return false; //已经不能插入任务组了
        if (tg.getStatus()!=StatusType.PREPARE&&tg.getStatus()!=StatusType.FAILD) {
            throw new Dtal0403CException("只有为[准备执行]/[执行失败]状态的任务组才能加入任务内存，当前任务组[id="+tg.getId()+"]的状态为["+tg.getStatus().getName()+"]");
        }

        if (tg.getTaskInfoSize()>0) {
            Map<String, TaskInfo> _m = tg.getTaskGraph().getTaskMap();

            //插入子表，包括待执行排序列表
            boolean inserted = false;
            for (String tiId: _m.keySet()) {
                TaskInfo ti = _m.get(tiId);
                inserted = false;
                try {
                    inserted = addTaskInfo_4Group(ti);
                } catch(Exception e) {
                    e.printStackTrace();
                }
                if (!inserted) break;
            }

            //插入组
            if (inserted&&tm.taskGroupMap.get(tg.getId())==null) {
                tg.setProcessing();//设置为正在执行
                tm.taskGroupMap.put(tg.getId(), tg);
            } else {//若不是所有的组信息都加入成功，则删除已插入的任务内容
                for (String tiId: _m.keySet()) {
                    TaskInfo ti = _m.get(tiId);
                    _removeTaskInfo(ti);
                }
            }
        }
        return false;
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
            tm.runningTaskMap.remove(ti.getId());
            if (!_find(ti)) tm.taskInfoSortList.add(getInsertIndex(ti), ti.getId());
            return true;
        }
        return false;
    }

    /**
     * 把执行失败的任务加入内存，以便再次执行
     * @param ti 预加入的任务
     * @return 加入成功返回true，否则返回false
     */
    public boolean removeFromRunningMap(TaskInfo ti) {
        if (ti.getStatus()==StatusType.SUCCESS||ti.getStatus()==StatusType.ABATE) {
            tm.runningTaskMap.remove(ti.getId());
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
        FileManageService fmService = (FileManageService)WebApplicationContextUtils.getWebApplicationContext(sc).getBean("fileManageService");

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
        Map<String, Object> m = new HashMap<String, Object>();
        for (TaskInfoPo tip: tiL) {
            selfTi = new TaskInfo(tip);
            //处理任务的关联文件
            FileInfo tempFi = fmService.getFileInfoById(tip.getRfId());
            if (tempFi!=null) {
                AnalResultFile arf = new AnalResultFile(tempFi);
                m.clear();
                m.put("FId", tip.getRfId());
                m.put("FType3", "task::"+selfTi.getId());
                FileCategoryPo fcp = fmService.getFileCategoryPo(m);
                if (fcp!=null) {
                    arf.setAnalType(fcp.getFType2());
                    arf.setSubType(fcp.getFType3());
                    Map<String, Object> extMap = (Map<String, Object>)JsonUtils.jsonToObj(fcp.getExtInfo(), Map.class);
                    arf.setJsonDCode((String)extMap.get("JSOND"));
                }
                selfTi.setResultFile(arf);
            }

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
                    addTaskInfo_Single(selfTi);
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
        if (ret!=null) {
            taskInfoId = tm.taskInfoSortList.remove(i);
            tm.runningTaskMap.put(taskInfoId, ret);
        }
        return ret;
    }

    /**
     * 清除已经完成的任务或任务组
     */
    public void cleanTaskMemory() {
        Map<String, TaskGroup> taskGroupMap = tm.taskGroupMap;
        Map<String, TaskInfo> taskInfoMap = tm.taskInfoMap;
        List<String> taskInfoSortList = tm.taskInfoSortList;
        int cleanLimitSize = tm.MEMORY_CLEANSIZE_TASK; //
        int cleanTG_Count = 0;
        int cleanTI_Count = 0;

        TaskGroup tg = null;
        TaskInfo ti = null;
        //清除任务组
        boolean canClean = true;
        if (taskGroupMap!=null&&taskGroupMap.size()>0) {
            for (String tgId: taskGroupMap.keySet()) {
                tg = taskGroupMap.get(tgId);
                for (String tiId: tg.getTaskGraph().getTaskMap().keySet()) {
                    canClean=true;
                    ti = taskInfoMap.get(tiId);
                    if (ti!=null) {
                        canClean = (ti.getStatus()==StatusType.SUCCESS||ti.getStatus()==StatusType.FAILD||ti.getStatus()==StatusType.ABATE);
                    } else canClean=false;
                    if (!canClean) break;
                }
                if (canClean) {
                    _removeTaskGroup(tgId);
                    cleanLimitSize--;
                    cleanTG_Count++;
                }
                if (cleanLimitSize==0) break;
            }
        }
        //清除单个的任务，不属于任何任务组的任务
        if (taskInfoMap.size()>0&&taskInfoSortList.size()>0&&cleanLimitSize>0) {
            for (String tiId: taskInfoSortList) {
                ti = taskInfoMap.get(tiId);
                if (ti!=null&&ti.getTaskGroup()==null&&(ti.getStatus()==StatusType.SUCCESS||ti.getStatus()==StatusType.FAILD||ti.getStatus()==StatusType.ABATE)) {//可删除
                    taskInfoMap.remove(tiId);
                    taskInfoSortList.remove(tiId);
                    cleanLimitSize--;
                    cleanTI_Count++;
                    if (cleanLimitSize==0) break;
                }
            }
        }
        //清除落单的任务，任务的任务组不在内存中维护的
        if (taskInfoMap.size()>0&&cleanLimitSize>0) {
            for (TaskInfo _ti: taskInfoMap.values()) {
                String taskId = _ti.getId();
                if (_ti.getTaskGroup()!=null&&taskGroupMap.get(_ti.getTaskGroup().getId())==null
                    &&(ti.getStatus()==StatusType.SUCCESS||ti.getStatus()==StatusType.FAILD||ti.getStatus()==StatusType.ABATE)) {
                    taskInfoMap.remove(taskId);
                    taskInfoSortList.remove(taskId);
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

    /**
     * 获取任务状态，此Id也是对应的数据文件的id
     * @param taskId 任务Id
     * @return 任务的情况
     */
    public Map<String, Object> getTaskStatus(String taskId) {
        TaskInfo ti = tm.taskInfoMap.get(taskId);
        Map<String, Object> retM = new HashMap<String, Object>();
        if (ti==null) {//已经执行完毕，到数据库中查找
            retM.put("status", -1);
            retM.put("progress", 1);//已执行100%
            retM.put("statusName", "未在执行态");
        } else {
            retM.put("status", ti.getStatus().getValue());
            retM.put("progress", 0.5);//已执行100%，这个功能目前还没有
            retM.put("statusName", ti.getStatus().getName());
        }
        return retM; 
    }

    //以下为内部函数
    /*
     * 把独立的任务信息加入内存
     * @param ti 预加入的任务
     * @return 加入成功返回true，否则返回false
     */
    private boolean addTaskInfo_Single(TaskInfo ti) {
        if (tm.taskInfoMap.size()>=tm.MEMORY_MAXSIZE_TASKINFO) return false; //任务信息数量已经达到了上限，不能再加入了
        if (ti.getStatus()!=StatusType.PREPARE&&ti.getStatus()!=StatusType.FAILD) {
            throw new Dtal0403CException("只有为[准备执行]/[执行失败]状态的独立任务才能加入任务内存，当前任务[id="+ti.getId()+"]的状态为["+ti.getStatus().getName()+"]");
        }
        if (tm.taskInfoMap.get(ti.getId())==null&&tm.runningTaskMap.get(ti.getId())==null) {
            if (ti.getFirstTime()==null) ti.setFirstTime(new Timestamp(System.currentTimeMillis()));
            ti.setWaiting();
            tm.taskInfoMap.put(ti.getId(), ti);
            if (ti.getStatus()!=StatusType.SUCCESS&&ti.getStatus()!=StatusType.FAILD&&ti.getStatus()!=StatusType.ABATE) {
                tm.taskInfoSortList.add(getInsertIndex(ti), ti.getId());
            }
        }
        return true;
    }
    /*
     * 从任务组把任务信息加入内存
     * @param ti 预加入的任务
     * @return 加入成功返回true，否则返回false
     */
    private boolean addTaskInfo_4Group(TaskInfo ti) {
        if (tm.taskInfoMap.size()>=tm.MEMORY_MAXSIZE_TASKINFO) return false; //任务信息数量已经达到了上限，不能再加入了
        if (tm.taskInfoMap.get(ti.getId())==null) {
            if (ti.getFirstTime()==null) ti.setFirstTime(new Timestamp(System.currentTimeMillis()));
            ti.setWaiting();
            tm.taskInfoMap.put(ti.getId(), ti);
            if (ti.getStatus()!=StatusType.SUCCESS&&ti.getStatus()!=StatusType.FAILD&&ti.getStatus()!=StatusType.ABATE) {
                tm.taskInfoSortList.add(getInsertIndex(ti), ti.getId());
            }
        }
        return true;
    }
    /*
     * 删除任务信息
     * @param ti 预删除的任务
     * @return 加入成功返回true，否则返回false
     */
    private boolean _removeTaskInfo(TaskInfo ti) {
        //先删除待处理表
        tm.taskInfoSortList.remove(ti.getId());
        tm.taskInfoMap.remove(ti.getId());
        return true;
    }
    /*
     * 删除指定的任务组，注意这里不判断任务组是否已执行完毕
     */
    private void _removeTaskGroup(String tgId) {
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
        if (tm.taskInfoMap.size()>0) {
            TaskInfo tempTi = null;
            for (int i=tm.taskInfoSortList.size()-1; i>=0; i--) {
                tempTi = tm.taskInfoMap.get(tm.taskInfoSortList.get(i));
                if (tempTi!=null) {
                    if (!tempTi.getFirstTime().after(ti.getFirstTime())) {
                        return i; 
                    }
                }
            }
        }
        return 0;
    }
    /*
     * 从待执行列表查找一个任务
     */
    private boolean _find(TaskInfo ti) {
        for (String id: tm.taskInfoSortList) {
            if (id.endsWith(ti.getId())) return true;
        }
        return false;
    }
}