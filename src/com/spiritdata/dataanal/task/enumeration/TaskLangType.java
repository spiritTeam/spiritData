package com.spiritdata.dataanal.task.enumeration;

/**
 * 任务执行语言分类
 * @author wh
 */
public enum TaskLangType {
    JAVA("Java");

    private String value;
    public String getValue() {
        return this.value;
    }

    private TaskLangType(String v) {
        this.value =v;
    }
}