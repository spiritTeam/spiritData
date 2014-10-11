package com.gmteam.spiritdata.matedata.pojo;

import com.gmteam.framework.core.model.BaseObject;

/** 
 * @author mht
 * @version  
 * 类说明  统计信息类
 * 其中当列为string类型的时候，max和min的
 * 取值范围是长度，当列类型为date的时候，
 * max和min的取值范围是毫秒数
 * 当列类型数值之类的，max和min的
 * 取值范围是数值，无论结果是什么，都转成String类型
 */
public class StatisticsInfo extends BaseObject {
    private String id;
    private String max;
    private String min;
    /**列名*/
    private String columnName;
    /**列的数据类型*/
    private String columnType;
    /**distinct后的个数，(用于算出压缩率)*/
    private Integer distinctCount;
    /** null 的个数(用于算出稀疏率)*/
    private Integer nullCount;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getColumnName() {
        return columnName;
    }
    public void setColumnName(String columnName) {
        this.columnName = columnName;
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
    public Integer getDistinctCount() {
        return distinctCount;
    }
    public void setDistinctCount(Integer distinctCount) {
        this.distinctCount = distinctCount;
    }
    public Integer getNullCount() {
        return nullCount;
    }
    public void setNullCount(Integer nullCount) {
        this.nullCount = nullCount;
    }
    public String getColumnType() {
        return columnType;
    }
    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }
    
}
