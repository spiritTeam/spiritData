package com.gmteam.spiritdata.metadata.relation.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;

import com.gmteam.framework.core.dao.mybatis.MybatisDAO;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataColumn;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;
import com.gmteam.spiritdata.metadata.relation.pojo.QuotaColumn;
import com.gmteam.spiritdata.metadata.relation.pojo.QuotaTable;
import com.gmteam.spiritdata.metadata.relation.pojo.TableMapOrg;
import com.gmteam.spiritdata.util.SequenceUUID;

/**
 * 计算表指标的服务，此服务直接从数据库中读取信息，而不从session中读取数据
 * @author wh
 */
public class MdQuotaService {
    @Resource
    private BasicDataSource dataSource;
    @Resource
    private MdEntityTableService tmoService;
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
     * @return 表指标信息
     * @throws Exception 当表名对应多个映射关系时
     */
    public QuotaTable caculateQuota(String tableName) throws Exception {
        Map<String, String> param = new HashMap<String, String>();
        param.put("tableName", tableName);
        List<TableMapOrg> l = tmoService.getList(param);
        if (l==null) return null;
        if (l.size()>1) throw new Exception("此表名对应多个表映射关系，无法处理！");

        return caculateQuota(l.get(0));
    }

    /**
     * 根据表映射关系，计算指标计信息
     * @param tmo 表映射关系
     * @return 表指标信息
     */
    public QuotaTable caculateQuota(TableMapOrg tmo) throws Exception {
        if (tmo.getId()==null||tmo.getId().equals("")) throw new IllegalArgumentException("参数中映射表Id字段为空，必须指定映射表Id");
        if (tmo.getMdMId()==null||tmo.getMdMId().equals("")) throw new IllegalArgumentException("参数中元数据模式mdMId字段为空，必须指定元数据模式Id");
        if (tmo.getTableName()==null||tmo.getTableName().equals("")) throw new IllegalArgumentException("参数中表名称字段tableName字段为空，必须指定表名称");
        MetadataModel mm = mdBasisService.getMetadataMode(tmo.getMdMId());
        if (mm==null) return null;

        return caculateQuota(mm, tmo.getTableName(), tmo.getMdMId());
    }

    /**
     * 根据元数据模似和数据表，得到表指标信息
     * @param mm 元数据模式
     * @param tableName 表名称
     * @return 表指标信息
     * @throws Exception
     */
    public QuotaTable caculateQuota(MetadataModel mm, String tableName) throws Exception {
        if (mm.getId()==null||mm.getId().equals("")) throw new IllegalArgumentException("必须指定元数据模式Id");
        TableMapOrg tmo = tmoService.getTableMapOrg(mm.getId(), tableName);
        String tmoId = tmo.getId();

        return caculateQuota(mm, tableName, tmoId);
    }

    /*
     * 计算表指标信息
     * @param mm 元数据信息
     * @param tableName 表名称
     * @param tmoId 映射表记录id
     * @throws SQLException
     */
    private QuotaTable caculateQuota(MetadataModel mm, String tableName, String tmoId) throws SQLException, Exception {
        if (mm.getColumnList()==null||mm.getColumnList().size()==0) return null;

        QuotaTable qt = new QuotaTable();
        Connection conn = null;
        PreparedStatement ps = null;
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
            qt.setLmTime(new Timestamp(new Date().getTime()));
            if (rs.next()) {
                qt.setAllCount(rs.getLong(1));
            } else {
                qt.setAllCount(-1);
            }
            qt.setId(SequenceUUID.getUUIDSubSegment(4));
            rs.close();rs=null;
            ps.close();ps=null;
            //对列进行指标统计
            for (MetadataColumn mc: mm.getColumnList()) {
                String fieldName = mc.getColumnName();
                String fieldType = mc.getColumnType();
                QuotaColumn qc = new QuotaColumn();
                qc.setColId(mc.getId());
                qc.setTqId(qt.getId());
                qc.setId(SequenceUUID.getUUIDSubSegment(4));
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
                    qc.setMax(rs.getString(1));
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
                    qc.setMin(rs.getString(1));
                } else {
                    qc.setMin("");
                }
                rs.close();rs=null;
                ps.close();ps=null;

                qt.addColumn(qc);
            }
            //计算一次存储一次，首先检查是否已经存在
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("tmoId", qt.getTmoId());
            List<QuotaTable> l =qtDao.queryForList(param);
            if (l==null||l.size()==0) {//新增
                qtDao.insert(qt);
            } else if (l.size()==1) {//修改
                QuotaTable _qt = l.get(0);
                qt.setId(_qt.getId());
                qtDao.update(qt);
            }
            List<QuotaColumn> qcl = qt.getColQuotaList();
            List<QuotaColumn> lc = null;
            if (qcl!=null&&qcl.size()>0) {
                for (QuotaColumn qc: qt.getColQuotaList()) {
                    param.clear();
                    param.put("tqId", qc.getTqId());
                    param.put("colId", qc.getColId());
                    lc = qcDao.queryForList(param);
                    if (lc==null||lc.size()==0) {//新增
                        qcDao.insert(qc);
                    } else if (lc.size()==1) {//修改
                        qcDao.update(qc);
                    }
                }
            }
            conn.commit();
            conn.setAutoCommit(autoCommitFlag);
        } catch (Exception e) {
            if (conn!=null) {
                conn.rollback();
                conn.setAutoCommit(autoCommitFlag);
            }
            e.printStackTrace();
        } finally {
            try { if (rs!=null) {rs.close();rs = null;} } catch (Exception e) {e.printStackTrace();} finally {rs = null;};
            try { if (ps!=null) {ps.close();ps = null;} } catch (Exception e) {e.printStackTrace();} finally {ps = null;};
            try { if (conn!=null) {conn.close();conn = null;} } catch (Exception e) {e.printStackTrace();} finally {conn = null;};
        }
        return qt;
    }

}