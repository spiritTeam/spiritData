package com.spiritdata.filemanage.core.enumeration;

/**
 * 文件分类中的大分类，目前只支持IMP,LOG,ANAL,REPORT。<br/>
 * IMP:导入文件；<br/>
 * LOG:日志文件；<br/>
 * ANAL:分析结果文件；<br/>
 * REPORT:模板文件；<br/>
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
}