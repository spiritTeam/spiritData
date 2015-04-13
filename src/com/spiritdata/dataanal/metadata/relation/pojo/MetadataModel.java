package com.spiritdata.dataanal.metadata.relation.pojo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spiritdata.framework.core.model.BaseObject;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.dataanal.exceptionC.Dtal0001CException;

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
    private Timestamp CTime; //记录创建时间
    private String tableName; //积累表名称
    private String desc; //模式说明
    //以上信息对应数据库中的信息
    private List<MetadataColumn> columnList; //列描述信息列表

    //表名称处理，根据Imp对应表中的内容，计数调整
    private String titleName; ////模式名称：此名称在数据库中对应的字段若有值，则这个值是手工设定的，若这个值为空，需要从sa_imp_tablog_rel表中得到最有可能的名称(头两个)
    public Map<String, Integer> titleMap;//页签名称列表，是装载Session时转载的

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
        if (this.columnList!=null&&this.columnList.size()>0) {
            for (MetadataColumn mc: this.columnList) {
                mc.setMdMId(id);
            }
        }
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
    public Timestamp getCTime() {
        return CTime;
    }
    public void setCTime(Timestamp cTime) {
        CTime = cTime;
    }
    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public String getTitleName() {
        return titleName;
    }
    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }
    public List<MetadataColumn> getColumnList() {
        return columnList;
    }
    public void setColumnList(List<MetadataColumn> columnList) {
        if (columnList!=null&&columnList.size()>0) {
            for (MetadataColumn mc: columnList) this.addColumn(mc);
        }
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    /**
     * 插入列描述对象到列描述列表
     * @param mdc 被插入的对象，其中Mid可以省略，本对象的Mid将填入传来的参数mdc
     * @throws Exception 若对象重复
     */
    public void addColumn(MetadataColumn mdc) {
        if (mdc==null) return;
        if (StringUtils.isNullOrEmptyOrSpace(mdc.getColumnType())) throw new Dtal0001CException(new IllegalArgumentException("列描述必须设置类型columnType"));
        if (StringUtils.isNullOrEmptyOrSpace(mdc.getTitleName())) throw new Dtal0001CException(new IllegalArgumentException("列描述必须设置列标题TitleName"));

        if (this.columnList==null) this.columnList = new ArrayList<MetadataColumn>();
        for (MetadataColumn c: this.columnList) {
            if (mdc.getId()!=null&&(c.getId().equals(mdc.getId()))) throw new Dtal0001CException("在列描述列表中已经有与所添加对象[列Id]相同的列描述对象，不能重复添加！");
            if (mdc.getTitleName()!=null&&(c.getTitleName().equals(mdc.getTitleName()))) throw new Dtal0001CException("在列描述列表中已经有与所添加对象[列意义名称]相同的列描述对象，不能重复添加！");
            if (mdc.getColumnName()!=null&&(c.getColumnName().equals(mdc.getColumnName()))) throw new Dtal0001CException("在列描述列表中已经有与所添加对象[列名称]相同的列描述对象，不能重复添加！");
        }
        mdc.setMdModel(this);
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
    public Map<String, Object> isSame(MetadataModel mm, int compareOwner) {
        Map<String, Object> ret = new HashMap<String, Object>();
        if (this.columnList==null) throw new Dtal0001CException("本模式无列描述信息，无法比较");
        if (mm.getColumnList()==null)  throw new Dtal0001CException("被比较模式无列描述信息，无法比较");
        if (compareOwner==1&&!this.ownerId.equals(mm.getOwnerId())) {
            ret.put("type", "0");
            ret.put("message", "所有者不一致");
            return ret;
        }
        MetadataColumn mc, _mc;
        Map<String, String> alterTable = new HashMap<String, String>(); //需要对table进行调整的
        String tagStr = "";
        int sameCount = 0;
        if (this.columnList.size()==mm.getColumnList().size()) {
            for (int i=0; i<this.columnList.size(); i++) {
                mc=this.columnList.get(i);
                for (int j=0; j<mm.getColumnList().size(); j++) {
                    if (tagStr.indexOf(","+j)!=-1) continue;
                    _mc=mm.getColumnList().get(j);
                    if (mc.getTitleName().equals(_mc.getTitleName())&&(
                        mc.getColumnType().equals(_mc.getColumnType())
                        ||(mc.getColumnType().equals("Integer")&&_mc.getColumnType().equals("Double"))
                        ||(_mc.getColumnType().equals("Integer")&&mc.getColumnType().equals("Double"))
                        ||(mc.getColumnType().equals("Long")&&_mc.getColumnType().equals("Double"))
                        ||(_mc.getColumnType().equals("Long")&&mc.getColumnType().equals("Double"))
                    )) {
                        sameCount++;
                        tagStr+=","+j;
                        if ((mc.getColumnType().equals("Integer")||(mc.getColumnType().equals("Long")))&&_mc.getColumnType().equals("Double")) {
                            alterTable.put(mc.getColumnName(), "Double");
                        }
                        break;
                    }
                }
            }
        }
        if (sameCount==this.columnList.size()) {
            ret.put("type", "1");
            ret.put("message", "所有列描述一致");
            if (alterTable.size()>0) ret.put("alterTable", alterTable);
            return ret;
        }
        ret.put("type", "-1");
        ret.put("message", "不一致");
        return ret;
    }
}