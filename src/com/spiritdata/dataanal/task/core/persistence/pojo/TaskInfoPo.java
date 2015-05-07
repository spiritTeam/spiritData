package com.spiritdata.dataanal.task.core.persistence.pojo;

import java.sql.Timestamp;

import com.spiritdata.framework.core.model.BaseObject;

/**
 * 任务信息表持久化对象<br/>
 * 对应持久化中数据库的表为SA_TASK_INFO
 * @author wh
 */
public class TaskInfoPo extends BaseObject {
    private static final long serialVersionUID = -1573157497712228072L;

    private String id; //任务id
    private String taskGId; //任务组id
    private String rfId; //结果jsonD文件Id
    private String taskName; //任务名称
    private String langType; //执行语言，默认为java
    private String excuteFunc; //任务执行方法
    private String param; //任务执行所需的参数，是Json串
    private int status; //任务状态：1=准备执行；2=正在执行；3=执行成功；4=执行失败；5=任务失效；6=等待执行
    private int excuteCount; //任务执行次数
    private String desc; //任务说明

    private Timestamp firstTime; //任务第一次准备执行时间
    private Timestamp beginTime; //本次开始执行时间
    private Timestamp endTime; //本次结束执行时间

    public TaskInfoPo() {
        this.status = 1;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTaskGId() {
        return taskGId;
    }
    public void setTaskGId(String taskGId) {
        this.taskGId = taskGId;
    }
    public String getRfId() {
        return rfId;
    }
    public void setRfId(String rfId) {
        this.rfId = rfId;
    }
    public String getTaskName() {
        return taskName;
    }
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    public String getLangType() {
        return langType;
    }
    public void setLangType(String langType) {
        this.langType = langType;
    }
    public String getExcuteFunc() {
        return excuteFunc;
    }
    public void setExcuteFunc(String excuteFunc) {
        this.excuteFunc = excuteFunc;
    }
    public String getParam() {
        return param;
    }
    public void setParam(String param) {
        this.param = param;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public int getExcuteCount() {
        return excuteCount;
    }
    public void setExcuteCount(int excuteCount) {
        this.excuteCount = excuteCount;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public Timestamp getFirstTime() {
        return firstTime;
    }
    public void setFirstTime(Timestamp firstTime) {
        this.firstTime = firstTime;
    }
    public Timestamp getBeginTime() {
        return beginTime;
    }
    public void setBeginTime(Timestamp beginTime) {
        this.beginTime = beginTime;
    }
    public Timestamp getEndTime() {
        return endTime;
    }
    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }
}