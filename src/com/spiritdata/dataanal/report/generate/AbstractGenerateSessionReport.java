package com.spiritdata.dataanal.report.generate;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.spiritdata.dataanal.exceptionC.Dtal1003CException;
import com.spiritdata.dataanal.report.model.TaskReport;
import com.spiritdata.dataanal.report.model.Report;
import com.spiritdata.dataanal.report.service.ReportService;
import com.spiritdata.dataanal.task.model.TaskGroup;
import com.spiritdata.filemanage.REPORT.model.ReportFile;
import com.spiritdata.filemanage.REPORT.service.ReportFileService;
import com.spiritdata.filemanage.core.model.FileInfo;
import com.spiritdata.framework.util.SequenceUUID;


/**
 * 以Session为基础的
 * @author wh
 */
public abstract class AbstractGenerateSessionReport implements GenerateReport {
    @Resource
    private ReportFileService rfService;
    @Resource
    private ReportService reportService;

    private HttpSession session;//session用来缓存与该会话相关的信息

    public HttpSession getSession() {
        return session;
    }
    public void setSession(HttpSession session) {
        this.session = session;
    }

    @Override
    /**
     * 以Session作为缓存机制，构建报告对象，生成task任务，并启动分析任务。<br/>
     * 应包括如下内容：<br/>
     * 1-通过与处理过程获得需要的数据<br/>
     * 2-生成报告<br/>
     * 3-生成任务<br/>
     * 4-进行持久化存储——报告+任务<br/>
     * 5-以Session为容器，构建任务执行的上下文<br/>
     * 6-启动任务
     * @param param 完成本过程需要的数据，必须包括预处理需要的数据，应放入preTreadParam参数中
     */
    /*
     * 1-通过与处理过程获得需要的数据
     * 2-生成报告 OK
     * 3-生成任务
     * 4-进行持久化存储——报告+任务
     * 5-以Session为容器，构建任务执行的上下文
     * 6-启动任务
     */
    public void buildANDprocess(Map<String, Object> param) {
        if (param==null||param.size()==0) throw new Dtal1003CException(new IllegalArgumentException("构建报告及任务时，必须设置参数！"));
        if (param.get("preTreadParam")==null) throw new Dtal1003CException(new IllegalArgumentException("构建报告及任务时，Map参数中必须设置key='preTreadParam'的元素！"));

        //1-执行预处理，得到报告及任务
        Map<String, Object> preTreadParam = (Map<String, Object>)param.get("preTreadParam");
        Map<String, Object> preTreatResult = preTreat(preTreadParam);
        if (preTreatResult==null) return; //预处理没有返回任何内容，不能进行任何处理
        TaskReport tr = null;
        try {
            tr = (TaskReport)preTreatResult.get("taskReport");
            if (tr==null) throw new Dtal1003CException("预处理结果未包含key='taskReport'的元素，无法进行后续处理");
        } catch(Exception e) {
            throw new Dtal1003CException("预处理结果异常，无法进行后续处理！", e);
        }
        Report report = tr.getReport();
        TaskGroup tg = tr.getTaskGroup();

        //2-处理报告，并存储文件及数据库
        //2.1-写报告文件
        ReportFile rfSeed = new ReportFile();
        rfSeed.setId(SequenceUUID.getPureUUID());
        rfSeed.setOwnerId(report.getOwnerId());
        rfSeed.setOwnerType(report.getOwnerType());
        rfSeed.setReportId(report.getId());
        rfSeed.setTasksId(tg.getId());
        FileInfo impFi = (FileInfo)preTreadParam.get("impFileInfo");
        rfSeed.setFileNameSeed("afterImport(IMPFID-"+impFi.getId()+"_RID-"+report.getId()+")");

        ReportFile rf = (ReportFile)rfService.write2FileAsJsonD(report, rfSeed); //保存文件，并把文件信息回写到report对象中
        report.setReportFile(rf);
        //2.2-报告文件数据库存储
        rfService.saveFile(rf);//报告的json存储
        //2.3-报告信息数据库存储
        reportService.saveReport(tr);
        //3-处理任务
    }
 }