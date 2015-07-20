package com.spiritdata.dataanal.analApp.query.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.dataanal.analApp.query.pojo.QueryViewPo;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.core.model.Page;

/**
 * 通用查询
 * @author yfo
 *
 */
public class QueryViewService {
    @Resource(name="defaultDAO")
	private MybatisDAO<QueryViewPo> queryViewDao;

    @PostConstruct
    public void initParam() {
    	queryViewDao.setNamespace("queryView");
    }

    /**
     * 条件查询文件列表
     * @param paramMap
     * @return
     */
    public List<QueryViewPo> searchGeneralList(Map paramMap){
    	return queryViewDao.queryForList("getQueryList",paramMap);
    }

    /**
     * 分页查询，条件查询文件列表
     * @param paramMap
     * @return
     */
    public Map<String,Object> searchGeneralPageList(Map paramMap){
    	Map<String,Object> datagridDataJsonMap = new HashMap<String,Object>();
		int pageNumber = ((Integer)paramMap.get("pageNumber")).intValue();
		int pageSize = ((Integer)(paramMap.get("pageSize"))).intValue();
		Page<Map<String,Object>> page = queryViewDao.pageQueryAutoTranform("getQueryListCount", "getQueryList", paramMap, pageNumber, pageSize);
		int totalTableCount = page.getDataCount();
		Collection<Map<String,Object>> dataCollect = page.getResult();
		List<Map<String,Object>> fileDataList = (List<Map<String,Object>>)dataCollect;
		datagridDataJsonMap.put("total", totalTableCount);
		datagridDataJsonMap.put("rows", fileDataList);		
    	return datagridDataJsonMap;
    }

}
