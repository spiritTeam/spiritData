package com.spiritdata.dataanal.task;

import com.spiritdata.dataanal.task.model.TaskInfo;
import com.spiritdata.filemanage.category.ANAL.model.AnalResultFile;
import com.spiritdata.jsonD.exceptionC.JsonD0103CException;
import com.spiritdata.jsonD.model.AccessJsonD;

public abstract class TaskUtils {

    public static AccessJsonD convert2AccessJsonDOne(TaskInfo task) {
        AccessJsonD aj = new AccessJsonD();
        AnalResultFile arf = task.getResultFile();
        if (arf==null) return null;
        aj.setJsonDCode(arf.getJsonDCode());
        if (arf.getId()==null||arf.getId().trim().length()==0) throw new JsonD0103CException("Task所对应的结果文件没有Id，无法转换");
        aj.setJsonDId(arf.getId()); //文件的id
        if (arf.getFileName()==null||arf.getFileName().trim().length()==0) throw new JsonD0103CException("Task所对应的结果文件没有设置存储文件名，无法转换");
        aj.setFilePath(arf.getFileName());
        aj.setUrl("jsonD/getJsonD.do?jsonDId="+arf.getId());
        return aj;
    }

}