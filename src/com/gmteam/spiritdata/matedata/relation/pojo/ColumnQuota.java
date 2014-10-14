package com.gmteam.spiritdata.matedata.relation.pojo;

import java.sql.Timestamp;

import com.gmteam.framework.core.model.BaseObject;

/**
 * 表中的列的指标信息
 * 对应持久化中数据库的表为SA_MD_COLQUOTA
 * @author wh, mht
 */
public class ColumnQuota extends BaseObject {
    private static final long serialVersionUID = 7021542418630072318L;

    private String cqId; //实体表指标Id
    private String tqId; //实体表指标Id
    private String colId; //列描述Id
    private String max; //列指标——最大值
    private String min; //列指标——最小值
    private long nullCount; //列指标——空值数
    private long distinctCount; //列指标——单值数

    private Timestamp cTime; //本记录创建时间
    private Timestamp lmTime; //本记录最后修改时间
    //以上信息对应数据库中的信息
    private MetaDataColumn column; //本列指标对应的列描述信息
    private TabQuota tabQuota; //本列指标对应的表指标信息

    public String getCqId() {
        return cqId;
    }
    public void setCqId(String cqId) {
        this.cqId = cqId;
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
    public MetaDataColumn getColumn() {
        return column;
    }
    public void setColumn(MetaDataColumn column) {
        this.column = column;
    }
    public TabQuota getTabQuota() {
        return tabQuota;
    }
    public void setTabQuota(TabQuota tabQuota) {
        this.tabQuota = tabQuota;
    }

    /**
     * 根据元数据列信息，计算最大值，主要是用于数据格式转换
     * @return 最大值
     */
    public Object getMaxValue() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 根据元数据列信息，计算最小值，主要是用于数据格式转换
     * @return 最小值
     */
    public Object getMinValue() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 得到数据范围描述的字符串
     * @return 数据范围描述字符串，如果找不到对应的数据类型，则返回null
     */
    public String getValueRange() {
        if (column==null) return null;
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 得到列稀疏率
     * @return 列稀疏率
     */
    public float getNullRate() {
        if (this.tabQuota.getAllCount()==0) return -1;
        return this.nullCount/this.tabQuota.getAllCount();
    }

    /**
     * 得到列稀疏率
     * @return 列稀疏率
     */
    public float getCompressRate() {
        if (this.tabQuota.getAllCount()==0) return -1;
        return this.distinctCount/this.tabQuota.getAllCount();
    }
}