package com.spiritdata.dataanal.report.generate;

import java.util.Map;

import com.spiritdata.dataanal.report.model.TaskReport;

/**
 * 生成report的接口，所有生成report的内容都需要以此类为基础。<br/>
 * 包括生成report和其对应的任务tasks。
 * @author wh
 */
public interface GenerateReport {

    /**
     * 预处理报告，主要是：<br/>
     * 1-生成报告所需要的数据；
     * 2-生成任务数据；
     * @param param 完成预处理任务所需的数据
     * @return 报告任务对象
     */
    public TaskReport preTreat(Map<String, Object> param);

    /**
     * 构建报告对象，生成task任务，并启动分析任务。
     * @param param 完成本过程需要的数据，必须包括预处理需要的数据，应放入preTreadParam参数中
     */
    public void buildANDprocess(Map<String, Object> param);
}