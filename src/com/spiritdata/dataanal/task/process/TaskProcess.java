package com.spiritdata.dataanal.task.process;

import java.util.Map;

/**
 * 任务的执行，所有需要执行的任务都要继承此接口
 * @author wh
 */
public interface TaskProcess {
    /**
     * 
     * @param param
     * @return
     */
    public abstract Map<String, Object> process(Map<String, Object> param);
}