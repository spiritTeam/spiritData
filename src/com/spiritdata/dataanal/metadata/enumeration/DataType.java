package com.spiritdata.dataanal.metadata.enumeration;

import com.spiritdata.dataanal.exceptionC.Dtal0002CException;

public enum DataType {
    STRING("String"),
    INTEGER("Integer"), LONG("Long"), DOUBLE("Double"),
    DATE("Date");

    private String value;
    public String getValue() {
        return this.value;
    }

    private DataType(String v) {
        this.value =v;
    }

    public static DataType getDataType(String value) {
        if (value.toUpperCase().equals("STRING")) return DataType.STRING;
        if (value.toUpperCase().equals("INTEGER")) return DataType.INTEGER;
        if (value.toUpperCase().equals("LONG")) return DataType.LONG;
        if (value.toUpperCase().equals("DOUBLE")) return DataType.DOUBLE;
        if (value.toUpperCase().equals("DATE")) return DataType.DATE;
        throw new Dtal0002CException("不能识别的数据类型:"+value+"！");
    }

    public String getName() {
        if (this==STRING) return "字符形(String)";
        if (this==INTEGER) return "整形(Integer)";
        if (this==LONG) return "长整形(Long)";
        if (this==DOUBLE) return "双精度(Double)";
        if (this==DATE) return "日期形(Date)";
        throw new Dtal0002CException("不能识别的数据类型:"+this.getValue()+"！");
    }
}