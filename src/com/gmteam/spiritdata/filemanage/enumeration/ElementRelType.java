package com.gmteam.spiritdata.filemanage.enumeration;

/**
 * 文件关系中元素的类型，原生文件和文件关系
 * 为与数据库配合，其对应的值是int
 * @author wh
 */
public enum ElementRelType {
    FILE_INFO(1), FILE_CLASS(2);

    private int value;
    public int getValue() {
        return this.value;
    }

    private ElementRelType(int v) {
        this.value = v;
    }
}