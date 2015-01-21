package com.spiritdata.dataanal.templet.model;

/**
 * D标签的显示类型，目前有：<br/>
 * IMP:导入文件；<br/>
 * LOG:日志文件；<br/>
 * ANAL:分析结果文件；<br/>
 * TEMPLET:模板文件；<br/>
 * @author wh
 */
public enum DtagShowType {
    IMP("IMP"), LOG("LOG"), ANAL("ANAL"), TEMPLET("TEMPLET");

    private String value;
    public String getValue() {
        return this.value;
    }

    private DtagShowType(String v) {
        this.value =v;
    }
}