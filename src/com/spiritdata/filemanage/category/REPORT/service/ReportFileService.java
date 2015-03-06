package com.spiritdata.filemanage.category.REPORT.service;

import java.io.File;

import javax.annotation.Resource;

import com.spiritdata.dataanal.report.model.Report;
import com.spiritdata.filemanage.category.REPORT.model.ReportFile;
import com.spiritdata.filemanage.core.model.FileInfo;
import com.spiritdata.filemanage.core.pattern.model.BeManageFile;
import com.spiritdata.filemanage.core.pattern.model.ToBeStoreFile;
import com.spiritdata.filemanage.core.pattern.service.AbstractWriteString2FileByToBeStoreFile;
import com.spiritdata.filemanage.core.pattern.service.WriteJsonD;
import com.spiritdata.filemanage.core.service.FileManageService;
import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.util.FileNameUtils;

public class ReportFileService extends AbstractWriteString2FileByToBeStoreFile implements WriteJsonD {
    @Resource
    private FileManageService fmService;

    @Override
    public String buildFileName(String fileNameSeed) {
        String root = (String)(SystemCache.getCache(FConstants.APPOSPATH)).getContent();
        String storeFile = FileNameUtils.concatPath(root, "reportJson"+File.separator+fileNameSeed+".json");
        return storeFile.replace("\\", "/");
    }

    /**
     * 把JsonD写入文件，并返回报告文件对象，同时要回写参数content所对应的报告report对象中的reportFile属性
     * @param content 写入数据
     * @param fileSeed 报告文件的种子，返回值将根据这个种子设置，种子
     * @return 
     */
    @Override
    public BeManageFile write2FileAsJson(Object content, ToBeStoreFile fileSeed) {
        Report report = (Report)content;
        ReportFile reportFileSeed = (ReportFile)fileSeed;

        //文件存储
        String storeFileName = this.getStoreFileName(fileSeed);
        report.setReportFile(reportFileSeed);
        this.writeJson2File(report.toJson(), fileSeed);

        //返回值处理
        reportFileSeed.setFileName(storeFileName);
        return reportFileSeed;
    }

    /**
     * 存储报告文件信息到数据库
     * @param rf 报告文件模型
     * @return 报告文件对应的模型化文件信息对象
     */
    public FileInfo saveFile(ReportFile rf) {
        return fmService.saveFile(rf);
    }
}