package com.spiritdata.dataanal.metadata.relation.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;

import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.dataanal.exceptionC.Dtal0201CException;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataColumn;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel;
import com.spiritdata.dataanal.metadata.relation.pojo.QuotaColumn;
import com.spiritdata.dataanal.metadata.relation.pojo.QuotaTable;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataTableMapRel;

/**
 * 计算表指标的服务，此服务直接从数据库中读取信息，而不从session中读取数据
 * @author wh
 */
public class MdQuotaService {
    @Resource
    private BasicDataSource dataSource;
    @Resource
    private TableMapService tmoService;
    @Resource
    private MdBasisService mdBasisService;
    @Resource(name="defaultDAO")
    private MybatisDAO<QuotaTable> qtDao;
    @Resource(name="defaultDAO")
    private MybatisDAO<QuotaColumn> qcDao;

    @PostConstruct
    public void initParam() {
        qtDao.setNamespace("quotaTable");
        qcDao.setNamespace("quotaColumn");
    }

    /**
     * 根据表名称，计算指标计信息
     * @param tableName 表名称
     * @param ownerId 所有者标识
     * @return 表指标信息
     * @throws Exception 当表名对应多个映射关系时
     */
    public QuotaTable caculateQuota(String tableName, String ownerId) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("tableName", tableName);
        param.put("ownerId", ownerId);
        List<MetadataTableMapRel> l;
        try {
            l = tmoService.getList(param);
        } catch (Exception e) {
            throw new Dtal0201CException(e);
        }
        if (l==null) return null;
        if (l.size()>1) throw new Dtal0201CException("此表名对应多个表映射关系，无法处理！");

