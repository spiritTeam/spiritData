package com.spiritdata.dataanal.templet.model;

import java.io.Serializable;

/**
 * 出现在Content中的D标签
 * @author wh
 */
public class D_Tag implements Serializable{
    private static final long serialVersionUID = -7400921505424634876L;

    private DtagShowType showType; //显示类型

    private String a;
    /**
     * 把D标签转换为html标签<d>
     * @return
     */
    public String toHtmlTag() {
        // TODO Auto-generated method stub
        return null;
    }

}