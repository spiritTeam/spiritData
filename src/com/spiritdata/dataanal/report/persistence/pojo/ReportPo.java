package com.spiritdata.dataanal.report.persistence.pojo;

import java.sql.Timestamp;

import com.spiritdata.framework.core.model.BaseObject;

/**
 * 报告信息表持久化对象<br/>
 * 对应持久化中数据库的表为SA_REPORT_INFO
 * @author wh
 */
public class ReportPo extends BaseObject {
    private static final long serialVersionUID = 579425924209963010L;

    private String id; //报告id
    private int ownerType; //报告所对应的所有者类型（1=注册用户;2=非注册用户(session);3=系统生成）
    private String ownerId; //所有者标识（可能是用户id，也可能是SessionID，也可能是'Sys'）
    private String taskGId; //报告对应的任务组Id
    private String FId; //对应文件id，文件信息，报告对应的文件report.json
    private String reportName; //报告id
    private String desc; //报告说明
    private Timestamp CTime; //记录创建时间

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
    public String getTaskGId() {
        return taskGId;
    }
    public void setTaskGId(String taskGId) {
        this.taskGId = taskGId;
    }
    public String getFId() {
        return FId;
    }
    public void setFId(String fId) {
        FId = fId;
    }
    public String getReportName() {
        return reportName;
    }
    public void setReportName(String reportName) {
        this.reportName = reportName;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public Timestamp getCTime() {
        return CTime;
    }
    public void setCTime(Timestamp cTime) {
        CTime = cTime;
    }
}