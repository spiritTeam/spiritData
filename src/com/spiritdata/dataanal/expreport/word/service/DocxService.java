package com.spiritdata.dataanal.expreport.word.service;

import java.util.HashMap;
import java.util.Map;

/**
 * 导出word report服务
 * @author mht
 */
public class DocxService {
	/**
	 * 1:获取基本数据：通过前台获得操作者信息("user信息、jsond、report信息等")。
	 * @return retMap
	 */
	public Map<String,Object> initReportInfo(){
		Map<String,Object> retMap = new HashMap<String,Object>();
		//TODO
		return retMap;
	}
	/**
	 * 2:获取数据：获取jsond和report。
	 * @return
	 */
	public Map<String,Object> getBasicReportDate(){
		Map<String,Object> retMap = new HashMap<String,Object>();
		//TODO
		return retMap;
	}
	/**
	 * 3:构建report结构：分析jsond和report，构建docx的大体结构。
	 * @return
	 */
	public Map<String,Object> getBuildReportFrame(){
		Map<String,Object> retMap = new HashMap<String,Object>();
		//TODO
		return retMap;
	}
	/**
	 * 4:装填数据，制作表格，插入图片。
	 * @return
	 */
	public Map<String,Object> fillReportFrame(){
		Map<String,Object> retMap = new HashMap<String,Object>();
		//TODO
		return retMap;
	}
	/**
	 * 5:下载
	 * @return
	 */
	public Map<String,Object> down(){
		Map<String,Object> retMap = new HashMap<String,Object>();
		//TODO
		return retMap;
	}
}
