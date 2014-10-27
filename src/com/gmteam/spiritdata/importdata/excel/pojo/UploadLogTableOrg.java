package com.gmteam.spiritdata.importdata.excel.pojo;

import java.util.Date;

import com.gmteam.framework.core.model.BaseObject;

public class UploadLogTableOrg extends BaseObject{
    private static final long serialVersionUID = 1L;

    private String id;

    private String ufId;

    private String tmoId;

    private String tmId;

    private String sheetName;

    private Integer sheetIndex;

    private Date cTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUfId() {
        return ufId;
    }

    public void setUfId(String ufId) {
        this.ufId = ufId;
    }

    public String getTmoId() {
        return tmoId;
    }

    public void setTmoId(String tmoId) {
        this.tmoId = tmoId;
    }

    public String getTmId() {
        return tmId;
    }

    public void setTmId(String tmId) {
        this.tmId = tmId;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public Integer getSheetIndex() {
        return sheetIndex;
    }

    public void setSheetIndex(Integer sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    public Date getcTime() {
        return cTime;
    }

    public void setcTime(Date cTime) {
        this.cTime = cTime;
    }
    
}