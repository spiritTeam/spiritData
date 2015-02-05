package com.spiritdata.dataanal.task.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.spiritdata.dataanal.task.persistence.pojo.TaskInfoPo;
import com.spiritdata.dataanal.task.persistence.pojo.TaskRelPo;
import com.spiritdata.filemanage.core.model.FileInfo;
import com.spiritdata.framework.util.SequenceUUID;

/**
 * 任务信息对象，包括任务的基本信息，前置任务列表，任务执行结果列表
 * @author wh
 */
public class TaskInfo implements Serializable {
    private static final long serialVersionUID = 5771297762371717292L;

    private String id; //任务
    private String taskName; //任务名称
    private String langType; //执行语言，默认为java
    private String excuteFunc; //任务执行方法
    private String param; //任务执行所需的参数
    private int status; //任务状态：1=准备执行；2=正在执行；3=执行成功；4=执行失败；5=任务失效；6=等待执行
    private String desc; //任务说明
    private Timestamp firstTime; //任务第一次准备执行时间
    private Timestamp beginTime; //本次开始执行时间
    private Timestamp endTime; //本次结束执行时间

    private TaskGroup taskGroup; //所属任务组，可为空
    private FileInfo resultFile; //结果文件，可为空

    private List<PreTask> preTasks; //前序任务列表

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
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
    public TaskGroup getTaskGroup() {
        return taskGroup;
    }
    public void setTaskGroup(TaskGroup taskGroup) {
        this.taskGroup = taskGroup;
    }
    public FileInfo getResultFile() {
        return resultFile;
    }
    public void setResultFile(FileInfo resultFile) {
        this.resultFile = resultFile;
    }

    //任务组状态设置
    /**
     * 设置为准备状态
     */
    public void setPrepared() {
        this.status=1;
    }
    /**
     * 设置为等待状态
     */
    public void setWaiting() {
        this.status=6;
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
     * 设置为执行失败
     */
    public void setFailed() {
        this.status=5;
    }

    public void addPreTask(TaskInfo task, boolean isUsed) {
        if (this.preTasks==null) this.preTasks = new ArrayList<PreTask>();
        PreTask pt = new PreTask();
        pt.setPreTask(task);
        pt.setUseResult(isUsed);
        this.preTasks.add(pt);
    }
    public List<PreTask> getPreTasks() {
        return this.preTasks;
    }

    /**
     * 当前对象转换为Po对象，为数据库操作做准备
     * @return 任务信息
     */
    public TaskInfoPo convert2Po() {
        TaskInfoPo ret = new TaskInfoPo();
        if (this.getId()==null||this.getId().trim().equals("")) {
            ret.setId(SequenceUUID.getPureUUID());
        } else {
            ret.setId(this.getId());
        }
        ret.setTaskName(this.taskName);
        ret.setLangType(this.langType);
        ret.setExcuteFunc(this.excuteFunc);
        ret.setParam(this.param);
        ret.setParam(this.param);
        ret.setStatus(this.status);
        ret.setDesc(this.desc);
        if (this.resultFile!=null&&(this.resultFile.getId()!=null&&this.resultFile.getId().trim().length()>0)) ret.setRfId(this.resultFile.getId());
        if (this.taskGroup!=null&&(this.taskGroup.getId()!=null&&this.taskGroup.getId().trim().length()>0)) ret.setTaskGId(this.taskGroup.getId());
        return ret;
    }

    /**
     * 将当前任务的多个前置任务转换为Po对象的列表，为数据库操作做准备
     * @return 前置任务组对应的Po对象列表，若没有前置任务，返回空
     */
    public List<TaskRelPo> convertProTasks2PoList() {
        if (this.preTasks==null||this.preTasks.size()==0) return null;

        List<TaskRelPo> retl = new ArrayList<TaskRelPo>();
        if (this.getId()==null||this.getId().trim().equals("")) this.setId(SequenceUUID.getPureUUID());
        for (PreTask pt: this.preTasks) {
            TaskRelPo trp = new TaskRelPo();
            trp.setId(SequenceUUID.getPureUUID());
            trp.setTaskId(this.getId());
            trp.setPreTaskId(pt.getPreTask().getId());
            if (pt.isUseResult()) trp.setUsedPreData(1);
            else  trp.setUsedPreData(2);
        }
        return retl;
    }
}