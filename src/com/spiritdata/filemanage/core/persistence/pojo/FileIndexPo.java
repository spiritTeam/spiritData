package com.spiritdata.filemanage.core.persistence.pojo;

import java.sql.Timestamp;

import com.spiritdata.framework.core.model.BaseObject;
import com.spiritdata.framework.util.FileNameUtils;

/**
 * 文件索引表持久化对象<br/>
 * 对应持久化中数据库的表为SA_FILE_INDEX
 * @author wh
 */
public class FileIndexPo extends BaseObject {
    private static final long serialVersionUID = 2308403169193332695L;

    protected String id; //文件id
    protected int ownerType; //模式所对应的所有者类型（1=注册用户;2=非注册用户(session);3=系统生成）
    protected String ownerId; //所有者标识（可能是用户id，也可能是SessionID，也可能是'Sys'）
    protected int accessType; //文件访问类型，可通过这个类型转换为file:///；ftp:///等，可能需要再定义访问的一些属性没，如ftp的用户名/密码/端口等
    protected String path; //文件路径，不包括文件名
    protected String fileName; //文件名称，包括扩展名
    protected String extName; //文件扩展名
    protected Long fileSize; //文件大小
    protected String desc; //文件说明
    protected Timestamp fcTime; //文件创建时间
    protected Timestamp flmTime; //文件最后修改时间
    protected Timestamp CTime; //记录创建时间

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
    public int getAccessType() {
        return accessType;
    }
    public void setAccessType(int accessType) {
        this.accessType = accessType;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getFileName() {
        return fileName;
    }
    /**
     * 设置文件名，同时根据文件名设置文件扩展名
     * @param fileName 文件名，包括文件扩展名
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
        this.extName = FileNameUtils.getExt(fileName);
    }
    public String getExtName() {
        return extName;
    }
    public Long getFileSize() {
        return fileSize;
    }
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public Timestamp getFcTime() {
        return fcTime;
    }
    public void setFcTime(Timestamp fcTime) {
        this.fcTime = fcTime;
    }
    public Timestamp getFlmTime() {
        return flmTime;
    }
    public void setFlmTime(Timestamp flmTime) {
        this.flmTime = flmTime;
    }
    public Timestamp getCTime() {
        return CTime;
    }
    public void setCTime(Timestamp CTime) {
        this.CTime = CTime;
    }
}