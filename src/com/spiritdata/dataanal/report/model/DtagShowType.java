package com.spiritdata.dataanal.report.model;

/**
 * D标签的显示类型，目前有：<br/>
 * VALUE:值；<br/>
 * FIRST:列表中的排序；<br/>
 * TABLE:列表；<br/>
 * PIE:饼图；<br/>
 * BAR:柱图；<br/>
 * LINE:折线图；<br/>
 * RADAR:雷达图；<br/>
 * @author wh
 */
public enum DtagShowType {
    VALUE("value"), FIRST("first"), TABLE("table"), PIE("pie"),
    BAR("bar"), LINE("line"), RADAR("radar");

    private String value;
    public String getValue() {
        return this.value;
    }

    private DtagShowType(String v) {
        this.value =v;
    }
}