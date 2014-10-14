package com.gmteam.spiritdata.matedata.relation.pojo;

import java.sql.Timestamp;

import com.gmteam.framework.core.model.BaseObject;

/**
 * 元数据列描述
 * 对应持久化中数据库的表为SD_MD_TABMODULE
 * @author wh, mht
 */
public class MetadataColumn extends BaseObject {
    private static final long serialVersionUID = 2522485255389052683L;

    private String colId; //列Id
    private String mdMId; //模式Id
    private String columnName; //列名称，此名称为英文，用于创建表时作为字段名称
    private String titleName; //列显示/标题名称，此名称为数据源中列的标题(中文/英文)
    private int columnIndex; //列顺序名称
    private String columnType; //列字段类型，目前可以为字符串[String]，数字型[Double(为兼容excel)]，日期型[timestamp]（类似数据库中字段的数据类型）
    private int isPk; //是否是主键，1=是；2=不是
    private Timestamp cTime; //记录创建时间
    //以上信息对应数据库中的信息
    private MetadataModel mdModel; //本列描述对应的元数据模式
    private MetadataColSemanteme colSem; //本列对应的语义信息

    public MetadataModel getMdModel() {
        return mdModel;
    }
    public void setMdModel(MetadataModel mdModel) {
        this.mdMId = mdModel.getMdMId();
        this.mdModel = mdModel;
    }
    public String getColId() {
        return colId;
    }
    public void setColId(String colId) {
        this.colId = colId;
    }
    public String getMdMId() {
        return mdMId;
    }
    /**
     * 设置元数据模式Id
     * @param mdMId 元数据模式Id
     * @throws Exception 若本实体已经设置元数据模式，但元数据模式Id与参数Id不同
     */
    public void setMdMId(String mdMId) throws Exception {
        if (this.mdModel==null) this.mdMId = mdMId;
        else if (this.mdModel.getMdMId().equals(mdMId)) this.mdMId = mdMId;
        else throw new Exception("所设置的元数据模式Id与已有的元数据模式不同！");
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
    public MetadataColSemanteme getColSem() {
        return colSem;
    }
    public void setColSem(MetadataColSemanteme colSem) {
        colSem.setColumn(this);
        this.colSem = colSem;
    }
}