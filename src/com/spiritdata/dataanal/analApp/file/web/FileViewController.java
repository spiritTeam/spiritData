package com.spiritdata.dataanal.analApp.file.web;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.dataanal.analApp.file.pojo.FileViewPo;
import com.spiritdata.dataanal.analApp.file.service.FileViewService;
import com.spiritdata.dataanal.analApp.util.ViewControllerUtil;
import com.spiritdata.dataanal.common.model.Owner;
import com.spiritdata.dataanal.common.util.SessionUtils;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataColumn;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel;
import com.spiritdata.dataanal.metadata.relation.pojo._OwnerMetadata;
import com.spiritdata.dataanal.metadata.relation.service.MdBasisService;
import com.spiritdata.dataanal.metadata.relation.service.MdSessionService;
import com.spiritdata.framework.util.DateUtils;

/**
 * 文件查询控制器
 * @author yfo
 *
 */
@Controller
@RequestMapping(value="/fileview")
public class FileViewController{
	private static Logger logger = Logger.getLogger(FileViewController.class);

    @Resource
	private FileViewService fileViewService;

    @Resource
    private MdSessionService mdSessionService;
	
    @Resource
    private MdBasisService mdBasisService;
    
	/**
	 * 条件查询文件列表
	 * @param req
	 * @return
	 */
    @RequestMapping("searchFileList.do")
	public @ResponseBody Map<String,Object> searchFileList(HttpServletRequest req){
		Map<String,Object> retMap = new HashMap<String,Object>();
		try{
//			logger.info("start search file list ...");
			Map<String,Object> paramMap = new HashMap<String,Object>();
			String cFileName = req.getParameter("searchStr");
			if(cFileName==null || cFileName.trim().length()==0){
				cFileName = null;
			}else{
				cFileName = cFileName.trim();
				paramMap.put("searchStr", cFileName);
			}
			Timestamp startTime = null;
			String startDateStr = req.getParameter("startDateStr");
			if(startDateStr==null || startDateStr.trim().length()==0){
				startTime = null;
			}else{
				if(startDateStr.indexOf(" ")==-1){
					startDateStr += " 00:00:00";
				}
				Date dt = DateUtils.getDateTime("yyyy-MM-dd HH:mm:ss", startDateStr);
				startTime = new Timestamp(dt.getTime());
				paramMap.put("startTime", startTime);
			}
			Timestamp endTime =null;
			String endDateStr = req.getParameter("endDateStr");
			if(endDateStr==null || endDateStr.trim().length()==0){
				endTime = null;
			}else{
				if(endDateStr.indexOf(" ")==-1){
					endDateStr += " 00:00:00";
				}
				Date dt = DateUtils.getDateTime("yyyy-MM-dd HH:mm:ss", endDateStr);
				endTime = new Timestamp(dt.getTime());
				paramMap.put("endTime", endTime);
			}		
			ViewControllerUtil.setSearchOwnerInfo(req,paramMap);
			
			List<FileViewPo> dataList = fileViewService.searchFileList(paramMap);
			int count = dataList!=null?dataList.size():0;
			retMap.put("total", new Integer(count));
			retMap.put("rows", dataList);
		}catch(Exception ex){
			logger.error("failed to search file list . ",ex);
		}
		return retMap;
	}
    
