package com.spiritdata.dataanal.importdata.excel.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.spiritdata.dataanal.importdata.excel.ExcelConstants;
import com.spiritdata.dataanal.importdata.excel.pojo.SheetTableInfo;
import com.spiritdata.dataanal.importdata.excel.util.PoiParseUtils;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataColumn;
import com.spiritdata.framework.core.dao.dialect.Dialect;
import com.spiritdata.framework.core.dao.dialect.MySqlDialect;

/**
 * 表元数据操作服务
 * 用于对表的元数据进行修改
 * 例如获取表列元数据描述信息
 * 对指定列字段长度扩容
 * @author yfo
 *
 */
public class TableDataProcessService {
    private Logger logger = Logger.getLogger(TableDataProcessService.class);
    
    @Resource
    private DataSource dataSource;
    
    /**
     * 数据库连接
     * 用于数据库表操作
     * 用完后记得关闭
     */
    private Connection conn;
    
    /**
     * 表元数据信息BEAN
     */
    private MetaDataTableInfo mdTabInfo;
    
    /**
     * 不同种类数据库的处理方法
     * 如在MYSQL中用UTF-8编码，一个中文=3个字节
     */
    private Dialect dialect;
    
    /**
     * 插入临时表时，过滤出哪些行需要等列字段长度修改后才插入
     */
    private FilterTmpTabResultBean tmpTbResultBean;
    
    /**
     * 初始化表元数据服务，获取表的元数据描述信息
     * 当每次需要对一张新表进行元数据操作时，需要先调用此方法，否则存储的是上一次表元数据的分析信息，会导致操作失败
     * 主要包括所使用的DB、编码、列名、类型、长度、列所在的位置
     * @param tableName
     */
    public boolean initTableMetaData(String tableName){
    	boolean isSucc = false;
    	this.mdTabInfo = null;
    	this.dialect = null;
//    	this.tmpTbResultBean = new FilterTmpTabResultBean();
    	
        if(!this.initConnection()){
        	return isSucc;
        }
    	String sqlSel = "select * from "+tableName;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            //获取数据库产品名称
            String dbProductName =conn.getMetaData().getDatabaseProductName().toUpperCase();
            //数据库编码
            String dbEncoding = "UTF-8";
            if(dbProductName.equals("MYSQL")){
            	this.dialect = new MySqlDialect();
            	//获取编码格式  SHOW VARIABLES LIKE '%char%set%database%';
            	ps = conn.prepareStatement("SHOW VARIABLES LIKE '%char%set%database%'");
            	rs = ps.executeQuery();
            	if(rs.next()){
            		dbEncoding = rs.getString(2);
            	}            	                
            	rs.close();
            	ps.close();
            }else{
            	logger.error("unsupported db="+dbProductName);
            	return isSucc;
            }
            
            //获取表元数据信息 
        	ps = conn.prepareStatement(sqlSel);
        	rs = ps.executeQuery();
        	ResultSetMetaData meta = rs.getMetaData();
        	int colCount = meta.getColumnCount();
        	Map<String,MetaDataColInfo> tbMdMap = new HashMap<String,MetaDataColInfo>();
        	//rs从1开始
        	for(int i=1;i<=colCount;i++){
        		String colName = meta.getColumnName(i);
        		String colTypeName = meta.getColumnTypeName(i);
        		int colLen = meta.getPrecision(i);
        		MetaDataColInfo aColMetaData = new MetaDataColInfo(colName,colTypeName,i,colLen);
        		tbMdMap.put(colName, aColMetaData);
        	}
        	
        	//构造表元数据BEAN 
        	this.mdTabInfo = new MetaDataTableInfo(tableName,dbProductName,dbEncoding,tbMdMap);
        	
        	isSucc = true;
        }catch(Exception ex){
        	logger.error("failed to query table column metadata info. sql="+sqlSel,ex);
        }finally {
            this.closeResultSet(rs);
            this.closeStatement(ps);
        }
        
