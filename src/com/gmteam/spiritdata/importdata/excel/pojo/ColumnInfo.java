package com.gmteam.spiritdata.importdata.excel.pojo;

import com.gmteam.framework.core.model.BaseObject;
/** 
 * @author mht
 * @version  
 * 类说明 
 */
@SuppressWarnings("serial")
public class ColumnInfo extends BaseObject{
    private String id;
    private String tableId;
    private Integer columnIndex;
    private String columnName;
    private String columnType;
    private String pk;
    
    public Integer getColumnIndex() {
        return columnIndex;
    }
    public void setColumnIndex(Integer columnIndex) {
        this.columnIndex = columnIndex;
    }
    public String getColumnName() {
        return columnName;
    }
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
    public String getColumnType() {
        return columnType;
    }
    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }
    public String getPk() {
        return pk;
    }
    public void setPk(String pk) {
        this.pk = pk;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTableId() {
        return tableId;
    }
    public void setTableId(String tableId) {
        this.tableId = tableId;
    }
}