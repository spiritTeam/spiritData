package com.spiritdata.dataanal.filemanage.core.enumeration;

/**
 * 文件分类中的大分类，目前只支持IMP,LOG,ANAL
 * 为与数据库配合，其对应的值是String
 * @author wh
 */
public enum FileCategoryType1 {
    IMP("IMP"), LOG("LOG"), ANAL("ANAL");

    private String value;
    public String getValue() {
        return this.value;
    }

    private FileCategoryType1(String v) {
        this.value =v;
    }
}