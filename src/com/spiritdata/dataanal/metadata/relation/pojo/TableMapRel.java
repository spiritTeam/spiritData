package com.spiritdata.dataanal.metadata.relation.pojo;

import java.sql.Timestamp;
import com.spiritdata.framework.core.model.BaseObject;

/**
 * 实体表|元数据映射关联信息
 * 对应持久化中数据库的表为SA_MD_TABMAP_REL
 * @author mht, wh
 */
/**
 * @author admi
 *
 */
public class TableMapRel extends BaseObject {
    private static final long serialVersionUID = 6654644065903338171L;

    private String id; //对照表Id
    private String mdMId; //元数据模式表Id
    private int ownerType; //模式所对应的所有者类型（1=注册用户;2=非注册用户(session)）
    private String ownerId; //所有者标识（可能是用户id，也可能是SessionID）
    private String tableName; //数据表名称
    private Integer tableType; //表类型：1=积累表；2=临时表
    private String tableDescn; //表说明
    private Timestamp CTime; //本记录创建时间，也是表实体创建时间

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getMdMId() {
        return mdMId;
    }
    public void setMdMId(String mdMId) {
        this.mdMId = mdMId;
    }
    public int getOwnerType() {
        return ownerType;
    }
    public void setOwnerType(int ownerType) {
        this.ownerType = ownerType;
    }
    public String getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
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
    public Timestamp getCTime() {
        return CTime;
    }
    public void setCTime(Timestamp cTime) {
        CTime = cTime;
    }
}