	/**
	 * 根据文件ID获取文件数据，用于显示文件内容，可能包含多个sheets，多张表
	 * @param req
	 * @return
	 */
    @RequestMapping("getFileData.do")
	public @ResponseBody Map<String,Object> getFileData(HttpServletRequest req){
		Map<String,Object> retMap = new HashMap<String,Object>();
		String fileId = null;
		try{
			Map<String,Object> paramMap = new HashMap<String,Object>();
			fileId = req.getParameter("fileId");
			if(fileId!=null && fileId.trim().length()>0){
				paramMap.put("fileId", fileId.trim());
			}	
			ViewControllerUtil.setSearchOwnerInfo(req, paramMap);
			
			List<FileViewPo> dataInfoList = fileViewService.getFileDataInfo(paramMap);
			int count = dataInfoList!=null?dataInfoList.size():0;
			retMap.put("totalSheet", new Integer(count)); //一个sheet一张表
			List<Map<String,Object>> sheetMapList = new ArrayList<Map<String,Object>>();
			retMap.put("SheetDataList", sheetMapList);
			for(int i=0;i<count;i++){
				Map<String,Object> aSheetDataMap = new HashMap<String,Object>();
				sheetMapList.add(aSheetDataMap);
				Map<String,Object> fileInfoMap = new HashMap<String,Object>();
				aSheetDataMap.put("fileInfoMap",fileInfoMap);
				FileViewPo fvp = (FileViewPo)dataInfoList.get(i);
				//获取SHEET页签名称
				String sheetName = fvp.getSheetName();
				fileInfoMap.put("title", sheetName);
				fileInfoMap.put("width", new Integer(980));
				fileInfoMap.put("height", "auto");
				fileInfoMap.put("fitColumns", true);
				fileInfoMap.put("pagination", true);
				//fileInfoMap.put("pageSize", 20);
				fileInfoMap.put("rownumbers", true);
				fileInfoMap.put("singleSelect", true);
				
				//获取元数据信息
				_OwnerMetadata _om = mdSessionService.loadcheckData(req.getSession());
				MetadataModel mm = _om!=null?_om.getMetadataById(fvp.getTmId()):null;
				if(mm==null){
					mm = mdBasisService.getMetadataMode(fvp.getTmId());	
				}	 
				//得到列信息
				List<List<Map<String,Object>>> colsJsonList = new ArrayList<List<Map<String,Object>>>();
				fileInfoMap.put("columns", colsJsonList);
				List<Map<String,Object>> colJsonList = new ArrayList<Map<String,Object>>();
				colsJsonList.add(colJsonList);
				List<MetadataColumn> colList = mm.getColumnList();
				//按照columnIndex排序列
				Collections.sort(colList, new Comparator<MetadataColumn>() {
		            public int compare(MetadataColumn arg0, MetadataColumn arg1) {
		                return new Integer(arg0.getColumnIndex()).compareTo(new Integer(arg1.getColumnIndex()));
		            }
		        });
		         
				//组装列JSON串
				for(int colidx = 0; colidx < colList.size(); colidx++){
					Map<String,Object> aColMap = new LinkedHashMap<String,Object>();
					colJsonList.add(aColMap);
					MetadataColumn aMetadataCol = (MetadataColumn)colList.get(colidx);
					aColMap.put("field", aMetadataCol.getColumnName());
					aColMap.put("title", aMetadataCol.getTitleName());
//					aColMap.put("width", new Integer(100));
				}
				
				//获取表数据
				String tmpTableName = fvp.getTmpTableName();
				aSheetDataMap.put("tableName",tmpTableName);
				logger.info("search tmp fileTable="+tmpTableName);

				//组装数据JSON,这是取所有数据
				List<Map<String,Object>> fileDataList = fileViewService.getFileData(tmpTableName,colList);
				Map<String,Object> fileDataMap = new LinkedHashMap<String,Object>();
				aSheetDataMap.put("fileDataMap",fileDataMap);
				fileDataMap.put("total", fileDataList.size());
				fileDataMap.put("rows", fileDataList);
			}
		}catch(Exception ex){
			logger.error("failed to get file data. fileId="+fileId,ex);
		}
		return retMap;
    }

