package com.spiritdata.dataanal.report.model;

import com.spiritdata.jsonD.Convert2Json;
import com.spiritdata.jsonD.model.AccessJsonD;

/**
 * 某一jsonD数据的访问对象
 * @author wh
 */
public class OneJsonD extends AccessJsonD implements Convert2Json {
    private static final long serialVersionUID = 336480238247137657L;

    private int rdId; //此Id是在report中进行标识用的，ReportDatalistId
    public int getRdId() {
        return rdId;
    }
    protected void setRdId(int rdId) {
        this.rdId = rdId;
    }

    /**
     * 简单构造函数
     */
    public OneJsonD() {
        super();
    }

    /**
     * 根据jsonD的访问对象构造在report中用到的jsonD对象
     */
    public OneJsonD(AccessJsonD aj) {
        this.setJsonDId(aj.getJsonDId());
        this.setUrl(aj.getUrl());
        this.setFilePath(aj.getFilePath());
        this.setJsonDCode(aj.getJsonDCode());
    }
    
    public String toJson() {
        return "{\"_id\":"+this.rdId+","
             + "\"_url\":\""+(this.getUrl()==null?"":this.getUrl())+"\","
             + "\"_jsonDCode\":\""+(this.getJsonDCode()==null?"":this.getJsonDCode())+"\"}";
    }
}