package com.spiritdata.dataanal.report.model;

import java.io.Serializable;
import java.util.Map;

import com.spiritdata.dataanal.exceptionC.Dtal1005CException;
import com.spiritdata.dataanal.report.enumeration.DtagShowType;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.jsonD.util.JsonUtils;

/**
 * 出现在Content中的D标签，主要为转换为d标签的html字符串。
 * <pre>
 * 注意：
 * 1、D标签中的内容，若遇到括号不够用的情况，用^代替双引号，用~代替单引号；
 * 2、目前没有对param、value的过滤函数、decorateView的扩展进行合法性检查
 * <pre>
 * @author wh
 */
public class D_Tag implements Serializable{
    private static final long serialVersionUID = -7400921505424634876L;

    private String did; //对应的data编码，report中_DLIST里的jsonD标签的顺序号
    private DtagShowType showType; //显示类型
    private Map<String, String> param; //showType附属说明，在标签串中以json格式存在
    private String value; //数据，获取_DATA中的那个对象
    private String valueFilterFun; //数据过滤函数
    private String label; //只在ds中有效
    private String decorateView; //显示修饰语法
    private Map<String, String> dvExt; //显示修饰的扩展部分，在标签中串中以json格式存在
    private Map<String, String> htmlExt; //html的扩展属性，是一个json串

    public String getDid() {
        return did;
    }
    public void setDid(String did) {
        this.did = did;
    }
    public DtagShowType getShowType() {
        return showType;
    }
    public void setShowType(DtagShowType showType) {
        this.showType = showType;
    }
    public Map<String, String> getParam() {
        return param;
    }
    public void setParam(Map<String, String> param) {
        this.param = param;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getValueFilterFun() {
        return valueFilterFun;
    }
    public void setValueFilterFun(String valueFilterFun) {
        this.valueFilterFun = valueFilterFun;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public String getDecorateView() {
        return decorateView;
    }
    public void setDecorateView(String decorateView) {
        this.decorateView = decorateView;
    }
    public Map<String, String> getDvExt() {
        return dvExt;
    }
    public void setDvExt(Map<String, String> dvExt) {
        this.dvExt = dvExt;
    }
    public Map<String, String> getHtmlExt() {
        return htmlExt;
    }
    public void setHtmlExt(Map<String, String> htmlExt) {
        this.htmlExt = htmlExt;
    }

    /**
     * 把D标签转换为html标签<d>
     * @return
     */
    public String toHtmlTag() {
        if (StringUtils.isNullOrEmptyOrSpace(this.did)) throw new Dtal1005CException("D标签不规范：did属性必须设置且不能为空！");
        if (this.showType==null) throw new Dtal1005CException("D标签不规范：showType属性必须设置！");
        if (StringUtils.isNullOrEmptyOrSpace(this.value)) throw new Dtal1005CException("D标签不规范：value属性必须设置且不能为空！");

        String temp = "";
        String ret = "<d"; //标签开始
        //1-did
        ret += " did='"+this.did+"'";
        //2-showType
        ret += " showType='"+this.showType.getValue()+"'";
        //3-param
        if (this.param!=null&&this.param.size()>0) {
            temp = JsonUtils.objToJson(this.param);
            ret += " param='"+replaceQuotation(temp)+"'";
        }
        //4-value
        ret += " value='"+this.value;
        if (!StringUtils.isNullOrEmptyOrSpace(this.valueFilterFun)) ret += "::"+this.valueFilterFun;
        ret += "'";
        //5-label
        if (!StringUtils.isNullOrEmptyOrSpace(this.label)) ret += " label='"+this.label+"'";
        //6-decorateView
        if (!StringUtils.isNullOrEmptyOrSpace(this.decorateView)) {
            ret += " decorateView='"+replaceQuotation(this.decorateView);
            if (this.dvExt!=null&&this.dvExt.size()>0) {
                temp = JsonUtils.objToJson(this.dvExt);
                ret += "::"+replaceQuotation(temp);
            }
            ret += "'";
        }
        //7-htmlExt
        if (this.htmlExt!=null&&this.htmlExt.size()>0) {
            temp = JsonUtils.objToJson(this.htmlExt);
            ret += " htmlExt='"+this.replaceQuotation(temp)+"'";
        }
        return ret+"/>"; //标签结束
    }

    private String replaceQuotation(String sourceStr) {
        sourceStr = sourceStr.replaceAll("'", "~");
        sourceStr = sourceStr.replaceAll("\"", "^");
        return sourceStr;
    }
}