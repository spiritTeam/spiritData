package com.spiritdata.dataanal.report.model;

import java.io.Serializable;

import com.spiritdata.dataanal.exceptionC.Dtal1002CException;
import com.spiritdata.dataanal.report.enumeration.DtagShowType;
import com.spiritdata.framework.util.StringUtils;

/**
 * 出现在Content中的D标签，主要为转换为d标签的html字符串<br/>
 * 此类由于标签设计的问题，可能今后还需要修改
 * @author wh
 */
public class D_Tag implements Serializable{
    private static final long serialVersionUID = -7400921505424634876L;

    private String did; //对应的data编码
    private DtagShowType showType; //显示类型
    private String funcStr; //类型函数，目前只对first相关
    private String value; //数据，获取_DATA中的那个对象、
    private String lable; //X轴或分类
    private String data; //Y轴或数值
    private String decorateView; //修饰语法

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
    public String getFuncStr() {
        return funcStr;
    }
    public void setFuncStr(String funcStr) {
        this.funcStr = funcStr;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getLable() {
        return lable;
    }
    public void setLable(String lable) {
        this.lable = lable;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public String getDecorateView() {
        return decorateView;
    }
    public void setDecorateView(String decorateView) {
        this.decorateView = decorateView;
    }

    /**
     * 把D标签转换为html标签<d>
     * @return
     */
    public String toHtmlTag() {
        if (StringUtils.isNullOrEmptyOrSpace(this.did)) throw new Dtal1002CException("D标签不规范：did属性必须设置且不能为空！");
        if (this.showType==null) throw new Dtal1002CException("D标签不规范：showType属性必须设置！");
        if (StringUtils.isNullOrEmptyOrSpace(this.value)) throw new Dtal1002CException("D标签不规范：value属性必须设置且不能为空！");

        String ret = "<d ";
        ret += "did='"+this.did+"'";
        if (this.showType==DtagShowType.TEXT) {
            if (StringUtils.isNullOrEmptyOrSpace(this.funcStr)) throw new Dtal1002CException("D标签不规范：若设置showType=first，funcStr属性必须设置且不能为空！");
            ret += " showType='"+this.funcStr+"'";
        } else {
            ret += " showType='"+this.showType.getValue()+"'";
        }
        ret += " value='"+this.value+"'";
        
        if (this.showType==DtagShowType.PIE||this.showType==DtagShowType.LINE||this.showType==DtagShowType.BAR||this.showType==DtagShowType.RADAR) {
            if (StringUtils.isNullOrEmptyOrSpace(this.lable)) throw new Dtal1002CException("D标签不规范：若设置showType="+this.showType.toString()+"，label属性必须设置且不能为空！");
            if (StringUtils.isNullOrEmptyOrSpace(this.data)) throw new Dtal1002CException("D标签不规范：若设置showType="+this.showType.toString()+"，data属性必须设置且不能为空！");
        }

        if (!StringUtils.isNullOrEmptyOrSpace(this.decorateView)) ret += " decorateView='"+this.decorateView+"'";
        
        return ret+"/>";
    }
}