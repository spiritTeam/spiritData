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
     * @return 返回值是一个Map，必须有如下结构：(其中*是必须的，#是可选的|)
     * <pre>
     *   *sysResultData{MAP}:
     *     *resultType{INT}:1=成功，2=失败
     *     #notSaveResult2File{INT}:是否不存储为JsonD文件，默认是必须存储，若没有这个参数，则按默认值处理
     *     #JsonDCode{STRING}:jsonD的编码，若要存储为文件，必须要设置此值
     *   *userResultData{MAP}:返回的真实数据
     * </pre>
     */
    public abstract Map<String, Object> process(Map<String, Object> param);
}