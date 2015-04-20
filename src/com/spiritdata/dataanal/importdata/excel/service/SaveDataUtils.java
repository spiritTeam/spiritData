package com.spiritdata.dataanal.importdata.excel.service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spiritdata.dataanal.exceptionC.Dtal0103CException;
import com.spiritdata.dataanal.importdata.excel.ExcelConstants;
import com.spiritdata.dataanal.importdata.excel.pojo.SheetTableInfo;
import com.spiritdata.dataanal.importdata.excel.util.PoiParseUtils;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataColumn;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel;
import com.spiritdata.framework.core.dao.dialect.Dialect;
import com.spiritdata.framework.core.dao.dialect.DialectFactory;

/**
 * 保存数据到数据表
 * @author wh
 */
public abstract class SaveDataUtils {

    /**
     * 保存临时表信息，并对字段长度进行判断，以便顺利插入数据
     * @param sti Sheet中的表结构区域信息
     * @param sysMm 元数据信息（已在系统注册过的）
     * @param tempTableName 临时表名称
     * @param parse excel解析器
     * @param dataSoource 数据源
     * @return 返回不能作为主键的列，注意，若是组合列，各列间用空格隔开，返回值是Map结构，key={不能是主键原因(目前只有主键超长)}, value={List<String>},目前只对单列主键进行处理。
     */
    protected static Map<String, List<String>> save2TempTable(SheetTableInfo sti, MetadataModel sysMm, String tempTableName, PoiParseUtils parse, DataSource dataSource) {
        if (dataSource==null) throw new IllegalArgumentException("数据源不能为空");
        if (sysMm==null||sysMm.getColumnList()==null||sysMm.getColumnList().size()==0) throw new IllegalArgumentException("元数据模型必须设置，且列信息不能为空");
        if (sti==null||sti.getTitleInfo()==null||sti.getTitleInfo().size()==0) throw new IllegalArgumentException("Sheet中的表结构区域信息必须设置，且表头信息不能为空");
        if (parse==null||parse.getSheetInfo()==null||parse.getSheetInfo().getStiList()==null) throw new IllegalArgumentException("excel解析单元必须设置");
        else {
            boolean isMate = false;
            for (SheetTableInfo _sti: parse.getSheetInfo().getStiList()) {
                if (_sti.equals(sti)) {
                    isMate=true;
                    break;
                }
            }
            if (!isMate) throw new IllegalArgumentException("参数：paras(excel解析单元)必须与参数：sti(表结构区域信息)相匹配");
        }

        //准备SQL语句
        String insertSql = "insert into "+tempTableName+"(#columnSql) values(#valueSql)", columnSql="", valueSql="";
        for (MetadataColumn mc: sysMm.getColumnList()) {
            columnSql+=","+mc.getColumnName();
            valueSql+=",?";
        }
        if (columnSql.length()>0) columnSql=columnSql.substring(1);
        if (valueSql.length()>0) valueSql=valueSql.substring(1);
        insertSql = insertSql.replaceAll("#columnSql", columnSql).replaceAll("#valueSql", valueSql);

        Map<String, List<String>> ret = new HashMap<String, List<String>>();
        //数据插入
        Map<String, Object> titleCol = null;
        //日志信息准备
        int _log_readAllCount/*读取总行数*/, _log_insertOkCount=0/*新增成功行数*/, _log_insertFailCount=0/*新增失败行数*/, _log_ignoreCount=0/*忽略行数*/;
        
        Dialect dialect = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null; //DB元数据获取结果
        boolean autoCommit = false;
        try {
            conn = dataSource.getConnection();
            dialect = DialectFactory.Generator(conn);
            autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(insertSql);

            List<Map<String, Object>> rowData = null;
            _log_readAllCount = sti.getEndY()-sti.getBeginY()+1;
            Object[] paramArray = new Object[sysMm.getColumnList().size()];
            Map<Integer, Object[]> iiInsertData = new HashMap<Integer, Object[]>();//第二次插入时需要的数据
            Map<String, Integer> changeLenColMap = new HashMap<String, Integer>();//需扩容的列列表

            //构造当前表字符串列长度的结构
            Map<String, Integer> strColLenMap = new HashMap<String, Integer>();
            DatabaseMetaData dbMetaData = conn.getMetaData();
            rs = dbMetaData.getColumns(null, null, tempTableName, null);
            if (rs!=null) {//注意这里不考虑非varchar2以外的数据类型，应为所有表都是系统自动建立的
                while (rs.next()) {
                    if (rs.getString("TYPE_NAME").toUpperCase().equals("VARCHAR")) {
                        strColLenMap.put(rs.getString("COLUMN_NAME"), rs.getInt("COLUMN_SIZE"));
                    }
                }
            }

            //第一次Insert
            Integer dbLen;
            int strLen;
            String colName, _tabKeys="", _tabPkName="";
            for (int i=sti.getBeginY(); i<=sti.getEndY(); i++) {
                rowData = parse.readOneRow(i);
                rowData = parse.convert2DataRow(rowData);
                if (parse.isEmptyRow(rowData)) {
                    _log_ignoreCount++;
//                    _log_ignoreMap.put(i, "第"+i+"行为空行。");
                    continue;
                }
                for (int j=0; j<paramArray.length; j++) paramArray[j]=null;

                int _mmDType, _infoDType;
                Object v;
                for (Map<String, Object> cell: rowData) {
                    titleCol = parse.findMatchTitle(cell, sti);
                    if (titleCol!=null) {
                        for (int k=0; k<sysMm.getColumnList().size(); k++) {
                            MetadataColumn mc = sysMm.getColumnList().get(k);
                            _mmDType = ExcelConstants.convert2DataType(mc.getColumnType());
                            _infoDType = -1;
                            if (mc.getTitleName().equals((String)titleCol.get("title"))) {
                                Map<String, Object> kv = (Map<String, Object>)cell.get("transData");
                                _infoDType = (Integer)kv.get("dType");
                                v = null;
                                if (_infoDType==_mmDType) v = kv.get("value");
                                else {
                                    kv = (Map<String, Object>)cell.get("nativeData");
                                    _infoDType = (Integer)kv.get("dType");
                                    if (_infoDType==_mmDType) v = kv.get("value");
                                    else if (_mmDType==ExcelConstants.DATA_TYPE_DOUBLE) {
                                        if (_infoDType==ExcelConstants.DATA_TYPE_INTEGER||_infoDType==ExcelConstants.DATA_TYPE_NUMERIC) {
                                            v = kv.get("value");
                                        }
                                    }
                                }
                                if (_mmDType==ExcelConstants.DATA_TYPE_STRING&&v==null) v=((Map<String, Object>)cell.get("transData")).get("value")+"";
                                paramArray[k]=v;
                            }
                        }
                    }
                }
                boolean canInsert = true;
                for (int j=0; j<paramArray.length; j++) {
                    if (paramArray[j]==null) {
                        canInsert = false;
                        break;
                    }
                }
                if (!canInsert) {
                    _log_ignoreCount++;
//                    _log_ignoreMap.put(i, "第"+i+"行数据与元数据不匹配，行数据为<<>>，元数据为<<>>。");
                    continue;
                }
                //构造不能插入的列
                for (int k=0; k<sysMm.getColumnList().size(); k++) {
                    colName = sysMm.getColumnList().get(k).getColumnName();
                    dbLen = strColLenMap.get(colName);
                    if (dbLen!=null&&sysMm.getColumnList().get(k).getColumnType().equals("String")) {
                        strLen = SaveDataUtils.getStringLen((String)paramArray[i], dialect);
                        if (strLen>dbLen) {
                            if (canInsert) canInsert = false; //阻止本次插入
                            if (iiInsertData.get(i)==null) iiInsertData.put(new Integer(i), paramArray);//缓存此数据
                            if (changeLenColMap.get(colName)==null||strLen>changeLenColMap.get(colName)) changeLenColMap.put(colName, strLen);//为调整字段长度做准备
                        }
                    }
                }
                
                //根据长度判断是否能够插入，这个主要为MySql数据库来处理
                //TODO 注意这里可能要判断列组合，现在先不做处理
                if (!canInsert) {
                    _log_ignoreCount++;
//                    _log_ignoreMap.put(i, "第"+i+"行数据与元数据不匹配，行数据为<<>>，元数据为<<>>。");
                    continue;
                }
                try{
                    for (int j=0; j<paramArray.length; j++) {
                        ps.setObject(j+1, paramArray[j]);
                    }
                    int insertOk = ps.executeUpdate();
                    if (insertOk>0) {
  //                      _log_insertOkCount += insertOk;
                    } else {
    //                    _log_insertFailCount++;
  //                      _log_failMap.put(i,  "第"+i+"行数据新增失败，原因未知！");
                    }
                } catch(SQLException sqlE) {
      //              _log_insertFailCount++;
    //                _log_failMap.put(i,  "第"+i+"行数据新增失败，原因为："+sqlE.getMessage());
                }
            }
            conn.commit();

            //第二次插入前调整主键及各列的长度
            rs.close();
            rs = dbMetaData.getPrimaryKeys(null, null, sysMm.getTableName());
            while (rs.next()){
                _tabKeys +=","+rs.getString("COLUMN_NAME"); //列名
                if (_tabPkName==null) _tabKeys = rs.getString("PK_NAME");//主键名称
            }
            _tabKeys = _tabKeys.substring(1);
            boolean hadDropKey = false, needDropKey=false;
            if (changeLenColMap.size()>0) {
                for (String _colName: changeLenColMap.keySet()) {
                    Integer colLen = changeLenColMap.get(_colName);
                    ps.execute("alter table "+tempTableName+" modify column "+_colName+" varchar("+changeLenColMap.get(_colName)+")"); //临时表
                }
            }
            //第二次插入
            for (Integer lineNum: iiInsertData.keySet()) {
                Object[] iiInsertParam = iiInsertData.get(lineNum);
                try{
                    for (int j=0; j<iiInsertParam.length; j++) {
                        ps.setObject(j+1, iiInsertParam[j]);
                    }
                    int insertOk = ps.executeUpdate();
                    if (insertOk>0) {
  //                      _log_insertOkCount += insertOk;
                    } else {
    //                    _log_insertFailCount++;
  //                      _log_failMap.put(i,  "第"+i+"行数据新增失败，原因未知！");
                    }
                } catch(SQLException sqlE) {
      //              _log_insertFailCount++;
    //                _log_failMap.put(i,  "第"+i+"行数据新增失败，原因为："+sqlE.getMessage());
                }
            }
        } catch (Exception e) {
            throw new Dtal0103CException("数据存入临时表", e);
        } finally {
            try { if (rs!=null) {rs.close();rs = null;} } catch (Exception e) {e.printStackTrace();} finally {rs = null;};
            try { if (ps!=null) {ps.close();ps = null;} } catch (Exception e) {e.printStackTrace();} finally {ps = null;};
            try { if (conn!=null) {conn.commit();conn.setAutoCommit(autoCommit);conn.close();conn = null;} } catch (Exception e) {e.printStackTrace();} finally {conn = null;};
        }

        return ret.size()==0?null:ret;
    }

