package com.spiritdata.dataanal.task.core.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.spiritdata.dataanal.common.model.Owner;
import com.spiritdata.dataanal.task.core.enumeration.StatusType;
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
    private StatusType status; //任务组状态：1=准备执行；2=正在执行；3=执行成功；4=执行失败；5=任务失效；
    private int defaultExecuteCountLimit; //其子任务默认的执行次数的上限，目前不支持各自任务有自己的执行次数上线，此属性是程序属性，不记录在数据库中
    private String desc; //任务组说明
    private Timestamp beginTime; //任务开始启动时间

    private TaskGraph taskGraph; //子任务图

    public TaskGroup() {
    }
    /**
     * 从po对象构建本对象，通过此方法构造的对象是不完整的
     * 
     * @param po po对象
     */
    public TaskGroup(TaskGroupPo po) {
        this.buildFromPo(po);
    }

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
    public StatusType getStatus() {
        return status;
    }
    public int getDefaultExecuteCountLimit() {
        return defaultExecuteCountLimit;
    }
    public void setDefaultExecuteCountLimit(int defaultExecuteCountLimit) {
        this.defaultExecuteCountLimit = defaultExecuteCountLimit;
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
        this.status=StatusType.PREPARE;
    }
    /**
     * 设置为正在执行
     */
    public void setProcessing() {
        this.status=StatusType.PROCESSING;
    }
    /**
     * 设置为失效
     */
    public void setAbatement() {
        this.status=StatusType.ABATE;
    }
    /**
     * 设置为执行成功
     */
    public void setSuccessed() {
        this.status=StatusType.SUCCESS;
    }
    /**
     * 设置为执行失败：其子任务图没有完全执行成功
     */
    public void setFailed() {
        this.status=StatusType.FAILD;
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
        ret.setStatus(this.status.getValue());
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
        this.status = StatusType.getStatusType(_po.getStatus());
        this.desc = _po.getDesc();
        this.beginTime = _po.getBeginTime();
    }

    /**
     * 获得任务组所包含的任务信息的数量。若本对象的任务图属性为空，或图对象的任务Map为空，返回-1。<br/>
     * 因此在调用此方法时，不能简单相加，应该如此：sum += (getTaskInfoSize()>0?getTaskInfoSize:0);
     * @return 任务信息的数量
     */
    public int getTaskInfoSize() {
        if (this.getTaskGraph()==null) return -1;
        if (this.getTaskGraph().getTaskMap()==null) return -1;
        return this.getTaskGraph().getTaskMap().size();
    }
}