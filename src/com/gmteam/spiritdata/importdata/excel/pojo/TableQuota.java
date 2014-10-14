package com.gmteam.spiritdata.importdata.excel.pojo;

import java.util.Date;

public class TableQuota {
    private String id;

    private String tmoId;

    private String tmId;

    private String tableName;

    private Integer allCount;

    private Date cTime;

    private Date lmTime;

    private Date laTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getAllCount() {
        return allCount;
    }

    public void setAllCount(Integer allCount) {
        this.allCount = allCount;
    }

    public Date getcTime() {
        return cTime;
    }

    public void setcTime(Date cTime) {
        this.cTime = cTime;
    }

    public Date getLmTime() {
        return lmTime;
    }

    public void setLmTime(Date lmTime) {
        this.lmTime = lmTime;
    }

    public Date getLaTime() {
        return laTime;
    }

    public void setLaTime(Date laTime) {
        this.laTime = laTime;
    }

}