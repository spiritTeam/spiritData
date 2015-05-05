package com.spiritdata.dataanal.task.run.mem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.web.context.support.WebApplicationContextUtils;

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
     * 把任务组插入Map，同时构造相关的任务Map
     */
    public void addTaskGroup(TaskGroup tg) {
        if (tm.taskGroupMap.size()>tm.MEMORY_MAXSIZE_TASKGROUP) return; //已经不能插入任务组了
        if (tg.getTaskInfoSize()>0) {
            if ((tm.taskInfoSortList.size()+tg.getTaskInfoSize())>tm.MEMORY_MAXSIZE_TASKINFO) return; //任务信息数量已经达到了上限，不能再加入了
            if (tm.taskGroupMap.get(tg.getId())==null) tm.taskGroupMap.put(tg.getId(), tg);
            Map<String, TaskInfo> _m = tg.getTaskGraph().getTaskMap();
            for (String tiId: _m.keySet()) {
                TaskInfo ti = _m.get(tiId);
                tm.taskInfoSortList.add(getInsertIndex(ti), ti.getId());
                tm.taskInfoMap.put(ti.getId(), ti);
            }
        }
    }

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
        List<TaskGroupPo> tgL = tmService.getCanExcuteTaskGroups();
        Map<String, TaskGroup> tempTGm = new HashMap<String, TaskGroup>();
        for (TaskGroupPo tgp: tgL) {
            tempTGm.put(tgp.getId(), new TaskGroup(tgp));
        }

        //2-处理任务信息结构
        List<TaskInfoPo> tiL = tmService.getCanExcuteTaskInfos();
        Map<String, TaskInfo> tempTIm = new HashMap<String, TaskInfo>();
        for (TaskInfoPo tip: tiL) {
            selfTi = new TaskInfo(tip);
            selfTi.setTaskGroup(tempTGm.get(tip.getTaskGId()));
            selfTi.getTaskGroup().addTask2Graph(selfTi);
            tempTIm.put(tip.getId(), new TaskInfo(tip));
        }

        //3-补充任务中的前置任务
        List<TaskRelPo> trL = tmService.getCanExcuteTaskRels();
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
        for (String tgId: tempTGm.keySet()) {
            addTaskGroup(tempTGm.get(tgId));
        }
    }

    /**
     * 获得下一个可执行的具体任务
     * @return
     */
    public TaskInfo getNextCanProcessTaskInfo() {
        TaskInfo ret = null;
        String taskInfoId = null;
        boolean preTaskComplete = false;
        int i=0;
        for (; i<tm.taskInfoSortList.size(); i++) {
            taskInfoId = tm.taskInfoSortList.get(i);
            ret = tm.taskInfoMap.get(taskInfoId);
            if (ret.getPreTasks()==null||ret.getPreTasks().size()==0) preTaskComplete = true;
            else {
                preTaskComplete = true;
                for (PreTask pt: ret.getPreTasks()) {
                    if (pt.getPreTask().getStatus()!=4&&pt.getPreTask().getStatus()!=5) {
                        preTaskComplete=false;
                        break;
                    }
                }
            }
            if (!preTaskComplete) ret = null;
        }
        if (ret!=null) tm.taskInfoSortList.remove(i);
        return ret;
    }

    
    /**
     * 删除指定的任务组，注意这里不判断任务组是否已执行完毕
     */
    private void removeOneTaskGroup(String tgId) {
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
}