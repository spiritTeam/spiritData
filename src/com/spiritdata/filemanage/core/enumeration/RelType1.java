package com.spiritdata.filemanage.core.enumeration;

/**
 * 文件关系分类1，正向关系，对等关系，反响关系
 * 为与数据库配合，其对应的值是int
 * @author wh
 */
public enum RelType1 {
    POSITIVE(1), INVERSE(-1), EQUAL(0);

    private int value;
    public int getValue() {
        return this.value;
    }

    private RelType1(int v) {
        this.value=v;
    }

    public RelType1 getContrary() {
        if (this==POSITIVE) return INVERSE;
        if (this==INVERSE) return POSITIVE;
        return EQUAL;
    }

    public static RelType1 getRelType1(int value) {
        if (value==1) return POSITIVE;
        if (value==-1) return INVERSE;
        if (value==0) return EQUAL;
        return null;
    }
}