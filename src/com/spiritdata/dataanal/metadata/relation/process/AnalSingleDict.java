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

import com.spiritdata.dataanal.SDConstants;
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

public class AnalSingleDict implements TaskProcess {

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

        Map<String, Object> ret = new HashMap<String, Object>();
        Map<String, Object> sysRd = new HashMap<String, Object>();
        sysRd.put("JsonDCode", SDConstants.JDC_MD_SDICT);
        ret.put("sysResultData", sysRd);

        //得到元数据信息，得到元数据的字典语义列
        MdBasisService mdbServcie = (MdBasisService)WebApplicationContextUtils.getWebApplicationContext(sc).getBean("mdBasisService");
        MetadataModel mm = mdbServcie.getMetadataModeWithColSemanteme((String) param.get("mid"));
        String colName = "";
        Map<MetadataColumn, MetadataColSemanteme> dictColM = new HashMap<MetadataColumn, MetadataColSemanteme>(); //字典列的列名，以及对应的列的字典组Id
        List<MetadataColSemanteme> mcsL = null;
        List<String> fieldL = new ArrayList<String>();//列选择器字符串
        Map<String, String> titleM = new HashMap<String, String>();//列描述对象，为生成title描述
        String tempStr = "", groupSql = "";

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
                tempStr = "count("+colName+") as COUNT_"+colName
                        +", sum("+colName+") as SUM_"+colName
                        +", avg("+colName+") as AVG_"+colName
                        +", max("+colName+") as MAX_"+colName
                        +", min("+colName+") as MIN_"+colName;
                fieldL.add(tempStr);
                titleM.put("COUNT_"+colName, "个数");
                titleM.put("SUM_"+colName, "总量");
                titleM.put("AVG_"+colName, "平均值");
                titleM.put("MAX_"+colName, "最大值");
                titleM.put("MIN_"+colName, "最小值");
            }
        }
        if (dictColM!=null&&dictColM.size()>0) {
            sysRd.put("resultType", 2);
            return ret;
        }
        //数据分析
        Map<String, Object> sumRow = new HashMap<String, Object>();
        Map<String, Object> groupRow = null;
        List<Map<String, Object>> groupTdList = null;
        Map<String, Object> groupMap = new HashMap<String, Object>();
        List<Map<String, Object>> groupList = new ArrayList<Map<String, Object>>();

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
            tempStr = "count(*) allCount";
            titleM.put("allCount", "总个数");
            for (String s: fieldL) {
                tempStr += ","+s;
            }
            tempStr = "select "+tempStr+" from "+mm.getTableName();
            ps = conn.prepareStatement(tempStr);
            rs = ps.executeQuery();
            if (rs.next()) {
                for (String key: titleM.keySet()) {
                    sumRow.put(key, rs.getObject(key));
                }
            }
            rs.close();
            //分项统计
            for (MetadataColumn mc: dictColM.keySet()) {
                groupMap.put("mc", mc);
                MetadataColSemanteme mcs = dictColM.get(mc);
                groupMap.put("mcs", mcs);
                colName = mc.getColumnName();
                groupSql = tempStr + " group by "+colName+" order by count("+colName+") desc";
                rs = ps.executeQuery(groupSql);
                groupTdList = new ArrayList<Map<String, Object>>();
                while (rs.next()) {
                    groupRow = new HashMap<String, Object>();
                    for (String key: titleM.keySet()) {
                        groupRow.put(key, rs.getObject(key));
                    }
                    groupTdList.add(groupRow);
                }
                groupMap.put("groupData", groupTdList);
                groupList.add(groupMap);
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

        sysRd.put("resultType", 1);
        List<Map<String, Object>> userDataList = new ArrayList<Map<String, Object>>();//元数据信息的数组
        for (Map<String, Object> gM: groupList) {
            userDataList.add(_getJsonDTable_SD(gM, sumRow));
        }
        ret.put("userResultData", userDataList);

        return ret;
    }

    /*
     * 按jsonD的table格式，得到元数据信息，包括统计信息
     * @param groupMap 元数据信息
     * @param qt 元数据指标信息
     * @return 转换完的数据
     */
    private Map<String, Object> _getJsonDTable_SD(Map<String, Object> groupMap, Map<String, Object> sumRow) {
        Map<String, Object> ret = null;
        return ret;
    }
}