package com.spiritdata.dataanal.importdata.excel.service;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.spiritdata.dataanal.SDConstants;
import com.spiritdata.dataanal.exceptionC.Dtal1003CException;
import com.spiritdata.dataanal.importdata.excel.pojo.SheetInfo;
import com.spiritdata.dataanal.importdata.excel.pojo.SheetTableInfo;
import com.spiritdata.dataanal.report.generate.AbstractGenerateSessionReport;
import com.spiritdata.dataanal.report.model.Report;
import com.spiritdata.dataanal.report.model.ReportHead;
import com.spiritdata.dataanal.report.model.TaskReport;
import com.spiritdata.dataanal.task.model.TaskGroup;
import com.spiritdata.filemanage.REPORT.service.ReportFileService;
import com.spiritdata.filemanage.core.model.FileInfo;
import com.spiritdata.framework.util.FileNameUtils;
import com.spiritdata.framework.util.SequenceUUID;

/**
 * 在上传一个Excel文件后，生成Report。<br/>
 * 包括生成Report+生成Task+启动Task。
 * @author wh
 */
public class BuildReportAfterUploadService extends AbstractGenerateSessionReport implements Serializable {
    private static final long serialVersionUID = 5557763867374849717L;

    @Resource
    private ReportFileService rfService;

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
    public Map<String, Object> preTreat(Map<String, Object> param) {
        //1-准备数据
        Map<SheetInfo, Map<SheetTableInfo, Map<String, Object>>> reportParam = (Map<SheetInfo, Map<SheetTableInfo, Map<String, Object>>>)param.get("reportParam");
        if (param.get("reportParam")==null) return null;//若参数中的reportParam为空，无法生成报告，返回空
        String ownerId = (String)param.get("ownerId");
        if (ownerId==null||ownerId.trim().length()==0) throw new Dtal1003CException(new IllegalArgumentException("Map参数中没有所有者Id[owenrId]的信息！"));
        int ownerType = Integer.parseInt(param.get("ownerType")+"");
        if (ownerType==0) throw new Dtal1003CException(new IllegalArgumentException("Map参数中没有所有者类型[ownerType]的信息！"));
        FileInfo impFi = (FileInfo)param.get("impFileInfo");
        if (impFi==null) throw new Dtal1003CException(new IllegalArgumentException("Map参数中没有导入文件对象[impFileInfo]的信息！"));

        TaskReport tr = new TaskReport();
        Report report = new Report();
        TaskGroup tg = new TaskGroup();

        //2-报告生成
        //2.1-设置数据
        ReportHead rHead = new ReportHead();
        report.set_HEAD(rHead);
        report.setId(SequenceUUID.getPureUUID()); //生成新的Id，头的id也设置了
        report.setOwnerType(ownerType);
        report.setOwnerId(ownerId);
        String reportName = FileNameUtils.getPureFileName(impFi.getAllFileName())+"——文件导入后数据分析报告";
        report.setReportName(reportName); //设置报告名称，头的reportName也设置了
        report.setCTime(new Timestamp((new Date()).getTime())); //设置报告生成时间，同时也设置了头的时间
        report.setDesc(reportName); //设置报告说明，同时也设置了报告头的说明
        report.setReportType("导入后即时报告");
        rHead.setCode(SDConstants.RP_AFTER_IMP);
        //2.2-报告文件，文件在基础类中处理，这里略过

        tr.setReport(report);
        tr.setTaskGroup(tg);

        Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("taskReport", tr);
        return ret;
    }
}