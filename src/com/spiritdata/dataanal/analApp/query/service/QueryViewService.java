package com.spiritdata.dataanal.analApp.query.service;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.dataanal.analApp.query.pojo.QueryViewPo;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;

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

}
