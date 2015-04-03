package com.spiritdata.dataanal.expreport.word.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.spiritdata.dataanal.UGA.pojo.User;
import com.spiritdata.dataanal.report.model.Report;
import com.spiritdata.dataanal.report.service.ReportService;
import com.spiritdata.jsonD.model.JsonD;
import com.spiritdata.jsonD.util.JsonUtils;
import com.spiritdata.jsonD.web.service.JsonDService;

/**
 * 导出word report服务
 * @author mht
 */
@Service
public class WordService {
    /**
     * report-->report;
     * obj模型，通过json2Obj转换而来
     */
    private Report report;
    /**
     * 用于json-->jsond类
     * obj模型，通过json2Obj转换而来
     */
    private List<JsonD> jsonDList = new ArrayList<JsonD>();
    /**
     * 用于获取report
     */
    @Resource
    private ReportService reportSerivce;
    /**
     * 用于获取jsond
     */
    @Resource
    private JsonDService jsonDSerive;
    /**
     * 入口方法：
     * @param userInfo 用户信息
     * @param reportId 报告id，
     */
    public Map<String,Object> expWord(String reportId, User userInfo,List<String> jsonDIdList){
        
        //1、获得report及jsond
        initReportAndJsonD(reportId,jsonDIdList);
        //2、bulidWord
        Map<String,Object> retMap = bulidWord();
        return retMap;
    }
    
    /**
     * 创建word
     * @return 
     */
    private Map<String, Object> bulidWord() {
        
        return null;
    }
    
    /**
     * 得到jsond和report
     * @param reportId
     * @param jsonDIdList
     */
    private void initReportAndJsonD(String reportId, List<String> jsonDIdList) {
        //report
        String reportJson =  reportSerivce.getReportJsonById(reportId);
        this.report = (Report) JsonUtils.jsonToObj(reportJson, Report.class);
        
        //jsond:如果report中有dataList的get方法，report.getDataList()可以获得，如果没有，则通过id获取
        for(String jsonDId :jsonDIdList){
            String jsonDjson =  jsonDSerive.getJsonDById(jsonDId);
            JsonD jsonD = (JsonD) JsonUtils.jsonToObj(jsonDjson, JsonD.class);
            this.jsonDList.add(jsonD);
        }
        
    }
}
