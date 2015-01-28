package com.spiritdata.dataanal.report.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import com.spiritdata.dataanal.exceptionC.Dtal1003CException;
import com.spiritdata.dataanal.report.persistence.pojo.ReportPo;
import com.spiritdata.dataanal.task.model.TaskGroup;
import com.spiritdata.framework.util.SequenceUUID;

/**
 * 报告任务，所有后台生成的报告都应准备这个对象。
 * 在生成报告的同时，组织数据处理的任务。
 * 目前只使用报告部分的功能，任务部分的功能后续再实现。
 * @author wh
 */
public class TaskReport implements Serializable {
    private static final long serialVersionUID = 5088715498847593517L;

    private Reprot report; //任务报告中的报告信息
    private TaskGroup taskGroup;//任务报告中的任务信息

    public Reprot getReport() {
        return report;
    }
    public void setReport(Reprot report) {
        this.report = report;
    }
    public TaskGroup getTaskGroup() {
        return taskGroup;
    }
    public void setTaskGroup(TaskGroup taskGroup) {
        this.taskGroup = taskGroup;
    }

    /**
     * 转换为报告类Po对象
     * @return 报告类Po对象
     */
    public ReportPo convert2ReportPo() {
        if (report==null||taskGroup==null) throw new Dtal1003CException("报告或任务组对象为空，不能进行转换");
        ReportPo rp = new ReportPo();
        if (this.report.getId()==null||this.report.getId().trim().equals("")) {
            rp.setId(SequenceUUID.getPureUUID());
        } else {
            rp.setId(this.report.getId());
        }
        rp.setFId(this.report.getReportFile().getId());
        rp.setTaskGId(this.getTaskGroup().getId());
        rp.setOwnerType(this.report.getOwnerType());
        rp.setOwnerId(this.report.getOwnerId());
        rp.setReportName(this.report.getReportName());
        rp.setDesc(this.report.getDesc());
        rp.setCTime(new Timestamp(new Date().getTime()));
        return rp;
    }
}