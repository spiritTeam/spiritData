package com.gmteam.filemanage.persistence.pojo;

import java.sql.Timestamp;

import com.gmteam.framework.core.model.BaseObject;
import com.gmteam.framework.util.FileNameUtils;

/**
 * 文件索引表
 * 对应持久化中数据库的表为SA_FILE_INDEX
 * @author wh
 */
public class FileIndex extends BaseObject {
    private static final long serialVersionUID = 2308403169193332695L;

    private String id; //文件id
    private int ownerType; //模式所对应的所有者类型（1=注册用户;2=非注册用户(session);3=系统生成）
    private String ownerId; //所有者标识（可能是用户id，也可能是SessionID，也可能是'Sys'）
    private int accessType; //文件访问类型，可通过这个类型转换为file:///；ftp:///等，可能需要再定义访问的一些属性没，如ftp的用户名/密码/端口等
    private String path; //文件路径
    private String fileName; //文件名称，包括扩展名
    private String extName; //文件扩展名
    private Long fileSize; //文件大小
    private String desc; //文件说明
    private Timestamp cTime; //文件创建时间
    private Timestamp lmTime; //文件最后修改时间

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
    public Timestamp getcTime() {
        return cTime;
    }
    public void setcTime(Timestamp cTime) {
        this.cTime = cTime;
    }
    public Timestamp getLmTime() {
        return lmTime;
    }
    public void setLmTime(Timestamp lmTime) {
        this.lmTime = lmTime;
    }
}