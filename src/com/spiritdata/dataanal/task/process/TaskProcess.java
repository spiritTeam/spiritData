package com.spiritdata.dataanal.task.process;

import java.util.Map;

/**
 * 任务的执行，所有需要执行的任务都要继承此接口
 * @author wh
 */
public interface TaskProcess {
    /**
     * 执行任务的具体逻辑
     * @param param 执行参数，是一个Map
     * @return 返回值是一个Map，必须有如下结构：
     */
    public abstract Map<String, Object> process(Map<String, Object> param);
}