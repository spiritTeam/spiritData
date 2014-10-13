package com.gmteam.spiritdata.matedata.relation.pojo;

import java.sql.Timestamp;

import com.gmteam.framework.core.model.BaseObject;

/**
 * 元数据列描述
 * 对应持久化中数据库的表为SD_MD_TABMODULE
 * @author wh, mht
 */
public class MetaDataColumn extends BaseObject {
    private static final long serialVersionUID = 2522485255389052683L;

    private String mdCId; //列Id
    private String mdMId; //模式Id
    private String columnName; //列名称，此名称为英文，用于创建表时作为字段名称
    private String titleName; //列显示/标题名称，此名称为数据源中列的标题(中文/英文)
    private int columnIndex; //列顺序名称
    private String columnType; //列字段类型，目前可以为字符串[String]，数字型[Double(为兼容excel)]，日期型[timestamp]（类似数据库中字段的数据类型）
    private int isPk; //是否是主键，1=是；2=不是
    private Timestamp cTime; //记录创建时间
    public String getMdCId() {
        return mdCId;
    }
    public void setMdCId(String mdCId) {
        this.mdCId = mdCId;
    }
    public String getMdMId() {
        return mdMId;
    }
    public void setMdMId(String mdMId) {
        this.mdMId = mdMId;
    }
    public String getColumnName() {
        return columnName;
    }
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
    public String getTitleName() {
        return titleName;
    }
    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }
    public int getColumnIndex() {
        return columnIndex;
    }
    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }
    public String getColumnType() {
        return columnType;
    }
    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }
    public int getIsPk() {
        return isPk;
    }
    public void setIsPk(int isPk) {
        this.isPk = isPk;
    }
    public Timestamp getcTime() {
        return cTime;
    }
    public void setcTime(Timestamp cTime) {
        this.cTime = cTime;
    }
}