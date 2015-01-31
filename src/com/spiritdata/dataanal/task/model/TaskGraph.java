package com.spiritdata.dataanal.task.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务有向图。目前仅用Map来存储。
 * 可以考虑为图这种数据结构包装一套框架，类似树，目前先不考虑
 * @author wh
 */
public class TaskGraph implements Serializable {
    private static final long serialVersionUID = 8714192466876786427L;

    private Map<String, TaskInfo> taskMap;

    /**
     * 新增一个任务
     * @param task
     */
    public void addTaskInfo(TaskInfo task) {
        if (this.taskMap==null) this.taskMap = new HashMap<String, TaskInfo>();
        this.taskMap.put(task.getId(), task);
    }

    /**
     * 根据Id获得一个任务
     * @param id 任务Id
     * @return 符合Id的任务，若没有返回null
     */
    public TaskInfo getTaskInfo(String id) {
        if (this.taskMap==null||this.taskMap.size()==0) return null;
        return this.taskMap.get(id);
    }
}