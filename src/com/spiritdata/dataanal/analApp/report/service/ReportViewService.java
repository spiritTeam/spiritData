package com.spiritdata.dataanal.analApp.report.service;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.dataanal.analApp.report.pojo.ReportViewPo;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;

public class ReportViewService {
	
    @Resource(name="defaultDAO")
	private MybatisDAO<ReportViewPo> reportViewDao;
    
    @PostConstruct
    public void initParam() {
    	reportViewDao.setNamespace("reportView");
    }
    
    /**
     * 条件查询报告列表
     * @param paramMap
     * @return
     */
    public List<ReportViewPo> searchReportList(Map<String,Object> paramMap){
    	return reportViewDao.queryForList("getReportList",paramMap);
    }
}
