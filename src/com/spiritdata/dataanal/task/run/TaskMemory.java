package com.spiritdata.dataanal.task.run;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.spiritdata.dataanal.task.core.model.TaskGroup;
import com.spiritdata.dataanal.task.core.model.TaskInfo;

/**
 * <p>任务的内存结构，所有任务处理都基于此结构。
 * <p>包括可执行的任务组信息和任务信息。
 * <p>本对象只维护本对象中的数据，若队列的增加、删除内容更新等。但队列的加载、数据的使用等都放在其他服务类中实现。
 * @author wh
 */
public class TaskMemory {
    private int MAX_QUEUE_LENGTH = 1<<9-1;
    public void setMAX_QUEUE_LENGTH(int mAX_QUEUE_LENGTH) {
        MAX_QUEUE_LENGTH = mAX_QUEUE_LENGTH;
    }

    private static Map<String, TaskGroup> taskGroupMap = new ConcurrentHashMap<String, TaskGroup>();

    private static Map<String, TaskInfo> taskInfoMap = new ConcurrentHashMap<String, TaskInfo>();

    /**
     * 把任务组插入Map，同时构造相关的任务信息
     */
    public void addTaskGroupMap() {
        
    }

    /**
     * 获得下一个可执行的具体任务
     * @return
     */
    public TaskInfo getNextCanProcessTaskInfo() {
        return null;
    }

}