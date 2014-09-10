package com.gmteam.importdata.excel.pojo;

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
    private Integer titleIndex;
    private String titleName;
    private String titleType;
    private String pk;
    
    public Integer getTitleIndex() {
        return titleIndex;
    }
    public void setTitleIndex(Integer titleIndex) {
        this.titleIndex = titleIndex;
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
    public String getTitleName() {
        return titleName;
    }
    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }
    public String getTitleType() {
        return titleType;
    }
    public void setTitleType(String titleType) {
        this.titleType = titleType;
    }
}