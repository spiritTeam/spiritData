package com.spiritdata.dataanal.report.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;

import com.spiritdata.filemanage.core.model.FileInfo;
import com.spiritdata.filemanage.core.service.FileManageService;
import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.util.FileNameUtils;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.jsonD.util.JsonUtils;
import com.spiritdata.dataanal.exceptionC.Dtal1001CException;
import com.spiritdata.dataanal.exceptionC.Dtal1004CException;
import com.spiritdata.dataanal.report.model.Report;
import com.spiritdata.dataanal.report.model.TaskReport;
import com.spiritdata.dataanal.report.persistence.pojo.ReportPo;

/**
 * 报告服务，主要是获得报告信息
 * @author wh
 */
public class ReportService {
    @Resource
    private FileManageService fmService;

    @Resource(name="defaultDAO")
    private MybatisDAO<ReportPo> reportDao;

    @PostConstruct
    public void initParam() {
        reportDao.setNamespace("report");
    }

    /**
     * 保存报告信息
     * @param report 报告对象
     */
    public void saveReport(Report report) {
        try {
            ReportPo rPo = report.convert2Po();
            reportDao.insert(rPo);
        } catch(Exception e) {
            throw new Dtal1004CException(e);
        }
    }

    /**
     * 保存报告信息
     * @param taskReport 任务报告对象
     */
    public void saveReport(TaskReport taskReport) {
        try {
            ReportPo rPo = taskReport.convert2ReportPo();
            reportDao.insert(rPo);
        } catch(Exception e) {
            throw new Dtal1004CException(e);
        }
    }

    /**
     * 根据报告Id，得到报告的文件信息
     * @param id 报告Id
     * @return 文件信息
     */
    public FileInfo getReportFiById(String id) {
        ReportPo rPo = reportDao.getInfoObject("getInfoById", id);
        if (rPo==null) return  null;
        return fmService.getFileInfoById(rPo.getFId());
    }

    /**
     * 根据报告Id，得到报告的Json串
     * @param reportId 报告Id
     * @return 报告json串，注意：此Json串格式为Map，包括
     * <pre>
     *   type:1=成功；2=失败；3=正在进行
     *   data:若type=1，是实际的json串
     * </pre>
     */
    public Map<String, Object> getReportJsonById(String reportId) {
        if (StringUtils.isNullOrEmptyOrSpace(reportId)) throw new Dtal1001CException("所给reportId参数为空，无法获取数据！");
        //根据id获取内容，现在先不处理//原文件名templet1.json
        Map<String, Object> retM = new HashMap<String, Object>();
        FileInfo reportFi = getReportFiById(reportId);
        if (reportFi==null) {
            retM.put("jsonType", 0);
            retM.put("message", "没有找到Id为["+reportId+"]的报告，无法获得数据");
        } else {
            retM = this.getReportJsonByUri(reportFi.getAllFileName());
        }
        if (retM.size()==0) retM=null;
        return retM;
    }

    /**
     * 根据Uri，得到报告的Json串。注意若Uri不带协议头，则指的是相对于服务目录根的相对地址
     * @param uri 报告的Uri
     * @return 报告json串
     */
    public Map<String, Object> getReportJsonByUri(String uri) {
        Map<String, Object> retM = new HashMap<String, Object>();
        String _jsonStr = "";

        if (uri.indexOf("\\\\:")!=-1||uri.indexOf("//:")!=-1) {//走协议方式
        } else {//走服务器目录方式
            String _OSNAME = System.getProperties().getProperty("os.name");
            if ((_OSNAME.toUpperCase().startsWith("WINDOW")&&uri.charAt(0)!='\\'&&uri.charAt(0)!='/'&&uri.indexOf(':')==-1)
              ||((!_OSNAME.toUpperCase().startsWith("WINDOW"))&&uri.charAt(0)!='\\'&&uri.charAt(0)!='/')) {
                uri = FileNameUtils.concatPath(((CacheEle<String>)SystemCache.getCache(FConstants.APPOSPATH)).getContent(), uri);
            }
            File f = FileUtils.getFile(uri);
            if (f.isFile()) {//读取文件
                try {
                    _jsonStr = FileUtils.readFileToString(f, "UTF-8");
                    _jsonStr = JsonUtils.getCompactJsonStr(_jsonStr);
                    retM.put("jsonType", 1);
                    retM.put("data",_jsonStr);
                } catch(IOException ioe) {
                    throw new Dtal1001CException("读取文件["+uri+"]失败！");
                }
            } else {
                throw new Dtal1001CException("Uri["+uri+"]所指向的地址不可用！");
            }
        }
        if (retM.size()==0) return null;
        return retM;
    }
}