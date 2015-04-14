package com.spiritdata.filemanage.core.enumeration;

import com.spiritdata.filemanage.exceptionC.Flmg0201CException;

/**
 * 文件分类中的大分类，目前只支持IMP,LOG,ANAL,REPORT。<br/>
 * IMP:导入文件；<br/>
 * LOG:日志文件；<br/>
 * ANAL:分析结果文件；都是jsonD文件<br/>
 * REPORT:报告文件；<br/>
 * 为与数据库配合，其对应的值是String
 * @author wh
 */
public enum FileCategoryType1 {
    IMP("IMP"), LOG("LOG"), ANAL("ANAL"), REPORT("REPORT");

    private String value;

    public String getValue() {
        return this.value;
    }

    private FileCategoryType1(String v) {
        this.value =v;
    }

    public static FileCategoryType1 getFileCategoryType1(String value) {
        if (value.toUpperCase().equals("IMP")) return FileCategoryType1.IMP;
        if (value.toUpperCase().equals("LOG")) return FileCategoryType1.LOG;
        if (value.toUpperCase().equals("ANAL")) return FileCategoryType1.ANAL;
        if (value.toUpperCase().equals("REPORT")) return FileCategoryType1.REPORT;
        throw new Flmg0201CException("不能识别的文件大分类:"+value+"！");
    }
}