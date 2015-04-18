package com.spiritdata.dataanal.importdata.excel.service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
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
 * 临时表数据处理
 * @author yfo
 *
 */
public class TmpTableDataProcessService extends TableDataProcessService {
    private Logger logger = Logger.getLogger(TmpTableDataProcessService.class);

    /**
     * 插入临时表时，过滤出哪些行需要等列字段长度修改后才插入
     */
    private FilterTmpTabResultBean tmpTbResultBean;
    

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
                                if(!judgeColLenNormal(mc.getColumnName(),v)){
                                	isRowNormal = false;
                                }
                            }
                        }
                    }
                }
                
                //当列需要扩容的时候，则不需要插入此行，等列扩容后再插入
                if(!isRowNormal){
                	addAFilterRowData(paramArray,i);
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
     * 判断给定列的值长度是否超过指定列长，如果超过了，则对列进行扩容
     * @param colName
     * @param valObj
     */
    private boolean judgeColLenNormal(String colName,Object colVal){
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
    		
    		//最好不要在此判断数据库类型，可以在dialect中来分析，否则产生分支太多，不好处理!!!!!!!!!
    		if(this.mdTabInfo.dbProductName.equals("MYSQL")){
    			isColNormal = judgeColLenNormalMySql(colMetaData,colVal);
    		}else{
    			logger.warn("unsupported database product type="+this.mdTabInfo.dbProductName);
    		}
    	}catch(Exception ex){
    		logger.error("failed to process col len. colName="+colName+" val="+colVal,ex);
    	}
    	
    	return isColNormal;
    }
    
    /**
     * 当临时表数据插入完成后，需要抽取出更新列长度信息，提供给积累表、主键分析等服务使用
     * @return
     */
    public Map<String,MetaDataColInfo> getColModiMap(){
    	return this.tmpTbResultBean.colMap;
    }
    
    /**
     * 处理MYSQL类型的数据 , 最好放在dialect中分析！！！
     * @param colMetaData
     * @param valObj
     */
    private boolean judgeColLenNormalMySql(MetaDataColInfo colMetaData,Object colVal){
    	boolean isColNormal = true; //默认为此行没问题，当发现为题才设为FALSE
    	//根据列类型做相应的判断
    	if(colMetaData.colTypeName.equalsIgnoreCase("VARCHAR")){//字符串处理  		
    		//mysql中字符串一个utf8字符占3个字节，英文占1个字节，因此需要先把中文替换成3个字节，再计算总长度
    		int valLen = this.dialect.getStrLen(String.valueOf(colVal), this.mdTabInfo.dbEncoding);
    		//varchar型最长65535个字节,一个utf8字符占3个字节、一个gbk字符占两个字节
    		if(valLen > colMetaData.colLen){
    			//“65535”应该在dialect中判断，类型的最大值！！！！！！！！！
    			if(valLen<=65535){//列长度可以扩容
    				MetaDataColInfo mdcol = new MetaDataColInfo(colMetaData.colName, "VARCHAR", valLen);    				
    				addAFilterCol(mdcol);
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
    				addAFilterCol(mdcol);   	
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
     * 由于列超长，所以需要存入此列信息，用于插入完可插入的数据后，修改列长度
     * 在存入修改列时，需要先将目前行此列的长度和缓存修改列中存储的长度比较，如果>缓存列长度，则替换缓存列长度
     * 保证在循环完所有行后，保存的是最大长度值
     * @param colName
     * @param mdcol
     * @param rowData
     */
    private void addAFilterCol(MetaDataColInfo mdCol){
    	//加入修改列
    	this.tmpTbResultBean.putAColInfo(mdCol);
    }
    
    /**
     * 由于列超长，无法插入临时表，所以需要先缓存下该行数据，等修改列长后，再插入临时表
     * @param rowData
     * @param rowNo
     */
    private void addAFilterRowData(Object[] rowData,int rowNo){
    	//拷贝行数据 
    	int len = rowData.length;
    	Object[] aRowData = new Object[len];
    	System.arraycopy(rowData, 0, aRowData, 0, len);
    	this.tmpTbResultBean.addARowData(aRowData,rowNo);
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