        return caculateQuota(l.get(0));
    }

    /**
     * 根据表映射关系，计算指标计信息
     * @param tmo 表映射关系
     * @return 表指标信息
     */
    public QuotaTable caculateQuota(MetadataTableMapRel tmo) {
        if (StringUtils.isNullOrEmptyOrSpace(tmo.getId())) throw new Dtal0201CException(new IllegalArgumentException("参数中映射表Id字段为空，必须指定映射表Id"));
        if (StringUtils.isNullOrEmptyOrSpace(tmo.getMdMId())) throw new Dtal0201CException(new IllegalArgumentException("参数中映射表元数据模式mdMId字段为空，必须指定元数据模式Id"));
        if (StringUtils.isNullOrEmptyOrSpace(tmo.getTableName())) throw new Dtal0201CException(new IllegalArgumentException("参数中映射表表名称字段tableName字段为空，必须指定表名称"));

        MetadataModel mm;
        try {
            mm = mdBasisService.getMetadataMode(tmo.getMdMId());
        } catch (Exception e) {
            throw new Dtal0201CException(e);
        }
        if (mm==null) return null;
        if (StringUtils.isNullOrEmptyOrSpace(mm.getOwnerId())) throw new Dtal0201CException(new IllegalArgumentException("参数中映射表元数据模式mdMId所对应的元数据信息中没有所有者信息，无法处理"));

        return caculateQuota(mm, tmo.getTableName(), tmo.getId());
    }

    /**
     * 根据元数据模型和数据表，得到表指标信息
     * @param mm 元数据模式
     * @param tableName 表名称
     * @return 表指标信息
     * @throws Exception
     */
    public QuotaTable caculateQuota(MetadataModel mm, String tableName) {
        if (StringUtils.isNullOrEmptyOrSpace(mm.getId())) throw new Dtal0201CException(new IllegalArgumentException("必须指定元数据模式Id"));
        if (StringUtils.isNullOrEmptyOrSpace(mm.getOwnerId())) throw new Dtal0201CException(new IllegalArgumentException("元数据信息中没有所有者信息，无法处理"));

        MetadataTableMapRel tmo;
        try {
            tmo = tmoService.getTableMapOrg(mm.getId(), tableName);
        } catch (Exception e) {
            throw new Dtal0201CException(e);
        }
        String tmoId = tmo.getId();

        return caculateQuota(mm, tableName, tmoId);
    }

    /*
     * 计算表指标信息，注意这里的分析会冲掉以前对改表分析的内容，用不用把表的分析存储在jsonD中呢？？？？
     * @param mm 元数据信息
     * @param tableName 表名称
     * @param tmoId 映射表记录id
     * @throws SQLException
     */
    private QuotaTable caculateQuota(MetadataModel mm, String tableName, String tmoId) {
        if (mm.getColumnList()==null||mm.getColumnList().size()==0) return null;

        QuotaTable qt = null;
        Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        boolean autoCommitFlag = true;
        try {
            conn = dataSource.getConnection();
            autoCommitFlag = conn.getAutoCommit();
            conn.setAutoCommit(false);
            //对表进行指标统计
            ps = conn.prepareStatement("select count(*) from "+tableName);
            rs = ps.executeQuery();
            qt = new QuotaTable();
            qt.setMdMId(mm.getId());
            qt.setTmoId(tmoId);
            qt.setTableName(tableName);
            qt.setLmTime(new Timestamp(System.currentTimeMillis()));
            if (rs.next()) {
                qt.setAllCount(rs.getLong(1));
            } else {
                qt.setAllCount(-1);
            }
            qt.setId(SequenceUUID.getPureUUID());
            rs.close();rs=null;
            ps.close();ps=null;
            //对列进行指标统计
            for (MetadataColumn mc: mm.getColumnList()) {
                String fieldName = mc.getColumnName();
                String fieldType = mc.getColumnType();
                QuotaColumn qc = new QuotaColumn();
                qc.setColId(mc.getId());
                qc.setTqId(qt.getId());
                qc.setId(SequenceUUID.getPureUUID());
                qc.setColumn(mc);
                //distinct
                String sql = "select count(distinct "+fieldName+") from "+tableName;
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();
                if (rs.next()) {
                    qc.setDistinctCount(rs.getLong(1));
                } else {
                    qc.setDistinctCount(-1);
                }
                rs.close();rs=null;
                ps.close();ps=null;
                //null
                sql = "select count(*) from "+tableName+" where "+fieldName+" is null";
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();
                if (rs.next()) {
                    qc.setNullCount(rs.getLong(1));
                } else {
                    qc.setNullCount(-1);
                }
                rs.close();rs=null;
                ps.close();ps=null;
                //最大值
                if (fieldType.equalsIgnoreCase("String")) {
                    sql = "select max(length("+fieldName+")) from "+tableName;
                } else {
                    sql = "select max("+fieldName+") from "+tableName;
                }
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();
                if (rs.next()) {
                    qc.setMax(rs.getString(1)==null?"":rs.getString(1));
                } else {
                    qc.setMax("");
                }
                rs.close();rs=null;
                ps.close();ps=null;
                //最小值
                if (fieldType.equalsIgnoreCase("String")) {
                    sql = "select min(length("+fieldName+")) from "+tableName;
                } else {
                    sql = "select min("+fieldName+") from "+tableName;
                }
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();
                if (rs.next()) {
                    qc.setMin(rs.getString(1)==null?"":rs.getString(1));
                } else {
                    qc.setMin("");
                }
                rs.close();rs=null;
                ps.close();ps=null;

                qt.addQuotaCol(qc);
            }
            //计算一次存储一次，首先检查是否已经存在
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("tmoId", qt.getTmoId());
            List<QuotaTable> l = qtDao.queryForList(param);
            if (l==null||l.size()==0) {//新增
                qtDao.insert(qt);
            } else if (l.size()==1) {//修改
                QuotaTable _qt = l.get(0);
                qt.setId(_qt.getId());
                qtDao.update(qt);
            }
            List<QuotaColumn> qcl = qt.getQuotaColList();
            ps = conn.prepareStatement("update sa_md_colquota SET max=?, min=?, nullCount=?, distinctCount=? where cId=? and tqId=?");
            ps2 = conn.prepareStatement("insert into sa_md_colquota ( id, cId, tqId, max, min, nullCount, distinctCount ) values ( ?, ?, ?, ?, ?, ?, ? )");
            if (qcl!=null&&qcl.size()>0) {
                for (QuotaColumn qc: qt.getQuotaColList()) {
                    ps.clearParameters();
                    ps.setString(1, qc.getMax());
                    ps.setString(2, qc.getMin());
                    ps.setLong(3, qc.getNullCount());
                    ps.setLong(4, qc.getDistinctCount());
                    ps.setString(5, qc.getColId());
                    ps.setString(6, qc.getTqId());
                    int updateSize = ps.executeUpdate();
                    if (updateSize<1) {
                        ps2.clearParameters();
                        ps2.setString(1, qc.getId());
                        ps2.setString(2, qc.getColId());
                        ps2.setString(3, qc.getTqId());
                        ps2.setString(4, qc.getMax());
                        ps2.setString(5, qc.getMin());
                        ps2.setLong(6, qc.getNullCount());
                        ps2.setLong(7, qc.getDistinctCount());
                        ps2.executeUpdate();
                    }
                }
            }
            conn.commit();
            conn.setAutoCommit(autoCommitFlag);
        } catch (Exception e) {
            if (conn!=null) {
                try {
                    conn.rollback();
                    conn.setAutoCommit(autoCommitFlag);
                } catch (SQLException sqlE) {
                    sqlE.printStackTrace();
                }
            }
            throw new Dtal0201CException(e);
        } finally {
            try { if (rs!=null) {rs.close();rs = null;} } catch (Exception e) {e.printStackTrace();} finally {rs = null;};
            try { if (ps!=null) {ps.close();ps = null;} } catch (Exception e) {e.printStackTrace();} finally {ps = null;};
            try { if (ps2!=null) {ps2.close();ps2 = null;} } catch (Exception e) {e.printStackTrace();} finally {ps2 = null;};
            try { if (conn!=null) {conn.close(); conn = null;} } catch (Exception e) {e.printStackTrace();} finally {conn = null;};
        }
        return qt;
    }

    /**
     * 根据表名，得到该表的指标信息
     * @param tableName 表名
     * @param mm 元数据模式对象
     * @return 表指标信息
     */
    public QuotaTable getQuotaTable(String tableName, MetadataModel mm) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("tmId", mm.getId());
        param.put("tableName", tableName);
        QuotaTable ret = qtDao.getInfoObject(param);
        if (ret!=null) {
            param.clear();
            param.put("tqId", ret.getId());
            List<QuotaColumn> qcl = qcDao.queryForList(param);
            if (qcl!=null&&qcl.size()>0) {
                for (int i=qcl.size()-1; i>=0; i--) {
                    QuotaColumn qc = qcl.get(i);
                    MetadataColumn mc = mm.getColumnByColId(qc.getColId());
                    if (mc==null) qcl.remove(i);
                    else qc.setColumn(mc);
                }
            }
            if (qcl!=null&&qcl.size()>0) {
                try {
                    ret.setQuotaColList(qcl);
                } catch (Exception e) {
                    throw new Dtal0201CException("得到表["+tableName+"]的列指标信息", e);
                }
            }
            else ret = null;
        }
        return ret;
    }

    /**
     * 根据元数据信息，得到元数据的指标信息（针对积累表）
     * @param mm 元数据信息 
     * @return 指标信息
     */
    public QuotaTable getQuotaTable(MetadataModel mm) {
        QuotaTable _qt = qtDao.getInfoObject("getInfoByMdMId", mm.getId());
        if (_qt==null) return null;
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("tqId", _qt.getId());
        List<QuotaColumn> qcL = qcDao.queryForList(m);
        if (qcL!=null&&qcL.size()>0) {
            for (QuotaColumn qc: qcL) {
                try {
                    qc.setColumn(mm.getColumnByColId(qc.getColId()));
                    _qt.addQuotaCol(qc);
                    
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (_qt.getQuotaColList()==null||_qt.getQuotaColList().size()==0) return null;
        return _qt;
    }
}