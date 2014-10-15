package com.gmteam.spiritdata.metadata.relation.pojo;

import java.util.Date;

import com.gmteam.framework.core.model.BaseObject;

/**
 * 
 * @author mht, wh
 */
public class TableMapOrg extends BaseObject {
    private static final long serialVersionUID = 6654644065903338171L;

    private String id;

    private String ownerId;

    private String tmId;

    private String tableName;

    private Integer tableType;

    private Date cTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getTmId() {
        return tmId;
    }

    public void setTmId(String tmId) {
        this.tmId = tmId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getTableType() {
        return tableType;
    }

    public void setTableType(Integer tableType) {
        this.tableType = tableType;
    }

    public Date getcTime() {
        return cTime;
    }

    public void setcTime(Date cTime) {
        this.cTime = cTime;
    }
    
}