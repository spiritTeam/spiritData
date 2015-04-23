package com.spiritdata.analApp.report.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 报表查看相关的控制器
 * @author yfo
 *
 */
@Controller
@RequestMapping(value="/reportview")
public class ReportViewController {
	private static Logger logger = Logger.getLogger(ReportViewController.class);

	/**
	 * 查看最新生成的还未看过的报告
	 * @param param
	 * @param req
	 * @return
	 */
    @RequestMapping("searchNewReport.do")
	public @ResponseBody Map<String,Object> searchNewReport(HttpServletRequest req){
		Map<String,Object> retMap = new HashMap<String,Object>();
		try{
			List<String> dataList = new ArrayList<String>(); 
		    Random random = new Random();
		    int count = random.nextInt(5);
		    int currCount = 0;
			while(true) {
			    if(currCount>= count){
			    	break;
			    }
			    
			    int idx = random.nextInt(10);
			    String reportFile = "report"+idx;
			    if(dataList.contains(reportFile)){
			    	continue;
			    }else{
			    	dataList.add(reportFile);
			    	currCount++;	
			    }
			}
			retMap.put("total", new Integer(count));
			retMap.put("data", dataList);
		}catch(Exception ex){
			logger.error("failed to search new report. ",ex);
		}
		return retMap;
	}
    
}