    /**
     * 保存积累表信息
     * @param em 从Excel中分析出来的元数据信息，注意，这里包括sheet信息
     * @param sysMm 元数据信息（已在系统注册过的），这其中包括积累表信息
     */
    protected static void save2AccumulationTable(SheetTableInfo sti, MetadataModel sysMm, PoiParseUtils parse, DataSource dataSource) {
        if (sysMm==null||sysMm.getColumnList()==null||sysMm.getColumnList().size()==0) throw new IllegalArgumentException("元数据模型必须设置，且列信息不能为空");
        String mainTableName = sysMm.getTableName();
        if (mainTableName==null||mainTableName.equals("")) throw new IllegalArgumentException("元数据模型中必须有积累表名称");
        if (sti==null||sti.getTitleInfo()==null||sti.getTitleInfo().size()==0) throw new IllegalArgumentException("Sheet中的表结构区域信息必须设置，且表头信息不能为空");
        if (parse==null||parse.getSheetInfo()==null||parse.getSheetInfo().getStiList()==null) throw new IllegalArgumentException("excel解析单元必须设置");
        else {
            boolean isMate = false;
            for (SheetTableInfo _sti: parse.getSheetInfo().getStiList()) {
                if (_sti.equals(sti)) {
                    isMate=true;
                    break;
                }
            }
            if (!isMate) throw new IllegalArgumentException("参数：paras(excel解析单元)必须与参数：sti(表结构区域信息)相匹配");
        }

        String insertSql = "insert into "+mainTableName+"(#columnSql) values(#valueSql)", columnSql="", valueSql="";
        String updateSql = "update "+mainTableName+" set #updateSet where #updateKey", updateSet="", updateKey="";
        Object[] paramArray4Insert = new Object[sysMm.getColumnList().size()];
        Object[] paramArray4Update = new Object[sysMm.getColumnList().size()];
        Map<String, Integer> insertColIndexMap = new HashMap<String, Integer>();
        Map<String, Integer> updateColIndexMap = new HashMap<String, Integer>();
        for (int k=0; k<sysMm.getColumnList().size(); k++) {
            MetadataColumn mc = sysMm.getColumnList().get(k);
            columnSql+=","+mc.getColumnName();
            valueSql+=",?";
            insertColIndexMap.put(mc.getColumnName(), k);
            if (!mc.isPk()) {
                updateSet += ","+mc.getColumnName()+"=?";
            } else {
                updateKey += " and "+mc.getColumnName()+"=?";
            }
        }
        if (columnSql.length()>0) columnSql=columnSql.substring(1);
        if (valueSql.length()>0) valueSql=valueSql.substring(1);
        insertSql = insertSql.replaceAll("#columnSql", columnSql).replaceAll("#valueSql", valueSql);
        if (updateKey.equals("")||updateSet.equals("")) updateSql=null;
        else {
            if (updateSet.length()>0) updateSet=updateSet.substring(1);
            if (updateKey.length()>0) updateKey=updateKey.substring(5);
            updateSql = updateSql.replaceAll("#updateSet", updateSet).replaceAll("#updateKey", updateKey);
            String[] s = updateSet.split(",");
            int k=0;
            for (; k<s.length; k++) updateColIndexMap.put(s[k].substring(0, s[k].length()-2), k);
            s = updateKey.split(" and ");
            for (int l=0; l<s.length; l++) updateColIndexMap.put(s[l].substring(0, s[l].length()-2), k+l);
        }

        Connection conn = null;
        PreparedStatement psInsert = null;
        PreparedStatement psUpdate = null;

        Map<String, Object> titleCol = null;
        //日志信息准备
        int _log_readAllCount/*读取总行数*/, _log_insertOkCount=0/*新增成功行数*/,_log_updateOkCount=0/*新增成功行数*/, _log_saveFailCount=0/*新增失败行数*/, _log_ignoreCount=0/*忽略行数*/;
        Map<Integer, String> _log_failMap = new HashMap<Integer, String>();//存储失败的行及其原因
        Map<Integer, String> _log_ignoreMap = new HashMap<Integer, String>();//忽略行及其原因

        boolean autoCommit = false;
        try {
            conn = dataSource.getConnection();
            autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(true);
            psInsert = conn.prepareStatement(insertSql);
            if (updateSql!=null) psUpdate = conn.prepareStatement(updateSql);

            int keyCount=0;
            int _mmDType, _infoDType;
            Object v;
            String tagStr;

            List<Map<String, Object>> rowData = null;
            _log_readAllCount = sti.getEndY()-sti.getBeginY()+1;
            for (int i=sti.getBeginY(); i<=sti.getEndY(); i++) {
                rowData = parse.readOneRow(i);
                rowData = parse.convert2DataRow(rowData);
                if (parse.isEmptyRow(rowData)) {
                    _log_ignoreCount++;
                    _log_ignoreMap.put(i, "第"+i+"行为空行。");
                    continue;
                }
                for (int j=0; j<paramArray4Insert.length; j++) paramArray4Insert[j]=null;
                if (updateSql!=null) for (int j=0; j<paramArray4Update.length; j++) paramArray4Update[j]=null;

                keyCount=0;
                tagStr = "";
                for (Map<String, Object> cell: rowData) {
                    titleCol = parse.findMatchTitle(cell, sti);
                    if (titleCol!=null) {
                        for (int k=0; k<sysMm.getColumnList().size(); k++) {
                            MetadataColumn mc = sysMm.getColumnList().get(k);
                            _mmDType = ExcelConstants.convert2DataType(mc.getColumnType());
                            _infoDType = -1;
                            if (tagStr.indexOf(","+k)!=-1) continue;
                            if (mc.getTitleName().equals((String)titleCol.get("title"))) {
                                Map<String, Object> kv = (Map<String, Object>)cell.get("transData");
                                _infoDType = (Integer)kv.get("dType");
                                v = null;
                                if (_infoDType==_mmDType) v = kv.get("value");
                                else {
                                    kv = (Map<String, Object>)cell.get("nativeData");
                                    _infoDType = (Integer)kv.get("dType");
                                    if (_infoDType==_mmDType) v = kv.get("value");
                                    else if (_mmDType==ExcelConstants.DATA_TYPE_DOUBLE) {
                                        if (_infoDType==ExcelConstants.DATA_TYPE_INTEGER||_infoDType==ExcelConstants.DATA_TYPE_NUMERIC) {
                                            v = kv.get("value");
                                        }
                                    }
                                    if (_mmDType==ExcelConstants.DATA_TYPE_STRING&&v==null) {
                                        v=((Map<String, Object>)cell.get("transData")).get("value")+"";
                                    }
                                }
                                paramArray4Insert[insertColIndexMap.get(mc.getColumnName())] = v;
                                if (updateSql!=null) paramArray4Update[updateColIndexMap.get(mc.getColumnName())] = v;
                                tagStr+=","+k;
                                break;
                            }
                        }
                    }
                }
                boolean canSave = false;
                for (int j=0; j<paramArray4Insert.length; j++) {
                    if (paramArray4Insert[j]!=null) {
                        canSave = true;
                        break;
                    }
                }
                if (!canSave) {
                    _log_ignoreCount++;
                    _log_ignoreMap.put(i, "第"+i+"行数据与元数据不匹配，行数据为<<>>，元数据为<<>>。");
                    continue;
                }
                //先修改，再新增
                boolean canInsert = true;
                int j=0;
                if (updateSql!=null) {
                    psUpdate.clearParameters();
                    try{
                        for (j=0; j<paramArray4Update.length; j++) {
                            psUpdate.setObject(j+1, paramArray4Update[j]);
                        }
                        int updateOk = psUpdate.executeUpdate();
                        if (updateOk>0) {
                            canInsert=false;
                            _log_updateOkCount += updateOk;
                        } else {
                            canInsert=true;
                        }
                        canInsert = !(psUpdate.executeUpdate()==1);
                    } catch(SQLException sqlE) {
                        canInsert=true;
                    }
                }
                if (canInsert) {
                    psInsert.clearParameters();
                    try {
                        for (j=0; j<paramArray4Insert.length; j++) {
                            psInsert.setObject(j+1, paramArray4Insert[j]);
                        }
                        int insertOk = psInsert.executeUpdate();
                        if (insertOk>0) {
                            _log_insertOkCount += insertOk;
                        } else {
                            _log_saveFailCount++;
                            _log_failMap.put(i,  "第"+i+"行数据新增失败，原因未知！");
                        }
                    } catch(SQLException sqlE) {
                        _log_saveFailCount++;
                        _log_failMap.put(i,  "第"+i+"行数据新增失败，原因为："+sqlE.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (psUpdate!=null) {psUpdate.close();psUpdate = null;} } catch (Exception e) {e.printStackTrace();} finally {psUpdate = null;};
            try { if (psInsert!=null) {psInsert.close();psInsert = null;} } catch (Exception e) {e.printStackTrace();} finally {psInsert = null;};
            try { if (conn!=null) {conn.setAutoCommit(autoCommit);conn.close();conn = null;} } catch (Exception e) {e.printStackTrace();} finally {conn = null;};
        }
    }

    /*
     * 获得字符串实际字节长度
     * @param str 被计算的字符串
     * @param d 数据库方言
     * @return
     */
    private static int getStringLen(String str, Dialect d) {
        if (d==null) return str.length();
        return d.getStrLen(str, "utf8");
    }
}