package com.spiritdata.dataanal.analApp.file.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.spiritdata.dataanal.analApp.file.pojo.FileViewPo;
import com.spiritdata.dataanal.analApp.file.web.FileViewController;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataColumn;
import com.spiritdata.filemanage.core.persistence.pojo.FileIndexPo;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.core.model.Page;
import com.spiritdata.framework.util.DateUtils;

public class FileViewService {
	private static Logger logger = Logger.getLogger(FileViewService.class);


    @Resource(name="defaultDAO")
    private MybatisDAO<FileViewPo> fileViewDao;
    
    @PostConstruct
    public void initParam() {
    	fileViewDao.setNamespace("fileView");
    }
    
    /**
     * 条件查询文件列表
     * @param paramMap
     * @return
     */
    public List<FileViewPo> searchFileList(Map paramMap){
    	return fileViewDao.queryForList("getFileList",paramMap);
    }

    /**
     * 条件查询文件列表
     * @param paramMap
     * @return
     */
    public int searchFileListCount(Map paramMap){
    	return fileViewDao.getCount("getFileListCount",paramMap);
    }

    /**
     * 条件查询文件列表，分页查询
     * @param paramMap
     * @return
     */
    public Map<String,Object> searchFilePageList(Map paramMap){
    	Map<String,Object> datagridDataJsonMap = new HashMap<String,Object>();
		int pageNumber = ((Integer)paramMap.get("pageNumber")).intValue();
		int pageSize = ((Integer)(paramMap.get("pageSize"))).intValue();
		Page<Map<String,Object>> page = fileViewDao.pageQueryAutoTranform("getFileListCount", "getFilePageList", paramMap, pageNumber, pageSize);
		int totalTableCount = page.getDataCount();
		Collection<Map<String,Object>> dataCollect = page.getResult();
		List<Map<String,Object>> fileDataList = (List<Map<String,Object>>)dataCollect;
		datagridDataJsonMap.put("total", totalTableCount);
		datagridDataJsonMap.put("rows", fileDataList);		
    	return datagridDataJsonMap;
    }
    
    /**
     * 获取指定ID的文件数据
     * @param paramMap
     * @return
     */
    public List<FileViewPo> getFileDataInfo(Map paramMap){
    	return fileViewDao.queryForList("getFileDataInfo",paramMap);
    }
    
    /**
     * 获取表数据，即文件详细信息
     * @param fileId
     * @return
     */
    public List<Map<String,Object>> getFileData(String fileTableName,List<MetadataColumn> colList){
    	List<Map<String,Object>> retList = new ArrayList<Map<String,Object>>();
    	Connection conn = null;
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	String sqlSel = "select * from "+fileTableName;
    	try {
			conn = fileViewDao.getSqlSession().getConfiguration().getEnvironment().getDataSource().getConnection();
			pstmt = conn.prepareStatement(sqlSel);
			rs = pstmt.executeQuery();
			while(rs.next()){
				Map<String,Object> aRowMap = new LinkedHashMap<String,Object>();
				retList.add(aRowMap);
				//读取一行记录
				for(int colidx = 0; colidx < colList.size(); colidx++){
					MetadataColumn aMetadataCol = (MetadataColumn)colList.get(colidx);
					if(aMetadataCol.getColumnType().equalsIgnoreCase("timestamp")){
						String timeVal = DateUtils.convert2LocalStr("yyyy-MM-dd HH:mm:ss", rs.getTimestamp(aMetadataCol.getColumnName()));
						aRowMap.put(aMetadataCol.getColumnName(), timeVal);
					}else if(aMetadataCol.getColumnType().equalsIgnoreCase("DATE")){
						String timeVal = DateUtils.convert2LocalStr("yyyy-MM-dd", rs.getDate(aMetadataCol.getColumnName()));
						aRowMap.put(aMetadataCol.getColumnName(), timeVal);
					}else if(aMetadataCol.getColumnType().equalsIgnoreCase("Double")){
						double dval = rs.getDouble(aMetadataCol.getColumnName());	      
						String str = String.valueOf(dval);
						if(str!=null && str.indexOf("E")>-1){
							BigDecimal bigDecimal = new BigDecimal(dval); 
							str = bigDecimal.toPlainString();
						}					
						aRowMap.put(aMetadataCol.getColumnName(), str);
					}
				    else{
						String val = rs.getObject(aMetadataCol.getColumnName()).toString();
						aRowMap.put(aMetadataCol.getColumnName(), val);
					}					
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try{if(rs!=null){rs.close();}rs=null;}catch(Exception ex){}
			try{if(pstmt!=null){pstmt.close();}pstmt=null;}catch(Exception ex){}
			try{if(conn!=null){conn.close();}conn=null;}catch(Exception ex){}			
		}
    	return retList;
    }
    
    /**
     * 分页查询临时表数据，组装成easyui的datagrid里的JSON数据格式
     * @param tableName
     * @param colList
     * @return
     */
    public Map<String,Object> getTablePageData(String tableName,String cols,int pageNumber,int pageSize){
    	Map<String,Object> datagridDataJsonMap = new HashMap<String,Object>();
    	try{
    		//组装sql里需要的变量
    		Map<String,String> searchParam = new HashMap<String,String>();
    		searchParam.put("aTableName", tableName);    		
    		searchParam.put("aTableCols", cols);
    		
    		Page<Map<String,Object>> page = fileViewDao.pageQueryAutoTranform(null, "getTablePageData", searchParam, pageNumber, pageSize);
    		int totalTableCount = page.getDataCount();
    		Collection<Map<String,Object>> dataCollect = page.getResult();
    		List<Map<String,Object>> fileDataList = (List<Map<String,Object>>)dataCollect;
    		datagridDataJsonMap.put("total", totalTableCount);
    		datagridDataJsonMap.put("rows", fileDataList);
    	}catch(Exception ex){
    		logger.error("failed to get file page data. tableName="+tableName+" pageNumber="+pageNumber+" pageSize="+pageSize,ex);
    	}
    	return datagridDataJsonMap;
    }
}