	/**
	 * 根据文件ID获取文件数据，用于显示文件内容，可能包含多个sheets，多张表,去分页数据，不是取所有数据
	 * @param req
	 * @return
	 */
    @RequestMapping("getFilePageData.do")
	public @ResponseBody Map<String,Object> getFilePageData(HttpServletRequest req){
		Map<String,Object> retMap = new HashMap<String,Object>();
		String fileId = null;
		int pageNumber = 0;
		int pageSize = 0;		
		try{
			Map<String,Object> paramMap = new HashMap<String,Object>();
			fileId = req.getParameter("fileId");
			if(fileId!=null && fileId.trim().length()>0){
				paramMap.put("fileId", fileId.trim());
			}	
			ViewControllerUtil.setSearchOwnerInfo(req, paramMap);
			
			pageNumber = Integer.parseInt(req.getParameter("pageNumber"));
			pageSize = Integer.parseInt(req.getParameter("pageSize"));

			List<FileViewPo> dataInfoList = fileViewService.getFileDataInfo(paramMap);
			int count = dataInfoList!=null?dataInfoList.size():0;
			retMap.put("totalSheet", new Integer(count)); //一个sheet一张表
			List<Map<String,Object>> sheetMapList = new ArrayList<Map<String,Object>>();
			retMap.put("SheetDataList", sheetMapList);
			for(int i=0;i<count;i++){
				Map<String,Object> aSheetDataMap = new HashMap<String,Object>();
				sheetMapList.add(aSheetDataMap);
				Map<String,Object> fileInfoMap = new HashMap<String,Object>();
				aSheetDataMap.put("fileInfoMap",fileInfoMap);
				FileViewPo fvp = (FileViewPo)dataInfoList.get(i);
				//获取SHEET页签名称
				String sheetName = fvp.getSheetName();
				fileInfoMap.put("title", sheetName);
				fileInfoMap.put("width", new Integer(980));
				fileInfoMap.put("height", "auto");
				fileInfoMap.put("fitColumns", true);
				fileInfoMap.put("pagination", true);
				//fileInfoMap.put("pageSize", 20);
				fileInfoMap.put("rownumbers", true);
				fileInfoMap.put("singleSelect", true);
				
				//获取元数据信息
				_OwnerMetadata _om = mdSessionService.loadcheckData(req.getSession());
				MetadataModel mm = _om!=null?_om.getMetadataById(fvp.getTmId()):null;
				if(mm==null){
					mm = mdBasisService.getMetadataMode(fvp.getTmId());	
				}	 
				//得到列信息
				List<List<Map<String,Object>>> colsJsonList = new ArrayList<List<Map<String,Object>>>();
				fileInfoMap.put("columns", colsJsonList);
				List<Map<String,Object>> colJsonList = new ArrayList<Map<String,Object>>();
				colsJsonList.add(colJsonList);
				List<MetadataColumn> colList = mm.getColumnList();
				//按照columnIndex排序列
				Collections.sort(colList, new Comparator<MetadataColumn>() {
		            public int compare(MetadataColumn arg0, MetadataColumn arg1) {
		                return new Integer(arg0.getColumnIndex()).compareTo(new Integer(arg1.getColumnIndex()));
		            }
		        });
		         
				//组装列JSON串
				StringBuffer sbfCols = new StringBuffer();
				for(int colidx = 0; colidx < colList.size(); colidx++){
					Map<String,Object> aColMap = new LinkedHashMap<String,Object>();
					colJsonList.add(aColMap);
					MetadataColumn aMetadataCol = (MetadataColumn)colList.get(colidx);
					aColMap.put("field", aMetadataCol.getColumnName());
					aColMap.put("title", aMetadataCol.getTitleName());
//					aColMap.put("width", new Integer(100));

					String aColName = aMetadataCol.getColumnName();
					if(colidx>0){
						sbfCols.append(",");
					}
					sbfCols.append(aColName);
				}
				aSheetDataMap.put("selCols",sbfCols.toString());
				
				//获取表数据
				String tmpTableName = fvp.getTmpTableName();
				aSheetDataMap.put("tableName",tmpTableName);
				logger.info("search tmp fileTable="+tmpTableName);
				//读取分页数据 
				Map<String,Object> datagridDataJsonMap = fileViewService.getTablePageData(tmpTableName, sbfCols.toString(), pageNumber, pageSize);
				//放入表中
				aSheetDataMap.put("fileDataMap",datagridDataJsonMap);
			}
		}catch(Exception ex){
			logger.error("failed to get file data. fileId="+fileId,ex);
		}
		return retMap;
    }

    /**
     * 获取某张表的分页查询结果数据，组装成easyui的datagrid格式数据
     * @param req
     * @return
     */
    @RequestMapping("getTablePageData.do")
	public @ResponseBody Map<String,Object> getTablePageData(HttpServletRequest req){
		Map<String,Object> datagridDataJsonMap = new HashMap<String,Object>();
		String tableName=null; 
		String selCols = null;
		int pageNumber=0;
		int pageSize=0;
		try{
			tableName = req.getParameter("tableName");
			selCols = req.getParameter("selCols");
			pageNumber = Integer.parseInt(req.getParameter("pageNumber"));
			pageSize = Integer.parseInt(req.getParameter("pageSize"));
			//读取分页数据 
			datagridDataJsonMap = fileViewService.getTablePageData(tableName, selCols, pageNumber, pageSize);
		}catch(Exception ex){
			logger.error("failed to get table page data. tableName="+tableName+" pageNumber="+pageNumber+" pageSize="+pageSize,ex);
		}
		return datagridDataJsonMap;
    }
}
