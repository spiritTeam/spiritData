package com.spiritdata.dataanal.importdata.excel.service;

import java.io.File;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.spiritdata.dataanal.SDConstants;
import com.spiritdata.dataanal.exceptionC.Dtal1003CException;
import com.spiritdata.dataanal.importdata.excel.pojo.SheetInfo;
import com.spiritdata.dataanal.importdata.excel.pojo.SheetTableInfo;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataColSemanteme;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataColumn;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataTableMapRel;
import com.spiritdata.dataanal.report.generate.AbstractGenerateSessionReport;
import com.spiritdata.dataanal.report.model.Report;
import com.spiritdata.dataanal.report.model.ReportHead;
import com.spiritdata.dataanal.report.model.ReportSegment;
import com.spiritdata.dataanal.report.model.TaskReport;
import com.spiritdata.dataanal.task.TaskUtils;
import com.spiritdata.dataanal.task.enumeration.TaskLangType;
import com.spiritdata.dataanal.task.model.TaskGroup;
import com.spiritdata.dataanal.task.model.TaskInfo;
import com.spiritdata.filemanage.ANAL.model.AnalResultFile;
import com.spiritdata.filemanage.REPORT.service.ReportFileService;
import com.spiritdata.filemanage.core.model.FileInfo;
import com.spiritdata.framework.util.FileNameUtils;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.jsonD.util.JsonUtils;

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
        List<String> mdl = getMDIdList(reportParam);
        if (mdl==null||mdl.size()==0) return null; //若报告参数中不包含任何元数据信息，无法生成报告，返回空

        String ownerId = (String)param.get("ownerId");
        if (ownerId==null||ownerId.trim().length()==0) throw new Dtal1003CException(new IllegalArgumentException("Map参数中没有所有者Id[owenrId]的信息！"));
        int ownerType = Integer.parseInt(param.get("ownerType")+"");
        if (ownerType==0) throw new Dtal1003CException(new IllegalArgumentException("Map参数中没有所有者类型[ownerType]的信息！"));
        FileInfo impFi = (FileInfo)param.get("impFileInfo");
        if (impFi==null) throw new Dtal1003CException(new IllegalArgumentException("Map参数中没有导入文件对象[impFileInfo]的信息！"));

        TaskReport tr = new TaskReport();
        Report report = new Report();
        TaskGroup tg = new TaskGroup();

        //2-报告主体生成
        //2.1-设置数据
        //2.1.1-得到客户端文件名称
        String clientFileName = impFi.getFileCategoryList().get(0).getExtInfo();
        ReportHead rHead = new ReportHead();
        report.set_HEAD(rHead);
        report.setId(SequenceUUID.getPureUUID()); //生成新的Id，头的id也设置了
        report.setOwnerType(ownerType);
        report.setOwnerId(ownerId);
        String reportName = "["+FileNameUtils.getFileName(clientFileName)+"]——文件导入后数据分析报告";
        report.setReportName(reportName); //设置报告名称，头的reportName也设置了
        report.setCTime(new Timestamp((new Date()).getTime())); //设置报告生成时间，同时也设置了头的时间
        report.setDesc(reportName); //设置报告说明，同时也设置了报告头的说明
        report.setReportType("导入后即时报告");
        rHead.setCode(SDConstants.RP_AFTER_IMP);
        //2.2-报告文件，文件在基础类中处理，这里略过

        //3-任务组处理——构建任务组
        //3.1-构建组
        tg.setId(SequenceUUID.getPureUUID());
        tg.setOwnerId(ownerId);
        tg.setOwnerType(ownerType);
        tg.setWorkName("["+FileNameUtils.getFileName(clientFileName)+"]——文件导入后分析任务");
        tg.setStatus(0);
        tg.setDesc("{\"任务名称\":\""+tg.getWorkName()+"\", 子任务}");
        
        //4-构建报告体，并生成相关的任务
        Map<String, Object> taskParam = new HashMap<String, Object>();//任务参数结构，所有的任务都用此函数作为
        //4.a.1-获得所有本次对应的元数据信息
        TaskInfo getMDInfos_Task = new TaskInfo();
        getMDInfos_Task.setId(SequenceUUID.getPureUUID());
        getMDInfos_Task.setTaskName("获得元数据信息");
        getMDInfos_Task.setLangType(TaskLangType.JAVA);
        getMDInfos_Task.setExcuteFunc("com.spiritdata.dataanal.metadata.relation.process.getMDInfos");
        getMDInfos_Task.setPrepared();
          //设置参数
        taskParam.clear();
        taskParam.put("metadataList", mdl);
        getMDInfos_Task.setParam(JsonUtils.objToJson(taskParam));
          //设置文件
        AnalResultFile arf = new AnalResultFile();
        arf.setId(SequenceUUID.getPureUUID());
        arf.setJsonDCode(SDConstants.JDC_MDINFO);
        arf.setAnalType(SDConstants.ANAL_MD_GETINFO); //分析类型
        arf.setSubType("multiple"); //下级分类
        arf.setObjType("metadatas"); //所分析对象
        arf.setObjId(JsonUtils.objToJson(mdl)); //所分析对象的ID
        arf.setFileNameSeed("METADATA"+File.separator+"info"+File.separator+"mdinfos_"+arf.getId());
        arf.setFileName(rfService.buildFileName(arf.getFileNameSeed()));
        getMDInfos_Task.setResultFile(arf);
          //任务组装：组装进任务组+组装进report的dlist
        tg.addTask2Graph(getMDInfos_Task);
        report.addOneJsond(TaskUtils.convert2AccessJsondOne(getMDInfos_Task));

        for (String idinfo: mdl) {
            //4.a.2-单项字典项分析
            TaskInfo analSingleDict_Task = new TaskInfo();
            analSingleDict_Task.setId(SequenceUUID.getPureUUID());
            analSingleDict_Task.setTaskName("??单项指标分析");
            analSingleDict_Task.setLangType(TaskLangType.JAVA);
            analSingleDict_Task.setExcuteFunc("com.spiritdata.dataanal.metadata.relation.process.analSingleDict");
            analSingleDict_Task.setPrepared();
              //设置参数
            taskParam.clear();
            taskParam.put("metadata", mdl);
            analSingleDict_Task.setParam(JsonUtils.objToJson(taskParam));
            tg.addTask2Graph(analSingleDict_Task);
              //设置文件
            arf = new AnalResultFile();
            arf.setId(SequenceUUID.getPureUUID());
            arf.setJsonDCode(SDConstants.JDC_MDINFO);
            arf.setAnalType(SDConstants.ANAL_MD_GETINFO); //分析类型
            arf.setSubType("multiple"); //下级分类
            arf.setObjType("metadatas"); //所分析对象
            arf.setObjId(JsonUtils.objToJson(mdl)); //所分析对象的ID
            arf.setFileNameSeed("METADATA"+File.separator+"info"+File.separator+"mdinfos_"+arf.getId());
            arf.setFileName(rfService.buildFileName(arf.getFileNameSeed()));
            getMDInfos_Task.setResultFile(arf);
        }

        //组织报告的内容
        if (mdl!=null&&mdl.size()>0) {
            Iterator<Map<SheetTableInfo, Map<String, Object>>> iter = reportParam.values().iterator();
            while (iter.hasNext()) {
                Map<SheetTableInfo, Map<String, Object>> value = iter.next();
                for (SheetTableInfo key : value.keySet()) {
                    Map<String, Object> _value = value.get(key);
                    MetadataModel mm = (MetadataModel)_value.get("sysMd");
                    MetadataTableMapRel[] tabMapOrgAry = (MetadataTableMapRel[])_value.get("tabMapOrgAry");
                    //处理report中的数据访问列表
                    ReportSegment rs1 = new ReportSegment();
                    rs1.setTitle(mm.getTitleName()+"分析");
                    rs1.setId(SequenceUUID.getPureUUID());
                    rs1.setContent("");
                    //字典处理
                    for (MetadataColumn mc: mm.getColumnList()) {
                        List<MetadataColSemanteme> csl = mc.getColSemList();
                        if (csl!=null&&csl.size()>0) {
                            for (MetadataColSemanteme mcs: csl) {
                                if (mcs.getSemantemeType()==2) {//是字典
                                    ReportSegment rs2 = new ReportSegment();
                                    rs2.setTitle("["+mc.getTitleName()+"]字典项<数量>分布");
                                    rs2.setId(SequenceUUID.getPureUUID());
                                    rs2.setContent("");
                                }
                            }
                        }
                    }
                }
            }
        }
        report.set_REPORT(null);
        //4.1-分不同元数据，进行分析，目前包括()
