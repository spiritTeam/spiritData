package com.spiritdata.dataanal.task.core.enumeration;

import com.spiritdata.dataanal.exceptionC.Dtal0402CException;

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

    public static TaskLangType getTaskLangType(String value) {
        if (value.toUpperCase().equals("JAVA")) return TaskLangType.JAVA;
        throw new Dtal0402CException("不能识别的语言分类:"+value+"！");
    }
}