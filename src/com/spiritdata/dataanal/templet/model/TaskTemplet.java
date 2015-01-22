package com.spiritdata.dataanal.templet.model;

import java.io.Serializable;

/**
 * 模板任务，所有后台生成的模板都应准备这个对象。
 * 在生成模板的同时，组织数据处理的任务。
 * 目前只使用模板部分的功能，任务部分的功能后续再实现。
 * @author wh
 */
public class TaskTemplet implements Serializable {
    private static final long serialVersionUID = 5088715498847593517L;

    private Templet templet;//任务模板中的模板数据
    private Object task;//task还需要设计

    public Templet getTemplet() {
        return templet;
    }
    public void setTemplet(Templet templet) {
        this.templet = templet;
    }
    public Object getTask() {
        return task;
    }
    public void setTask(Object task) {
        this.task = task;
    }
}