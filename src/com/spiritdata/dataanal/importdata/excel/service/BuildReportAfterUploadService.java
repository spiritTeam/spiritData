package com.spiritdata.dataanal.importdata.excel.service;

import java.io.File;
import java.io.Serializable;
import java.sql.Timestamp;
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
import com.spiritdata.dataanal.report.model.SegmentList;
import com.spiritdata.dataanal.report.model.TaskReport;
import com.spiritdata.dataanal.task.TaskUtils;
import com.spiritdata.dataanal.task.enumeration.TaskLangType;
import com.spiritdata.dataanal.task.model.TaskGroup;
import com.spiritdata.dataanal.task.model.TaskInfo;
import com.spiritdata.filemanage.category.ANAL.model.AnalResultFile;
import com.spiritdata.filemanage.category.REPORT.service.ReportFileService;
import com.spiritdata.filemanage.core.model.FileInfo;
import com.spiritdata.framework.core.model.tree.TreeNode;
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
    /*
     * 参数中的preTreadParam键的值还是Map，包括：
     * ownerType——所有者类型
     * ownerId——所有者Id
     * impFileInfo——导入文件，FileInfo对象，此信息用于保存文件关系
     * reportParam——在初次分析文件时，所提取的为报告声称所需要的信息，包括：也是一个Map，包括
     *   HashMap<SheetInfo, Map<SheetTableInfo, Map<String, Object>>>
     *   每个sheet的信息，及其对应的每个数据表
     *   每个数据表的信息，又对应一个map，此Map为：
     *     tabMapOrgAry：实体表愿数据对应数据，通过此可以判断是否是新增元数据
     *     sysMd：本数据表元数据
     *
     * @see com.spiritdata.dataanal.report.generate.GenerateReport#preTreat(java.util.Map)
     */
    public Map<String, Object> preTreat(Map<String, Object> param) {
        //判断数据可用性
        //1-准备数据
        Map<SheetInfo, Map<SheetTableInfo, Map<String, Object>>> reportParam = (Map<SheetInfo, Map<SheetTableInfo, Map<String, Object>>>)param.get("reportParam");
        if (isEmptyParam(reportParam)) return null; //若报告参数中不包含任何元数据信息，无法生成报告，返回空
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
        tg.setDesc("{\"任务名称\":\""+tg.getWorkName()+"\"}");
        
        //4-构建报告体，并生成相关的任务
        AnalResultFile arf;
        Map<String, Object> taskParam = new HashMap<String, Object>();//任务参数结构，所有的任务都用此函数作为
        String mids = ""; //元数据id列表字符串，以,隔开
        SegmentList<ReportSegment> reportBody = new SegmentList<ReportSegment>();

        Iterator<Map<SheetTableInfo, Map<String, Object>>> iter = reportParam.values().iterator();
        while (iter.hasNext()) {
            Map<SheetTableInfo, Map<String, Object>> value = iter.next();
            for (SheetTableInfo key : value.keySet()) {
                Map<String, Object> _value = value.get(key);
                MetadataModel mm = (MetadataModel)_value.get("sysMd");
                MetadataTableMapRel[] tabMapOrgAry = (MetadataTableMapRel[])_value.get("tabMapOrgAry");
                mids += ","+mm.getId();

                //任务处理
                //4.a.2-单项字典项分析
                TaskInfo analSingleDict_Task = new TaskInfo();
                analSingleDict_Task.setId(SequenceUUID.getPureUUID());
                analSingleDict_Task.setTaskName(mm.getTitleName()+"单项指标分析");
                analSingleDict_Task.setLangType(TaskLangType.JAVA);
                analSingleDict_Task.setExcuteFunc("com.spiritdata.dataanal.metadata.relation.process.AnalSingleDict");
                analSingleDict_Task.setPrepared();
                  //设置参数
                taskParam.clear();
                taskParam.put("pType", "metadata");
                taskParam.put("mid", mm.getId());
                analSingleDict_Task.setParam(JsonUtils.objToJson(taskParam));
                tg.addTask2Graph(analSingleDict_Task);
                  //设置文件
                arf = new AnalResultFile();
                arf.setId(SequenceUUID.getPureUUID());
                arf.setJsonDCode(SDConstants.JDC_MD_SDICT);
                arf.setAnalType(SDConstants.ANAL_MD_SDICT); //分析类型
                arf.setSubType("SingleDict"); //下级分类
                arf.setObjType("metadata"); //所分析对象
                arf.setObjId(mm.getId()); //所分析对象的ID
                arf.setFileNameSeed("METADATA"+File.separator+"info"+File.separator+"mdSingleDict_"+arf.getId());
                arf.setFileName(rfService.buildFileName(arf.getFileNameSeed()));
                analSingleDict_Task.setResultFile(arf);
                report.addOneJsonD(TaskUtils.convert2AccessJsonDOne(analSingleDict_Task));
                //任务处理end

                //处理report中的数据访问列表
                ReportSegment rs1 = new ReportSegment();
                rs1.setNodeName(mm.getTitleName());
                rs1.setTitle(mm.getTitleName()+"分析");
                rs1.setId(SequenceUUID.getPureUUID());
                TreeNode<ReportSegment> rsTn1 = new TreeNode<ReportSegment>(rs1);
                reportBody.add(rsTn1);

                ReportSegment rs1_1 = new ReportSegment();
                rs1_1.setTitle("单向指标分析");
                rs1_1.setId(SequenceUUID.getPureUUID());
                TreeNode<ReportSegment> rsTn1_1 = new TreeNode<ReportSegment>(rs1_1);
                rsTn1.addChild(rsTn1_1);
                //字典处理
                for (MetadataColumn mc: mm.getColumnList()) {
                    List<MetadataColSemanteme> csl = mc.getColSemList();
                    if (csl!=null&&csl.size()>0) {
                        for (MetadataColSemanteme mcs: csl) {
                            if (mcs.getSemantemeType()==2) {//是字典
                                ReportSegment rs1_1_loop = new ReportSegment();
                                rs1_1_loop.setNodeName(mc.getTitleName()+"指标");
                                rs1_1_loop.setTitle("<div style='font-height:bold;'>"+mc.getTitleName()+"["+mc.getTitleName()+"]<div/>指标");
                                rs1_1_loop.setId(SequenceUUID.getPureUUID());
                                //TODO 判断有几个字典项目，若大于三个，采用下面的方式
                                rs1_1_loop.setContent("");
                                TreeNode<ReportSegment> rsTn1_1_loop = new TreeNode<ReportSegment>(rs1_1_loop);
                                rsTn1_1.addChild(rsTn1_1_loop);
                            }
                        }
                    }
                }
            }
        }

        //4.a.1-获得所有本次对应的元数据信息
        mids = mids.substring(1);
        TaskInfo getMDInfos_Task = new TaskInfo();
        getMDInfos_Task.setId(SequenceUUID.getPureUUID());
        getMDInfos_Task.setTaskName("获得元数据信息");
        getMDInfos_Task.setLangType(TaskLangType.JAVA);
        getMDInfos_Task.setExcuteFunc("com.spiritdata.dataanal.metadata.relation.process.GetMDInfos");
        getMDInfos_Task.setPrepared();
          //设置参数
        taskParam.clear();
        taskParam.put("pType", "metadatas");
        taskParam.put("mids", mids);
        getMDInfos_Task.setParam(JsonUtils.objToJson(taskParam));
          //设置文件
        arf = new AnalResultFile();
        arf.setId(SequenceUUID.getPureUUID());
        arf.setJsonDCode(SDConstants.JDC_MD_INFO);
        arf.setAnalType(SDConstants.ANAL_MD_GETINFO); //分析类型
        arf.setSubType("multiple"); //下级分类
        arf.setObjType("metadatas"); //所分析对象
        arf.setObjId(mids); //所分析对象的ID
        arf.setFileNameSeed("METADATA"+File.separator+"info"+File.separator+"mdinfos_"+arf.getId());
        arf.setFileName(rfService.buildFileName(arf.getFileNameSeed()));
        getMDInfos_Task.setResultFile(arf);
          //任务组装：组装进任务组+组装进report的dlist
        tg.addTask2Graph(getMDInfos_Task);
        report.addOneJsonD(TaskUtils.convert2AccessJsonDOne(getMDInfos_Task));

        report.set_REPORT(reportBody);
        //4.1-分不同元数据，进行分析，目前包括()
        //4.2-导入日志，第一部分，都导入了那些内容

        //5-组装
        tr.setReport(report);
        tr.setTaskGroup(tg);

        Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("taskReport", tr);
        return ret;
    }

    /*
     * 判断参数是否可用
     * @param reportParam 报告所需参数
     * @return 若参数有效 返回true，否则返回false
     */
    private boolean isEmptyParam(Map<SheetInfo, Map<SheetTableInfo, Map<String, Object>>> reportParam) {
        if (reportParam==null||reportParam.size()==0) return true;
        Iterator<Map<SheetTableInfo, Map<String, Object>>> iter = reportParam.values().iterator();
        while (iter.hasNext()) {
            Map<SheetTableInfo, Map<String, Object>> value = iter.next();
            for (SheetTableInfo key : value.keySet()) {
                Map<String, Object> _value = value.get(key);
                MetadataModel mm = (MetadataModel)_value.get("sysMd");
                if (mm!=null) return false;
            }
        }
        return true;
    }
}
