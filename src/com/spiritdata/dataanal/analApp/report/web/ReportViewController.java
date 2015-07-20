package com.spiritdata.dataanal.analApp.report.web;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.dataanal.analApp.report.pojo.ReportViewPo;
import com.spiritdata.dataanal.analApp.report.service.ReportViewService;
import com.spiritdata.dataanal.analApp.util.ViewControllerUtil;
import com.spiritdata.dataanal.common.model.Owner;
import com.spiritdata.dataanal.common.util.SessionUtils;
import com.spiritdata.dataanal.report.persistence.pojo.ReportPo;
import com.spiritdata.dataanal.visitmanage.service.ReportVisitService;
import com.spiritdata.framework.util.DateUtils;

/**
 * 报表查看相关的控制器
 * @author yfo
 *
 */
@Controller
@RequestMapping(value="/reportview")
public class ReportViewController {
    private static Logger logger = Logger.getLogger(ReportViewController.class);

    @Resource
    private ReportViewService reportViewService;

    @Resource
    private ReportVisitService rvService;

    /**
     * 条件查询报告列表，不分页，显示所有符合条件的数据
     * @param req
     * @return
     */
    @RequestMapping("searchReportList.do")
    public @ResponseBody Map<String,Object> searchReportList(HttpServletRequest req){
        Map<String,Object> retMap = new HashMap<String,Object>();
        try {
            Map<String,Object> paramMap = new HashMap<String,Object>();
            String reportName = this.trimStr(req.getParameter("searchStr"));
            if (reportName!=null) paramMap.put("searchStr", reportName);
            Timestamp startTime = this.str2TimeStamp(req.getParameter("startDateStr"));
            if (startTime!=null) paramMap.put("startTime", startTime);
            Timestamp endTime = this.str2TimeStamp(req.getParameter("endDateStr"));
            if (endTime!=null)paramMap.put("endTime", endTime);
            ViewControllerUtil.setSearchOwnerInfo(req, paramMap);

            List<ReportViewPo> dataList = reportViewService.searchReportList(paramMap);
            int count = dataList!=null?dataList.size():0;
            retMap.put("total", new Integer(count));
            retMap.put("rows", dataList);            
        }catch(Exception ex){
            logger.error("failed to search report list.",ex);
        }
        return retMap;
    }

    /**
     * 条件查询报告列表，分页显示符合条件的数据
     * @param req
     * @return
     */
    @RequestMapping("searchReportPageList.do")
    public @ResponseBody Map<String,Object> searchReportPageList(HttpServletRequest req){
        Map<String,Object> retMap = new HashMap<String,Object>();
        try {
            Map<String,Object> paramMap = new HashMap<String,Object>();
            String reportName = this.trimStr(req.getParameter("searchStr"));
            if (reportName!=null) paramMap.put("searchStr", reportName);
            Timestamp startTime = this.str2TimeStamp(req.getParameter("startDateStr"));
            if (startTime!=null) paramMap.put("startTime", startTime);
            Timestamp endTime = this.str2TimeStamp(req.getParameter("endDateStr"));
            if (endTime!=null)paramMap.put("endTime", endTime);
            
			int pageNumber = Integer.parseInt(req.getParameter("pageNumber"));
			paramMap.put("pageNumber", pageNumber);
			int pageSize = Integer.parseInt(req.getParameter("pageSize"));
			paramMap.put("pageSize", pageSize);

            ViewControllerUtil.setSearchOwnerInfo(req, paramMap);

            retMap = reportViewService.searchReportPageList(paramMap);
        }catch(Exception ex){
            logger.error("failed to search report list.",ex);
        }
        return retMap;
    }

    /**
     * 查看最新生成的还未看过的报告
     * @param param
     * @param req
     * @return
     */
    @RequestMapping("searchNewReport.do")
    public @ResponseBody Map<String,Object> searchNewReport(HttpServletRequest req){
        Map<String,Object> retMap = new HashMap<String,Object>();
        List<NewReportBean> dataList = new ArrayList<NewReportBean>(); 
        try{
            Owner o = SessionUtils.getOwner(req.getSession());
            List<ReportPo> nvrl = rvService.getNoVisitList(o);
            if (nvrl!=null) {
                for (ReportPo rp: nvrl) {
                    dataList.add(new NewReportBean(rp.getId(), rp.getReportName(), rp.getReportType(), DateUtils.convert2TimeChineseStr(rp.getCTime())));
                }
            }
            if (dataList.size()>0) {
                retMap.put("total", dataList.size());
                retMap.put("rows", dataList);
            } else {
                retMap.put("total", 0);
                retMap.put("rows", null);
            }
        }catch(Exception ex){
            logger.error("failed to search new report. ",ex);
        }
        return retMap;
    }

