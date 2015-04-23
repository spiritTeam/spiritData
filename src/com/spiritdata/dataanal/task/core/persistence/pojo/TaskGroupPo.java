package com.spiritdata.dataanal.task.core.persistence.pojo;

import java.sql.Timestamp;

import com.spiritdata.framework.core.model.BaseObject;

/**
 * 任务组表持久化对象<br/>
 * 对应持久化中数据库的表为SA_TASK_GROUP
 * @author wh
 */
public class TaskGroupPo extends BaseObject {
    private static final long serialVersionUID = -7368594337440094584L;

    private String id; //任务组id
    private String reportId; //所对应的报告Id,可为空,可以不对应任何报告
    private int ownerType; //任务组所对应的所有者类型（1=注册用户;2=非注册用户(session);3=系统生成）
    private String ownerId; //所有者标识（可能是用户id，也可能是SessionID，也可能是'Sys'）
    private String workName; //任务组工作名称
    private int status; //任务组状态：1=准备执行；2=正在执行；3=任务失效；4=执行成功；5=执行失败；

    public TaskGroupPo() {
        this.status = 1;
    }

    private String desc; //任务组说明
    private Timestamp beginTime; //任务组启动时间

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getReportId() {
        return reportId;
    }
    public void setReportId(String reportId) {
        this.reportId = reportId;
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
}