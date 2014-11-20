package com.gmteam.filemanage.persistence.pojo;

import java.sql.Timestamp;

import com.gmteam.framework.core.model.BaseObject;

/**
 * 文件分类
 * 对应持久化中数据库的表为SA_FILE_CLASS
 * @author wh
 */
public class FileClass extends BaseObject {
    private static final long serialVersionUID = -8686440848224680046L;

    private String id; //文件分类id
    private String fId; //文件id
    private String fType1; //文件分类—大类，目前支持持三种,IMP、LOG和ANAL
    private String fType2; //文件分类—中类
    private String fType3; //文件分类—小类
    private String extInfo; //扩展信息
    private Timestamp cTime; //创建时间

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getfId() {
        return fId;
    }
    public void setfId(String fId) {
        this.fId = fId;
    }
    public String getfType1() {
        return fType1;
    }
    public void setfType1(String fType1) {
        this.fType1 = fType1;
    }
    public String getfType2() {
        return fType2;
    }
    public void setfType2(String fType2) {
        this.fType2 = fType2;
    }
    public String getfType3() {
        return fType3;
    }
    public void setfType3(String fType3) {
        this.fType3 = fType3;
    }
    public String getExtInfo() {
        return extInfo;
    }
    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
    }
    public Timestamp getcTime() {
        return cTime;
    }
    public void setcTime(Timestamp cTime) {
        this.cTime = cTime;
    }
}