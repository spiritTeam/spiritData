package com.spiritdata.dataanal.task.process;

import com.spiritdata.dataanal.task.core.model.TaskInfo;

/**
 * 执行
 * @author wh
 */
public interface ProcessTask {

    /**
     * 按任务描述信息执行任务
     * @param ti 任务描述信息
     */
    public void process(TaskInfo ti);
}