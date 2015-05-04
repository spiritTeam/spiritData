package com.spiritdata.dataanal.task.run.mem;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.ArrayList;

import com.spiritdata.dataanal.task.core.model.TaskGroup;
import com.spiritdata.dataanal.task.core.model.TaskInfo;
import com.spiritdata.dataanal.task.run.TaskContextConfig;

/**
 * <p>任务的内存结构，所有任务处理都基于此结构。
 * <p>包括可执行的任务组信息和任务信息。
 * <p>本对象只维护本对象中的数据，若队列的增加、删除内容更新等。但队列的加载、数据的使用等都放在其他服务类中实现。
 * @author wh
 */
public class TaskMemory {
    //java的占位单例模式===begin
    private static class InstanceHolder {
        public static TaskMemory instance = new TaskMemory();
    }
    public static TaskMemory getInstance() {
        return InstanceHolder.instance;
    }
    //java的占位单例模式===end

    //参数处理部分
    //任务内存对象中最大任务组个数
    private int MEMORY_MAXSIZE_TASKGROUP = (1<<10)-1; //2^10=1023
    public void setMEMORY_MAXSIZE_TASKGROUP(int mEMORY_MAXSIZE_TASKGROUP) {
        MEMORY_MAXSIZE_TASKGROUP = mEMORY_MAXSIZE_TASKGROUP;
    }
    //任务内存对象中最大任务个数
    private int MEMORY_MAXSIZE_TASKINFO = (1<<13)-1; //2^13=8095
    public void setMEMORY_MAXSIZE_TASKINFO(int mEMORY_MAXSIZE_TASKINFO) {
        MEMORY_MAXSIZE_TASKINFO = mEMORY_MAXSIZE_TASKINFO;
    }

    //所有可执行任务组
    private Map<String, TaskGroup> taskGroupMap = null;
    //所有可执行任务的队列，按照创建时间进行排序
    private List<TaskInfo> taskInfoSortList = null;
    //已执行任务，包括正在执行和执行完成(成功或失败)的任务
    private Map<String, TaskInfo> executedTaskInfoMap = null;

    /**
     * 参数初始化，必须首先执行这个方法，任务内存类才能使用
     */
    public void initParam(TaskContextConfig tcc) {
        if (tcc!=null) {
            this.setMEMORY_MAXSIZE_TASKGROUP(tcc.getMEMORY_MAXSIZE_TASKGROUP());
            this.setMEMORY_MAXSIZE_TASKINFO(tcc.getMEMORY_MAXSIZE_TASKINFO());
            taskGroupMap = new ConcurrentHashMap<String, TaskGroup>();
            taskInfoSortList = new CopyOnWriteArrayList<TaskInfo>();
            executedTaskInfoMap = new ConcurrentHashMap<String, TaskInfo>();
        }
    }

    /**
     * 把任务组插入Map，同时构造相关的任务Map
     */
    public void addTaskGroup(TaskGroup tg) {
        if (this.taskGroupMap.size()>this.MEMORY_MAXSIZE_TASKGROUP) return; //已经不能插入任务组了
        if (tg.getTaskInfoSize()>0) {
            if ((this.taskInfoSortList.size()+tg.getTaskInfoSize())>this.MEMORY_MAXSIZE_TASKINFO) return; //任务信息数量已经达到了上限，不能再加入了
            if (this.taskGroupMap.get(tg.getId())==null) this.taskGroupMap.put(tg.getId(), tg);
            Map<String, TaskInfo> _m = tg.getTaskGraph().getTaskMap();
            for (String tiId: _m.keySet()) {
                TaskInfo ti = _m.get(tiId);
                this.taskInfoSortList.add(getInsertIndex(ti), ti);
            }
        }
    }

    /**
     * 获得下一个可执行的具体任务
     * @return
     */
    public TaskInfo getNextCanProcessTaskInfo() {
        TaskInfo ret = null;
        int i=0;
        for (; i<this.taskInfoSortList.size(); i++) {
            ret = this.taskInfoSortList.get(i);
            
        }
        ret = this.taskInfoSortList.remove(i);
        executedTaskInfoMap.put(ret.getId(), ret);
        return ret;
    }

    /*
     * 从任务信息列表中找到插入的位置，按时间排序，越靠前的时间序号越小
     * @param ti 要插入的任务
     * @return 插入的索引号
     */
    private int getInsertIndex(TaskInfo ti) {
        if (this.taskInfoSortList.size()==0) return 0;
        return _getInsertIndex(ti);
    }
    /*
     * 从后查找法
     * @param ti 要插入的任务
     * @return 插入的索引号
     */
    private int _getInsertIndex(TaskInfo ti) {
        for (int i=this.taskInfoSortList.size()-1; i>=0; i--) {
            if (!this.taskInfoSortList.get(i).getFirstTime().after(ti.getFirstTime())) {
                return i; 
            }
        }
        return 0;
    }
    /*
     * 二分法，找到位置
     * @param ti
     * @param beginIndex
     * @param endIndex
     * @return
     */
    //在目前这个环境下，二分法不一定是最快的方法
    private int _getInsertIndex(TaskInfo ti, int beginIndex, int endIndex) {
        if (beginIndex==endIndex) {
            if (this.taskInfoSortList.get(beginIndex).getFirstTime().before(ti.getFirstTime())) {
                return 1;
            } else {
                
            }
        } else if ((endIndex-beginIndex)==1) {
            
        } else {
            
        }
        return 1;
    }
}