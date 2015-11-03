package com.spiritdata.dataanal.task.run.mem;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
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
//注意，目前任务的失败状态(任务+任务组)都是无用的，都要等变成无效后再处理
public class TaskMemoryService {
    protected Logger log = Logger.getLogger(this.getClass());

    //java的占位单例模式===begin
    private static class InstanceHolder {
        public static TaskMemoryService instance = new TaskMemoryService();
    }
    public static TaskMemoryService getInstance() {
        TaskMemoryService tms = InstanceHolder.instance;
        tms.setTaskMemory();
        return tms;
    }
    //java的占位单例模式===end

    /**
     * 任务内存数据
     */
    protected TaskMemory tm = null;
    protected void setTaskMemory() {
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
        if (tm.dealedObjMap.get("group::"+tg.getId())!=null) return false; //本任务组已经被处理了，不用再加入了
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
            }
        }
        return false;
    }

    /*
     * 把独立的任务信息加入内存
     * @param ti 预加入的任务
     * @return 加入成功返回true，否则返回false
     */
    private boolean addTaskInfo_Single(TaskInfo ti) {
        if (tm.taskInfoMap.size()>=tm.MEMORY_MAXSIZE_TASKINFO) return false; //任务信息数量已经达到了上限，不能再加入了
        if (tm.dealedObjMap.get("task::"+ti.getId())!=null) return false; //本任务已经被处理了，不用再加入了
        if (ti.getTaskGroup()==null) return false; //独立任务不能属于任何任务组
        if (ti.getStatus()!=StatusType.PREPARE&&ti.getStatus()!=StatusType.FAILD) {
            throw new Dtal0403CException("只有为[准备执行]/[执行失败]状态的独立任务才能加入任务内存，当前任务[id="+ti.getId()+"]的状态为["+ti.getStatus().getName()+"]");
        }
        _addTaskInfo(ti);//包括插入待处理排序列表
        return true;
    }
    /*
     * 从任务组把任务信息加入内存
     * @param ti 预加入的任务
     * @return 加入成功返回true，否则返回false
     */
    private boolean addTaskInfo_4Group(TaskInfo ti) {
        if (tm.taskInfoMap.size()>=tm.MEMORY_MAXSIZE_TASKINFO) return false; //任务信息数量已经达到了上限，不能再加入了
        _addTaskInfo(ti);//包括插入待处理排序列表
        return true;
    }
    private void _addTaskInfo(TaskInfo ti) {
        if (tm.taskInfoMap.get(ti.getId())==null) {
            if (ti.getFirstTime()==null) ti.setFirstTime(new Timestamp(System.currentTimeMillis()));
            tm.taskInfoMap.put(ti.getId(), ti);
            if (ti.getStatus()!=StatusType.SUCCESS&&ti.getStatus()!=StatusType.FAILD&&ti.getStatus()!=StatusType.ABATE) {
                _addSortList(ti);
            }
        }
    }

    //以下函数是需要在线程中调用的
    /**
     * 装载并清理内存数据：
     * 先清理数据，再装载数据，这样能够保证数据的一致性
     */
    public void cleanANDloadData() {
        log.info("[清理任务缓存，并装载可执行任务到缓存]"+(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSSS", Locale.CHINESE)).format(new Date()));
        //这里需要用到Spring的容器
        ServletContext sc = (ServletContext)SystemCache.getCache(FConstants.SERVLET_CONTEXT).getContent();
        if (WebApplicationContextUtils.getWebApplicationContext(sc)==null) return;
        TaskManageService tmService = (TaskManageService)WebApplicationContextUtils.getWebApplicationContext(sc).getBean("taskManageService");
        FileManageService fmService = (FileManageService)WebApplicationContextUtils.getWebApplicationContext(sc).getBean("fileManageService");

        TaskInfo selfTi, preTi;
        TaskGroup tg = null;

        //一、清理数据
        Map<String, TaskGroup> taskGroupMap = tm.taskGroupMap;
        Map<String, TaskInfo> taskInfoMap = tm.taskInfoMap;
        List<String> taskInfoSortList = tm.taskInfoSortList;
        Map<String, Date> dealedObjMap = tm.dealedObjMap;
        int cleanLimitSize = tm.MEMORY_CLEANSIZE_TASK;
        int cleanTG_Count = 0;
        int cleanTI_Count = 0;
        int cleanDO_Count = 0;
        //一.1 清理内存中的任务结构，注意要在这个过程中，增加已处理对象存储
        //一.1.1 清除任务组
        if (taskGroupMap!=null&&taskGroupMap.size()>0) {
            boolean canClean = false;
            for (String tgId: taskGroupMap.keySet()) {
                canClean = (tg.getStatus()==StatusType.SUCCESS||tg.getStatus()==StatusType.ABATE);
                tg = taskGroupMap.get(tgId);
                tg.adjustStatus();
                if (canClean) {
                    //写数据库
                    tmService.updateTaskGroupStatus(tg);
                    //写已处理
                    dealedObjMap.put("group::"+tg.getId(), new Date(System.currentTimeMillis()));
                    //删除实际数据
                    _removeTaskGroup(tgId);
                    cleanLimitSize--;
                    cleanTG_Count++;
                }
                if (cleanLimitSize==0) break;
            }
        }
        //一.1.2 清除单个的任务，不属于任何任务组的任务
        if (taskInfoMap.size()>0&&cleanLimitSize>0) {
            for (TaskInfo _ti: taskInfoMap.values()) {
                if (_ti!=null&&_ti.getTaskGroup()==null&&(_ti.getStatus()==StatusType.SUCCESS||_ti.getStatus()==StatusType.FAILD||_ti.getStatus()==StatusType.ABATE)) {//可删除
                    //写已处理
                    dealedObjMap.put("task::"+_ti.getId(), new Date(System.currentTimeMillis()));
                    //删除
                    taskInfoMap.remove(_ti.getId());
                    taskInfoSortList.remove(_ti.getId());
                    cleanLimitSize--;
                    cleanTI_Count++;
                    if (cleanLimitSize==0) break;
                }
            }
        }
        //一.2 清理已处理对象存储
        if (dealedObjMap!=null&&dealedObjMap.size()>0) {
            List<String> _tempL = new ArrayList<String>();
            for (String key: dealedObjMap.keySet()) {
                Date _d = dealedObjMap.get(key);
                if (_d!=null) {
                    if ((System.currentTimeMillis()-dealedObjMap.get(key).getTime())>tm.getCLEANDEALEDOBJ_AFTERTIME())
                        _tempL.add(key);
                }
            }
            if (_tempL.size()>0) {
                for (String _key: _tempL) {
                    dealedObjMap.remove(_key);
                    cleanDO_Count++;
                }
            }
        }
        if ((cleanTI_Count+cleanTG_Count+cleanDO_Count)>0) {
            log.info("本次清除已完成的任务组["+cleanTG_Count+"]个、任务["+cleanTI_Count+"]个、已处理对象["+cleanDO_Count+"]个。");
        }

        //二、读取信息并装载数据
        //二.1 从数据库中读取数据
        //二.1.1 构造任务组结构，主表
        List<TaskGroupPo> tgL = tmService.getCanExecuteTaskGroups();
        Map<String, TaskGroup> tempTGm = new HashMap<String, TaskGroup>();
        for (TaskGroupPo tgp: tgL) {
            tempTGm.put(tgp.getId(), new TaskGroup(tgp));
        }
        //二.1.1.2 处理任务信息结构，子表
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
        //二.1.3 补充任务中的前置任务
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
        //二.2 加入内存
        //二.2.1 任务组
        for (String tgId: tempTGm.keySet()) {//任务组
            try {
                tg = tempTGm.get(tgId);
                //若tg不是空，并且tg的子任务个数和tg图中的任务个数相同（若不同，则说明任务的读取有问题）,是准备或错误状态
                if (tg!=null&&(tg.getSubCount()==tg.getTaskInfoSize())&&(tg.getStatus()==StatusType.PREPARE||tg.getStatus()==StatusType.FAILD)) {
                    addTaskGroup(tg);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        //二.2.1 不属于任何任务组的任务
        for (String tiId: tempTIm.keySet()) {
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
        if (ret!=null) tm.taskInfoSortList.remove(i); //删除待处理任务列表
        return ret;
    }

    /**
     * 完成任务后的处理：包括更新数据库+更新缓存结构
     * @param ti
     */
    public void completeTaskInfo(TaskInfo ti) {
        ServletContext sc = (ServletContext)SystemCache.getCache(FConstants.SERVLET_CONTEXT).getContent();
        TaskManageService tmService = (TaskManageService)WebApplicationContextUtils.getWebApplicationContext(sc).getBean("taskManageService");
        ti.setEndTime(new Timestamp(System.currentTimeMillis()));
        //更新数据库
        tmService.completeTaskInfo(ti);
        //把失败的任务再放入内存继续执行
        if (ti.getStatus()==StatusType.FAILD) _addSortList(ti);
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
        TaskInfo ti = null;
        if (tm!=null&&tm.taskInfoMap!=null)ti = tm.taskInfoMap.get(taskId);
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

    /*
     * 删除指定的任务组，注意这里不判断任务组是否已执行完毕
     */
    private void _removeTaskGroup(String tgId) {
        TaskGroup tg = tm.taskGroupMap.get(tgId);
        if (tg!=null) {
            for (String tiId: tg.getTaskGraph().getTaskMap().keySet()) {
                //删除Map中内容
                tm.taskInfoMap.remove(tiId);
                //删除待处理列表
                tm.taskInfoSortList.remove(tiId);
            }
        }
        tm.taskGroupMap.remove(tgId);
    }

    /*
     * 从待执行列表查找一个任务
     */
    private void _addSortList(TaskInfo ti) {
        if (ti==null) return;
        boolean canAdd = true;
        for (String id: tm.taskInfoSortList) {
            if (id.equals(ti.getId())) {
                canAdd = false;
                break;
            }
        }
        if (canAdd) {
            int insertIndex = _getInsertIndex(ti);//也可能是其他方法，比如二分法
            ti.setWaiting();
            tm.taskInfoSortList.add(insertIndex, ti.getId());
        }
        
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
}