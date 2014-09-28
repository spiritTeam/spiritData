package com.gmteam.spiritdata.importdata.excel.pojo;

import org.springframework.stereotype.Component;

import com.gmteam.framework.core.model.BaseObject;
/** 
 * @author mht
 * @version  
 * 类说明 
 */
@Component
@SuppressWarnings("serial")
public class TableInfo extends BaseObject{
    private String id;
    private String tableName;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}