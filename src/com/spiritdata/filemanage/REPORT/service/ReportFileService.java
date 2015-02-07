package com.spiritdata.filemanage.REPORT.service;

import java.io.File;

import javax.annotation.Resource;

import com.spiritdata.dataanal.report.model.Report;
import com.spiritdata.filemanage.REPORT.model.ReportFile;
import com.spiritdata.filemanage.core.model.FileInfo;
import com.spiritdata.filemanage.core.pattern.model.BeManageFile;
import com.spiritdata.filemanage.core.pattern.model.ToBeStoreFile;
import com.spiritdata.filemanage.core.pattern.service.AbstractWriteString2File;
import com.spiritdata.filemanage.core.pattern.service.WriteJsonD;
import com.spiritdata.filemanage.core.service.FileManageService;
import com.spiritdata.filemanage.exceptionC.Flmg0003CException;
import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.util.FileNameUtils;

public class ReportFileService extends AbstractWriteString2File implements WriteJsonD {
    @Resource
    private FileManageService fmService;

    @Override
    public String buildFileName() {
        String root = (String)(SystemCache.getCache(FConstants.APPOSPATH)).getContent();
        String storeFile = FileNameUtils.concatPath(root, "reportJson"+File.separator+this.fileNameSeed+".json");
        return storeFile.replace("\\", "/");
    }

    /**
     * 把JsonD写入文件，并返回报告文件对象，同时要回写参数content所对应的报告report对象中的reportFile属性
     * @param content 写入数据
     * @param fileSeed 报告文件的种子，返回值将根据这个种子设置，种子
     * @return 
     */
    @Override
    public BeManageFile write2FileAsJsonD(Object content, ToBeStoreFile fileSeed) {
        Report report = (Report)content;
        ReportFile reportFileSeed = (ReportFile)fileSeed;
        if ((reportFileSeed.getFileNameSeed()==null||reportFileSeed.getFileNameSeed().trim().length()==0)
                &&(reportFileSeed.getFullFileName()==null||reportFileSeed.getFullFileName().trim().length()==0)) {
                  throw new Flmg0003CException(new IllegalArgumentException("文件种子对象中'文件名生成种子(fileNameSeed)'或'文件全名(fullFileName)'至少设定一个"));
              }

        //文件名处理
        String _fileNameSeed = reportFileSeed.getFileNameSeed();
        if (_fileNameSeed!=null&&_fileNameSeed.trim().length()>0) {
            this.setFileNameSeed(_fileNameSeed);
        }
        String _fullFileName = reportFileSeed.getFullFileName();
        if (_fullFileName!=null&&_fullFileName.trim().length()>0) {
            this.setFullFileName(_fullFileName);
        }

        //文件存储
        String storeFileName = this.getStoreFileName();
        report.setReportFile(reportFileSeed);
        this.writeJson2File(report.toJson());

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