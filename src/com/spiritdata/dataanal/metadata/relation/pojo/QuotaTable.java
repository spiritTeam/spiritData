package com.spiritdata.dataanal.metadata.relation.pojo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.spiritdata.framework.core.model.BaseObject;

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

    private Timestamp CTime; //本记录创建时间，也是表实体创建时间
    private Timestamp lmTime; //本记录最后修改时间

    private List<QuotaColumn> quotaColList; //列指标列表

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
        if (this.quotaColList!=null&&this.quotaColList.size()>0) {
            for (QuotaColumn qc: this.quotaColList) {
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
    public Timestamp getCTime() {
        return CTime;
    }
    public void setCTime(Timestamp cTime) {
        CTime = cTime;
    }
    public Timestamp getLmTime() {
        return lmTime;
    }
    public void setLmTime(Timestamp lmTime) {
        this.lmTime = lmTime;
    }
    public List<QuotaColumn> getQuotaColList() {
        return quotaColList;
    }
    public void setQuotaColList(List<QuotaColumn> quotaColList) throws Exception {
        if (quotaColList!=null&&quotaColList.size()>0) {
            for (QuotaColumn qc: quotaColList) this.addQuotaCol(qc);
        }
    }

    /**
     * 插入列指标对象到列指标列表
     * @param cq 被插入的列指标对象，其中tqId/tabQuota可以省略，本对象的tqId/tabQuota将填入参数cq
     * @throws Exception
     */
    public void addQuotaCol(QuotaColumn qc) throws Exception {
        if (qc==null) return;
        if (qc.getId().equals(null)) throw new Exception("列指标Id不能为空");

        MetadataColumn mdc=qc.getColumn();
        if (this.quotaColList==null) this.quotaColList=new ArrayList<QuotaColumn>();
        for (QuotaColumn c: this.quotaColList) {
            MetadataColumn _c=c.getColumn();
            if (_c.getId().equals(mdc.getId())) throw new Exception("在列指标列表中已经有与所添加对象[列Id]相同的列指标对象，不能重复添加！");
            if (_c.getTitleName().equals(mdc.getTitleName())) throw new Exception("在列描述列表中已经有与所添加对象[列意义名称]相同的列指标对象，不能重复添加！");
            if (_c.getColumnName().equals(mdc.getColumnName())) throw new Exception("在列描述列表中已经有与所添加对象[列名称]相同的列指标对象，不能重复添加！");
        }
        qc.setTabQuota(this);
        this.quotaColList.add(qc);
    }

    /**
     * 根据列意义名称得到列描述对象
     * @param titleName 意义名称
     * @return 列描述对象
     */
    public QuotaColumn getQuotaColByTName(String titleName) {
        QuotaColumn param = new QuotaColumn();
        MetadataColumn mc = new MetadataColumn(); 
        mc.setTitleName(titleName);
        param.setColumn(mc);
        return getQuotaCol(param);
    }

    /**
     * 根据列名称得到列指标对象
     * @param columnName 列名称
     * @return 列指标对象
     */
    public QuotaColumn getQuotaColByCName(String columnName) {
        QuotaColumn param = new QuotaColumn();
        MetadataColumn mc = new MetadataColumn(); 
        mc.setColumnName(columnName);
        param.setColumn(mc);
        return getQuotaCol(param);
    }

    /**
     * 根据列描述Id得到列指标对象，此方法不常用
     * @param colId 列描述Id
     * @return 列指标对象
     */
    public QuotaColumn getQuotaColByColId(String colId) {
        QuotaColumn param = new QuotaColumn(); 
        param.setColId(colId);
        return getQuotaCol(param);
    }

    /**
     * 根据列指标Id得到列指标对象，此方法不常用
     * @param cqId 列指标Id
     * @return 列指标对象
     */
    public QuotaColumn getQuotaColById(String cqId) {
        QuotaColumn param = new QuotaColumn(); 
        param.setId(cqId);
        return getQuotaCol(param);
    }

    private QuotaColumn getQuotaCol(QuotaColumn cq) {
        if (this.quotaColList==null||this.quotaColList.size()==0||cq==null) return null;
        QuotaColumn ret = null;
        MetadataColumn mdc=cq.getColumn();
        for (QuotaColumn c: this.quotaColList) {
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