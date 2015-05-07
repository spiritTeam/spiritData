package com.spiritdata.dataanal.task.core.enumeration;

import com.spiritdata.dataanal.exceptionC.Dtal0402CException;

/**
 * 任务执行状态分类，目前只支持6个状态：<br/>
 * <pre>
 * PREPARE(1)—准备，任务和任务组；
 * PROCESSING(2)—正在执行，任务和任务组；
 * SUCCESS(3)—执行成功，任务和任务组；
 * FAILD(4)—执行失败，任务和任务组；
 * ABATE(5)—失效，任务和任务组；
 * WAITING(6)—等待执行，仅任务；
 * </pre>
 * 为与数据库配合，其对应的值是int
 * @author wh
 */
public enum StatusType {
    PREPARE(1), PROCESSING(2), SUCCESS(3), FAILD(4), ABATE(5), WAITING(6);

    private int value;
    public int getValue() {
        return this.value;
    }

    private StatusType(int v) {
        this.value=v;
    }
    public static StatusType getStatusType(int value) {
        if (value==1) return StatusType.PREPARE;
        if (value==2) return StatusType.PROCESSING;
        if (value==3) return StatusType.SUCCESS;
        if (value==4) return StatusType.FAILD;
        if (value==5) return StatusType.ABATE;
        if (value==6) return StatusType.WAITING;
        throw new Dtal0402CException("不能识别的执行状态分类:"+value+"！");
    }
}