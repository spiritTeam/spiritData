package com.gmteam.spiritdata.dictdata.pojo;

import java.sql.Timestamp;

import com.gmteam.framework.core.model.tree.TreeNodeBean;

/**
 * 字典项
 * 对应持久化中数据库的表为PLAT_DICTD
 * @author wh
 */
public class DictDetail extends TreeNodeBean {
    private static final long serialVersionUID = -4154673243407172158L;

    //String id; //字典项ID，在TreeNodeBean中对应id
    private String mid; //字典组ID
    //String pId; //上级字典项ID，若为直接属于某字典组的字典项，则此只为0，在TreeNodeBean中对应parentId
    //int sort; //排序，在TreeNodeBean中对应sort
    private int isValidate; //字典组是否可用 1可用，2不可用
    //String ddName; //字典项名称，在TreeNodeBean中对应nodeName
    private String NPy; //字典名称拼音
    private String aliasName; //字典项别名
    private String anPy; //字典项别名拼音
    private String BCode; //字典项业务编码
    private int DType; //字典项类型：1系统保留；2系统；3定义；4引用：其他字典项ID
    private String DRef; //字典项引用
    private String descn; //说明
    private Timestamp CTime; //记录创建时间
    private Timestamp lmTime; //记录最后修改时间

    public String getMid() {
        return mid;
    }
    public void setMid(String mid) {
        this.mid = mid;
    }
    public int getIsValidate() {
        return isValidate;
    }
    public void setIsValidate(int isValidate) {
        this.isValidate = isValidate;
    }
    public String getNPy() {
        return NPy;
    }
    public void setNPy(String nPy) {
        NPy = nPy;
    }
    public Timestamp getCTime() {
        return CTime;
    }
    public void setCTime(Timestamp cTime) {
        CTime = cTime;
    }
    public String getAliasName() {
        return aliasName;
    }
    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }
    public String getAnPy() {
        return anPy;
    }
    public void setAnPy(String anPy) {
        this.anPy = anPy;
    }
    public String getBCode() {
        return BCode;
    }
    public void setBCode(String bCode) {
        BCode = bCode;
    }
    public int getDType() {
        return DType;
    }
    public void setDType(int dType) {
        DType = dType;
    }
    public String getDRef() {
        return DRef;
    }
    public void setDRef(String dRef) {
        DRef = dRef;
    }
    public String getDescn() {
        return descn;
    }
    public void setDescn(String descn) {
        this.descn = descn;
    }
    public Timestamp getLmTime() {
        return lmTime;
    }
    public void setLmTime(Timestamp lmTime) {
        this.lmTime = lmTime;
    }
}