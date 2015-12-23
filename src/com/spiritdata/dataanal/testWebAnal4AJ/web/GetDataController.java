package com.spiritdata.dataanal.testWebAnal4AJ.web;

import java.sql.SQLException;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.dataanal.testWebAnal4AJ.service.GetDataService;

@Controller
public class GetDataController {
    @Resource
    private GetDataService getDataService;

    @RequestMapping("historyHourVisit.do")
    public @ResponseBody
    Map<String, String> getHistoryHourVisit(HttpServletRequest req) throws SQLException {
        return getDataService.getHistoryHourVisit();
    }

    @RequestMapping("visitFrom.do")
    public @ResponseBody
    Map<String, String> getVisitFrom(HttpServletRequest req) throws SQLException {
        return getDataService.getVisitFrom();
    }

    @RequestMapping("initMonitor.do")
    public @ResponseBody
    Map<String, String> getIntervalVisit(HttpServletRequest req) throws SQLException {
        int minites = 1;//默认1分钟
        try {
            minites = Integer.parseInt(""+req.getParameter("minites"));
        } catch(Exception e) {
        }
        return getDataService.getVisitCount(minites);
    }

    @RequestMapping("getRealVisit.do")
    public @ResponseBody
    Map<String, String> getRealVisit(HttpServletRequest req) throws SQLException {
        int minites = 1;//默认1分钟
        String beginTime="";
        try {
            minites = Integer.parseInt(""+req.getParameter("minites"));
            beginTime = req.getParameter("beginTime");
        } catch(Exception e) {
        }
        return getDataService.getRealVisitCount(minites, beginTime==null?"":beginTime);
    }

    @RequestMapping("getRealCount.do")
    public @ResponseBody
    Map<String, String> getRealCount(HttpServletRequest req) throws SQLException {
        return getDataService.getRealCount();
    }

    @RequestMapping("perDate.do")
    public @ResponseBody
    Map<String, String> getPerDate(HttpServletRequest req) throws SQLException {
        return getDataService.getPerDate();
    }
}