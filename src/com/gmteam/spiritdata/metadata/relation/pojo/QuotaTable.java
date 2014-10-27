package com.gmteam.spiritdata.metadata.relation.pojo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.gmteam.framework.core.model.BaseObject;

/**
 * 实体表指标信息
 * 对应持久化中数据库的表为SA_MD_TABQUOTA
 * @author wh, mht
 */
public class QuotaTable extends BaseObject {
    private static final long serialVersionUID = -6314845278741671296L;

    private String id; //实体表指标Id
    private String mdMId; //元数据模式表Id
    private String tmoId; //实体表对应的对照表Id
    private String tableName; //实体表名称

    private long allCount; //实体表指标——表总行数
    private Timestamp laTime; //实体表指标——最新用于分析的访问时间，只对积累表有意义

    private Timestamp cTime; //本记录创建时间，也是表实体创建时间
    private Timestamp lmTime; //本记录最后修改时间

    private List<QuotaColumn> colQuotaList; //列指标列表

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
        if (this.colQuotaList!=null&&this.colQuotaList.size()>0) {
            for (QuotaColumn qc: this.colQuotaList) {
                qc.setTqId(id);
            }
        }
    }
    public String getMdMId() {
        return mdMId;
    }
    public void setMdMId(String mdMId) {
        this.mdMId = mdMId;
    }
    public String getTmoId() {
        return tmoId;
    }
    public void setTmoId(String tmoId) {
        this.tmoId = tmoId;
    }
    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public long getAllCount() {
        return allCount;
    }
    public void setAllCount(long allCount) {
        this.allCount = allCount;
    }
    public Timestamp getLaTime() {
        return laTime;
    }
    public void setLaTime(Timestamp laTime) {
        this.laTime = laTime;
    }
    public Timestamp getcTime() {
        return cTime;
    }
    public void setcTime(Timestamp cTime) {
        this.cTime = cTime;
    }
    public Timestamp getLmTime() {
        return lmTime;
    }
    public void setLmTime(Timestamp lmTime) {
        this.lmTime = lmTime;
    }
    public List<QuotaColumn> getColQuotaList() {
        return colQuotaList;
    }
    public void setColQuotaList(List<QuotaColumn> colQuotaList) throws Exception {
        if (colQuotaList!=null&&colQuotaList.size()>0) {
            for (QuotaColumn qc: colQuotaList) this.addColumn(qc);
        }
    }

    /**
     * 插入列指标对象到列指标列表
     * @param cq 被插入的列指标对象，其中tqId/tabQuota可以省略，本对象的tqId/tabQuota将填入参数cq
     * @throws Exception
     */
    public void addColumn(QuotaColumn qc) throws Exception {
        if (qc==null) return;
        if (qc.getId().equals(null)) throw new Exception("列指标Id不能为空");
        if (qc.getColumn()==null) throw new Exception("列描述不能为空");

        MetadataColumn mdc=qc.getColumn();
        if (this.colQuotaList==null) this.colQuotaList=new ArrayList<QuotaColumn>();
        for (QuotaColumn c: this.colQuotaList) {
            MetadataColumn _c=c.getColumn();
            if (_c.getId().equals(mdc.getId())) throw new Exception("在列指标列表中已经有与所添加对象[列Id]相同的列指标对象，不同重复添加！");
            if (_c.getTitleName().equals(mdc.getTitleName())) throw new Exception("在列描述列表中已经有与所添加对象[列意义名称]相同的列指标对象，不同重复添加！");
            if (_c.getColumnName().equals(mdc.getColumnName())) throw new Exception("在列描述列表中已经有与所添加对象[列名称]相同的列指标对象，不同重复添加！");
        }
        this.colQuotaList.add(qc);
    }

    /**
     * 根据列意义名称得到列描述对象
     * @param titleName 意义名称
     * @return 列描述对象
     */
    public QuotaColumn getColumnByTName(String titleName) {
        QuotaColumn param = new QuotaColumn();
        MetadataColumn mc = new MetadataColumn(); 
        mc.setTitleName(titleName);
        param.setColumn(mc);
        return getColQuota(param);
    }

    /**
     * 根据列名称得到列指标对象
     * @param columnName 列名称
     * @return 列指标对象
     */
    public QuotaColumn getColumnByCName(String columnName) {
        QuotaColumn param = new QuotaColumn();
        MetadataColumn mc = new MetadataColumn(); 
        mc.setColumnName(columnName);
        param.setColumn(mc);
        return getColQuota(param);
    }

    /**
     * 根据列描述Id得到列指标对象，此方法不常用
     * @param colId 列描述Id
     * @return 列指标对象
     */
    public QuotaColumn getColQuotaByColId(String colId) {
        QuotaColumn param = new QuotaColumn(); 
        param.setColId(colId);
        return getColQuota(param);
    }

    /**
     * 根据列指标Id得到列指标对象，此方法不常用
     * @param cqId 列指标Id
     * @return 列指标对象
     */
    public QuotaColumn getColQuotaById(String cqId) {
        QuotaColumn param = new QuotaColumn(); 
        param.setId(cqId);
        return getColQuota(param);
    }

    private QuotaColumn getColQuota(QuotaColumn cq) {
        if (this.colQuotaList==null||this.colQuotaList.size()==0||cq==null) return null;
        QuotaColumn ret = null;
        MetadataColumn mdc=cq.getColumn();
        for (QuotaColumn c: this.colQuotaList) {
            if (c.getColId().equals(cq.getColId())||c.getId().equals(cq.getId())) {
                ret = c;
                break;
            }
            if (c.getColumn()!=null&&cq.getColumn()!=null) {
                MetadataColumn _c=c.getColumn();
                if (_c.getTitleName().equals(mdc.getTitleName())||_c.getColumnName().equals(mdc.getColumnName())) {
                    ret = c;
                    break;
                }
            }
        }
        return ret;
    }
}