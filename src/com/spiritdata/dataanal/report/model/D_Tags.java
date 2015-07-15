package com.spiritdata.dataanal.report.model;

import java.util.ArrayList;
import java.util.List;

import com.spiritdata.dataanal.exceptionC.Dtal1005CException;
import com.spiritdata.dataanal.report.enumeration.DtagShowType;

/**
 * 出现在Content中的Ds标签，主要为转换为ds标签的html字符串。
 * <pre>
 * 注意：
 * 1、本标签用来实现在一个画布上同时显示多个图形，图形可以是同类型的，也可以是不同类型的；
 * 2、目前可组合的类型只有line，bar，以后可能还会支持多种图
 * <pre>
 * @author wh
 */

public class D_Tags {
    private List<D_Tag> D_TagList;

    /**
     * 获得D标签组中的所有D标签
     * @return D标签组中的所有D标签
     */
    public List<D_Tag> getD_TagList() {
        return D_TagList;
    }

    /**
     * 向D标签组插入一个D标签
     * @param oneTag 预插入的D标签
     */
    public void addOneDTag(D_Tag oneTag) {
        if (oneTag.getShowType()!=DtagShowType.BAR&&oneTag.getShowType()!=DtagShowType.LINE) throw new Dtal1005CException("标签组不能组合显示类型为["+oneTag.getShowType()+"]的信息");
        if (this.D_TagList==null) this.D_TagList=new ArrayList<D_Tag>();
        this.D_TagList.add(oneTag);
    }

    /**
     * 把D标签转换为html标签<d>
     * @return
     */
    public String toHtmlTag() {
        if (this.D_TagList==null||this.D_TagList.size()==0) return "";
        String ret = "<ds>"; //标签开始
        for (int i=0; i<this.D_TagList.size(); i++) {
            ret+=this.D_TagList.get(i).toHtmlTag();
        }
        return ret+"</ds>";
    }
}