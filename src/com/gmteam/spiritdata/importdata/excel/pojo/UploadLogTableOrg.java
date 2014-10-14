package com.gmteam.spiritdata.importdata.excel.pojo;

import java.util.Date;

public class UploadLogTableOrg {
    private String id;

    private String ufid;

    private String tmoid;

    private String tmid;

    private String sheetname;

    private Integer sheetindex;

    private Date ctime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUfid() {
        return ufid;
    }

    public void setUfid(String ufid) {
        this.ufid = ufid;
    }

    public String getTmoid() {
        return tmoid;
    }

    public void setTmoid(String tmoid) {
        this.tmoid = tmoid;
    }

    public String getTmid() {
        return tmid;
    }

    public void setTmid(String tmid) {
        this.tmid = tmid;
    }

    public String getSheetname() {
        return sheetname;
    }

    public void setSheetname(String sheetname) {
        this.sheetname = sheetname;
    }

    public Integer getSheetindex() {
        return sheetindex;
    }

    public void setSheetindex(Integer sheetindex) {
        this.sheetindex = sheetindex;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }
}