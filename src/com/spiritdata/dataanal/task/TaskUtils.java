package com.spiritdata.dataanal.task;

import com.spiritdata.dataanal.task.core.enumeration.TaskLangType;
import com.spiritdata.dataanal.task.core.model.TaskInfo;
import com.spiritdata.dataanal.task.process.TaskProcess;
import com.spiritdata.filemanage.category.ANAL.model.AnalResultFile;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.dataanal.exceptionC.Dtal0401CException;
import com.spiritdata.dataanal.exceptionC.Dtal0405CException;
import com.spiritdata.jsonD.model.AccessJsonD;

/**
 * 任务通用方法
 * @author wh
 */
public abstract class TaskUtils {

    /**
     * 任务信息转换为jsonD的访问对象
     * @param task 任务信息
     * @return jsonD访问对象
     */
    public static AccessJsonD convert2AccessJsonDOne(TaskInfo task) {
        AccessJsonD aj = new AccessJsonD();
        AnalResultFile arf = task.getResultFile();
        if (arf==null) return null;
        aj.setJsonDCode(arf.getJsonDCode());
        if (StringUtils.isNullOrEmptyOrSpace(arf.getId())) throw new Dtal0401CException("Task所对应的结果文件没有Id，无法转换");
        aj.setJsonDId(arf.getId()); //文件的id
        if (StringUtils.isNullOrEmptyOrSpace(arf.getFileName())) throw new Dtal0401CException("Task所对应的结果文件没有设置存储文件名，无法转换");
        aj.setFilePath(arf.getFileName());
        aj.setUrl("jsonD/getJsonD.do?jsonDId="+arf.getId());
        return aj;
    }

    public static TaskProcess loadClass(TaskInfo ti) {
        //注意，这里没有重写classLoader，类必须在classpath中能够找到；
        TaskProcess tp = null;
        try {
            if (ti.getLangType()!=TaskLangType.JAVA) throw new Dtal0405CException("目前只支持Java类的外部函数！");
            Object o = Class.forName(ti.getExecuteFunc()).newInstance();
            if (o instanceof TaskProcess) {
                tp = (TaskProcess)o;
            } else throw new Dtal0405CException("["+o.getClass().getName()+"]不是<"+TaskProcess.class.getName()+">的子类！");
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new Dtal0405CException(e);
        }
        return tp;
    }
 }