package com.spiritdata.dataanal.templet.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.spiritdata.dataanal.exceptionC.Dtal1002CException;
import com.spiritdata.framework.core.model.BaseObject;
import com.spiritdata.jsonD.ConvertJson;
import com.spiritdata.jsonD.util.JsonDUtils;

/**
 * 模板头，可能需要扩充，目前只有reportName
 * @author wh
 */
public class TempletHead extends BaseObject implements ConvertJson {
    private static final long serialVersionUID = -2739194799723001355L;

    private String reportName;//报告名称
    private String code; //模板类型编码，此编码分为组织编码：大写的以.隔开的名称，<::>后是小类型编号，采用5位，最大为9999个类型(可扩充)
    private Date CTime; //创建时间 此事件也是整个templet生成的时间戳
    private String desc; //描述

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        if (code==null||code.length()==0) throw new Dtal1002CException("templetD头信息不规范：code必须设置！", new IllegalArgumentException("code不能为null或空串！"));
        if (!JsonDUtils.isLegalCode(code)) throw new Dtal1002CException("templetD头信息不规范：code["+code+"]不合规！", new IllegalArgumentException("code不合规，请参看Templet相关文档！"));
        this.code = code;
    }
    public String getReportName() {
        return reportName;
    }
    public void setReportName(String reportName) {
        if (reportName==null||reportName.length()==0) throw new Dtal1002CException("templetD头信息不规范：reportName必须设置！", new IllegalArgumentException("reportName不能为null或空串！"));
        this.reportName = reportName;
    }
    public Date getCTime() {
        return CTime;
    }
    public void setCTime(Date cTime) {
        CTime = cTime;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    
    /**
     * 把头信息转换为Json串
     * @return json串
     */
    public String toJson() {
        if (this.reportName==null||this.reportName.length()==0) throw new Dtal1002CException("templetD头信息不规范：报告名称(reportName)必须设置！");
        if (this.code==null||this.code.length()==0) throw new Dtal1002CException("templetD头信息不规范：模板类型编码(code)必须设置！");
        if (!JsonDUtils.isLegalCode(this.code)) throw new Dtal1002CException("templetD头信息不规范：code["+code+"]不合规！，请参看Templet相关文档！");

        if (this.CTime==null) CTime= new Date();
        String ret = "\"_HEAD\":{\"_reportName\":\""+this.reportName+"\", \"_code\":\""+this.code+"\", \"_cTime\":\""+this.CTime.getTimezoneOffset()+"::"+this.CTime.getTime()+"\"";
        if (this.desc!=null&&this.desc.length()>0) ret +=", \"_desc\":\""+this.desc+"\"";
        return ret+"}";
    }

    /**
     * 把头信息转换为Map对象，以便和其他结构相组合
     * @return Map
     */
    public Map<String, Object> toMap() {
        if (this.reportName==null||this.reportName.length()==0) throw new Dtal1002CException("templetD头信息不规范：报告名称(reportName)必须设置！");
        if (this.code==null||this.code.length()==0) throw new Dtal1002CException("templetD头信息不规范：模板类型编码(code)必须设置！");
        if (!JsonDUtils.isLegalCode(this.code)) throw new Dtal1002CException("templetD头信息不规范：code["+code+"]不合规！，请参看Templet相关文档！");
        
        if (this.CTime==null) CTime= new Date();
        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("_reportName", this.reportName);
        retMap.put("_code", this.code);
        retMap.put("_cTime", this.CTime.getTimezoneOffset()+"::"+this.CTime.getTime());
        if (this.desc!=null&&this.desc.length()>0) retMap.put("_desc", this.desc);
        Map<String, Object> _retMap = new HashMap<String, Object>();
        _retMap.put("_HEAD", retMap);
        return _retMap;
    }
}