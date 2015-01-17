package com.spiritdata.dataanal.templet.model;

import com.spiritdata.jsonD.ConvertJson;
import com.spiritdata.jsonD.model.AccessJsondOne;

/**
 * 某一jsonD数据的访问对象
 * @author wh
 */
public class OneJsond extends AccessJsondOne implements ConvertJson {
    private static final long serialVersionUID = 336480238247137657L;

    private int tdid; //此Id是在templet中进行标识用的

    public int getTdid() {
        return tdid;
    }

    protected void setTdid(int did) {
        this.tdid = did;
    }

    public String toJson() {
        String ret = "{";
        ret += "\"_id\":"+this.tdid+",\"_url\":\""+(this.getUrl()==null?"":this.getUrl())+"\","
             + "\"_jsonDcode\":\""+(this.getJsondCode()==null?"":this.getJsondCode())+"\","
             + "\"_jsonDid\":\""+(this.getUrl()==null?"":this.getUrl())+"\"";
        return ret+"}";
    }
}