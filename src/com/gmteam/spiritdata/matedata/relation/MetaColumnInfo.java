package com.gmteam.spiritdata.matedata.relation;
/** 
 * @author 
 * @version  
 * 类说明 
 */
public class MetaColumnInfo {
    private Integer columnIndex;
    private String columnName;
    private String columnType;
    private String pk;
    public String getPk() {
        return pk;
    }
    public void setPk(String pk) {
        this.pk = pk;
    }
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
    
}
