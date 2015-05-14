package com.spiritdata.dataanal.task.core.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.spiritdata.dataanal.task.core.enumeration.StatusType;
import com.spiritdata.dataanal.task.core.enumeration.TaskLangType;
import com.spiritdata.dataanal.task.core.persistence.pojo.TaskInfoPo;
import com.spiritdata.dataanal.task.core.persistence.pojo.TaskRelPo;
import com.spiritdata.filemanage.category.ANAL.model.AnalResultFile;
import com.spiritdata.framework.core.model.ModelSwapPo;
import com.spiritdata.framework.exceptionC.Plat0006CException;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.jsonD.util.JsonUtils;

/**
 * 任务信息对象，包括任务的基本信息，前置任务列表，任务执行结果列表
 * @author wh
 */
public class TaskInfo implements Serializable, ModelSwapPo {
    private static final long serialVersionUID = 5771297762371717292L;

    private String id; //任务
    private String taskName; //任务名称
    private TaskLangType langType; //执行语言，默认为java
    private String executeFunc; //任务执行方法
    private Map<String, Object> param; //任务执行所需的参数
    private StatusType status; //任务状态：1=准备执行；2=正在执行；3=执行成功；4=执行失败；5=任务失效；6=等待执行
    private int executeCount; //任务执行次数
    private String desc; //任务说明

    private Timestamp firstTime; //任务第一次准备执行时间
    private Timestamp beginTime; //本次开始执行时间
    private Timestamp endTime; //本次结束执行时间

    private TaskGroup taskGroup; //所属任务组，可为空
    private AnalResultFile resultFile; //分析结果文件，可为空
    private List<PreTask> preTasks; //前序任务列表

    public TaskInfo() {
    }
    /**
     * 从po对象构建本对象，通过此方法构造的对象是不完整的
     * 
     * @param po po对象
     */
    public TaskInfo(TaskInfoPo po) {
        this.buildFromPo(po);
    }

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
    public TaskLangType getLangType() {
        return langType;
    }
    public void setLangType(TaskLangType langType) {
        this.langType = langType;
    }
    public String getExecuteFunc() {
        return executeFunc;
    }
    public void setExecuteFunc(String executeFunc) {
        this.executeFunc = executeFunc;
    }
    public Map<String, Object> getParam() {
        return param;
    }
    @SuppressWarnings("unchecked")
    public void setParam(String param) {
        this.param = (Map<String, Object>)JsonUtils.jsonToObj(param, Map.class);
    }
    public StatusType getStatus() {
        return status;
    }
    public int getExecuteCount() {
        return executeCount;
    }
    public void setExecuteCount(int executeCount) {
        this.executeCount = executeCount;
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
    public AnalResultFile getResultFile() {
        return resultFile;
    }
    public void setResultFile(AnalResultFile resultFile) {
        this.resultFile = resultFile;
    }

    //任务组状态设置
    /**
     * 设置为准备状态
     */
    public void setPrepared() {
        this.status=StatusType.PREPARE;
    }
    /**
     * 设置为等待状态
     */
    public void setWaiting() {
        this.status=StatusType.WAITING;
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
     * 设置为执行失败
     */
    public void setFailed() {
        this.status=StatusType.FAILD;
    }

    /**
     * 新增前置任务
     * @param task 任务信息
     * @param isUsed 是否使用前置任务的分析结果
     */
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
    @Override
    public TaskInfoPo convert2Po() {
        TaskInfoPo ret = new TaskInfoPo();
        if (StringUtils.isNullOrEmptyOrSpace(this.getId())) ret.setId(SequenceUUID.getPureUUID());
        else ret.setId(this.getId());

        ret.setTaskName(this.taskName);
        ret.setLangType(this.langType.getValue());
        ret.setExecuteFunc(this.executeFunc);
        ret.setParam(JsonUtils.objToJson(this.param));
        ret.setStatus(this.status.getValue());
        ret.setExecuteCount(this.executeCount);
        ret.setDesc(this.desc);
        if (this.resultFile!=null&&!StringUtils.isNullOrEmptyOrSpace(this.resultFile.getId())) ret.setRfId(this.resultFile.getId());
        if (this.taskGroup!=null&&!StringUtils.isNullOrEmptyOrSpace(this.taskGroup.getId())) ret.setTaskGId(this.taskGroup.getId());
        return ret;
    }

    /**
     * 将当前任务的多个前置任务转换为Po对象的列表，为数据库操作做准备
     * @return 前置任务组对应的Po对象列表，若没有前置任务，返回空
     */
    public List<TaskRelPo> convertProTasks2PoList() {
        if (this.preTasks==null||this.preTasks.size()==0) return null;

        List<TaskRelPo> retl = new ArrayList<TaskRelPo>();
        if (StringUtils.isNullOrEmptyOrSpace(this.getId())) this.setId(SequenceUUID.getPureUUID());
        for (PreTask pt: this.preTasks) {
            TaskRelPo trp = new TaskRelPo();
            trp.setId(SequenceUUID.getPureUUID());
            trp.setTaskId(this.getId());
            trp.setPreTaskId(pt.getPreTask().getId());
            if (pt.isUseResult()) trp.setUsedPreData(1);
            else  trp.setUsedPreData(2);
            retl.add(trp);
        }
        return retl;
    }

    /**
     * <p>从po得到模型对象，对于任务信息对象来说：
     * <p>taskGroup属性（所属任务组），没有做处理，通过数据库检索可以得到这组属性，之所以没有处理，是要把这个功能留到Service中再处理。
     * 这样做考虑如下：读取数据库，慢！而在Service中，可能上下文已经得到了文件的信息，这样可能更快，而且不用从数据库获得两次(本方法中一次，Service中一次)。
     * <p>同样理由，resultFile(对应的jsonD文件)、preTasks（前序任务列表）的构造也不在这里处理。(通过读取数据库相关信息，这三个列表也是能够得到的)
     * <p>因此要注意：通过本方法构建的模型对象信息是不完整的。
     */
    @Override
    public void buildFromPo(Object po) {
        if (po==null) throw new Plat0006CException("Po对象为空，无法从空对象得到概念/逻辑对象！");
        if (!(po instanceof TaskInfoPo)) throw new Plat0006CException("Po对象不是TaskInfoPo的实例，无法从此对象构建任务信息对象！");

        TaskInfoPo _po = (TaskInfoPo)po;
        this.id = _po.getId();
        this.taskName = _po.getTaskName();
        this.langType = TaskLangType.getTaskLangType(_po.getLangType());
        this.executeFunc = _po.getExecuteFunc();
        this.setParam(_po.getParam());
        this.status = StatusType.getStatusType(_po.getStatus());
        this.executeCount = _po.getExecuteCount();
        this.desc = _po.getDesc();
        this.firstTime = _po.getFirstTime();
        this.beginTime = _po.getBeginTime();
        this.endTime = _po.getEndTime();
    }
}