package com.spiritdata.dataanal.report.model;

import java.io.Serializable;

import com.spiritdata.dataanal.exceptionC.Dtal1003CException;
import com.spiritdata.dataanal.report.persistence.pojo.ReportPo;
import com.spiritdata.dataanal.task.model.TaskGroup;
import com.spiritdata.dataanal.task.persistence.pojo.TaskGroupPo;

/**
 * 报告任务，所有后台生成的报告都应准备这个对象。
 * 在生成报告的同时，组织数据处理的任务。
 * 目前只使用报告部分的功能，任务部分的功能后续再实现。
 * @author wh
 */
public class TaskReport implements Serializable {
    private static final long serialVersionUID = 5088715498847593517L;

    private Report report; //任务报告中的报告信息
    private TaskGroup taskGroup;//任务报告中的任务信息

    public Report getReport() {
        return report;
    }
    public void setReport(Report report) {
        this.report = report;
    }
    public TaskGroup getTaskGroup() {
        return taskGroup;
    }
    public void setTaskGroup(TaskGroup taskGroup) {
        this.taskGroup = taskGroup;
    }

    /**
     * 转换为报告类Po对象，为数据库操作做准备
     * @return 报告类Po对象
     */
    public ReportPo convert2ReportPo() {
        if (report==null||taskGroup==null) throw new Dtal1003CException("报告或任务组对象为空，不能进行转换");
        ReportPo rp = new ReportPo();
        rp = this.report.convert2Po();
        rp.setTaskGId(this.getTaskGroup().getId());
        return rp;
    }

    /**
     * 转换为任务组类Po对象，为数据库操作做准备
     * @return 任务组类Po对象
     */
    public TaskGroupPo convert2TaskGroupPo() {
        if (report==null||taskGroup==null) throw new Dtal1003CException("报告或任务组对象为空，不能进行转换");
        TaskGroupPo ret = new TaskGroupPo();
        ret = this.taskGroup.convert2Po();
        ret.setReportId(this.report.getId());
        return ret;
    }
}