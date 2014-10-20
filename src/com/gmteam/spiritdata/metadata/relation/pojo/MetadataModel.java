package com.gmteam.spiritdata.metadata.relation.pojo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gmteam.framework.core.model.BaseObject;

/**
 * 元数据模式
 * 对应持久化中数据库的表为SD_MD_TABMODULE
 * @author wh, mht
 */
public class MetadataModel extends BaseObject {
    private static final long serialVersionUID = 946755758804895450L;

    private String id; //模式Id
    private int ownerType; //模式所对应的所有者类型（1=注册用户;2=非注册用户(session)）
    private String ownerId; //所有者标识（可能是用户id，也可能是SessionID）
    private Timestamp cTime; //记录创建时间
    private String tableName; //积累表名称
    private String memo; //模式说明

    //以上信息对应数据库中的信息
    private List<MetadataColumn> columnList; //列描述信息列表

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    /**
     * 插入列描述对象到列描述列表
     * @param mdc 被插入的对象，其中Mid可以省略，本对象的Mid将填入传来的参数mdc
     * @throws Exception 若对象重复
     */
    public void addColumn(MetadataColumn mdc) throws Exception{
        if (mdc==null) return;
        if (mdc.getId().equals(null)) throw new Exception("列Id不能为空");
        for (MetadataColumn c: this.columnList) {
            if (c.getId().equals(mdc.getId())) throw new Exception("在列描述列表中已经有与所添加对象[列Id]相同的列描述对象，不同重复添加！");
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
        param.setId(colId);
        return getColumn(param);
    }

    private MetadataColumn getColumn(MetadataColumn mdc) {
        if (this.columnList==null||this.columnList.size()==0||mdc==null) return null;
        MetadataColumn ret = null;
        for (MetadataColumn c: this.columnList) {
            if (c.getId().equals(mdc.getId())||c.getTitleName().equals(mdc.getTitleName())||c.getColumnName().equals(mdc.getColumnName())) {
                ret = c;
                break;
            }
        }
        return ret;
    }

    /**
     * 比较两个模式是否相同
     * @param mm 被比较模式
     * @param compareOwner 是否比较所有者；=0不比较；=1比较
     * @return 是一个Map<String, String>，key包括type和message；
     *   type=0，message=所有者不一致
     *   type=1，message=所有列描述一致，语义不比较
     *   type=2，message=所有列描述一致，语义也一致
     *   type=3，message=本模式包含[列描述一致，语义不比较]
     *   type=4，message=本模式包含[列描述一致，语义也一致]
     *   type=5，message=被比较模式包含[列描述一致，语义不比较]
     *   type=6，message=被比较模式包含[列描述一致，语义也一致]
     *   type=-1，message=不一致
     */
    //目前只实现type=-1,0,1
    public Map<String, String> isSame(MetadataModel mm, int compareOwner) throws Exception {
        Map<String, String> ret = new HashMap<String, String>();
        if (this.columnList==null) throw new Exception("本模式无列描述信息，无法比较");
        if (mm.getColumnList()==null)  throw new Exception("被比较模式无列描述信息，无法比较");
        if (compareOwner==1&&!this.ownerId.equals(mm.getOwnerId())) {
            ret.put("type", "0");
            ret.put("message", "所有者不一致");
            return ret;
        }
        MetadataColumn mc, _mc;
        int sameCount = 0;
        if (this.columnList.size()==mm.getColumnList().size()) {
            for (int i=0; i<this.columnList.size(); i++) {
                mc=this.columnList.get(i);
                for (int j=0; j<mm.getColumnList().size(); j++) {
                    _mc=mm.getColumnList().get(j);
                    if (mc.getTitleName().equals(_mc.getTitleName())&&mc.getColumnType().equals(_mc.getColumnType())) {
                        sameCount++;
                        break;
                    }
                }
            }
        }
        if (sameCount==this.columnList.size()) {
            ret.put("type", "1");
            ret.put("message", "所有列描述一致");
            return ret;
        }
        ret.put("type", "-1");
        ret.put("message", "不一致");
        return ret;
    }
}