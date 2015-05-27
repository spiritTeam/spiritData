package com.spiritdata.dataanal.visitmanage.core.enumeration;

import com.spiritdata.dataanal.exceptionC.Dtal1202CException;

public enum ObjType {
    REPORT(1);

    private int value;
    public int getValue() {
        return this.value;
    }
    private ObjType(int v) {
        this.value=v;
    }

    public static ObjType getObjType(int value) {
        if (value==1) return ObjType.REPORT;
        throw new Dtal1202CException("不能识别的访问对象类别:"+value+"！");
    }

    public String getName() {
        if (this==REPORT) return "report";
        return "未知类型";
    }
}