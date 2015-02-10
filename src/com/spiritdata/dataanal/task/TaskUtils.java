package com.spiritdata.dataanal.task;

import com.spiritdata.dataanal.task.model.TaskInfo;
import com.spiritdata.filemanage.ANAL.model.AnalResultFile;
import com.spiritdata.jsonD.exceptionC.Jsond0103CException;
import com.spiritdata.jsonD.model.AccessJsond;

public abstract class TaskUtils {

    public static AccessJsond convert2AccessJsondOne(TaskInfo task) {
        AccessJsond aj = new AccessJsond();
        AnalResultFile arf = task.getResultFile();
        if (arf==null) return null;
        aj.setJsondCode(arf.getJsonDCode());
        if (arf.getId()==null||arf.getId().trim().length()==0) throw new Jsond0103CException("Task所对应的结果文件没有Id，无法转换");
        aj.setJsondId(arf.getId()); //文件的id
        if (arf.getFileName()==null||arf.getFileName().trim().length()==0) throw new Jsond0103CException("Task所对应的结果文件没有设置存储文件名，无法转换");
        aj.setFilePath(arf.getFileName());
        aj.setUrl("jsonD/getJsonD.do?jsondId="+arf.getId());
        return aj;
    }

}