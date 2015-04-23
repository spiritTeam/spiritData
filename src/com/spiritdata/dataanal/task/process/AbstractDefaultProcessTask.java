package com.spiritdata.dataanal.task.process;

import java.util.Map;

import com.spiritdata.dataanal.task.core.model.TaskInfo;

/**
 * 默认任务执行的虚拟类，本分析项目中的具体任务执行都应继承自此类
 * @author wh
 */
public abstract class AbstractDefaultProcessTask implements ProcessTask {

    /**
     * 任务执行的主方法
     * @param param 此参数是根据taskInfo已经组织好的数据，也包括taskInfo
     * @return 返回一个Map，
     */
    public abstract Map<String, Object> processMain(Map<String, Object> param);

    /**
     * 按任务描述信息执行任务，本方法会自动解析taskInfo，把其中的参数组织为Map，并调用processMain方法。
     * 在执行processMain方法后，还要进行一些后续处理，包括写入结果文件。
     * @param ti 任务描述信息
     */
    public void process(TaskInfo ti) {
        // TODO Auto-generated method stub
    }
}