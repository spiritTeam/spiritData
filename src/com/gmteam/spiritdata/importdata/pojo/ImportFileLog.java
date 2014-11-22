package com.gmteam.spiritdata.importdata.pojo;

import java.sql.Timestamp;

import com.gmteam.framework.core.model.BaseObject;

/**
 * 文件上传日志
 * 对应持久化中数据库的表为SA_IMP_LOG
 * @author wh, mht
 */
public class ImportFileLog extends BaseObject{
    private static final long serialVersionUID = -6413748884964474948L;
    private String id; //文件上传id
    private String ownerId; //所有者标识（可能是用户id，也可能是SessionID）
    private int ownerType; //模式所对应的所有者类型（1=注册用户;2=非注册用户(session),3系统）
    private int accessType;//文件访问类型，如ftp,操作系统文件等，目前只支持1=操作系统文件
    private String filePath; //文件地址，实际就是文件存储的路径，不包括文件名称
    private String fileName; //文件名称，包括扩展名，注意只有文件名称，没有路径
    private String fileExtName; //文件扩展名称
    private Long fileSize; //文件大小
    private String descn; //客户端文件路径
    private Timestamp cTime; //记录创建时间
    private Timestamp lmTime; //记录创建时间
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
    public int getOwnerType() {
        return ownerType;
    }
    public void setOwnerType(int ownerType) {
        this.ownerType = ownerType;
    }
    public int getAccessType() {
        return accessType;
    }
    public void setAccessType(int accessType) {
        this.accessType = accessType;
    }
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getFileExtName() {
        return fileExtName;
    }
    public void setFileExtName(String fileExtName) {
        this.fileExtName = fileExtName;
    }
    public Long getFileSize() {
        return fileSize;
    }
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    public String getDescn() {
        return descn;
    }
    public void setDescn(String descn) {
        this.descn = descn;
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