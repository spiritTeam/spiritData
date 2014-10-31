package com.gmteam.spiritdata.dictdata.pojo;

import java.sql.Timestamp;
import com.gmteam.framework.core.model.BaseObject;

/**
 * 字典组
 * 对应持久化中数据库的表为PLAT_DICTM
 * @author wh
 */
public class DictMaster extends BaseObject {
    /**
     * 
     */
    private static final long serialVersionUID = -5935730569262158194L;

    private String id; //字典组id
    private String ownerId; //所有者标识（可能是用户id，也可能是SessionID）
    private int ownerType; //模式所对应的所有者类型（1=注册用户;2=非注册用户(session)）
    private String dmName; //典组名称
    private String nPy; //字典组名称拼音
    private int sort; //排序号，越大越靠前
    private int isValidate; //字典组是否可用 1可用，2不可用
    private int mType; //字典组类型：1系统保留；2系统；3定义；
    private String mRef; //字典组引用，当mType=3
    private String descn; //说明
    private Timestamp cTime; //记录创建时间
    private Timestamp lmTime; //最后修改时间

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
    public String getDmName() {
        return dmName;
    }
    public void setDmName(String dmName) {
        this.dmName = dmName;
    }
    public String getnPy() {
        return nPy;
    }
    public void setnPy(String nPy) {
        this.nPy = nPy;
    }
    public int getSort() {
        return sort;
    }
    public void setSort(int sort) {
        this.sort = sort;
    }
    public int getIsValidate() {
        return isValidate;
    }
    public void setIsValidate(int isValidate) {
        this.isValidate = isValidate;
    }
    public int getmType() {
        return mType;
    }
    public void setmType(int mType) {
        this.mType = mType;
    }
    public String getmRef() {
        return mRef;
    }
    public void setmRef(String mRef) {
        this.mRef = mRef;
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