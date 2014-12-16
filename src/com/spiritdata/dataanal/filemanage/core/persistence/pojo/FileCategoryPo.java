package com.spiritdata.dataanal.filemanage.core.persistence.pojo;

import java.sql.Timestamp;

import com.spiritdata.framework.core.model.BaseObject;

/**
 * 文件分类
 * 对应持久化中数据库的表为SA_FILE_CATEGORY
 * @author wh
 */
public class FileCategoryPo extends BaseObject {
    private static final long serialVersionUID = -8686440848224680046L;

    private String id; //文件分类id
    private String FId; //文件id
    private String FType1; //文件分类—大类，目前支持持三种,IMP、LOG和ANAL
    private String FType2; //文件分类—中类
    private String FType3; //文件分类—小类
    private String extInfo; //扩展信息
    private Timestamp CTime; //创建时间

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getFId() {
        return FId;
    }
    public void setFId(String fId) {
        FId = fId;
    }
    public String getFType1() {
        return FType1;
    }
    public void setFType1(String fType1) {
        FType1 = fType1;
    }
    public String getFType2() {
        return FType2;
    }
    public void setFType2(String fType2) {
        FType2 = fType2;
    }
    public String getFType3() {
        return FType3;
    }
    public void setFType3(String fType3) {
        FType3 = fType3;
    }
    public String getExtInfo() {
        return extInfo;
    }
    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
    }
    public Timestamp getCTime() {
        return CTime;
    }
    public void setCTime(Timestamp cTime) {
        CTime = cTime;
    }
}