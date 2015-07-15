package com.spiritdata.dataanal.report.enumeration;

import com.spiritdata.dataanal.exceptionC.Dtal1005CException;

/**
 * D标签的显示类型，目前有：<br/>
 * VALUE:值；<br/>
 * TEXT:列表中的排序；<br/>
 * TABLE:列表；<br/>
 * PIE:饼图；<br/>
 * BAR:柱图；<br/>
 * LINE:折线图；<br/>
 * RADAR:雷达图；<br/>
 * MAP_PTS:地图-点位图；<br/>
 * @author wh
 */
public enum DtagShowType {
    VALUE("value"), TEXT("text"), TABLE("table"), PIE("pie"),
    BAR("bar"), LINE("line"), RADAR("radar"), MAP_PTS("map_pts");

    private String value;
    public String getValue() {
        return this.value;
    }

    private DtagShowType(String v) {
        this.value =v;
    }

    public static DtagShowType getDtagShowType(String value) {
        if (value.toUpperCase().equals("VALUE")) return DtagShowType.VALUE;
        if (value.toUpperCase().equals("TEXT")) return DtagShowType.TEXT;
        if (value.toUpperCase().equals("TABLE")) return DtagShowType.TABLE;
        if (value.toUpperCase().equals("PIE")) return DtagShowType.PIE;
        if (value.toUpperCase().equals("BAR")) return DtagShowType.BAR;
        if (value.toUpperCase().equals("LINE")) return DtagShowType.LINE;
        if (value.toUpperCase().equals("RADAR")) return DtagShowType.RADAR;
        if (value.toUpperCase().equals("MAP_PTS")) return DtagShowType.MAP_PTS;
        throw new Dtal1005CException("不能识别的D标签类型:"+value+"！");    }
}