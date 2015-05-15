package com.spiritdata.dataanal.report.service;

import java.io.File;
import java.io.IOException;

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
        ReportPo rPo = reportDao.getInfoObject(id);
        if (rPo==null) return  null;
        return fmService.getFileInfoById(id);
    }

    /**
     * 根据报告Id，得到报告的Json串
     * @param reportId 报告Id
     * @return 报告json串
     */
    public String getReportJsonById(String reportId) {
        if (StringUtils.isNullOrEmptyOrSpace(reportId)) throw new Dtal1001CException("所给reportId参数为空，无法获取数据！");
        //根据id获取内容，现在先不处理//原文件名templet1.json
        return this.getReportJsonByUri("demo\\reportDemo\\report1.json");
    }

    /**
     * 根据Uri，得到报告的Json串。注意若Uri不带协议头，则指的是相对于服务目录根的相对地址
     * @param uri 报告的Uri
     * @return 报告json串
     */
    public String getReportJsonByUri(String uri) {
        String ret = null;
        if (uri.indexOf("\\\\:")!=-1||uri.indexOf("//:")!=-1) {//走协议方式
            
        } else {//走服务器目录方式
            uri = FileNameUtils.concatPath(((CacheEle<String>)SystemCache.getCache(FConstants.APPOSPATH)).getContent(), uri);
            File f = FileUtils.getFile(uri);
            if (f.isFile()) {//读取文件
                try {
                    ret = FileUtils.readFileToString(f, "UTF-8");
                    ret = JsonUtils.getCompactJsonStr(ret);
                } catch(IOException ioe) {
                    throw new Dtal1001CException("读取文件["+uri+"]失败！");
                }
            } else {
                throw new Dtal1001CException("Uri["+uri+"]所指向的地址不可用！");
            }
        }
        return ret;
    }
}
