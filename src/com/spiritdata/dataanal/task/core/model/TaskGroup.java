package com.spiritdata.dataanal.task.core.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.spiritdata.dataanal.common.model.Owner;
import com.spiritdata.dataanal.task.core.persistence.pojo.TaskGroupPo;
import com.spiritdata.framework.core.model.ModelSwapPo;
import com.spiritdata.framework.exceptionC.Plat0006CException;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;

/**
 * 任务组模型，包括任务组的信息，任务组中的任务(是一个有向图)
 * @author wh
 */
public class TaskGroup implements Serializable, ModelSwapPo {
    private static final long serialVersionUID = 6627157875372740607L;

    private String id; //任务组id
    private String reportId; //所对应的报告Id，可为空
    private Owner owner; //所有者
    private String workName; //任务组工作名称
    private int status; //任务组状态：1=准备执行；2=正在执行；3=任务失效；4=执行成功；5=执行失败；
    private String desc; //任务组说明
    private Timestamp beginTime; //任务开始启动时间

    private TaskGraph taskGraph; //子任务图

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
    public Owner getOwner() {
        return owner;
    }
    public void setOwner(Owner owner) {
        this.owner = owner;
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

    public TaskGraph getTaskGraph() {
        return taskGraph;
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
        if (this.taskGraph==null) this.taskGraph = new TaskGraph();
        this.taskGraph.addTaskInfo(task);
        task.setTaskGroup(this);
    }

    /**
     * 当前对象转换为Po对象，为数据库操作做准备
     * @return 任务组信息
     */
    public TaskGroupPo convert2Po() {
        TaskGroupPo ret = new TaskGroupPo();
        if (StringUtils.isNullOrEmptyOrSpace(this.getId())) ret.setId(SequenceUUID.getPureUUID());
        else ret.setId(this.getId());

        ret.setReportId(this.reportId);
        ret.setOwnerType(this.owner.getOwnerType());
        ret.setOwnerId(this.owner.getOwnerId());
        ret.setWorkName(this.workName);
        ret.setStatus(this.status==0?1:this.status);
        ret.setDesc(this.desc);
        return ret;
    }

    /**
     * <p>从po得到模型对象，对于任务组信息对象来说：
     * <p>tasks属性（子任务图），没有做处理，通过数据库检索可以得到这组属性，之所以没有处理，是要把这个功能留到Service中再处理。
     * 这样做考虑如下：读取数据库，慢！而在Service中，可能上下文已经得到了文件的信息，这样可能更快，而且不用从数据库获得两次(本方法中一次，Service中一次)。
     * <p>因此要注意：通过本方法构建的模型对象信息是不完整的。
     */
    @Override
    public void buildFromPo(Object po) {
        if (po==null) throw new Plat0006CException("Po对象为空，无法从空对象得到概念/逻辑对象！");
        if (!(po instanceof TaskGroupPo)) throw new Plat0006CException("Po对象不是TaskGroupPo的实例，无法从此对象构建任务组对象！");

        TaskGroupPo _po = (TaskGroupPo)po;
        this.id = _po.getId();
        this.reportId = _po.getReportId();
        this.owner = new Owner(_po.getOwnerType(), _po.getOwnerId());
        this.workName = _po.getWorkName();
        this.status = _po.getStatus();
        this.desc = _po.getDesc();
        this.beginTime = _po.getBeginTime();
    }
}