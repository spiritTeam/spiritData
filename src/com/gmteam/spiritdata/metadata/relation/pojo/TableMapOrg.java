package com.gmteam.spiritdata.metadata.relation.pojo;

import java.sql.Timestamp;
import com.gmteam.framework.core.model.BaseObject;

/**
 * 实体表|元数据映射关联信息
 * 对应持久化中数据库的表为SA_MD_TABMAP_ORG
 * @author mht, wh
 */
public class TableMapOrg extends BaseObject {
    private static final long serialVersionUID = 6654644065903338171L;

    private String id; //对照表Id
    private String ownerId; //所有者标识（可能是用户id，也可能是SessionID）
    private String mdMId; //元数据模式表Id
    private String tableName; //数据表名称
    private Integer tableType; //表类型：1=积累表；2=临时表
    private String tableDescn; //表说明
    private Timestamp cTime; //本记录创建时间，也是表实体创建时间

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

    public String getMdMId() {
        return mdMId;
    }

    public void setMdMId(String mdMId) {
        this.mdMId = mdMId;
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

    public String getTableDescn() {
        return tableDescn;
    }

    public void setTableDescn(String tableDescn) {
        this.tableDescn = tableDescn;
    }

    public Timestamp getcTime() {
        return cTime;
    }

    public void setcTime(Timestamp cTime) {
        this.cTime = cTime;
    }
    
}