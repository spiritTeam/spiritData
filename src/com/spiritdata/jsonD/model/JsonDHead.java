package com.spiritdata.jsonD.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.jsonD.Convert2Json;
import com.spiritdata.jsonD.exceptionC.JsonD0002CException;
import com.spiritdata.jsonD.exceptionC.JsonD0003CException;
import com.spiritdata.jsonD.util.JsonDUtils;

/**
 * jsonD格式的数据头信息。
 * 参看相关文档
 * @author wh
 */
public class JsonDHead implements Serializable, Convert2Json {
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
        if (StringUtils.isNullOrEmptyOrSpace(id)) throw new JsonD0002CException("id必须设置！", new IllegalArgumentException("id不能为null或空串！"));
        this.id = id;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        if (StringUtils.isNullOrEmptyOrSpace(code)) throw new JsonD0002CException("code必须设置！", new IllegalArgumentException("code不能为null或空串！"));
        if (!JsonDUtils.isLegalCode(code)) throw new JsonD0003CException("code["+code+"]不合规！", new IllegalArgumentException("code不合规，请参看JsonD相关文档！"));
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
        if (StringUtils.isNullOrEmptyOrSpace(this.id)) throw new JsonD0002CException("id必须设置！");
        if (StringUtils.isNullOrEmptyOrSpace(this.code)) throw new JsonD0002CException("code必须设置！");
        if (!JsonDUtils.isLegalCode(this.code)) throw new JsonD0003CException("code["+code+"]不合规，请参看JsonD相关文档！");

        if (this.CTime==null) CTime= new Date();
        String ret = "\"_HEAD\":{\"_id\":\""+this.id+"\", \"_code\":\""+this.code+"\", \"_cTime\":\""+this.CTime.getTimezoneOffset()+"::"+this.CTime.getTime()+"\"";
        if (!StringUtils.isNullOrEmptyOrSpace(this.parseFun)) ret +=", \"_parseFun\":\""+this.parseFun+"\"";
        if (!StringUtils.isNullOrEmptyOrSpace(this.node)) ret +=", _node:\""+this.node+"\"";
        if (!StringUtils.isNullOrEmptyOrSpace(this.fileName)) ret +=", \"_fileName\":\""+this.fileName+"\"";
        if (!StringUtils.isNullOrEmptyOrSpace(this.desc)) ret +=", \"_desc\":\""+this.desc+"\"";
        return ret+"}";
    }

    /**
     * 把头信息转换为Map对象，以便和其他结构相组合
     * @return Map
     */
    public Map<String, Object> toMap() {
        if (StringUtils.isNullOrEmptyOrSpace(this.id)) throw new JsonD0002CException("id必须设置！");
        if (StringUtils.isNullOrEmptyOrSpace(this.code)) throw new JsonD0002CException("code必须设置！");
        if (!JsonDUtils.isLegalCode(this.code)) throw new JsonD0003CException("code["+code+"]不合规，请参看JsonD相关文档！");

        if (this.CTime==null) CTime= new Date();
        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("_id", this.id);
        retMap.put("_code", this.code);
        retMap.put("_cTime", this.CTime.getTimezoneOffset()+"::"+this.CTime.getTime());
        if (!StringUtils.isNullOrEmptyOrSpace(this.parseFun)) retMap.put("_parseFun", this.parseFun);
        if (!StringUtils.isNullOrEmptyOrSpace(this.node)) retMap.put("_node", this.node);
        if (!StringUtils.isNullOrEmptyOrSpace(this.fileName)) retMap.put("_fileName", this.fileName);
        if (!StringUtils.isNullOrEmptyOrSpace(this.desc)) retMap.put("_desc", this.desc);
        Map<String, Object> _retMap = new HashMap<String, Object>();
        _retMap.put("_HEAD", retMap);
        return _retMap;
    }
}