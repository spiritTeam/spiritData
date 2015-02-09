package com.spiritdata.dataanal.report.model;

import com.spiritdata.jsonD.ConvertJson;
import com.spiritdata.jsonD.model.AccessJsond;

/**
 * 某一jsonD数据的访问对象
 * @author wh
 */
public class OneJsond extends AccessJsond implements ConvertJson {
    private static final long serialVersionUID = 336480238247137657L;

    private int rdId; //此Id是在report中进行标识用的

    public int getRdId() {
        return rdId;
    }

    protected void setRdId(int rdId) {
        this.rdId = rdId;
    }

    public String toJson() {
        String ret = "{";
        ret += "\"_id\":"+this.rdId+",\"_url\":\""+(this.getUrl()==null?"":this.getUrl())+"\","
             + "\"_jsonDcode\":\""+(this.getJsondCode()==null?"":this.getJsondCode())+"\","
             + "\"_jsonDid\":\""+(this.getUrl()==null?"":this.getUrl())+"\"";
        return ret+"}";
    }
}