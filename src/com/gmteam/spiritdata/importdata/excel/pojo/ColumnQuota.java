package com.gmteam.spiritdata.importdata.excel.pojo;

import java.util.Date;

public class ColumnQuota {
    private String id;

    private String cId;

    private String tqId;

    private String max;

    private String min;

    private Integer nullCount;

    private Integer distinctCount;

    private Date cTime;

    private Date lmTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getTqId() {
        return tqId;
    }

    public void setTqId(String tqId) {
        this.tqId = tqId;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public Integer getNullCount() {
        return nullCount;
    }

    public void setNullCount(Integer nullCount) {
        this.nullCount = nullCount;
    }

    public Integer getDistinctCount() {
        return distinctCount;
    }

    public void setDistinctCount(Integer distinctCount) {
        this.distinctCount = distinctCount;
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
    
}