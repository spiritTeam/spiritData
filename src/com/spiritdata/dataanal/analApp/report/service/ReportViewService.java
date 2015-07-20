package com.spiritdata.dataanal.analApp.report.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.dataanal.analApp.report.pojo.ReportViewPo;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.core.model.Page;

public class ReportViewService {
	
    @Resource(name="defaultDAO")
	private MybatisDAO<ReportViewPo> reportViewDao;
    
    @PostConstruct
    public void initParam() {
    	reportViewDao.setNamespace("reportView");
    }
    
    /**
     * 条件查询报告列表,不分页，查询出所有符合条件的数据 
     * @param paramMap
     * @return
     */
    public List<ReportViewPo> searchReportList(Map<String,Object> paramMap){
    	return reportViewDao.queryForList("getReportList",paramMap);
    }
    
    /**
     * 条件查询报告列表，分页查询出符合条件的数据
     * @param paramMap
     * @return
     */
    public Map<String,Object> searchReportPageList(Map<String,Object> paramMap){
    	Map<String,Object> datagridDataJsonMap = new HashMap<String,Object>();
		int pageNumber = ((Integer)paramMap.get("pageNumber")).intValue();
		int pageSize = ((Integer)(paramMap.get("pageSize"))).intValue();
		Page<Map<String,Object>> page = reportViewDao.pageQueryAutoTranform("getReportListCount", "getReportList", paramMap, pageNumber, pageSize);
		int totalTableCount = page.getDataCount();
		Collection<Map<String,Object>> dataCollect = page.getResult();
		List<Map<String,Object>> fileDataList = (List<Map<String,Object>>)dataCollect;
		datagridDataJsonMap.put("total", totalTableCount);
		datagridDataJsonMap.put("rows", fileDataList);		
    	return datagridDataJsonMap;
    }
}
