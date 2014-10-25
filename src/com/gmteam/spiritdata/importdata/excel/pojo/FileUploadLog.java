package com.gmteam.spiritdata.importdata.excel.pojo;

import java.util.Date;

import com.gmteam.framework.core.model.BaseObject;

public class FileUploadLog extends BaseObject{
    private static final long serialVersionUID = 1L;

    private String id;

    private String ownerId;
    /**服务端文件路径*/
    private String sFileName;
    private Long fileSize;
    /**客户端文件路径*/
    private String cFileName;

    private Date cTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getsFileName() {
        return sFileName;
    }
    public void setsFileName(String sFileName) {
        this.sFileName = sFileName;
    }

    public String getcFileName() {
        return cFileName;
    }

    public void setcFileName(String cFileName) {
        this.cFileName = cFileName;
    }

    public Date getcTime() {
        return cTime;
    }

    public void setcTime(Date cTime) {
        this.cTime = cTime;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
}   