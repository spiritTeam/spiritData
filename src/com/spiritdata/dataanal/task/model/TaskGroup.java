package com.spiritdata.dataanal.task.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.spiritdata.dataanal.task.persistence.pojo.TaskGroupPo;
import com.spiritdata.framework.util.SequenceUUID;

/**
 * 任务组模型，包括任务组的信息，任务组中的任务(是一个有向图)
 * @author wh
 */
public class TaskGroup implements Serializable {
    private static final long serialVersionUID = 6627157875372740607L;

    private String id; //任务组id
    private int ownerType; //任务组所对应的所有者类型（1=注册用户;2=非注册用户(session);3=系统生成）
    private String ownerId; //所有者标识（可能是用户id，也可能是SessionID，也可能是'Sys'）
    private String workName; //任务组工作名称
    private int status; //任务组状态
    private String desc; //任务组说明
    private Timestamp beginTime; //任务开始启动时间

    private TaskGraph tasks; //子任务图

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public int getOwnerType() {
        return ownerType;
    }
    public void setOwnerType(int ownerType) {
        this.ownerType = ownerType;
    }
    public String getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
    public String getWorkName() {
        return workName;
    }
    public void setWorkName(String workName) {
        this.workName = workName;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public Timestamp getBeginTime() {
        return beginTime;
    }
    public void setBeginTime(Timestamp beginTime) {
        this.beginTime = beginTime;
    }

    public TaskGraph getTasks() {
        return tasks;
    }

    //任务组状态设置
    /**
     * 设置为准备状态
     */
    public void setPrepared() {
        this.status=1;
    }
    /**
     * 设置为正在执行
     */
    public void setProcessing() {
        this.status=2;
    }
    /**
     * 设置为失效
     */
    public void setAbatement() {
        this.status=3;
    }
    /**
     * 设置为执行成功
     */
    public void setSuccessed() {
        this.status=4;
    }
    /**
     * 设置为执行失败：其子任务图没有完全执行成功
     */
    public void setFailed() {
        this.status=5;
    }

    /**
     * 新增一个子任务到任务图
     * @param task
     */
    public void addTask2Graph(TaskInfo task) {
        if (this.tasks==null) this.tasks = new TaskGraph();
        this.tasks.addTaskInfo(task);
        task.setTaskGroup(this);
    }

    /**
     * 当前对象转换为Po对象，为数据库操作做准备
     * @return 任务组信息
     */
    public TaskGroupPo convert2Po() {
        TaskGroupPo ret = new TaskGroupPo();
        if (this.getId()==null||this.getId().trim().equals("")) {
            ret.setId(SequenceUUID.getPureUUID());
        } else {
            ret.setId(this.getId());
        }
        ret.setOwnerType(this.ownerType);
        ret.setOwnerId(this.ownerId);
        ret.setWorkName(this.workName);
        ret.setStatus(this.status);
        ret.setDesc(this.desc);
        return ret;
    }
}
