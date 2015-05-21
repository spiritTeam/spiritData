package com.spiritdata.filemanage.category.REPORT.model;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

import com.spiritdata.dataanal.common.model.Owner;
import com.spiritdata.filemanage.core.enumeration.FileCategoryType1;
import com.spiritdata.filemanage.core.model.FileCategory;
import com.spiritdata.filemanage.core.model.FileInfo;
import com.spiritdata.filemanage.core.pattern.model.AbstractToBeStoreFile;
import com.spiritdata.filemanage.core.pattern.model.BeManageFile;
import com.spiritdata.filemanage.exceptionC.Flmg0001CException;
import com.spiritdata.framework.util.DateUtils;
import com.spiritdata.framework.util.FileNameUtils;
import com.spiritdata.framework.util.FileUtils;

/**
 * 报告文件对象
 * 注意：<br/>
 * 1-这些有实际意义的文件都要在文件分类表中有对应
 * @author wh
 */
public class ReportFile extends AbstractToBeStoreFile implements Serializable, BeManageFile {
    private static final long serialVersionUID = -1625546654030117440L;

    private String id; //报告文件的id
    private Owner owner; //所有者
    private String fileName; //报告文件文件全名(包括目录和文件名)
    private String reportId; //报告所对应的Id，记录在表中
    private String taskGId; //任务组的Id，此Id也是taskG的id还要设计，存储在sa_file_category.type3中

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Owner getOwner() {
        return owner;
    }
    public void setOwner(Owner owner) {
        this.owner = owner;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getReportId() {
        return reportId;
    }
    public void setReportId(String reportId) {
        this.reportId = reportId;
    }
    public String getTaskGId() {
        return taskGId;
    }
    public void setTaskGId(String taskGId) {
        this.taskGId = taskGId;
    }

    /**
     * 转换为模型化文件信息，注意，这里要验证服务器端文件是否存在。<br/>
     * @return 模型化文件信息
     */
    public FileInfo convert2FileInfo() {
        File f = new File(this.fileName);
        if (f==null||!f.isFile()) throw new Flmg0001CException(new IllegalArgumentException("报告文件对象中fileName所指向的文件为空或是一个目录！"));

        FileInfo ret = new FileInfo();
        //主信息
        ret.setFile(f);
        ret.setId(this.id);
        ret.setOwner(this.owner);
        ret.setAccessType(1);
        ret.setDesc("分析报告文件:"+FileNameUtils.getFileName(this.fileName)+"；生成报告时间:"+DateUtils.convert2TimeChineseStr(new Date(FileUtils.getFileCreateTime4Win(f)))
                +"；报告文件Id："+this.reportId+"，对应的任务组ID："+this.taskGId);
        //分类信息
        FileCategory fc = new FileCategory();
        fc.setFType1(FileCategoryType1.REPORT);
        fc.setFType2(this.reportId);
        fc.setFType3(this.taskGId);
        ret.addFileCategoryList(fc);

        return ret;
    }
    @Override
    public void buildFromFileInfo(FileInfo fi) {
        // TODO Auto-generated method stub
        
    }
}