        return isSucc;
    }
    
    
    /**
     * 将EXCEL中数据按行读取，并插入到指定的数据表中
     * @param tbname
     * @param mdColList
     * @param sti
     * @param parse
     */
    public void insertDatas2TempTab(String tbname,List<MetadataColumn> mdColList,SheetTableInfo sti, PoiParseUtils parse){
    	if(!this.initTableMetaData(tbname)){//如果没取得表的元数据信息，则无法做后续处理
    		return;
    	}
    	//初始化数据处理结果信息
    	this.tmpTbResultBean = new FilterTmpTabResultBean();
    	
        //构建插入语句
        Object[] paramArray = new Object[mdColList.size()];
        String insertSql = "insert into "+tbname+"(#columnSql) values(#valueSql)", columnSql="", valueSql="";
        for (MetadataColumn mc: mdColList) {
            columnSql+=","+mc.getColumnName();
            valueSql+=",?";
        }
        if (columnSql.trim().length()>0) columnSql=columnSql.substring(1);
        if (valueSql.trim().length()>0) valueSql=valueSql.substring(1);
        insertSql = insertSql.replaceAll("#columnSql", columnSql).replaceAll("#valueSql", valueSql);


        Map<String, Object> titleCol = null;
        //日志信息准备
//        int _log_readAllCount/*读取总行数*/, _log_insertOkCount=0/*新增成功行数*/, _log_insertFailCount=0/*新增失败行数*/, _log_ignoreCount=0/*忽略行数*/;
        Map<Integer, String> _log_failMap = new HashMap<Integer, String>();//新增失败的行及其原因
        Map<Integer, String> _log_ignoreMap = new HashMap<Integer, String>();//忽略行及其原因

        boolean autoCommit = false;
        PreparedStatement ps = null;
        try {
            autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(insertSql);
            
            List<Map<String, Object>> rowData = null;
//            _log_readAllCount = sti.getEndY()-sti.getBeginY()+1;
            for (int i=sti.getBeginY(); i<=sti.getEndY(); i++) {
                rowData = parse.readOneRow(i);
                rowData = parse.convert2DataRow(rowData);
                //去除空行
                if (parse.isEmptyRow(rowData)) {
  //                  _log_ignoreCount++;
                    _log_ignoreMap.put(i, "第"+i+"行为空行。");
                    continue;
                }
                for (int j=0; j<paramArray.length; j++) paramArray[j]=null;

                int _mmDType, _infoDType;
                Object v;
                
                //判断此行是否有问题，如果有问题，则不能插入
                boolean isRowNormal = true;
                //为每列赋值
                for (Map<String, Object> cell: rowData) {
                    titleCol = parse.findMatchTitle(cell, sti);
                    if (titleCol!=null) {
                        for (int k=0; k<mdColList.size(); k++) {
                            MetadataColumn mc = mdColList.get(k);
                            _mmDType = ExcelConstants.convert2DataType(mc.getColumnType());
                            _infoDType = -1;
                            // 循环处理每列值信息
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
                                }
                                if (_mmDType==ExcelConstants.DATA_TYPE_STRING&&v==null) v=((Map<String, Object>)cell.get("transData")).get("value")+"";
                                paramArray[k]=v;
                                
                                //对数据进行判断，可能会产生列长度不够的问题，所以需要扩容
                                if(!judgeColLenNormal(mc.getColumnName(),v,paramArray,i)){
                                	isRowNormal = false;
                                }
                            }
                        }
                    }
                }
                
                //当列需要扩容的时候，则不需要插入此行，等列扩容后再插入
                if(!isRowNormal){
                    _log_ignoreMap.put(i, "第"+i+"行数据列需要扩容，等插完正常数据后，会自动修改列长度，然后再行插入。");
                	continue;
                }
                
                //是否可以插入此行
                boolean canInsert = false;                
                //判断此行数据是否都为看空，如果不为空才能插入
                for (int j=0; j<paramArray.length; j++) {
                    if (paramArray[j]!=null) {
                        canInsert = true;
                        break;
                    }
                }
                if (!canInsert) {
//                    _log_ignoreCount++;
                    _log_ignoreMap.put(i, "第"+i+"行数据与元数据不匹配，行数据为<<>>，元数据为<<>>。");
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
                        _log_failMap.put(i,  "第"+i+"行数据新增失败，原因未知！");
                    }
                } catch(SQLException sqlE) {
      //              _log_insertFailCount++;
                    _log_failMap.put(i,  "第"+i+"行数据新增失败，原因为："+sqlE.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	this.commitConn();
            this.closeStatement(ps);
//            this.closeConnection(autoCommit);//由于后面还要修改列长度，插入剩余数据，所以不能关闭CONN，等到都操作完后再关闭
        }       
        
        //根据过滤行列数据的结果，修改列长度，并插入剩余数据
        try{
	        //修改列长度
	        if(!this.tmpTbResultBean.colMap.isEmpty()){
	        	for(Map.Entry<String, MetaDataColInfo> entry:this.tmpTbResultBean.colMap.entrySet()){ 
	        		MetaDataColInfo mdCol = (MetaDataColInfo)entry.getValue();
	        		alterColLen(this.mdTabInfo.tableName,mdCol.colName,mdCol.colTypeName,mdCol.colLen,ps);
	        	}
	        }
	        
	        //插入剩余数据
	        ps = conn.prepareStatement(insertSql);
	        List<Object[]> rowDataList = this.tmpTbResultBean.rowDataList;
	        int colLen = ((Object[])rowDataList.get(0)).length;
	        for(int idxRow=0;idxRow<rowDataList.size();idxRow++){
	        	//取一行数据
	        	Object[] aRowData = (Object[])rowDataList.get(idxRow);
	        	int rowNo = this.tmpTbResultBean.getRowNo(idxRow); //取得该行数据在原始表中的行号
	        	try{
		        	//设置每列的值
		        	for(int idxCol=0;idxCol<colLen;idxCol++){
		        		ps.setObject(idxCol+1, aRowData[idxCol]);
		        	}
		        	//插入一行数据
	                int insertOk = ps.executeUpdate();
	                if (insertOk>0) {
	//                      _log_insertOkCount += insertOk;
	                } else {
	//                    _log_insertFailCount++;
	                    _log_failMap.put(rowNo,  "第"+rowNo+"行数据新增失败，原因未知！");
	                }
	        	}catch(SQLException sqlE) {
	        		  //              _log_insertFailCount++;
	                _log_failMap.put(rowNo,  "第"+rowNo+"行数据新增失败，原因为："+sqlE.getMessage());
	            }
	        }        
        }catch(Exception ex){
        	logger.error("failed to modi col len or insert rows",ex);
        }finally{
        	this.commitConn();
        	this.closeStatement(ps);
        	this.closeConnection(autoCommit); //都操作完了，因此需要关闭conn
        }
    }
    
    /**
     * 将EXCEL数据按行读出导入到积累表中，并更新相关积累表数据
     * @param mainTableName
     * @param mdColList
     * @param sti
     * @param parse
     */
    public void saveData2AccumulateTab(String mainTableName,List<MetadataColumn> mdColList,SheetTableInfo sti, PoiParseUtils parse){
        //读取积累表的元数据信息,此操作将会把上面插入临时表的元数据信息清除掉!!!
    	if(!this.initTableMetaData(mainTableName)){//如果没取得积累表元数据信息，则无法进行后续处理
    		return;
    	}

    	//临时表数据分析结果不能被初始化，需要根据此结果修改列长度
//    	this.tmpTbResultBean = new FilterTmpTabResultBean();    	
    	//修正表列字段长度,如果某列数据超过该列定义的长度，则扩容该列
    	PreparedStatement psModiCol = null;
    	if(this.tmpTbResultBean!=null && this.tmpTbResultBean.colMap!=null && !this.tmpTbResultBean.colMap.isEmpty()){
    		Map<String,MetaDataColInfo> mdColMap = this.mdTabInfo.tableMetaDataMap; //表中元数据信息
    		//循环遍历修改列字段长度
        	for(Map.Entry<String, MetaDataColInfo> entry:this.tmpTbResultBean.colMap.entrySet()){ 
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
    
    /**
     * 判断给定列的值长度是否超过指定列长，如果超过了，则对列进行扩容
     * @param colName
     * @param valObj
     */
    public boolean judgeColLenNormal(String colName,Object colVal,Object[] rowData,int rowNo){
    	boolean isColNormal = true; //默认为此行没问题，当发现为题才设为FALSE
    	if(colName == null || colVal == null){return isColNormal;}
    	if(this.mdTabInfo==null || mdTabInfo.tableName==null || mdTabInfo.tableMetaDataMap == null){return isColNormal;}
    	
    	try{
    		MetaDataColInfo colMetaData = this.mdTabInfo.tableMetaDataMap.get(colName);
    		//没有找到列名所对应的列元数据信息
    		if(colMetaData == null){
    			logger.error("failed to get col metadata info. colName="+colName);
    			return isColNormal;
    		}
    		
    		if(this.mdTabInfo.dbProductName.equals("MYSQL")){
    			isColNormal = judgeColLenNormalMySql(colMetaData,colVal,rowData,rowNo);
    		}else{
    			logger.warn("unsupported database product type="+this.mdTabInfo.dbProductName);
    		}
    	}catch(Exception ex){
    		logger.error("failed to process col len. colName="+colName+" val="+colVal,ex);
    	}
    	
    	return isColNormal;
    }
    
    /**
     * 处理MYSQL类型的数据
     * @param colMetaData
     * @param valObj
     */
    private boolean judgeColLenNormalMySql(MetaDataColInfo colMetaData,Object colVal,Object[] rowData,int rowNo){
    	boolean isColNormal = true; //默认为此行没问题，当发现为题才设为FALSE
    	//根据列类型做相应的判断
    	if(colMetaData.colTypeName.equalsIgnoreCase("VARCHAR")){//字符串处理  		
    		//mysql中字符串一个utf8字符占3个字节，英文占1个字节，因此需要先把中文替换成3个字节，再计算总长度
    		int valLen = this.dialect.getStrLen(String.valueOf(colVal), this.mdTabInfo.dbEncoding);
    		//varchar型最长65535个字节,一个utf8字符占3个字节、一个gbk字符占两个字节
    		if(valLen > colMetaData.colLen){
    			if(valLen<=65535){//列长度可以扩容
    				MetaDataColInfo mdcol = new MetaDataColInfo(colMetaData.colName, "VARCHAR", valLen);    				
    				addAFilterRow(mdcol,rowData,rowNo);
    				isColNormal = false; 
//    				if(this.alterColLen(this.mdTabInfo.tableName, colMetaData.colName, "VARCHAR", valLen)){
//    					colMetaData.colLen = valLen;
//    				}
    			}else{//列长度已经超出了该列类型的最大长度，无法扩容
    				logger.error("exceed max len 65535 of varch. valLen="+valLen);
    			}
    		}
    	}else if(colMetaData.colTypeName.equalsIgnoreCase("CHAR")){//字串处理	
    		//mysql中字符串一个utf8字符占3个字节，英文占1个字节，因此需要先把中文替换成3个字节，再计算总长度
//    		int valLen = getReplacedChineseStrLen(String.valueOf(valObj),3);
    		int valLen = this.dialect.getStrLen(String.valueOf(colVal), this.mdTabInfo.dbEncoding);
    		if(valLen > colMetaData.colLen){
    			if(valLen<=255){//列长度可以扩容
    				MetaDataColInfo mdcol = new MetaDataColInfo(colMetaData.colName, "CHAR", valLen);   				
    				addAFilterRow(mdcol,rowData,rowNo);    	
    				isColNormal = false; 			
//    				if(this.alterColLen(this.tableName, colMetaData.colName, "CHAR", valLen)){
//    					colMetaData.colLen = valLen;
//    				}
    			}else{
    				logger.error("exceed max len 255 of varch. valLen="+valLen);
    			}
    		}    		
    	}
    	else{
    		logger.warn("unsupported col type="+colMetaData.colTypeName);
    	}
    	
    	return isColNormal; 
    }
    
    /**
     * 由于列超长，所以需要在过滤结果中加入一行需要插入的记录，并且记录修改列长度
     * @param colName
     * @param mdcol
     * @param rowData
     */
    private void addAFilterRow(MetaDataColInfo mdCol,Object[] rowData,int rowNo){
    	//拷贝行数据 
    	int len = rowData.length;
    	Object[] aRowData = new Object[len];
    	System.arraycopy(rowData, 0, aRowData, 0, len);
    	this.tmpTbResultBean.addARowData(aRowData,rowNo);
    	//加入修改列
    	this.tmpTbResultBean.putAColInfo(mdCol);
    }

    /**
     * 当字段类型长度不够的时候，需要扩容字段长度
     * @param mm
     * @param mc
     * @param newLen
     */
    public boolean alterColLen( String tableName,String colName,String colType,int newLen,PreparedStatement ps){
    	boolean isSucc = false;
    	String sqlAlter = "ALTER TABLE "+tableName+" MODIFY COLUMN "+colName+" "+colType+"("+newLen+")";
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sqlAlter);                          
            isSucc = ps.execute(sqlAlter);
            isSucc = true;
            logger.debug(isSucc+" alter col len .sql="+sqlAlter);
        }catch(Exception ex){
        	logger.error("failed to alter col len. sql="+sqlAlter,ex);
        }finally {
        }
        return isSucc;
    }
    
    /**
     * 初始化数据库连接
     * 获得一个数据库连接
     */
    private boolean initConnection(){
    	boolean isSucc = false;
    	try {
			if(this.conn ==null || this.conn.isClosed()){
				this.conn = this.dataSource.getConnection();
			}
			isSucc = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("failed to init conn.",e);
		}
    	return isSucc;
    }
    
    private void closeConnection(boolean isAutoCommit){
    	this.setConnAutoCommit(isAutoCommit);
    	this.closeConnection();
    }
    
    /**
     * 关闭数据库连接
     */
    private void closeConnection(){
    	try{
    		if(this.conn!=null){
    			this.conn.close();
    		}
    	}catch(Exception ex){
    		logger.error("failed to close conn.",ex);
    	}finally{
    		this.conn = null;
    	}
    }
    
    private void setConnAutoCommit(boolean isAutoCommit){
    	try{
    		if(this.conn!=null){
    			this.conn.setAutoCommit(isAutoCommit);
    		}
    	}catch(Exception ex){
    		logger.error("failed to set conn autoCommit.",ex);
    	}
    }

    private void commitConn(){
    	try{
    		if(this.conn!=null){
    			this.conn.commit();
    		}
    	}catch(Exception ex){
    		logger.error("failed to commit conn.",ex);
    	}
    }
    
    private void closeResultSet(ResultSet rs){
    	try { 
    		if (rs!=null) {
    			rs.close();
    		} 
    	}catch (Exception e) {
    		logger.error("failed to close resultset.",e);
    	} finally {
    		rs = null;
    	}
    }

    private void closeStatement(Statement st){
    	try { 
    		if (st!=null) {
    			st.close();
    		} 
    	}catch (Exception e) {
    		logger.error("failed to close statement.",e);
    	} finally {
    		st = null;
    	}
    }
    
    /**
     * 表元数据信息BEAN
     * @author yfo
     *
     */
    public class MetaDataTableInfo{
        /**
         * 数据库名称，使用的是哪类数据库
         */
        String dbProductName;
        
        /**
         * 数据库的编码格式
         */
        String dbEncoding;
        /**
         * 表名，用于指定操作哪张表的元数据信息
         */
        String tableName;
        /**
         * 存储表的元数据信息
         * 主要存储列名、类型、长度、位置等信息
         */
        Map<String,MetaDataColInfo> tableMetaDataMap;       
        
        public MetaDataTableInfo(){
        	
        }
        
        public MetaDataTableInfo(String tableName,String dbProductName,String dbEncoding,Map<String,MetaDataColInfo> tableMetaDataMap){
        	this.tableName = tableName;
        	this.dbProductName = dbProductName;
        	this.dbEncoding = dbEncoding;
        	this.tableMetaDataMap = tableMetaDataMap;
        }
    }
    
    
    /**
     * 内部类，用于定义列元数据信息
     * @author yfo
     *
     */
    public class MetaDataColInfo{
    	/**
    	 * 列名
    	 */
    	String colName;
    	/**
    	 * 列类型 
    	 */
    	String colTypeName;
    	/**
    	 * 列长度
    	 */
    	int colLen;
    	/**
    	 * 列在表中的位置
    	 * 即第几列
    	 */
    	int colIdx;
    	
    	public MetaDataColInfo(){
    		
    	}
    	
    	public MetaDataColInfo(String colName,String colTypeName,int colIdx,int colLen){
    		this.colName = colName;
    		this.colTypeName = colTypeName;
    		this.colIdx = colIdx;
    		this.colLen = colLen;
    	}
    	public MetaDataColInfo(String colName,String colTypeName,int colLen){
    		this.colName = colName;
    		this.colTypeName = colTypeName;
    		this.colLen = colLen;
    	}
    }
    
    /**
     * 过滤临时表后生成的过滤结果信息
     * rowDataList保存了哪些行需要插入，之前由于列长度原因没有插入，留待修改列长度之后再插入
     * colMap保存了哪些列需要扩容到多大长度
     * @author yfo
     *
     */
    public class FilterTmpTabResultBean{
    	List<Object[]> rowDataList;//行数据列表
    	List<Integer> rowIdxList;//行号列表，对应行数据，用于日志记录操作了哪行数据
    	Map<String,MetaDataColInfo> colMap;//列元数据BEAN,存储列名、类型、长度等 
    	
    	public FilterTmpTabResultBean(){
    		this.rowDataList = new ArrayList<Object[]>();
    		this.rowIdxList = new ArrayList<Integer>();
    		this.colMap = new HashMap<String,MetaDataColInfo>();
    	}
    	
    	/**
    	 * 加入一行数据
    	 * @param rowData
    	 */
    	public void addARowData(Object[] rowData,int rowNo){
    		this.rowDataList.add(rowData);
    		this.rowIdxList.add(new Integer(rowNo));
    	}
    	
    	/**
    	 * 获得一行数据所对应的原始表中的行号
    	 * @param idxrow
    	 * @return
    	 */
    	public int getRowNo(int idxrow){
    		return this.rowIdxList.get(idxrow).intValue();
    	}
    	
    	/**
    	 * 存需要扩容的列，如果MAP中一存储此列，则只需要判断修改长度为最长值即可
    	 * @param colName
    	 * @param mdCol
    	 */
    	public void putAColInfo(MetaDataColInfo mdCol){
    		MetaDataColInfo aMdCol = this.colMap.get(mdCol.colName);
    		if(aMdCol==null){
    			this.colMap.put(mdCol.colName, mdCol);
    		}else if(aMdCol.colLen < mdCol.colLen){
    			aMdCol.colLen = mdCol.colLen;
    		}
    	}
    }

    
}
