package com.spiritdata.dataanal.importdata.excel.service;

import java.io.File;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.spiritdata.dataanal.SDConstants;
import com.spiritdata.dataanal.common.model.Owner;
import com.spiritdata.dataanal.dictionary.model.DictModel;
import com.spiritdata.dataanal.dictionary.model._OwnerDictionary;
import com.spiritdata.dataanal.exceptionC.Dtal1003CException;
import com.spiritdata.dataanal.importdata.excel.pojo.SheetInfo;
import com.spiritdata.dataanal.importdata.excel.pojo.SheetTableInfo;
import com.spiritdata.dataanal.metadata.enumeration.DataType;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataColSemanteme;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataColumn;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataTableMapRel;
import com.spiritdata.dataanal.report.enumeration.DtagShowType;
import com.spiritdata.dataanal.report.generate.AbstractGenerateSessionReport;
import com.spiritdata.dataanal.report.model.D_Tag;
import com.spiritdata.dataanal.report.model.D_Tags;
import com.spiritdata.dataanal.report.model.Report;
import com.spiritdata.dataanal.report.model.ReportHead;
import com.spiritdata.dataanal.report.model.ReportSegment;
import com.spiritdata.dataanal.report.model.SegmentList;
import com.spiritdata.dataanal.report.model.TaskReport;
import com.spiritdata.dataanal.task.TaskUtils;
import com.spiritdata.dataanal.task.core.enumeration.TaskLangType;
import com.spiritdata.dataanal.task.core.model.TaskGraph;
import com.spiritdata.dataanal.task.core.model.TaskGroup;
import com.spiritdata.dataanal.task.core.model.TaskInfo;
import com.spiritdata.filemanage.category.ANAL.model.AnalResultFile;
import com.spiritdata.filemanage.category.ANAL.service.AnalResultFileService;
import com.spiritdata.filemanage.core.model.FileInfo;
import com.spiritdata.framework.core.model.tree.TreeNode;
import com.spiritdata.framework.core.model.tree.TreeNodeBean;
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
    private AnalResultFileService arfService;

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
     * ownerDict——字典缓存
     * @see com.spiritdata.dataanal.report.generate.GenerateReport#preTreat(java.util.Map)
     */
    public Map<String, Object> preTreat(Map<String, Object> param) {
        //准备通用变量
        String tempStr = "", tempContent = "", mids = ""; //元数据id列表字符串，以,隔开
        Map<String, Object> _value = null;
        MetadataModel mm = null;
        MetadataTableMapRel[] tabMapOrgAry = null;
        boolean hasDictTask = false;

        //判断数据可用性
        //1-准备数据
        Map<SheetInfo, Map<SheetTableInfo, Map<String, Object>>> reportParam = (Map<SheetInfo, Map<SheetTableInfo, Map<String, Object>>>)param.get("reportParam");
        if (isEmptyParam(reportParam)) return null; //若报告参数中不包含任何元数据信息，无法生成报告，返回空
        Owner owner = (Owner)param.get("owner");
        if (owner==null) throw new Dtal1003CException(new IllegalArgumentException("Map参数中没有所有者Owner信息！"));
        FileInfo impFi = (FileInfo)param.get("impFileInfo");
        if (impFi==null) throw new Dtal1003CException(new IllegalArgumentException("Map参数中没有导入文件对象[impFileInfo]的信息！"));
        _OwnerDictionary _od = (_OwnerDictionary)param.get("ownerDict");

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
        report.setOwner(owner);
        String reportName = "["+FileNameUtils.getFileName(clientFileName)+"]——文件导入后数据分析报告";
        report.setReportName(reportName); //设置报告名称，头的reportName也设置了
        report.setCTime(new Timestamp(System.currentTimeMillis())); //设置报告生成时间，同时也设置了头的时间
        report.setDesc(reportName); //设置报告说明，同时也设置了报告头的说明
        report.setReportType("导入后即时报告");
        rHead.setCode(SDConstants.RP_AFTER_IMP);
        //2.2-报告文件，文件在基础类中处理，这里略过

        //3-任务组处理——构建任务组
        //3.1-构建组
        tg.setId(SequenceUUID.getPureUUID());
        tg.setOwner(owner);
        tg.setWorkName("["+FileNameUtils.getFileName(clientFileName)+"]——文件导入后分析任务");
        tg.setPrepared();
        tg.setDesc("{\"任务名称\":\""+tg.getWorkName()+"\"}");
        
        //4-构建报告体，并生成相关的任务
        AnalResultFile arf = null;
        Map<String, Object> taskParam = new HashMap<String, Object>();//任务参数结构，所有的任务都用此函数作为
        SegmentList<TreeNode<ReportSegment>> reportBody = new SegmentList<TreeNode<ReportSegment>>();

        Iterator<Map<SheetTableInfo, Map<String, Object>>> iter = reportParam.values().iterator();
        while (iter.hasNext()) {
            Map<SheetTableInfo, Map<String, Object>> value = iter.next();
            for (SheetTableInfo key : value.keySet()) {
                _value = value.get(key);
                mm = (MetadataModel)_value.get("sysMd");
                tabMapOrgAry = (MetadataTableMapRel[])_value.get("tabMapOrgAry");
                mids += ","+mm.getId();
                tempStr = SequenceUUID.getPureUUID();

                //处理report中的字典项目
                ReportSegment rs1 = new ReportSegment();
                rs1.setNodeName(mm.getTitleName());
                rs1.setTitle(mm.getTitleName()+"分析");
                rs1.setId(SequenceUUID.getPureUUID());
                TreeNode<ReportSegment> rsTn1 = new TreeNode<ReportSegment>(rs1);
                reportBody.add(rsTn1);
                //字典内容处理——某一个表
                ReportSegment rs1_1 = new ReportSegment();
                rs1_1.setTitle("单字典项指标分析");
                rs1_1.setId(tempStr);
                String sIContent="";
                TreeNode<ReportSegment> rsTn1_1 = new TreeNode<ReportSegment>(rs1_1);

            	//找出数值列
            	List<MetadataColumn> numColList = new ArrayList<MetadataColumn>();
            	for (MetadataColumn mc: mm.getColumnList()) {
            		if (mc.isPk()) continue;
                    DataType colDT = DataType.getDataType(mc.getColumnType());
                    if (colDT==DataType.DOUBLE||colDT==DataType.LONG||colDT==DataType.INTEGER) {
                    	numColList.add(mc);
                    }
            	}
                //字典处理——表内各字典项，可能没有字典项
                hasDictTask = false;
                for (MetadataColumn mc: mm.getColumnList()) {
                    List<MetadataColSemanteme> csl = mc.getColSemList();
                    if (csl!=null&&csl.size()>0) {
                        for (int i=0; i<csl.size(); i++) {
                            MetadataColSemanteme mcs = csl.get(i);
                            if (mcs.getSemantemeType()==1&&_od!=null&&_od.getDictModelById(mcs.getSemantemeCode())!=null) {//是字典
                                sIContent+="、"+mc.getColumnName();
                                if (!hasDictTask) { //有字典项，生成字典项任务
                                    //任务处理
                                    //4.a.1-单项字典项分析
                                    TaskInfo analSingleDict_Task = new TaskInfo();
                                    analSingleDict_Task.setId(tempStr);
                                    analSingleDict_Task.setTaskName(mm.getTitleName()+"单字典项指标分析");
                                    analSingleDict_Task.setTaskType(SDConstants.ANAL_MD_SDICT);
                                    analSingleDict_Task.setLangType(TaskLangType.JAVA);
                                    analSingleDict_Task.setExecuteFunc("com.spiritdata.dataanal.metadata.relation.process.AnalSingleDict");
                                    analSingleDict_Task.setPrepared();
                                      //设置参数
                                    taskParam.clear();
                                    taskParam.put("pType", "metadata");
                                    taskParam.put("mid", mm.getId());
                                    analSingleDict_Task.setParam(JsonUtils.objToJson(taskParam));
                                    tg.addTask2Graph(analSingleDict_Task);
                                      //设置文件
                                    arf = new AnalResultFile();
                                    arf.setId(tempStr);
                                    arf.setJsonDCode(SDConstants.JDC_MD_SDICT);
                                    arf.setAnalType(analSingleDict_Task.getTaskType()); //分析类型
                                    arf.setSubType("task::"+analSingleDict_Task.getId()); //下级分类
//                                    arf.setObjType("metadata"); //所分析对象
//                                    arf.setObjId(mm.getId()); //所分析对象的ID
                                    arf.setFileNameSeed(analSingleDict_Task.getTaskType()+File.separator+analSingleDict_Task.getId());
                                    arf.setFileName(arfService.buildFileName(arf.getFileNameSeed()));
                                    analSingleDict_Task.setResultFile(arf);
                                    report.addOneJsonD(TaskUtils.convert2AccessJsonDOne(analSingleDict_Task));
                                    //任务处理end
                                    hasDictTask=true;
                                }
                                if (rsTn1.getChild(tempStr)==null) rsTn1.addChild(rsTn1_1);
                                ReportSegment rs1_1_loop = new ReportSegment();
                                rs1_1_loop.setNodeName(mc.getTitleName()+"指标");
                                rs1_1_loop.setTitle("<span style='font-weight:bold;'>"+mm.getTitleName()+"["+mc.getTitleName()+"]</span>指标");
                                rs1_1_loop.setId(SequenceUUID.getPureUUID());
                                DictModel dm = _od.getDictModelById(mcs.getSemantemeCode());
                                
                                //构建显示内容
                                tempContent = "";
                                //第一步：显示字典项单项指标分析，只有字典项的二维表和饼图
                                //显示文本
                                D_Tag discriptDt = new D_Tag();
                                discriptDt.setShowType(DtagShowType.TEXT);
                                discriptDt.setDid(report.getDid(tempStr)+"");
                                discriptDt.setValue("dictData[^"+mc.getColumnName()+"^]");
                                discriptDt.setValueFilterFun("first(3|count)");
                                discriptDt.setDecorateView("{#category#}占#percent(count)#%");
                                tempContent += "<span style='font-weight:bold'>数量分布</span>";
                                if (dm.dictTree.getChildCount()>3) tempContent += "，前三位是";

                                //显示二维表格
                                D_Tag tableDt = new D_Tag();
                                tableDt.setShowType(DtagShowType.TABLE);
                                tableDt.setDid(report.getDid(tempStr)+"");
                                tableDt.setValue("dictData[^"+mc.getColumnName()+"^]");
                                  //显示哪些列
                                Map<String,String> tempMap = new LinkedHashMap<String,String>();
                                tempMap.put(mc.getTitleName(), "category");
                                tempMap.put("数量", "count");
                                tempMap.put("百分比", "percent(count)");
                                tableDt.setParam(tempMap);
                                //显示饼图
                                D_Tag pieDt = new D_Tag();
                                pieDt.setShowType(DtagShowType.PIE);
                                pieDt.setDid(report.getDid(tempStr)+"");
                                pieDt.setValue("dictData[^"+mc.getColumnName()+"^]");
                                tempMap = new HashMap<String,String>();
                                tempMap.put("xAxis", "category");
                                tempMap.put("yAxis", "count");
                                pieDt.setParam(tempMap);
                                pieDt.setDecorateView("#category#, #percent(count)#");
                                tempContent += discriptDt.toHtmlTag()+"，具体数据为：<br/><table><tr><td>"+tableDt.toHtmlTag()+"</td><td>"+pieDt.toHtmlTag()+"</td></tr></table>";
                                //第二：针对每个字典项，循环数值列，显示二维表和饼图
                                String numbContent="";
                                for(int nidx=0;nidx<numColList.size();nidx++){
                                	MetadataColumn acolmc = (MetadataColumn)numColList.get(nidx);
                                	if (acolmc.getTitleName().equals(mc.getTitleName())) continue;
                                    numbContent += "数值列<span style='font-weight:bold;'>["+acolmc.getTitleName()+"]</span>对["+mc.getTitleName()+"]的统计情况如下：<br/>";
                                    //1：二维表数据
                                    tableDt = new D_Tag();
                                    tableDt.setShowType(DtagShowType.TABLE);
                                    tableDt.setDid(report.getDid(tempStr)+"");
                                    tableDt.setValue("dictData[^"+mc.getColumnName()+"^]");
                                    //显示哪些列
                                    tempMap = new LinkedHashMap<String,String>();
                                    tempMap.put(mc.getTitleName(), "category");
                                    tempMap.put("总量(Σ)", "SUM_"+acolmc.getColumnName());
                                    tempMap.put("最大值(max)", "MAX_"+acolmc.getColumnName());
                                    tempMap.put("最小值(max)", "MIN_"+acolmc.getColumnName());
                                    tempMap.put("平均值(max)", "AVG_"+acolmc.getColumnName());
                                    tempMap.put("非空个数", "COUNT_"+acolmc.getColumnName());
                                    tableDt.setParam(tempMap);
                                    tempMap = new HashMap<String,String>();
                                    tempMap.put("style", "{~width~:~500px~}");
                                    tableDt.setHtmlExt(tempMap);
                                    //2：总量饼图
                                    pieDt = new D_Tag();
                                    pieDt.setShowType(DtagShowType.PIE);
                                    pieDt.setDid(report.getDid(tempStr)+"");
                                    pieDt.setValue("dictData[^"+mc.getColumnName()+"^]");
                                    tempMap = new HashMap<String,String>();
                                    tempMap.put("xAxis", "category");
                                    tempMap.put("yAxis", "SUM_"+acolmc.getColumnName());
                                    pieDt.setParam(tempMap);
                                    pieDt.setDecorateView("#category#, #percent(SUM_"+acolmc.getColumnName()+")#");
                                    tempMap = new HashMap<String,String>();
                                    tempMap.put("style", "{~width~:~240px~, ~height~:~160px~}");
                                    pieDt.setHtmlExt(tempMap);
                                    //3：数值显示组图
                                    D_Tags dTags = new D_Tags();
                                    tempMap = new HashMap<String,String>();
                                    tempMap.put("style", "{~width~:~240px~, ~height~:~160px~}");
                                    dTags.setHtmlExt(tempMap);
                                    //最大值
                                    D_Tag lineMaxDt = new D_Tag();
                                    lineMaxDt.setShowType(DtagShowType.LINE);
                                    lineMaxDt.setDid(report.getDid(tempStr)+"");
                                    lineMaxDt.setValue("dictData[^"+mc.getColumnName()+"^]");         
                                    lineMaxDt.setLabel("最大值");
                                    tempMap = new HashMap<String,String>();
                                    tempMap.put("xAxis", "category");
                                    tempMap.put("yAxis", "MAX_"+acolmc.getColumnName());
                                    lineMaxDt.setParam(tempMap);
                                    lineMaxDt.setDecorateView("#category#, #MAX_"+acolmc.getColumnName()+"#");
                                    dTags.addOneDTag(lineMaxDt);
                                    //最小值
                                    D_Tag lineMinDt = new D_Tag();
                                    lineMinDt.setShowType(DtagShowType.LINE);
                                    lineMinDt.setDid(report.getDid(tempStr)+"");
                                    lineMinDt.setValue("dictData[^"+mc.getColumnName()+"^]");
                                    lineMinDt.setLabel("最小值");
                                    tempMap = new HashMap<String,String>();
                                    tempMap.put("xAxis", "category");
                                    tempMap.put("yAxis", "MIN_"+acolmc.getColumnName());
                                    lineMinDt.setParam(tempMap);
                                    lineMinDt.setDecorateView("#category#, #MIN_"+acolmc.getColumnName()+"#");
                                    dTags.addOneDTag(lineMinDt);
                                    //平均值
                                    D_Tag lineAvgDt = new D_Tag();
                                    lineAvgDt.setShowType(DtagShowType.LINE);
                                    lineAvgDt.setDid(report.getDid(tempStr)+"");
                                    lineAvgDt.setValue("dictData[^"+mc.getColumnName()+"^]");
                                    lineAvgDt.setLabel("平均值");
                                    tempMap = new HashMap<String,String>();
                                    tempMap.put("xAxis", "category");
                                    tempMap.put("yAxis", "AVG_"+acolmc.getColumnName());
                                    lineAvgDt.setParam(tempMap);
                                    lineAvgDt.setDecorateView("#category#, #AVG_"+acolmc.getColumnName()+"#");
                                    dTags.addOneDTag(lineAvgDt);

                                    numbContent += "<table><tr><td colspan=2>"+tableDt.toHtmlTag()+"</td></tr>"
                                        +"<tr><td>"+pieDt.toHtmlTag()+"</td><td>"+dTags.toHtmlTag()+"</td></tr></table><br/>";
                                }
                                //加到节点上
                                if (!numbContent.equals("")) numbContent="<span style='font-weight:bold'>本指标对应各数值列情况如下:</span><br/>"+numbContent;
                                rs1_1_loop.setContent(tempContent+numbContent);
                                TreeNode<ReportSegment> rsTn1_1_loop = new TreeNode<ReportSegment>(rs1_1_loop);
                                rsTn1_1.addChild(rsTn1_1_loop);
                            }
                        }
                        sIContent+="经分析Sheet1中可作为指标(字典)项处理的列为："+sIContent.substring(1)+",各指标项分析情况如下——";
                    }
                }
                //元数据分析，结构分析
                ReportSegment rs1_2 = new ReportSegment();
                rs1_2.setTitle("结构分析");
                rs1_2.setId(mm.getId());
                rs1_2.setContent("["+mm.getTitleName()+"]为已有结构");
                if (tabMapOrgAry.length==3) rs1_2.setContent("["+mm.getTitleName()+"]为新增结构");
                TreeNode<ReportSegment> rsTn1_2 = new TreeNode<ReportSegment>(rs1_2);
                rsTn1.addChild(rsTn1_2);
            }
        }

        //任务处理
        //4.a.2-获得所有本次对应的所有元数据信息
        mids = mids.substring(1);
        TaskInfo getMDInfos_Task = new TaskInfo();
        getMDInfos_Task.setId(SequenceUUID.getPureUUID());
        tempStr = getMDInfos_Task.getId();
        getMDInfos_Task.setTaskName("获得元数据信息");
        getMDInfos_Task.setTaskType(SDConstants.ANAL_MD_GETINFO);
        getMDInfos_Task.setLangType(TaskLangType.JAVA);
        getMDInfos_Task.setExecuteFunc("com.spiritdata.dataanal.metadata.relation.process.GetMDInfos");
        getMDInfos_Task.setPrepared();
          //设置参数
        taskParam.clear();
        taskParam.put("pType", "metadatas");
        taskParam.put("mids", mids);
        getMDInfos_Task.setParam(JsonUtils.objToJson(taskParam));
          //设置文件
        arf = new AnalResultFile();
        arf.setId(getMDInfos_Task.getId());
        arf.setJsonDCode(SDConstants.JDC_MD_INFO);
        arf.setAnalType(getMDInfos_Task.getTaskType()); //分析类型
        arf.setSubType("task::"+getMDInfos_Task.getId()); //下级分类
//        arf.setObjType("metadatas"); //所分析对象
//        arf.setObjId(mids); //所分析对象的ID
//        arf.setFileNameSeed("METADATA"+File.separator+"info"+File.separator+"mdinfos_"+arf.getId());
        arf.setFileNameSeed(getMDInfos_Task.getTaskType()+File.separator+getMDInfos_Task.getId());
        arf.setFileName(arfService.buildFileName(arf.getFileNameSeed()));
        getMDInfos_Task.setResultFile(arf);
          //任务组装：组装进任务组+组装进report的dlist
        tg.addTask2Graph(getMDInfos_Task);
        report.addOneJsonD(TaskUtils.convert2AccessJsonDOne(getMDInfos_Task));

        //处理数据结构段落中的did
        if (reportBody!=null&&reportBody.size()>0) {
            for (TreeNode<ReportSegment> rsTn: reportBody) {
                if (rsTn.getChildCount()>0) {
                    for (TreeNode<? extends TreeNodeBean> _rsTn: rsTn.getChildren()) {
                        ReportSegment rs = (ReportSegment)_rsTn.getTnEntity();
                        if (rs.getTitle().equals("结构分析")) {
                            D_Tag tableDt = new D_Tag();
                            tableDt.setShowType(DtagShowType.TABLE);
                            tableDt.setDid(report.getDid(tempStr)+"");
                            tableDt.setValue("mdInfos[^"+rs.getId()+"^]");
                            rs.setContent(rs.getContent()+"，对其结构的分析结果如下：<br/>"+tableDt.toHtmlTag());
                        }
                    }
                }
            }
        }

        report.set_REPORT(reportBody);
        //4.1-分不同元数据，进行分析，目前包括()
        //4.2-导入日志，第一部分，都导入了那些内容

        //5-组装
        tr.setReport(report);

        //为测试====BEGIN
        TaskGraph tGraph = tg.getTaskGraph();
        Map<String, TaskInfo> taskMap = tGraph.getTaskMap();
        for (String taskId: taskMap.keySet()) {
            TaskInfo ti = taskMap.get(taskId);
            if (!ti.getId().equals(getMDInfos_Task.getId())) {
                ti.addPreTask(getMDInfos_Task, false);
            }
        }
        //为测试====END
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