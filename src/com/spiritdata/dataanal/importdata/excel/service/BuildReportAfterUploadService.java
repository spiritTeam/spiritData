package com.spiritdata.dataanal.importdata.excel.service;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.spiritdata.dataanal.importdata.excel.pojo.SheetTableInfo;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel;
import com.spiritdata.dataanal.report.generate.AbstractGenerateSessionReport;
import com.spiritdata.dataanal.report.model.Report;
import com.spiritdata.dataanal.report.model.ReportHead;
import com.spiritdata.dataanal.report.model.TaskReport;
import com.spiritdata.dataanal.task.model.TaskGroup;
import com.spiritdata.framework.util.FileNameUtils;
import com.spiritdata.framework.util.SequenceUUID;

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

        //1-报告头生成
        //1.1-准备数据
        //元数据模型
        MetadataModel mm = (MetadataModel)param.get("mdInfo");
        //页签表格信息
        SheetTableInfo sti = (SheetTableInfo)param.get("sheetTableInfo");

        //1.2-设置数据
        ReportHead rHead = new ReportHead();
        report.set_HEAD(rHead);
        report.setId(SequenceUUID.getPureUUID()); //生成新的Id，头的id也设置了
        report.setOwnerType(mm.getOwnerType());
        report.setOwnerId(mm.getOwnerId());
        String reportName = sti.getSheetInfo().getFileName();
        reportName = FileNameUtils.getPureFileName(reportName);
        reportName += "——文件导入后数据分析报告";
        report.setReportName(reportName); //设置报告名称，头的reportName也设置了
        report.setReportFile(null);//
        rHead.setCode("");
        //1.3-报告文件
        

        tr.setReport(report);
        tr.setTaskGroup(tg);
        return tr;
    }
}