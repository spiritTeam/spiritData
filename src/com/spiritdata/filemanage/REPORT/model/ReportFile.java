package com.spiritdata.filemanage.REPORT.model;

import java.io.File;
import java.io.Serializable;

import com.spiritdata.filemanage.core.BeManageFile;
import com.spiritdata.filemanage.core.model.FileInfo;
import com.spiritdata.filemanage.exceptionC.Flmg0001CException;

public class ReportFile implements Serializable, BeManageFile {
    private static final long serialVersionUID = -1625546654030117440L;

    private String id; //模板(或报告)文件的id
    private int ownerType; //模板文件所对应的所有者类型（1=注册用户;2=非注册用户(session)）
    private String ownerId; //模板文件所有者标识（可能是用户id，也可能是SessionID）
    private String reportFileName; //模板文件文件全名(包括目录和文件名)
    private String reportId; //模板对象的Id，此Id也是数据库表中report(报告)的id，存储在sa_file_category.type2中
    private String tasksId; //任务组的Id，此Id也是taskd的id???还要设计

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public int getOwnerType() {
        return ownerType;
    }
    public void setOwnerType(int ownerType) {
        this.ownerType = ownerType;
    }
    public String getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
    public String getReportFileName() {
        return reportFileName;
    }
    public void setReportFileName(String reportFileName) {
        this.reportFileName = reportFileName;
    }

    /**
     * 转换为模型化文件信息，注意，这里要验证服务器端文件是否存在。<br/>
     * @return 模型化文件信息
     */
    public FileInfo convert2FileInfo() {
        File f = new File(this.reportFileName);
        if (f==null||!f.isFile()) throw new Flmg0001CException(new IllegalArgumentException("分析结果文件对象中fileName所指向的文件为空或是一个目录！"));

        FileInfo ret = new FileInfo();
        //主信息
        ret.setFile(f);
        ret.setId(this.id);
        ret.setOwnerId(this.getOwnerId());
        ret.setOwnerType(this.getOwnerType());
        ret.setAccessType(1);
//        ret.setDesc("分析报告文件:"+FileNameUtils.getFileName(this.templetFileName)+"；生成报告模板时间:"+DateUtils.convert2TimeChineseStr(new Date(FileUtils.getFileCreateTime4Win(f)))
//                +"；报告类型："+this.analType+"::"+this.subType+"，jsonD代码："+this.jsonDCode);
//
//        //分类信息
//        FileCategory fc = new FileCategory();
//        fc.setFType1(FileCategoryType1.REPORT);
//        fc.setFType2(this.analType);
//        fc.setFType3(this.subType);

        return null;
    }
}