    /**
     * 查询指定reportId的报告关系信息
     * @param req
     * @return
     */
    @RequestMapping("searchReportRelation.do")
    public @ResponseBody Map<String,Object> searchReportRelation(HttpServletRequest req){
        Map<String,Object> retMap = new HashMap<String,Object>();
        try{
            //***组装力向导图数据
            Map<String,Object> forceMap = new HashMap<String,Object>();
            retMap.put("forceData", forceMap);
            //标题信息
            forceMap.put("title", "报告1");
            forceMap.put("subTitle", "");
            //节点信息
            List<Map<String,Object>> nodesList = new ArrayList<Map<String,Object>>(); 
            forceMap.put("nodes", nodesList);
            //{category:0, name: '报告1', value : 10, label: '报告1\n（主要）'},
            Map<String,Object> nodeMap = new HashMap<String,Object>();
            nodesList.add(nodeMap);            
            nodeMap.put("category", "0");
            nodeMap.put("name", "报告1");
            nodeMap.put("value", "10");
            nodeMap.put("label", "报告1\n（主要）");
            //{category:1, name: '报告2',value : 2},
            nodeMap = new HashMap<String,Object>();
            nodesList.add(nodeMap);    
            nodeMap.put("category", "1");
            nodeMap.put("name", "报告2");
            nodeMap.put("value", "2");
            //{category:1, name: '报告3',value : 3},
            nodeMap = new HashMap<String,Object>();
            nodesList.add(nodeMap);    
            nodeMap.put("category", "1");
            nodeMap.put("name", "报告3");
            nodeMap.put("value", "3");
            //{category:1, name: '报告4',value : 7},
            nodeMap = new HashMap<String,Object>();
            nodesList.add(nodeMap);    
            nodeMap.put("category", "1");
            nodeMap.put("name", "报告4");
            nodeMap.put("value", "7");
            //{category:2, name: '文件1',value : 5},
            nodeMap = new HashMap<String,Object>();
            nodesList.add(nodeMap);    
            nodeMap.put("category", "2");
            nodeMap.put("name", "文件1");
            nodeMap.put("value", "5");
            //{category:2, name: '文件2',value : 8},
            nodeMap = new HashMap<String,Object>();
            nodesList.add(nodeMap);    
            nodeMap.put("category", "2");
            nodeMap.put("name", "文件2");
            nodeMap.put("value", "8");
            //{category:2, name: '文件3',value : 9},
            nodeMap = new HashMap<String,Object>();
            nodesList.add(nodeMap);    
            nodeMap.put("category", "2");
            nodeMap.put("name", "文件3");
            nodeMap.put("value", "9");
            //{category:2, name: '文件4',value : 4},
            nodeMap = new HashMap<String,Object>();
            nodesList.add(nodeMap);    
            nodeMap.put("category", "2");
            nodeMap.put("name", "文件4");
            nodeMap.put("value", "4");
            //{category:2, name: '文件5',value : 6}
            nodeMap = new HashMap<String,Object>();
            nodesList.add(nodeMap);    
            nodeMap.put("category", "2");
            nodeMap.put("name", "文件5");
            nodeMap.put("value", "6");
            
            //关系信息
            List<Map<String,Object>> linksList = new ArrayList<Map<String,Object>>(); 
            forceMap.put("links", linksList);
            //{source : '报告2', target : '报告1', weight : 1, name: '子报告'},
            Map<String,Object> linkMap = new HashMap<String,Object>();
            linksList.add(linkMap);            
            linkMap.put("source", "报告2");
            linkMap.put("target", "报告1");
            linkMap.put("weight", new Integer(1));
            linkMap.put("name", "子报告");
            //{source : '报告3', target : '报告1', weight : 2, name: '父报告'},
            linkMap = new HashMap<String,Object>();
            linksList.add(linkMap);            
            linkMap.put("source", "报告3");
            linkMap.put("target", "报告1");
            linkMap.put("weight", new Integer(2));
            linkMap.put("name", "父报告");
            //{source : '报告4', target : '报告1', weight : 2},
            linkMap = new HashMap<String,Object>();
            linksList.add(linkMap);            
            linkMap.put("source", "报告4");
            linkMap.put("target", "报告1");
            linkMap.put("weight", new Integer(2));
            //{source : '文件1', target : '报告1', weight : 3, name: '报告引用该文件'},
            linkMap = new HashMap<String,Object>();
            linksList.add(linkMap);            
            linkMap.put("source", "文件1");
            linkMap.put("target", "报告1");
            linkMap.put("weight", new Integer(3));
            linkMap.put("name", "报告引用该文件");
            //{source : '文件2', target : '报告1', weight : 1},
            linkMap = new HashMap<String,Object>();
            linksList.add(linkMap);            
            linkMap.put("source", "文件2");
            linkMap.put("target", "报告1");
            linkMap.put("weight", new Integer(1));
            //{source : '文件3', target : '报告1', weight : 6, name: '报告引用该文件'},
            linkMap = new HashMap<String,Object>();
            linksList.add(linkMap);            
            linkMap.put("source", "文件3");
            linkMap.put("target", "报告1");
            linkMap.put("weight", new Integer(6));
            linkMap.put("name", "报告引用该文件");
            //{source : '文件4', target : '报告1', weight : 1, name: '报告引用该文件'},
            linkMap = new HashMap<String,Object>();
            linksList.add(linkMap);            
            linkMap.put("source", "文件4");
            linkMap.put("target", "报告1");
            linkMap.put("weight", new Integer(1));
            linkMap.put("name", "报告引用该文件");
            //{source : '文件5', target : '报告1', weight : 1},
            linkMap = new HashMap<String,Object>();
            linksList.add(linkMap);            
            linkMap.put("source", "文件5");
            linkMap.put("target", "报告1");
            linkMap.put("weight", new Integer(1));
            //{source : '文件2', target : '报告3', weight : 1},
            linkMap = new HashMap<String,Object>();
            linksList.add(linkMap);            
            linkMap.put("source", "文件2");
            linkMap.put("target", "报告3");
            linkMap.put("weight", new Integer(1));
            //{source : '文件2', target : '报告4', weight : 1},
            linkMap = new HashMap<String,Object>();
            linksList.add(linkMap);            
            linkMap.put("source", "文件2");
            linkMap.put("target", "报告4");
            linkMap.put("weight", new Integer(1));
            //{source : '文件2', target : '文件1', weight : 1},
            linkMap = new HashMap<String,Object>();
            linksList.add(linkMap);            
            linkMap.put("source", "文件2");
            linkMap.put("target", "文件1");
            linkMap.put("weight", new Integer(1));
            //{source : '文件3', target : '文件2', weight : 6},
            linkMap = new HashMap<String,Object>();
            linksList.add(linkMap);            
            linkMap.put("source", "文件3");
            linkMap.put("target", "文件2");
            linkMap.put("weight", new Integer(6));
            //{source : '文件5', target : '文件2', weight : 1}
            linkMap = new HashMap<String,Object>();
            linksList.add(linkMap);            
            linkMap.put("source", "文件5");
            linkMap.put("target", "文件2");
            linkMap.put("weight", new Integer(1));
                    
            
            //***组装tabs表数据
            //报告-报告关系表
            Map<String,Object> tbRepRepMap = new HashMap<String,Object>();
            retMap.put("tbRepRep", tbRepRepMap); 
            //存入数据
            List<Map<String,Object>> tbRepRepList = new ArrayList<Map<String,Object>>();
            tbRepRepMap.put("total", new Integer(6));
            tbRepRepMap.put("rows", tbRepRepList);
            //存入一行数据
            Map<String,Object> aRepRepMap = new HashMap<String,Object>();
            tbRepRepList.add(aRepRepMap);
            aRepRepMap.put("src", "报告1");
            aRepRepMap.put("dest", "报告2");
            aRepRepMap.put("rel", "父报告");
            //存入一行数据
            aRepRepMap = new HashMap<String,Object>();
            tbRepRepList.add(aRepRepMap);
            aRepRepMap.put("src", "报告1");
            aRepRepMap.put("dest", "报告3");
            aRepRepMap.put("rel", "父报告");
            //存入一行数据
            aRepRepMap = new HashMap<String,Object>();
            tbRepRepList.add(aRepRepMap);
            aRepRepMap.put("src", "报告1");
            aRepRepMap.put("dest", "报告4");
            aRepRepMap.put("rel", "子报告");
            //存入一行数据
            aRepRepMap = new HashMap<String,Object>();
            tbRepRepList.add(aRepRepMap);
            aRepRepMap.put("src", "报告2");
            aRepRepMap.put("dest", "报告3");
            aRepRepMap.put("rel", " -- ");
            //存入一行数据
            aRepRepMap = new HashMap<String,Object>();
            tbRepRepList.add(aRepRepMap);
            aRepRepMap.put("src", "报告2");
            aRepRepMap.put("dest", "报告4");
            aRepRepMap.put("rel", " -- ");
            //存入一行数据
            aRepRepMap = new HashMap<String,Object>();
            tbRepRepList.add(aRepRepMap);
            aRepRepMap.put("src", "报告3");
            aRepRepMap.put("dest", "报告4");
            aRepRepMap.put("rel", "子报告");
            //存入总数和列表
            tbRepRepMap.put("total", new Integer(tbRepRepList.size()));
            tbRepRepMap.put("rows", tbRepRepList);
            
            //报告-文件关系表
            Map<String,Object> tbRepFileMap = new HashMap<String,Object>();
            retMap.put("tbRepFile", tbRepFileMap);
            //存入数据
            List<Map<String,Object>> tbRepFileList = new ArrayList<Map<String,Object>>();
            //存入一行数据
            Map<String,Object> aRepFileMap = new HashMap<String,Object>();
            tbRepFileList.add(aRepFileMap);
            aRepFileMap.put("src", "报告1");
            aRepFileMap.put("dest", "文件1");
            aRepFileMap.put("rel", "全部引用该文件");
            //存入一行数据
            aRepFileMap = new HashMap<String,Object>();
            tbRepFileList.add(aRepFileMap);
            aRepFileMap.put("src", "报告1");
            aRepFileMap.put("dest", "文件2");
            aRepFileMap.put("rel", "全部引用该文件");
            //存入一行数据
            aRepFileMap = new HashMap<String,Object>();
            tbRepFileList.add(aRepFileMap);
            aRepFileMap.put("src", "报告1");
            aRepFileMap.put("dest", "文件3");
            aRepFileMap.put("rel", "部分引用该文件");
            //存入一行数据
            aRepFileMap = new HashMap<String,Object>();
            tbRepFileList.add(aRepFileMap);
            aRepFileMap.put("src", "报告1");
            aRepFileMap.put("dest", "文件4");
            aRepFileMap.put("rel", "部分引用该文件");
            //存入一行数据
            aRepFileMap = new HashMap<String,Object>();
            tbRepFileList.add(aRepFileMap);
            aRepFileMap.put("src", "报告1");
            aRepFileMap.put("dest", "文件5");
            aRepFileMap.put("rel", "全部引用该文件");
            //存入一行数据
            aRepFileMap = new HashMap<String,Object>();
            tbRepFileList.add(aRepFileMap);
            aRepFileMap.put("src", "报告3");
            aRepFileMap.put("dest", "文件2");
            aRepFileMap.put("rel", "全部引用该文件");
            //存入一行数据
            aRepFileMap = new HashMap<String,Object>();
            tbRepFileList.add(aRepFileMap);
            aRepFileMap.put("src", "报告4");
            aRepFileMap.put("dest", "文件2");
            aRepFileMap.put("rel", "部分引用该文件");
            //存入总数和列表
            tbRepFileMap.put("total", new Integer(tbRepFileList.size()));
            tbRepFileMap.put("rows", tbRepFileList);
        }catch(Exception ex){
            logger.error("failed to search new report. ",ex);
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
    
    /**
     * 将时间字符串转化为Timestamp
     * @param astrTime
     * @return
     */
    private Timestamp str2TimeStamp(String astrTime){
        Timestamp retTime = null;        
        try{
            String strTime = trimStr(astrTime);
            if(strTime!=null){
                if(strTime.indexOf(" ")==-1){
                    strTime += " 00:00:00";
                }
                Date dt = DateUtils.getDateTime("yyyy-MM-dd HH:mm:ss", strTime);
                retTime = new Timestamp(dt.getTime());
            }
        }catch(Exception ex){
            logger.error("failed to parse timestamp. strTime="+astrTime,ex);
        }
        return retTime;
    }
    
    
    /**
     * 新生成报告BEAN
     * @author yfo
     *
     */
    class NewReportBean implements Serializable{
        /**
         * 
         */
        private static final long serialVersionUID = 558467119425972711L;
        public String reportId;
        public String reportName;
        public String reportCategory;
        public String createDate;
        public NewReportBean(){
            
        }
        public NewReportBean(String reportId,String reportName,String reportCategory,String createDate){
            this.reportId = reportId;
            this.reportName = reportName;
            this.reportCategory = reportCategory;
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
        public String getReportCategory() {
            return reportCategory;
        }
        public void setReportCategory(String reportCategory) {
            this.reportCategory = reportCategory;
        }
        public String getCreateDate() {
            return createDate;
        }
        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }
    }
}
