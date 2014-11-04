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
    private int ownerType; //模式所对应的所有者类型（1=注册用户;2=非注册用户(session)）
    private String ownerId; //所有者标识（可能是用户id，也可能是SessionID）
    private String sFileName; //服务端文件路径
    private String cFileName; //客户端文件路径
    private Long fileSize; //文件大小
    private Timestamp cTime; //记录创建时间

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
    public Long getFileSize() {
        return fileSize;
    }
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    public Timestamp getcTime() {
        return cTime;
    }
    public void setcTime(Timestamp cTime) {
        this.cTime = cTime;
    }
}