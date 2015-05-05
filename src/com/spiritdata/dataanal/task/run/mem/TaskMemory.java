package com.spiritdata.dataanal.task.run.mem;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.spiritdata.dataanal.task.core.model.TaskGroup;
import com.spiritdata.dataanal.task.core.model.TaskInfo;
import com.spiritdata.dataanal.task.run.TaskContextConfig;

/**
 * <p>任务的内存结构，所有任务处理都基于此结构。
 * <p>包括可执行的任务组信息和任务信息，以及一些控制信息。
 * <p>本对象只存储数据，不进行任何相关的操作，相关操作在TaskMemoryService中
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
    protected int MEMORY_MAXSIZE_TASKGROUP = (1<<10)-1; //2^10=1023
    public void setMEMORY_MAXSIZE_TASKGROUP(int MEMORY_MAXSIZE_TASKGROUP) {
        this.MEMORY_MAXSIZE_TASKGROUP = MEMORY_MAXSIZE_TASKGROUP;
    }
    //任务内存对象中最大任务个数
    protected int MEMORY_MAXSIZE_TASKINFO = (1<<13)-1; //2^13=8095
    public void setMEMORY_MAXSIZE_TASKINFO(int MEMORY_MAXSIZE_TASKINFO) {
        this.MEMORY_MAXSIZE_TASKINFO = MEMORY_MAXSIZE_TASKINFO;
    }
    //每次清除任务组或任务信息的个数
    protected int MEMORY_CLEANSIZE_TASK = (1<<4)-1; //2^4-1=15
    public void setMEMORY_CLEANSIZE_TASK(int MEMORY_CLEANSIZE_TASK) {
        this.MEMORY_CLEANSIZE_TASK = MEMORY_CLEANSIZE_TASK;
    }

    //所有可执行任务组
    protected Map<String, TaskGroup> taskGroupMap = null;
    //所有可执行任务信息
    protected Map<String, TaskInfo> taskInfoMap = null;
    //所有可执行任务Id的队列，按照创建时间进行排序
    protected List<String> taskInfoSortList = null;

    /**
     * 参数初始化，必须首先执行这个方法，任务内存类才能使用
     */
    public void init(TaskContextConfig tcc) {
        if (tcc!=null) {
            this.setMEMORY_MAXSIZE_TASKGROUP(tcc.getMEMORY_MAXSIZE_TASKGROUP());
            this.setMEMORY_MAXSIZE_TASKINFO(tcc.getMEMORY_MAXSIZE_TASKINFO());
            this.setMEMORY_CLEANSIZE_TASK(tcc.getMEMORY_CLEANSIZE_TASK());
            taskGroupMap = new ConcurrentHashMap<String, TaskGroup>();
            taskInfoMap = new ConcurrentHashMap<String, TaskInfo>();
            taskInfoSortList = new CopyOnWriteArrayList<String>();
        }
    }

    /*
     * 二分法，找到位置
     * @param ti
     * @param beginIndex
     * @param endIndex
     * @return
    //在目前这个环境下，二分法不一定是最快的方法
    private int _getInsertIndex(TaskInfo ti, int beginIndex, int endIndex) {
        TaskInfo tempTi1 = null;
        if (beginIndex==endIndex) {
            tempTi1 = this.taskInfoMap.get(this.taskInfoSortList.get(beginIndex));
            if (tempTi1.getFirstTime().before(ti.getFirstTime())) {
                return 1;
            } else {
                
            }
        } else if ((endIndex-beginIndex)==1) {
            
        } else {
            
        }
        return 1;
    }
     */
}