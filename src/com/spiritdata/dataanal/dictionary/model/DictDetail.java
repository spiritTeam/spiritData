package com.spiritdata.dataanal.dictionary.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.spiritdata.dataanal.dictionary.persistence.pojo.DictDetailPo;
import com.spiritdata.framework.core.model.ModelSwapPo;
import com.spiritdata.framework.core.model.tree.TreeNodeBean;
import com.spiritdata.framework.util.ChineseCharactersUtils;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;

/**
 * 字典项
 * 对应持久化中数据库的表为PLAT_DICTD
 * @author wh
 */
public class DictDetail extends TreeNodeBean implements Serializable, ModelSwapPo {
    private static final long serialVersionUID = -4154673243407172158L;

    //String id; //字典项ID，在TreeNodeBean中对应id
    private String MId; //字典组ID
    //String pId; //上级字典项ID，若为直接属于某字典组的字典项，则此只为0，在TreeNodeBean中对应parentId
    //int sort; //排序，在TreeNodeBean中对应Order
    private int isValidate; //字典组是否可用 1可用，2不可用
    //String ddName; //字典项名称，在TreeNodeBean中对应nodeName
    private String NPy; //字典名称拼音
    private String aliasName; //字典项别名
    private String anPy; //字典项别名拼音
    private String BCode; //字典项业务编码
    private int DType; //字典项类型：1系统保留；2系统；3定义；4引用：其他字典项ID
    private String DRef; //字典项引用
    private String desc; //说明
    private Timestamp CTime; //记录创建时间
    private Timestamp lmTime; //记录最后修改时间

    public String getMId() {
        return MId;
    }
    public void setMId(String MId) {
        this.MId = MId;
    }
    public int getIsValidate() {
        return isValidate;
    }
    public void setIsValidate(int isValidate) {
        this.isValidate = isValidate;
    }

    @Override
    public void setNodeName(String nodeName) {
        this.setNodeName(nodeName);
        if (StringUtils.isNullOrEmptyOrSpace(this.NPy)) this.NPy=ChineseCharactersUtils.getFullSpellFirstUp(nodeName);
    }

    public String getNPy() {
        if (StringUtils.isNullOrEmptyOrSpace(this.NPy)&&!StringUtils.isNullOrEmptyOrSpace(this.getNodeName())) {
            this.NPy=ChineseCharactersUtils.getFullSpellFirstUp(this.getNodeName());
        }
        return this.NPy;
    }

    public String getAliasName() {
        return aliasName;
    }
    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
        if (StringUtils.isNullOrEmptyOrSpace(this.anPy)) this.anPy=ChineseCharactersUtils.getFullSpellFirstUp(aliasName);
    }
    public String getAnPy() {
        if (StringUtils.isNullOrEmptyOrSpace(this.NPy)&&!StringUtils.isNullOrEmptyOrSpace(this.getNodeName())) {
            this.NPy=ChineseCharactersUtils.getFullSpellFirstUp(this.getNodeName());
        }
        return this.anPy;
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
    public Timestamp getCTime() {
        return CTime;
    }
    public void setCTime(Timestamp cTime) {
        CTime = cTime;
    }
    public String getDRef() {
        return DRef;
    }
    public void setDRef(String dRef) {
        DRef = dRef;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public Timestamp getLmTime() {
        return lmTime;
    }
    public void setLmTime(Timestamp lmTime) {
        this.lmTime = lmTime;
    }

    @Override
    public Object convert2Po() {
        DictDetailPo ret = new DictDetailPo();
        //id处理，没有id，自动生成一个
        if (StringUtils.isNullOrEmptyOrSpace(this.getId())) ret.setId(SequenceUUID.getPureUUID());
        else ret.setId(this.getId());

        ret.setMId(this.MId);
        ret.setParentId(this.getParentId());
        ret.setOrder(this.getOrder());
        ret.setIsValidate(this.isValidate);
        ret.setDdName(this.getNodeName());
        ret.setAliasName(this.aliasName);
        return ret;
    }
    @Override
    public Object getFromPo(Object po) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void buildFromPo(Object po) {
        // TODO Auto-generated method stub
        
    }
}