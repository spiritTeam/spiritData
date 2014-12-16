package com.spiritdata.dataanal.metadata.relation.pojo;

import java.sql.Timestamp;

import com.spiritdata.framework.core.model.BaseObject;

/**
 * 表中的列的指标信息
 * 对应持久化中数据库的表为SA_MD_COLQUOTA
 * @author wh, mht
 */
public class QuotaColumn extends BaseObject {
    private static final long serialVersionUID = 7021542418630072318L;

    private String id; //实体表指标Id
    private String tqId; //实体表指标Id
    private String colId; //列描述Id
    private String max; //列指标——最大值
    private String min; //列指标——最小值
    private long nullCount; //列指标——空值数
    private long distinctCount; //列指标——单值数
    private Timestamp CTime; //记录创建时间
    private Timestamp lmTime; //本记录最后修改时间
    //以上信息对应数据库中的信息
    private MetadataColumn column; //本列指标对应的列描述信息
    private QuotaTable tabQuota; //本列指标对应的表指标信息

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTqId() {
        return tqId;
    }
    public void setTqId(String tqId) {
        this.tqId = tqId;
    }
    public String getColId() {
        return colId;
    }
    public void setColId(String colId) {
        this.colId = colId;
    }
    public String getMax() {
        return max;
    }
    public void setMax(String max) {
        this.max = max;
    }
    public String getMin() {
        return min;
    }
    public void setMin(String min) {
        this.min = min;
    }
    public long getNullCount() {
        return nullCount;
    }
    public void setNullCount(long nullCount) {
        this.nullCount = nullCount;
    }
    public long getDistinctCount() {
        return distinctCount;
    }
    public void setDistinctCount(long distinctCount) {
        this.distinctCount = distinctCount;
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
    public MetadataColumn getColumn() {
        return column;
    }
    public void setColumn(MetadataColumn column) {
        this.colId = column.getId();
        this.column = column;
    }
    public QuotaTable getTabQuota() {
        return tabQuota;
    }
    public void setTabQuota(QuotaTable tabQuota) {
        this.tqId = tabQuota.getId();
        this.tabQuota = tabQuota;
    }

    /**
     * 得到列稀疏率
     * @return 列稀疏率
     */
    public float getNullRate() {
        if (this.tabQuota.getAllCount()==0) return -1;
        float a = Float.valueOf(this.nullCount+"");
        float b = Float.valueOf(this.tabQuota.getAllCount()+"");
        return a/b;
    }

    /**
     * 得到列稀疏率
     * @return 列稀疏率
     */
    public float getCompressRate() {
        if (this.tabQuota.getAllCount()==0) return -1;
        float a = Float.valueOf(this.distinctCount+"");
        float b = Float.valueOf(this.tabQuota.getAllCount()+"");
        return a/b;
    }
}