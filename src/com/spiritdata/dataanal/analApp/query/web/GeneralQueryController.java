package com.spiritdata.dataanal.analApp.query.web;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.dataanal.analApp.file.pojo.FileViewPo;
import com.spiritdata.dataanal.analApp.file.service.FileViewService;
import com.spiritdata.dataanal.analApp.query.pojo.QueryViewPo;
import com.spiritdata.dataanal.analApp.query.service.QueryViewService;
import com.spiritdata.dataanal.analApp.report.pojo.ReportViewPo;
import com.spiritdata.dataanal.analApp.report.service.ReportViewService;
import com.spiritdata.dataanal.analApp.util.ViewControllerUtil;
import com.spiritdata.dataanal.common.model.Owner;
import com.spiritdata.dataanal.common.util.SessionUtils;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.util.JsonUtils;

/**
 * 通用查询控制器，查询文件和报告，按时间降序排列
 * @author yfo
 *
 */
@Controller
@RequestMapping(value="/listview")
public class GeneralQueryController {
	private static Logger logger = Logger.getLogger(GeneralQueryController.class);

    @Resource
	private QueryViewService queryViewService; //通用查询服务

    /**
     * 通用查询文件、报告列表，按时间降序排列
     * @param req
     * @return
     */
    @RequestMapping("searchGeneralList.do")
    public @ResponseBody Map<String,Object> searchGeneralList(HttpServletRequest req){
		Map<String,Object> retMap = new HashMap<String,Object>();
		try{
			//组装查询参数
			Map<String,Object> paramMap = new HashMap<String,Object>();
			String searchStr = this.trimStr(req.getParameter("searchStr"));
			if(searchStr!=null){
				paramMap.put("searchStr", searchStr);
			}
			ViewControllerUtil.setSearchOwnerInfo(req, paramMap);

			//查询通用列表(包括文件和报告)
			List<QueryViewPo> generalList = queryViewService.searchGeneralList(paramMap);
			int count = generalList!=null?generalList.size():0;
			
			retMap.put("total", new Integer(count));
			retMap.put("rows", generalList);			
		}catch(Exception ex){
			logger.error("failed to search report list.",ex);
		}
		return retMap;
    }
    
    /**
     * 去掉字符串的空格，如果为空则返回NULL
     * @param strObj
     * @return
     */
    private String trimStr(String astr){    	
    	return (astr==null || astr.trim().length()==0)?null:astr.trim();
    }
    
}
