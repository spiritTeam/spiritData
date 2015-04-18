package com.spiritdata.dataanal.importdata.excel.service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.spiritdata.dataanal.importdata.excel.ExcelConstants;
import com.spiritdata.dataanal.importdata.excel.pojo.SheetTableInfo;
import com.spiritdata.dataanal.importdata.excel.service.TableDataProcessService.MetaDataColInfo;
import com.spiritdata.dataanal.importdata.excel.util.PoiParseUtils;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataColumn;

/**
 * 积累表操作
 * @author yfo
 *
 */
public class AccumulateTableDataProcessService extends TableDataProcessService {
    private Logger logger = Logger.getLogger(AccumulateTableDataProcessService.class);

    /**
     * 将EXCEL数据按行读出导入到积累表中，并更新相关积累表数据
     * @param mainTableName
     * @param mdColList
     * @param sti
     * @param parse
     */
    public void saveData2AccumulateTab(String mainTableName,List<MetadataColumn> mdColList,SheetTableInfo sti, PoiParseUtils parse,Map<String,MetaDataColInfo> colModiMap){
        //读取积累表的元数据信息,此操作将会把上面插入临时表的元数据信息清除掉!!!
    	if(!this.initTableMetaData(mainTableName)){//如果没取得积累表元数据信息，则无法进行后续处理
    		return;
    	}

    	//临时表数据分析结果不能被初始化，需要根据此结果修改列长度
//    	this.tmpTbResultBean = new FilterTmpTabResultBean();    	
    	//修正表列字段长度,如果某列数据超过该列定义的长度，则扩容该列
    	PreparedStatement psModiCol = null;
    	if(colModiMap!=null && !colModiMap.isEmpty()){
    		Map<String,MetaDataColInfo> mdColMap = this.mdTabInfo.tableMetaDataMap; //表中元数据信息
    		//循环遍历修改列字段长度
        	for(Map.Entry<String, MetaDataColInfo> entry:colModiMap.entrySet()){ 
        		MetaDataColInfo modiCol = (MetaDataColInfo)entry.getValue(); //需要修改的列信息，主要是长度
        		MetaDataColInfo metaDataCol = mdColMap.get(modiCol.colName); //通过需要修改的列名，找到该列的元数据信息
        		if(modiCol.colLen > metaDataCol.colLen){//当需要修改列的长度>改列实际长度时，则需修改长度
        			//需要积累表和临时表有相同的列名、列名的类型相同!!!
        			alterColLen(this.mdTabInfo.tableName,modiCol.colName,modiCol.colTypeName,modiCol.colLen,psModiCol);	
        		}
        	}
        	this.closeStatement(psModiCol);
    	}
    	
    	//插入、更新数据
        String insertSql = "insert into "+mainTableName+"(#columnSql) values(#valueSql)", columnSql="", valueSql="";
        String updateSql = "update "+mainTableName+" set #updateSet where #updateKey", updateSet="", updateKey="";
        Object[] paramArray4Insert = new Object[mdColList.size()];
        Object[] paramArray4Update = new Object[mdColList.size()];
        Map<String, Integer> insertColIndexMap = new HashMap<String, Integer>();
        Map<String, Integer> updateColIndexMap = new HashMap<String, Integer>();
        for (int k=0; k<mdColList.size(); k++) {
            MetadataColumn mc = mdColList.get(k);
            columnSql+=","+mc.getColumnName();
            valueSql+=",?";
            insertColIndexMap.put(mc.getColumnName(), k);
            if (!mc.isPk()) {
                updateSet += ","+mc.getColumnName()+"=?";
            } else {
                updateKey += " and "+mc.getColumnName()+"=?";
            }
        }
        if (columnSql.trim().length()>0) columnSql=columnSql.substring(1);
        if (valueSql.trim().length()>0) valueSql=valueSql.substring(1);
        insertSql = insertSql.replaceAll("#columnSql", columnSql).replaceAll("#valueSql", valueSql);
        if (updateKey.trim().length()==0||updateSet.trim().length()==0) updateSql=null;
        else {
            if (updateSet.trim().length()>0) updateSet=updateSet.substring(1);
            if (updateKey.trim().length()>0) updateKey=updateKey.substring(5);
            updateSql = updateSql.replaceAll("#updateSet", updateSet).replaceAll("#updateKey", updateKey);
            String[] s = updateSet.split(",");
            int k=0;
            for (; k<s.length; k++) updateColIndexMap.put(s[k].substring(0, s[k].length()-2), k);
            s = updateKey.split(" and ");
            for (int l=0; l<s.length; l++) updateColIndexMap.put(s[l].substring(0, s[l].length()-2), k+l);
        }

        Map<String, Object> titleCol = null;
        //日志信息准备
        int _log_readAllCount/*读取总行数*/, _log_insertOkCount=0/*新增成功行数*/,_log_updateOkCount=0/*新增成功行数*/, _log_saveFailCount=0/*新增失败行数*/, _log_ignoreCount=0/*忽略行数*/;
        Map<Integer, String> _log_failMap = new HashMap<Integer, String>();//存储失败的行及其原因
        Map<Integer, String> _log_ignoreMap = new HashMap<Integer, String>();//忽略行及其原因

        boolean autoCommit = false;
        PreparedStatement psInsert = null;
        PreparedStatement psUpdate = null;
        try {
            autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
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
                        for (int k=0; k<mdColList.size(); k++) {
                            MetadataColumn mc = mdColList.get(k);
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
                                        if (_infoDType==ExcelConstants.DATA_TYPE_INTEGER||_infoDType==ExcelConstants.DATA_TYPE_LONG||_infoDType==ExcelConstants.DATA_TYPE_NUMERIC) {
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

                                //对数据进行判断，可能会产生列长度不够的问题，所以需要扩容
//                                processColLen(mc.getColumnName(), v);
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
                //先修改，再新增,如果存在主键的情况下，则对应主键的值需要修改而不是新增
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
        	this.commitConn();
        	this.closeStatement(psUpdate);
        	this.closeStatement(psInsert);
        	this.closeConnection(autoCommit);
        }
    }
    
}
