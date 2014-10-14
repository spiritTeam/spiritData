package com.gmteam.spiritdata.matedata.relation.pojo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.gmteam.framework.core.model.BaseObject;

/**
 * 元数据模式
 * 对应持久化中数据库的表为SD_MD_TABMODULE
 * @author wh, mht
 */
public class MetadataModel extends BaseObject {
    private static final long serialVersionUID = 946755758804895450L;

    private String mdMId; //模式Id
    private int ownerType; //模式所对应的所有者类型（1=注册用户;2=非注册用户(session)）
    private String ownerId; //所有者标识（可能是用户id，也可能是SessionID）
    private Timestamp cTime; //记录创建时间
    private String tableName; //积累表名称

    private List<MetadataColumn> columnList; //列描述信息列表

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

    public Timestamp getcTime() {
        return cTime;
    }

    public void setcTime(Timestamp cTime) {
        this.cTime = cTime;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<MetadataColumn> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<MetadataColumn> columnList) {
        this.columnList = columnList;
    }

    /**
     * 插入列描述对象到列描述列表
     * @param mdc 被插入的对象，其中Mid可以省略，本对象的Mid将填入传来的参数mdc
     * @throws Exception 若对象重复
     */
    public void addColumn(MetadataColumn mdc) throws Exception{
        if (mdc==null) return;
        if (mdc.getColId().equals(null)) throw new Exception("列Id不能为空");
        for (MetadataColumn c: this.columnList) {
            if (c.getColId().equals(mdc.getColId())) throw new Exception("在列描述列表中已经有与所添加对象[列Id]相同的列描述对象，不同重复添加！");
            if (c.getTitleName().equals(mdc.getTitleName())) throw new Exception("在列描述列表中已经有与所添加对象[列意义名称]相同的列描述对象，不同重复添加！");
            if (c.getColumnName().equals(mdc.getColumnName())) throw new Exception("在列描述列表中已经有与所添加对象[列名称]相同的列描述对象，不同重复添加！");
        }
        if (this.columnList==null) this.columnList = new ArrayList<MetadataColumn>();
        this.columnList.add(mdc);
    }

    /**
     * 根据列意义名称得到列描述对象
     * @param titleName 意义名称
     * @return 列描述对象
     */
    public MetadataColumn getColumnByTName(String titleName) {
        MetadataColumn param = new MetadataColumn(); 
        param.setTitleName(titleName);
        return getColumn(param);
    }

    /**
     * 根据列名称得到列描述对象
     * @param columnName 名称
     * @return 列描述对象
     */
    public MetadataColumn getColumnByCName(String columnName) {
        MetadataColumn param = new MetadataColumn(); 
        param.setColumnName(columnName);
        return getColumn(param);
    }

    /**
     * 根据列Id得到列描述对象，此方法不常用
     * @param cId 列Id
     * @return 列描述对象
     */
    public MetadataColumn getColumnByColId(String colId) {
        MetadataColumn param = new MetadataColumn(); 
        param.setColId(colId);
        return getColumn(param);
    }

    private MetadataColumn getColumn(MetadataColumn mdc) {
        if (this.columnList==null||this.columnList.size()==0||mdc==null) return null;
        MetadataColumn ret = null;
        for (MetadataColumn c: this.columnList) {
            if (c.getColId().equals(mdc.getColId())||c.getTitleName().equals(mdc.getTitleName())||c.getColumnName().equals(mdc.getColumnName())) {
                ret = c;
                break;
            }
        }
        return ret;
    }
}