//        private String id; //任务
//        private String taskName; //任务名称
//        private String langType; //执行语言，默认为java
//        private String excuteFunc; //任务执行方法
//        private String param; //任务执行所需的参数
//        private int status; //任务状态：1=准备执行；2=正在执行；3=执行成功；4=执行失败；5=任务失效；6=等待执行
//        private String desc; //任务说明
//        private Timestamp firstTime; //任务第一次准备执行时间
//        private Timestamp beginTime; //本次开始执行时间
//        private Timestamp endTime; //本次结束执行时间
        //4.2-导入日志，第一部分，都导入了那些内容

        //5-组装
        tr.setReport(report);
        tr.setTaskGroup(tg);

        Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("taskReport", tr);
        return ret;
    }

    /*
     * 从报告参数中得到元数据Id的列表
     * @param reportParam
     * @return
     */
    private List<String> getMDIdList(Map<SheetInfo, Map<SheetTableInfo, Map<String, Object>>> reportParam) {
        if (reportParam==null||reportParam.size()==0) return null;
        List<String> ret = new ArrayList<String>();
        Iterator<Map<SheetTableInfo, Map<String, Object>>> iter = reportParam.values().iterator();
        while (iter.hasNext()) {
            Map<SheetTableInfo, Map<String, Object>> value = iter.next();
            for (SheetTableInfo key : value.keySet()) {
                Map<String, Object> _value = value.get(key);
                MetadataModel mm = (MetadataModel)_value.get("sysMd");
                ret.add(mm.getId());
            }
        }
        if (ret.size()==0) return null;
        return ret;
    }
}