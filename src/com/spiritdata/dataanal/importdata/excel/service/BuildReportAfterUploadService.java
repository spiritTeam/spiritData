package com.spiritdata.dataanal.importdata.excel.service;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.spiritdata.dataanal.report.generate.AbstractGenerateSessionReport;
import com.spiritdata.dataanal.report.model.Report;
import com.spiritdata.dataanal.report.model.TaskReport;
import com.spiritdata.dataanal.task.model.TaskGroup;

/**
 * 在上传一个Excel文件后，生成Report。<br/>
 * 包括生成Report+生成Task+启动Task。
 * @author wh
 */
public class BuildReportAfterUploadService extends AbstractGenerateSessionReport implements Serializable {
    private static final long serialVersionUID = 5557763867374849717L;

    /**
     * 无参构造函数，用此方式创建对象，必须设置Session
     */
    public BuildReportAfterUploadService() {
        super();
    }

    /**
     * 构造实例，并设置Sesion
     * @param session 所设置的对象
     */
    public BuildReportAfterUploadService(HttpSession session) {
        super();
        super.setSession(session);
    }

    @Override
    public TaskReport preTreat(Map<String, Object> param) {
        TaskReport tr = new TaskReport();
        Report report = new Report();
        TaskGroup tg = new TaskGroup();

        //报告头生成

        tr.setReport(report);
        tr.setTaskGroup(tg);
        return tr;
    }
}