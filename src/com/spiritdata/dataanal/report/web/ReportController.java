package com.spiritdata.dataanal.report.web;

import java.util.HashMap;
import java.util.Map;

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
    public Map<String, Object> getReport(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> ret = new HashMap<String, Object>();
        String reportId = request.getParameter("reportId");
        if (reportId!=null) {
            try {
                ret=reportService.getReportJsonById(reportId);
            } catch(Exception e) {
                ret.put("jsonType", 0);
                ret.put("message", e.getMessage());
            }
        }
        String uri = request.getParameter("uri");
        if (uri!=null) {
            try {
                ret=reportService.getReportJsonByUri(uri);
            } catch(Exception e) {
                ret.put("jsonType", 0);
                ret.put("message", e.getMessage());
            }
        }
        if (ret==null) {
            ret = new HashMap<String, Object>();
            ret.put("jsonType", 0);
            ret.put("message", "空");
        }
        return ret;
    }
}