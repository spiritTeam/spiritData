package com.spiritdata.dataanal.task.persistence.pojo;

import com.spiritdata.framework.core.model.BaseObject;

/**
 * 任务关系表持久化对象<br/>
 * 对应持久化中数据库的表为SA_TASK_REF
 * @author wh
 */
public class TaskRelPo extends BaseObject {
    private static final long serialVersionUID = -8722868318345923626L;

    private String id; //任务关系id
    private String taskId; //任务id
    private String preTaskId; //前置任务id
    private int usedPreData; //是否使用前置任务生成的数据

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTaskId() {
        return taskId;
    }
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    public String getPreTaskId() {
        return preTaskId;
    }
    public void setPreTaskId(String preTaskId) {
        this.preTaskId = preTaskId;
    }
    public int getUsedPreData() {
        return usedPreData;
    }
    public void setUsedPreData(int usedPreData) {
        this.usedPreData = usedPreData;
    }
}