package com.gmteam.spiritdata.filemanage.core.persistence.pojo;

import java.sql.Timestamp;
import com.gmteam.framework.core.model.BaseObject;

/**
 * 文件关系表
 * 对应持久化中数据库的表为SA_FILE_REL
 * @author wh
 */
public class FileRelationPo extends BaseObject {
    private static final long serialVersionUID = 5192098948005184377L;

    private String id; //文件id
    private int AType; //第一文件类型：=1是对原生态表的关联关系；=2是文件关联表
    private String AId; //第一文件类Id
    private int BType; //第二文件类型：=1是对原生态表的关联关系；=2是文件关联表
    private String BId; //第二文件类Id
    private int RType1; //关联类型1:=1单向-说明aid是bid的子；=0平等；=-1反向-说明bid是aid的子(这个通过视图实现)
    private String RType2; //关联类型2
    private String desc; //说明
    private Timestamp CTime; //关系创建时间

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public int getAType() {
        return AType;
    }
    public void setAType(int aType) {
        AType = aType;
    }
    public String getAId() {
        return AId;
    }
    public void setAId(String aId) {
        AId = aId;
    }
    public int getBType() {
        return BType;
    }
    public void setBType(int bType) {
        BType = bType;
    }
    public String getBId() {
        return BId;
    }
    public void setBId(String bId) {
        BId = bId;
    }
    public int getRType1() {
        return RType1;
    }
    public void setRType1(int rType1) {
        RType1 = rType1;
    }
    public String getRType2() {
        return RType2;
    }
    public void setRType2(String rType2) {
        RType2 = rType2;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public Timestamp getCTime() {
        return CTime;
    }
    public void setCTime(Timestamp CTime) {
        this.CTime = CTime;
    }
}