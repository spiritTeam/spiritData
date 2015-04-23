package com.spiritdata.dataanal.task.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>任务有向图(前序关联图)，且此图是封闭的，即所有任务点的前序点任务必须在这个图的集合中。
 * <p>目前仅用Map来存储。可以考虑为图这种数据结构包装一套框架，类似树，目前先不考虑图。
 * <p>这个对象处理后，与持久化中的任务关联关系相对应。
 * @author wh
 */
public class TaskGraph implements Serializable {
    private static final long serialVersionUID = 8714192466876786427L;

    private Map<String, TaskInfo> taskMap;
    public Map<String, TaskInfo> getTaskMap() {
        return taskMap;
    }
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

    /**
     * 检查这个有向封闭图是否合法。
     * @return 合法返回true，否则返回false
     */
    public boolean checkGraph() {
        if (taskMap==null||taskMap.size()==0) return false;

        for (String taskId: taskMap.keySet()) {
            TaskInfo ti = taskMap.get(taskId);
            List<PreTask> ptl = ti.getPreTasks();
            if (ptl!=null&&ptl.size()>0) {
                for (PreTask pt: ptl) {
                    TaskInfo tempTi = taskMap.get(pt.getPreTask().getId());
                    if (tempTi==null) return false;
                }
            }
        }
        return true;
    }
}