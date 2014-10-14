package com.gmteam.spiritdata.importdata.excel.pojo;

import java.util.Date;

public class ColSemanteme {
    private String id;

    private String cId;

    private String tmId;

    private Integer semantemeCode;

    private Integer semantemeType;

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

    public String getTmId() {
        return tmId;
    }

    public void setTmId(String tmId) {
        this.tmId = tmId;
    }

    public Integer getSemantemeCode() {
        return semantemeCode;
    }

    public void setSemantemeCode(Integer semantemeCode) {
        this.semantemeCode = semantemeCode;
    }

    public Integer getSemantemeType() {
        return semantemeType;
    }

    public void setSemantemeType(Integer semantemeType) {
        this.semantemeType = semantemeType;
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