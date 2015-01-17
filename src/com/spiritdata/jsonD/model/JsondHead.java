package com.spiritdata.jsonD.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.spiritdata.jsonD.exceptionC.Jsond0002CException;
import com.spiritdata.jsonD.exceptionC.Jsond0003CException;
import com.spiritdata.jsonD.util.JsonDUtils;

/**
 * jsonD格式的数据头信息。
 * 参看相关文档
 * @author wh
 */
public class JsondHead implements Serializable {
    private static final long serialVersionUID = 8454602778917800099L;

    private String id; //UUID
    private String code; //数据的编码，此编码分为组织编码：大写的以.隔开的名称，<::>后是小类型编号，采用5位，最大为9999个类型(可扩充)
    private String parseFun; //这个属性现在不用
    private String node; //这个属性现在不用
    private String fileName; //存储JsonD的文件地址，可能需要和_node共同使用
    private String desc; //描述
    private Date CTime; //创建时间

    public String getId() {
        return id;
    }
    public void setId(String id) {
        if (id==null||id.length()==0) throw new Jsond0002CException("id必须设置！", new IllegalArgumentException("id不能为null或空串！"));
        this.id = id;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        if (code==null||code.length()==0) throw new Jsond0002CException("code必须设置！", new IllegalArgumentException("code不能为null或空串！"));
        if (!JsonDUtils.isLegalCode(code)) throw new Jsond0003CException("code["+code+"]不合规！", new IllegalArgumentException("code不合规，请参看JsonD相关文档！"));
        this.code = code;
    }
    public String getParseFun() {
        return parseFun;
    }
    public void setParseFun(String parseFun) {
        this.parseFun = parseFun;
    }
    public String getNode() {
        return node;
    }
    public void setNode(String node) {
        this.node = node;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public Date getCTime() {
        return CTime;
    }
    public void setCTime(Date cTime) {
        CTime = cTime;
    }

    /**
     * 把头信息转换为Json串
     * @return json串
     */
    public String toJson() {
        if (this.id==null||this.id.length()==0) throw new Jsond0002CException("_id必须设置！");
        if (this.code==null||this.code.length()==0) throw new Jsond0002CException("_code必须设置！");
        if (!JsonDUtils.isLegalCode(this.code)) throw new Jsond0003CException("code["+code+"]不合规，请参看JsonD相关文档！");

        if (this.CTime==null) CTime= new Date();
        String ret = "\"_HEAD\":{\"_id\":\""+this.id+"\", \"_code\":\""+this.code+"\", \"_cTime\":\""+this.CTime.getTimezoneOffset()+"::"+this.CTime.getTime()+"\"";
        if (this.parseFun!=null&&this.parseFun.length()>0) ret +=", \"_parseFun\":\""+this.parseFun+"\"";
        if (this.node!=null&&this.node.length()>0) ret +=", _node:\""+this.node+"\"";
        if (this.fileName!=null&&this.fileName.length()>0) ret +=", \"_fileName\":\""+this.fileName+"\"";
        if (this.desc!=null&&this.desc.length()>0) ret +=", \"_desc\":\""+this.desc+"\"";
        return ret+"}";
    }

    /**
     * 把头信息转换为Map对象，以便和其他结构相组合
     * @return Map
     */
    public Map<String, Object> toMap() {
        if (this.id==null||this.id.length()==0) throw new Jsond0002CException("_id必须设置！");
        if (this.code==null||this.code.length()==0) throw new Jsond0002CException("_code必须设置！");
        if (!JsonDUtils.isLegalCode(this.code)) throw new Jsond0003CException("code["+code+"]不合规，请参看JsonD相关文档！");

        if (this.CTime==null) CTime= new Date();
        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("_id", this.id);
        retMap.put("_code", this.code);
        retMap.put("_cTime", this.CTime.getTimezoneOffset()+"::"+this.CTime.getTime());
        if (this.parseFun!=null&&this.parseFun.length()>0) retMap.put("_parseFun", this.parseFun);
        if (this.node!=null&&this.node.length()>0) retMap.put("_node", this.node);
        if (this.fileName!=null&&this.fileName.length()>0) retMap.put("_fileName", this.fileName);
        if (this.desc!=null&&this.desc.length()>0) retMap.put("_desc", this.desc);
        Map<String, Object> _retMap = new HashMap<String, Object>();
        _retMap.put("_HEAD", retMap);
        return _retMap;
    }
}