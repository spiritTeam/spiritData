package com.spiritdata.dataanal.dictionary.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.spiritdata.dataanal.common.model.Owner;
import com.spiritdata.dataanal.dictionary.persistence.pojo.DictMasterPo;
import com.spiritdata.framework.core.model.Model2Po;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;

/**
 * 字典组
 * 对应持久化中数据库的表为PLAT_DICTM
 * @author wh
 */
public class DictMaster implements Serializable, Model2Po {
    private static final long serialVersionUID = -5935730569262158194L;

    private String id; //字典组id
    private Owner owner;
    private String dmName; //字典组名称
    private int order; //排序号，越大越靠前
    private int isValidate; //字典组是否可用 1可用，2不可用
    private int MType; //字典组类型：1系统保留；2系统；3定义；
    private String MRef; //字典组引用，当mType=3
    private String desc; //说明
    private Timestamp CTime; //记录创建时间
    private Timestamp lmTime; //最后修改时间

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Owner getOwner() {
        return owner;
    }
    public void setOwner(Owner owner) {
        this.owner = owner;
    }
    public String getDmName() {
        return dmName;
    }
    public void setDmName(String dmName) {
        this.dmName = dmName;
    }
    public int getOrder() {
        return order;
    }
    public void setOrder(int order) {
        this.order = order;
    }
    public int getIsValidate() {
        return isValidate;
    }
    public void setIsValidate(int isValidate) {
        this.isValidate = isValidate;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public int getMType() {
        return MType;
    }
    public void setMType(int mType) {
        MType = mType;
    }
    public String getMRef() {
        return MRef;
    }
    public void setMRef(String mRef) {
        MRef = mRef;
    }
    public Timestamp getCTime() {
        return CTime;
    }
    public void setCTime(Timestamp cTime) {
        CTime = cTime;
    }
    public Timestamp getLmTime() {
        return lmTime;
    }
    public void setLmTime(Timestamp lmTime) {
        this.lmTime = lmTime;
    }

    /**
     * 得到字典名称拼音
     * @return 字典名称拼音
     */
    public String getNPy() {
        return "";
    }

    @Override
    public DictMasterPo convert2Po() {
        DictMasterPo ret = new DictMasterPo();
        //id处理，没有id，自动生成一个
        if (StringUtils.isNullOrEmptyOrSpace(this.id)) ret.setId(SequenceUUID.getPureUUID());
        else ret.setId(this.id);

        ret.setOwnerId(this.owner.getOwnerId());
        ret.setOwnerType(this.owner.getOwnerType());
        ret.setDmName(this.dmName);
        ret.setOrder(this.order);
        ret.setIsValidate(this.isValidate);
        ret.setMRef(this.MRef);
        ret.setMType(this.MType);
        ret.setDesc(this.desc);
        ret.setCTime(this.CTime);
        ret.setLmTime(this.lmTime);
        return ret;
    }
}