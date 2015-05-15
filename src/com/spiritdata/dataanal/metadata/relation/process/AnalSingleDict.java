package com.spiritdata.dataanal.metadata.relation.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.spiritdata.dataanal.exceptionC.Dtal0404CException;
import com.spiritdata.dataanal.metadata.enumeration.DataType;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataColSemanteme;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataColumn;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel;
import com.spiritdata.dataanal.metadata.relation.semanteme.SemantemeType;
import com.spiritdata.dataanal.metadata.relation.service.MdBasisService;
import com.spiritdata.dataanal.task.process.TaskProcess;
import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.SystemCache;

public class AnalSingleDict/* implements TaskProcess */{

    /**
     * 单向指标项的分析，包括：
     * 字典项-个数：个数/百分比
     * 字典项-每一数值项：总数/平均数/总占比
     */
//    @Override
    public Map<String, Object> process(Map<String, Object> param) {
        System.out.println("正在AnalSignleDict[分析单项字典指标]中执行！！！");
        if (param==null) throw new Dtal0404CException(new NullPointerException("任务执行必须设定参数！"));
        if (param.get("pType")==null)  throw new Dtal0404CException(new NullPointerException("参数中没有“参数类型pType”的数据！"));
        if (!((String)param.get("pType")).toLowerCase().equals("metadata")) new Dtal0404CException(new IllegalArgumentException("参数中必须指定参数类型pType是元数据id[metadata]"));
        if (param.get("mid")==null)  throw new Dtal0404CException(new NullPointerException("参数中没有“数据id”信息！"));

        ServletContext sc = (ServletContext)SystemCache.getCache(FConstants.SERVLET_CONTEXT).getContent();//通过此获得Spring中定义的对象，这种方法似乎不好

        //得到元数据信息，得到元数据的字典语义列
        MdBasisService mdbServcie = (MdBasisService)WebApplicationContextUtils.getWebApplicationContext(sc).getBean("mdBasisService");
        MetadataModel mm = mdbServcie.getMetadataModeWithColSemanteme((String) param.get("mid"));
        String tableName = mm.getTableName();
        String colName = "";
        Map<MetadataColumn, MetadataColSemanteme> dictColM = new HashMap<MetadataColumn, MetadataColSemanteme>(); //字典列的列名，以及对应的列的字典组Id
        List<MetadataColumn> numCols = new ArrayList<MetadataColumn>(); //数值列的列名，包整型和实数
        List<MetadataColSemanteme> mcsL = null;
        List<String> fieldL = new ArrayList<String>();//列选择器字符串
        Map<String, String> titleM = new HashMap<String, String>();//列描述对象，为生成title描述
        for (MetadataColumn mc: mm.getColumnList()) {
            if (mc.isPk()) continue;
            //找字典项
            mcsL = mc.getColSemList();
            if (mcsL==null||mcsL.size()==0) continue;
            boolean isDict = false;
            for (MetadataColSemanteme mcs: mcsL) {
                if (mcs.getSemantemeType()==SemantemeType.DICT) {
                    dictColM.put(mc, mcs);
                    isDict = true;
                    break;
                }
            }
            if (isDict) continue;
            //数值列
            DataType colDT = DataType.getDataType(mc.getColumnType());
            if (colDT==DataType.DOUBLE||colDT==DataType.LONG||colDT==DataType.INTEGER) {
                colName = mc.getColumnName();
                colName = "count("+colName+") as COUNT_"+colName
                        +", sum("+colName+") as SUM_"+colName
                        +", avg("+colName+") as AVG_"+colName
                        +", max("+colName+") as MAX_"+colName
                        +", min("+colName+") as MIN_"+colName;
                fieldL.add(colName);
                titleM.put("COUNT_"+mc.getColumnName(), "");
                titleM.put("SUM_"+mc.getColumnName(), "");
                titleM.put("AVG_"+mc.getColumnName(), "");
                titleM.put("MAX_"+mc.getColumnName(), "");
                titleM.put("MIN_"+mc.getColumnName(), "");
                numCols.add(mc);
            }
        }
        //数据分析
        DataSource dataSource = (BasicDataSource)WebApplicationContextUtils.getWebApplicationContext(sc).getBean("dataSouce");
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        boolean autoCommitFlag = true;
        try {
            conn = dataSource.getConnection();
            autoCommitFlag = conn.getAutoCommit();
            conn.setAutoCommit(false);
            //数据统计
            String tempStr = "count(*) allCount";
            if (numCols.size()>0) {
                
            }
        } catch (Exception e) {
            throw new Dtal0404CException(e);
        } finally {
            if (conn!=null) {
                try {
                    conn.rollback();      
                    conn.setAutoCommit(autoCommitFlag);
                } catch (SQLException sqlE) {
                    sqlE.printStackTrace();
                }
            }
            try { if (rs!=null) {rs.close();rs = null;} } catch (Exception e) {e.printStackTrace();} finally {rs = null;};
            try { if (ps!=null) {ps.close();ps = null;} } catch (Exception e) {e.printStackTrace();} finally {ps = null;};
            try { if (conn!=null) {conn.close(); conn = null;} } catch (Exception e) {e.printStackTrace();} finally {conn = null;};
        }
        //获得总数
        
        return null;
    }
}