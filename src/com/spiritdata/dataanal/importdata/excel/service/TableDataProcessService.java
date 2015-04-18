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
    protected DataSource dataSource;
    
    /**
     * 数据库连接
     * 用于数据库表操作
     * 用完后记得关闭
     */
    protected Connection conn;
    
    /**
     * 表元数据信息BEAN
     */
    protected MetaDataTableInfo mdTabInfo;
    
    /**
     * 不同种类数据库的处理方法
     * 如在MYSQL中用UTF-8编码，一个中文=3个字节
     */
    protected Dialect dialect;
    
    
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
    
    protected void closeConnection(boolean isAutoCommit){
    	this.setConnAutoCommit(isAutoCommit);
    	this.closeConnection();
    }
    
    /**
     * 关闭数据库连接
     */
    protected void closeConnection(){
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
    
    protected void setConnAutoCommit(boolean isAutoCommit){
    	try{
    		if(this.conn!=null){
    			this.conn.setAutoCommit(isAutoCommit);
    		}
    	}catch(Exception ex){
    		logger.error("failed to set conn autoCommit.",ex);
    	}
    }

    protected void commitConn(){
    	try{
    		if(this.conn!=null){
    			this.conn.commit();
    		}
    	}catch(Exception ex){
    		logger.error("failed to commit conn.",ex);
    	}
    }
    
    protected void closeResultSet(ResultSet rs){
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

    protected void closeStatement(Statement st){
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
    	
    	public int getColLen(){
    		return this.colLen;
    	}
    	
    	public String getColTypeName(){
    		return this.colTypeName;
    	}
    }
}
