package com.spiritdata.analApp.report.web;

import java.io.Serializable;
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
			List<NewRepotBean> dataList = new ArrayList<NewRepotBean>(); 
		    Random random = new Random();
		    int count = random.nextInt(50);
		    int currCount = 0;
			while(true) {
			    if(currCount>= count){
			    	break;
			    }
			    
			    int idx = random.nextInt(100);
			    String reportId = "rptId"+idx;
			    String reportName = "report"+idx;
			    String size = idx+"M";
			    String createDate = "2015-01-02";
			    
			    //检查是否已经有了重名的报告，如果有则抛弃
			    boolean hasReport = false;
			    for(NewRepotBean nrb:dataList){
			    	if(nrb.isSameReportId(reportId)){
			    		hasReport = true;
			    		break;
			    	}
			    }
			    if(hasReport){
			    	continue;
			    }else{
			    	NewRepotBean anrb = new NewRepotBean(reportId,reportName,size,createDate);
			    	dataList.add(anrb);
			    	currCount++;	
			    }
			}
			retMap.put("total", new Integer(count));
			retMap.put("rows", dataList);
			//retMap.put("rows", dataList.toArray());
			//retMap.put("rows",new ArrayList().add("1"));
		}catch(Exception ex){
			logger.error("failed to search new report. ",ex);
		}
		return retMap;
	}
    
    /**
     * 新生成报告BEAN
     * @author yfo
     *
     */
    class NewRepotBean implements Serializable{
    	/**
		 * 
		 */
		private static final long serialVersionUID = 558467119425972711L;
		public String reportId;
		public String reportName;
		public String size;
		public String createDate;
    	public NewRepotBean(){
    		
    	}
    	public NewRepotBean(String reportId,String reportName,String size,String createDate){
    		this.reportId = reportId;
    		this.reportName = reportName;
    		this.size = size;
    		this.createDate = createDate;
    	}
    	
    	/**
    	 * 是否和指定的报告重名
    	 * @param rptName
    	 * @return
    	 */
    	boolean isSameReportId(String rptId){
    		return this.reportId.equalsIgnoreCase(rptId);
    	}
		public String getReportId() {
			return reportId;
		}
		public void setReportId(String reportId) {
			this.reportId = reportId;
		}
		public String getReportName() {
			return reportName;
		}
		public void setReportName(String reportName) {
			this.reportName = reportName;
		}
		public String getSize() {
			return size;
		}
		public void setSize(String size) {
			this.size = size;
		}
		public String getCreateDate() {
			return createDate;
		}
		public void setCreateDate(String createDate) {
			this.createDate = createDate;
		}
    }
}
