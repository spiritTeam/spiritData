package com.spiritdata.dataanal.analApp.file.web;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.spiritdata.dataanal.common.model.Owner;
import com.spiritdata.dataanal.common.util.SessionUtils;

/**
 * 文件查询控制器
 * @author yfo
 *
 */
@Controller
@RequestMapping(value="/fileview")
public class FileViewController {
	private static Logger logger = Logger.getLogger(FileViewController.class);

    @Resource
	private FileViewService fileViewService;
	
	/**
	 * 条件查询文件列表
	 * @param req
	 * @return
	 */
    @RequestMapping("searchFileList.do")
	public @ResponseBody Map<String,Object> searchFileList(HttpServletRequest req){
		Map<String,Object> retMap = new HashMap<String,Object>();
		try{
			logger.info("start search file list ...");
			Map<String,Object> paramMap = new HashMap<String,Object>();
			String cFileName = req.getParameter("searchStr");
			if(cFileName==null || cFileName.trim().length()==0){
				cFileName = null;
			}else{
				cFileName = cFileName.trim();
				paramMap.put("cFileName", cFileName);
			}
			Timestamp startTime = null;
			String startDateStr = req.getParameter("startDateStr");
			if(startDateStr==null || startDateStr.trim().length()==0){
				startTime = null;
			}else{
				startTime = Timestamp.valueOf(startDateStr);
				paramMap.put("startTime", startTime);
			}
			Timestamp endTime =null;
			String endDateStr = req.getParameter("endDateStr");
			if(endDateStr==null || endDateStr.trim().length()==0){
				endTime = null;
			}else{
				endTime = Timestamp.valueOf(endDateStr);
				paramMap.put("endTime", endTime);
			}			
			Owner owner = SessionUtils.getOwner(req.getSession());
			String ownerId = owner.getOwnerId();
			//paramMap.put("ownerId", ownerId);
			int ownerType = owner.getOwnerType();
			//paramMap.put("ownerType", new Integer(ownerType));
			
			logger.info("paramMap:"+cFileName+"  "+startDateStr+"  "+endDateStr);
			List<FileViewPo> dataList = fileViewService.searchFileList(paramMap);
			int count = dataList!=null?dataList.size():0;
			logger.info("count="+count);
			retMap.put("total", new Integer(count));
			retMap.put("rows", dataList);
		}catch(Exception ex){
			logger.error("failed to search file list . ",ex);
		}
		return retMap;
	}
}
