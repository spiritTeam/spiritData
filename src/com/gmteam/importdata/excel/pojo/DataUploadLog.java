package com.gmteam.importdata.excel.pojo;

import java.util.Date;

import com.gmteam.framework.core.model.BaseObject;
/** 
 * @author mht
 * @version  
 * 类说明 
 */
@SuppressWarnings("serial")
public class DataUploadLog extends BaseObject{
    private String id;
    private String sourceFileName;
    private Date uploadDate;
    private Integer sheetSize;
    private String uploadUser;
    private String descn;
    public int getSheetSize() {
        return sheetSize;
    }
    public void setSheetSize(int sheetSize) {
        this.sheetSize = sheetSize;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getSourceFileName() {
        return sourceFileName;
    }
    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }
    public Date getUploadDate() {
        return uploadDate;
    }
    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }
    public String getUploadUser() {
        return uploadUser;
    }
    public void setUploadUser(String uploadUser) {
        this.uploadUser = uploadUser;
    }
    public String getDescn() {
        return descn;
    }
    public void setDescn(String descn) {
        this.descn = descn;
    }
}