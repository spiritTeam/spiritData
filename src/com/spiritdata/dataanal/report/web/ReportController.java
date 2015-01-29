package com.spiritdata.dataanal.report.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.dataanal.report.service.ReportService;

/**
 * 报告功能的web控制类
 * @author wh
 */
@Controller
public class ReportController {
    @Resource
    private ReportService reportService;
    @RequestMapping(value="/report/getReport.do")
    @ResponseBody
    public String getReport(HttpServletRequest request, HttpServletResponse response) {
        String ret = null;
        String reportId = request.getParameter("reportId");
        if (reportId!=null) {
            try {
                ret="{jsonType:1, data:"+reportService.getReportJsonById(reportId)+"}";
            } catch(Exception e) {
                ret="{jsonType:0, message:'"+e.getMessage()+"'}";
            }
        }
        String uri = request.getParameter("uri");
        if (uri!=null) {
            try {
                ret="{jsonType:1, data:"+reportService.getReportJsonByUri(uri)+"}";
            } catch(Exception e) {
                ret="{jsonType:0, message:'"+e.getMessage()+"'}";
            }
        }
        return ret;
    }
}