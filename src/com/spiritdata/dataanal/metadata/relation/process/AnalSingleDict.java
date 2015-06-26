package com.spiritdata.dataanal.metadata.relation.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import com.spiritdata.jsonD.model.JsonDAtomData;

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
        String mid = (String) param.get("mid");
        MetadataModel mm = mdbServcie.getMetadataModeWithColSemanteme(mid);
        String colName = "";
        Map<MetadataColumn, MetadataColSemanteme> dictColM = new HashMap<MetadataColumn, MetadataColSemanteme>(); //字典列的列名，以及对应的列的字典组Id
        List<MetadataColSemanteme> mcsL = null;
        List<String> fieldL = new ArrayList<String>();//列选择器字符串
        Map<String, String> titleM = new LinkedHashMap<String, String>();//列描述对象，为生成title描述
        String tempColStr = "", groupSql = "";

        for (MetadataColumn mc: mm.getColumnList()) {
            if (mc.isPk()) continue;
            //找字典项
            mcsL = mc.getColSemList();
            if (mcsL!=null && mcsL.size()>0){ //判断是否字典项
                boolean isDict = false;
                for (MetadataColSemanteme mcs: mcsL) {
                    if (mcs.getSemantemeType()==SemantemeType.DICT) {
                        dictColM.put(mc, mcs);
                        isDict = true;
                        break;
                    }
                }
                if (isDict) continue;
            }
            //数值列
            DataType colDT = DataType.getDataType(mc.getColumnType());
            if (colDT==DataType.DOUBLE||colDT==DataType.LONG||colDT==DataType.INTEGER) {
                colName = mc.getColumnName();
                tempColStr = "count("+colName+") as COUNT_"+colName
                        +", sum("+colName+") as SUM_"+colName
                        +", avg("+colName+") as AVG_"+colName
                        +", max("+colName+") as MAX_"+colName
                        +", min("+colName+") as MIN_"+colName;
                fieldL.add(tempColStr);
                titleM.put("COUNT_"+colName, mc.getTitleName()+"个数");
                titleM.put("SUM_"+colName, mc.getTitleName()+"总量");
                titleM.put("AVG_"+colName, mc.getTitleName()+"平均值");
                titleM.put("MAX_"+colName, mc.getTitleName()+"最大值");
                titleM.put("MIN_"+colName, mc.getTitleName()+"最小值");
            }
        }
        if (dictColM!=null&&dictColM.size()>0) {
            sysRd.put("resultType", 2);
        }
        //数据分析
        Map<String, Object> sumRow = new HashMap<String, Object>();
        Map<String, Object> groupRow = null;
        List<Map<String, Object>> groupTdList = null;
        List<Map<String, Object>> groupList = new ArrayList<Map<String, Object>>();

        DataSource dataSource = (BasicDataSource)WebApplicationContextUtils.getWebApplicationContext(sc).getBean("dataSource");
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        boolean autoCommitFlag = true;
        try {
            conn = dataSource.getConnection();
            autoCommitFlag = conn.getAutoCommit();
            conn.setAutoCommit(false);
            //数据统计
            tempColStr = "count(*) allCount";
            titleM.put("allCount", "总个数");
            for (String s: fieldL) {
                tempColStr += ","+s;
            }
            String countSql = "select "+tempColStr+" from "+mm.getTableName();
            ps = conn.prepareStatement(countSql);
            rs = ps.executeQuery();
            if (rs.next()) {
                for (String key: titleM.keySet()) {
                    sumRow.put(key, rs.getObject(key));
                }
            }
            rs.close();
            //分项统计
            for (MetadataColumn mc: dictColM.keySet()) {
                Map<String, Object> groupMap = new HashMap<String, Object>();
                groupMap.put("mc", mc);
                MetadataColSemanteme mcs = dictColM.get(mc);
                groupMap.put("mcs", mcs);
                colName = mc.getColumnName();               
                groupSql = "select "+tempColStr+","+colName+" from "+mm.getTableName() + " group by "+colName+" order by count("+colName+") desc";
                rs = ps.executeQuery(groupSql);
                groupTdList = new ArrayList<Map<String, Object>>();
                while (rs.next()) {
                    groupRow = new HashMap<String, Object>();
                    groupRow.put(colName, rs.getObject(colName));
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

        Map<String, Object> userDataM = new HashMap<String, Object>();//元数据信息的数组
        for (Map<String, Object> gM: groupList) {
            userDataM.put(((MetadataColumn)gM.get("mc")).getColumnName(),_getJsonDTable_SD(gM, sumRow,titleM));
        }
        if (userDataM.size()>0) {
            sysRd.put("resultType", 1);
            Map<String, Object> t = new HashMap<String, Object>();
            //表数据描述
            Map<String, Object> tbInfoM = new HashMap<String, Object>();
            JsonDAtomData _dataElement = new JsonDAtomData("_mdMId", "string", mm.getId());
            _dataElement.setAtomData("_tableName", "string", mm.getTableName());
            tbInfoM.putAll(_dataElement.toJsonMap());
            t.put("tbInfo", tbInfoM);
            //字典项统计数据
            t.put("dictData", userDataM);
            
            ret.put("userResultData", t);
        } else sysRd.put("resultType", 2);

        return ret;
    }

    /*
     * 按jsonD的table格式，得到元数据信息，包括统计信息
     * @param groupMap 元数据信息
     * @param qt 元数据指标信息
     * @return 转换完的数据
     */
    private Map<String, Object> _getJsonDTable_SD(Map<String, Object> groupMap, Map<String, Object> sumRow, Map<String, String> titleM) {
        Map<String, Object> ret = new HashMap<String, Object>();
        MetadataColumn mc = (MetadataColumn)groupMap.get("mc");
        List<Map<String, Object>> groupTdList = (List<Map<String, Object>>)groupMap.get("groupData");
        
        
        String colName = mc.getColumnName();
        Map<String, Object> tableInfoM = new HashMap<String, Object>();
        ret.put("colInfo", tableInfoM);
        //tableInfoM.put("tableName", mc.getMdModel().getTableName());
        tableInfoM.put("titleName", mc.getTitleName());
        tableInfoM.put("colName", colName);
        //表数据处理
        Map<String, Object> tableM = new HashMap<String, Object>();
        //title
        Map<String, String> _titleM = new HashMap<String, String>();
        _titleM.put("category", mc.getTitleName()); 
        _titleM.put("count", "数量"); 
        _titleM.put("percent(count)", "百分比"); 
        //加入数值类型统计信息列名
        if(titleM!=null && titleM.size()>0){
            Iterator<String> iterTitleM = titleM.keySet().iterator();
            while(iterTitleM.hasNext()){
                String keyTitleM = (String)iterTitleM.next();
                String valTitleM = (String)titleM.get(keyTitleM);
                _titleM.put(keyTitleM, valTitleM); 
            }
        }

        tableM.put("titles", _titleM);
        //dataList
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        tableM.put("dataList", dataList);
        for(Map<String, Object> groupRow : groupTdList){
            Map<String, Object> rowM = new HashMap<String, Object>();
            rowM.put("category", groupRow.get(colName));
            rowM.put("count", groupRow.get("allCount"));
            //百分比
            float aRowCount = groupRow.get("allCount")==null?0:Float.parseFloat(groupRow.get("allCount").toString());
            float colCount =  sumRow.get("allCount")==null?0:Float.parseFloat(sumRow.get("allCount").toString());
            float percent = aRowCount * 100 / colCount;
            rowM.put("percent(count)", percent);
            //加入数值类型统计信息列值
            if(titleM!=null && titleM.size()>0){
                Iterator<String> iterTitleM = titleM.keySet().iterator();
                while(iterTitleM.hasNext()){
                    String keyTitleM = (String)iterTitleM.next();
                    Object valTitleM = (Object)groupRow.get(keyTitleM);
                    rowM.put(keyTitleM, valTitleM);
                }
            }
            dataList.add(rowM);            
        }
        ret.put("tableData", tableM);
        return ret;
    }
}