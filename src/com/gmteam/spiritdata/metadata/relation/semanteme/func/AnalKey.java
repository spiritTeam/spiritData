package com.gmteam.spiritdata.metadata.relation.semanteme.func;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.stereotype.Component;

import com.gmteam.framework.FConstants;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataColumn;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;
import com.gmteam.spiritdata.metadata.relation.pojo.QuotaColumn;
import com.gmteam.spiritdata.metadata.relation.pojo.QuotaTable;
import com.gmteam.spiritdata.metadata.relation.semanteme.AnalTable;
import com.gmteam.spiritdata.metadata.relation.service.MdQuotaService;
import com.gmteam.spiritdata.util.SequenceUUID;
import com.gmteam.framework.core.cache.SystemCache;
import com.gmteam.framework.util.FileNameUtils;
import com.gmteam.framework.util.JsonUtils;

/**
 * 主键分析器
 * @author wangxia
 */

@Component
public class AnalKey implements AnalTable {
    @Resource
    private BasicDataSource dataSource;
    @Resource
    private MdQuotaService mdQuotaService;

    /**
     * 分析某一个表主键。
     * 分析方法，查看指标表，看是否有主键可能性，若类型为String或Integer则可能性更大，float可能性%50，Data可能性*10%。
     * 若没有单列主键，则查双列，三列，直到查到为止。
     * 分析结构以json的形式存储在文件中，便于以后查找。
     * @param tableName 表名称
     * @param md 元数据信息
     * @return 是一个Map<String, Float>，其中String是列名，float是主键可能性
     */
    @Override
    public Map<String, Double> scanOneTable(String tableName, MetadataModel mm) throws Exception {
        if (mm.getColumnList()==null||mm.getColumnList().size()==0) return null;
        //先分析指标表
        QuotaTable qt = mdQuotaService.getQuotaInfo(tableName, mm.getId());
        if (qt==null) {//为空，则重新计算主键
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                conn = dataSource.getConnection();
                //对表进行指标统计
                ps = conn.prepareStatement("select count(*) from "+tableName);
                rs = ps.executeQuery();
                qt = new QuotaTable();
                qt.setMdMId(mm.getId());
                qt.setTmoId("_tempAnalKey");
                qt.setTableName(tableName);
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

                    qt.addColumn(qc);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try { if (rs!=null) {rs.close();rs = null;} } catch (Exception e) {e.printStackTrace();} finally {rs = null;};
                try { if (ps!=null) {ps.close();ps = null;} } catch (Exception e) {e.printStackTrace();} finally {ps = null;};
                try { if (conn!=null) {conn.close();conn = null;} } catch (Exception e) {e.printStackTrace();} finally {conn = null;};
            }
        }
        //按指标分析
        if (qt.getColQuotaList()==null||qt.getColQuotaList().size()==0) return null;
        Map<String, Double> ret  = new HashMap<String, Double>();
        Double one = new Double("1");
        for (QuotaColumn qc: qt.getColQuotaList()) {
            if (one.equals(qc.getCompressRate())) {
                if (mm.getColumnByColId(qc.getColId()).getColumnType().equalsIgnoreCase("String")) {
                    ret.put(mm.getColumnByColId(qc.getColId()).getColumnName(), one);
                } else if (mm.getColumnByColId(qc.getColId()).getColumnType().equalsIgnoreCase("Number")) {
                    ret.put(mm.getColumnByColId(qc.getColId()).getColumnName(), one);
                } else if (mm.getColumnByColId(qc.getColId()).getColumnType().equalsIgnoreCase("Double")) {
                    ret.put(mm.getColumnByColId(qc.getColId()).getColumnName(), one*0.5);
                } else if (mm.getColumnByColId(qc.getColId()).getColumnType().equalsIgnoreCase("Date")) {
                    ret.put(mm.getColumnByColId(qc.getColId()).getColumnName(), one*0.1);
                }
            }
        }
        if (ret.size()==0) { //说明一列不能满足要求，主键可能是多列，这个以后再加
            
        }
        //写json文件，此方法目前为测试方法，今后把他变为一个更好用的包
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("test", "Test");
        //写文件
        String root = (String)(SystemCache.getCache(FConstants.APPOSPATH)).getContent();
        //文件格式：analData\{用户名}\MM_{模式Id}\tab_{TABId}\keyAnal.json
        String storeFile = FileNameUtils.concatPath(root, mm.getOwnerId()+File.separator+"MM_"+mm.getId()+File.separator+"tab_"+tableName+File.separator+"keyAnal.json");
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(storeFile);
            fileOutputStream.write((JsonUtils.beanToJson(jsonMap)).getBytes()); 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream!=null) {
                try {fileOutputStream.close();}catch(IOException e) {e.printStackTrace();}
            }
        }

        return ret;
    }
}