package com.spiritdata.dataanal.task.process;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.spiritdata.dataanal.exceptionC.Dtal0402CException;
import com.spiritdata.dataanal.exceptionC.Dtal0404CException;
import com.spiritdata.dataanal.report.service.ReportService;
import com.spiritdata.dataanal.task.TaskUtils;
import com.spiritdata.dataanal.task.core.enumeration.StatusType;
import com.spiritdata.dataanal.task.core.model.TaskInfo;
import com.spiritdata.dataanal.task.core.service.TaskManageService;
import com.spiritdata.dataanal.task.run.mem.TaskMemoryService;
import com.spiritdata.filemanage.category.ANAL.model.AnalResultFile;
import com.spiritdata.filemanage.category.ANAL.service.AnalResultFileService;
import com.spiritdata.filemanage.core.enumeration.RelType1;
import com.spiritdata.filemanage.core.model.FileInfo;
import com.spiritdata.filemanage.core.model.FileRelation;
import com.spiritdata.filemanage.core.service.FileManageService;
import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.jsonD.model.JsonD;
import com.spiritdata.jsonD.model.JsonDHead;

public class TaskExecutorShell implements Runnable {
    //需要执行的任务信息
    private TaskInfo ti;

    /**
     * 构造函数，此对象的构造必须要有一个任务信息
     * @param ti
     */
    public TaskExecutorShell(TaskInfo ti) {
        super();
        this.ti = ti;
    }

    /**
     * 执行任务信息中所指定的任务
     */
    @Override
    public void run() {
        if (this.ti!=null) {
            ServletContext sc = (ServletContext)SystemCache.getCache(FConstants.SERVLET_CONTEXT).getContent();

            //1-设置为执行状态
            this.ti.setProcessing();
            this.ti.setBeginTime(new Timestamp(System.currentTimeMillis()));
            //2-执行
            boolean success=false; //是否执行成功
            boolean notSaveResult2File=false; //是否把结果存储为文件，默认情况下，会把结果存入文件
            String JDC = null; //若写入文件，这里是JsonDCode
            Map<String, Object> resultMap = null; //执行的结果
            try {
                //2-classLoader
                TaskProcess tp = TaskUtils.loadClass(this.ti);
                resultMap = tp.process(ti.getParam());
                //根据结果，设置处理参数
                if (resultMap!=null&&resultMap.get("sysResultData")!=null) {
                    Map<String, String> sysResultData = (Map<String, String>)resultMap.get("sysResultData");
                    if (sysResultData.get("resultType").trim().equals("1")) success=true;
                    if (sysResultData.get("notSaveResult2File").trim().equals("1")) notSaveResult2File=true;
                    if (StringUtils.isNullOrEmptyOrSpace(sysResultData.get("JsonDCode"))) JDC=sysResultData.get("JsonDCode");
                }
            } catch(Exception e) {
                (new Dtal0404CException(e)).printStackTrace();
            }
            //3-执行结束处理
            this.ti.setEndTime(new Timestamp(System.currentTimeMillis()));
            if (!success) ti.setFailed();
            else {
                ti.setSuccessed();
                if (!notSaveResult2File&&resultMap!=null) {//文件存储为文件
                    Map<String, Object> userResultData = (Map<String, Object>)resultMap.get("userResultData");
                    if (userResultData!=null) { //写文件
                        try {
                            //写文件
                            if (JDC==null) {
                                throw new Dtal0402CException("把分析结果以jsonD格式进行存储时，需要明确指定JsonDCode！");
                            }
                            JsonD analDictJsonD = new JsonD();
                            //头
                            JsonDHead jsonDHead = new JsonDHead();
                            jsonDHead.setId(SequenceUUID.getPureUUID());
                            jsonDHead.setCode(JDC);
                            jsonDHead.setCTime(new Date());
                            jsonDHead.setDesc("分析任务[id="+ti.getId()+"]，分析名称["+ti.getTaskName()+"]"+(StringUtils.isNullOrEmptyOrSpace(ti.getDesc())?"":(":"+ti.getDesc())));
                            //数据体
                            analDictJsonD.set_HEAD(jsonDHead);
                            analDictJsonD.set_DATA(userResultData);
                            //分析结果文件种子设置
                            AnalResultFile arfSeed = new AnalResultFile();
                            arfSeed.setAnalType(ti.getTaskType()); //分析类型，相当于文件类型表中的type2
                            arfSeed.setSubType(ti.getId()); //任务Id，三级分类，相当于文件文件类型表中的type3
                            arfSeed.setExtInfo(ti.getParam());
                            arfSeed.setFileNameSeed(ti.getTaskType()+File.separator+ti.getId());
                            arfSeed.setJsonDCode(JDC);
                            AnalResultFileService arfService = (AnalResultFileService)WebApplicationContextUtils.getWebApplicationContext(sc).getBean("analResultFileService");
                            AnalResultFile arf = (AnalResultFile)arfService.write2FileAsJson(analDictJsonD, arfSeed);
                            //写数据库
                            FileInfo arFi = arfService.saveFile(arf);
                            //写文件关联关系，注意这里的关系是文件分类到文件的直接关系
                            if (!StringUtils.isNullOrEmptyOrSpace(ti.getTaskGroup().getReportId())) {
                                //通过reportId得到reportFile对象
                                ReportService rService = (ReportService)WebApplicationContextUtils.getWebApplicationContext(sc).getBean("reportService");
                                FileInfo reportFi = rService.getReportFiById(ti.getTaskGroup().getReportId());
                                if (reportFi!=null) {
                                    FileRelation fr = new FileRelation();
                                    fr.setElement1(reportFi.getFileCategoryList().get(0));
                                    fr.setElement2(arFi);
                                    fr.setCTime(new Timestamp((new Date()).getTime()));
                                    fr.setRType1(RelType1.POSITIVE);
                                    fr.setRType2("报告中的数据");
                                    fr.setDesc(ti.getTaskType()+"::"+ti.getTaskName());
                                    FileManageService fmService = (FileManageService)WebApplicationContextUtils.getWebApplicationContext(sc).getBean("fileManageService");
                                    fmService.saveFileRelation(fr);//文件关联存储
                                }
                            }
                        } catch(Exception e) {
                            (new Dtal0404CException(e)).printStackTrace();
                        }
                    }
                }
            }
            //写入数据库
            //这里需要用到Spring的容器
            TaskManageService tmService = (TaskManageService)WebApplicationContextUtils.getWebApplicationContext(sc).getBean("taskManageService");
            tmService.completeTaskInfo(ti, (success?StatusType.SUCCESS:StatusType.FAILD));
            //把失败的任务再放入内存继续执行
            if (ti.getStatus()==StatusType.FAILD) (TaskMemoryService.getInstance()).addFaildTaskInfo(ti);
        }
    }
}