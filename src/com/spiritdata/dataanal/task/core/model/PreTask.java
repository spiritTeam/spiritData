package com.spiritdata.dataanal.task.core.model;

import java.io.Serializable;

/**
 * 前序任务对象
 * @author wh
 */
public class PreTask implements Serializable {
    private static final long serialVersionUID = 3145870992211118527L;

    private TaskInfo preTask;
    private boolean isUseResult; //是否利用前置任务的结果

    public TaskInfo getPreTask() {
        return preTask;
    }
    public void setPreTask(TaskInfo preTask) {
        this.preTask = preTask;
    }
    public boolean isUseResult() {
        return isUseResult;
    }
    public void setUseResult(boolean isUseResult) {
        this.isUseResult = isUseResult;
    }
}