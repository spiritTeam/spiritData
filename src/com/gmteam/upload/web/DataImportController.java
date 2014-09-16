package com.gmteam.upload.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gmteam.framework.core.web.AbstractFileUploadController;
import com.gmteam.importdata.excel.ExcelContentAttributes;
import com.gmteam.importdata.excel.pojo.ColumnInfo;
import com.gmteam.importdata.excel.service.DataImportService;
import com.gmteam.importdata.excel.util.CommonUtils;
import com.gmteam.importdata.excel.util.HSSFPoiUtil;
import com.gmteam.importdata.excel.util.SheetInfo;
import com.gmteam.importdata.excel.util.XSSFPoiUtil;

/** 
 * @author 
 * @version  
 * 类说明 包括文件上传，poi解析，
 * 数据存储，比对分析。
 */
@Controller
public class DataImportController extends AbstractFileUploadController {
    CommonUtils cu = new CommonUtils();
    @Resource
    private DataImportService dataImportService;
    /**数据map*/
    private Map<SheetInfo, Object[][]> dataMap ;
    @SuppressWarnings("unchecked")
    @Override
    public @ResponseBody Map<String, Object> afterUploadOneFileOnSuccess(
            Map<String, Object> uploadInfoMap, Map<String, Object> rqtAttrs,
            Map<String, Object> rqtParams) {
        ExcelContentAttributes contentAttributes = new ExcelContentAttributes();
        /**上传路径id*/
        String filePath = (String) uploadInfoMap.get("storeFilename");
        contentAttributes.setSavePath(filePath);
        /**poi类*/
        Object poi = new HSSFPoiUtil();
        try {
            dataMap=((HSSFPoiUtil) poi).getMessageMap(filePath);//倒入数据2003
        }catch(Exception e){
        }
        if (dataMap==null) {
            poi = new XSSFPoiUtil();
            try {
                //dataMap=((XSSFPoiUtil) poi).getMessageMap(filePath);//倒入数据2007+
            }catch(Exception e) {}
        }
        contentAttributes.setUploadInfoMap(uploadInfoMap);
        /**
         * 获取前台交互信息map
         */
        List<Map<String, Object>> title_ColumnInfo_DataListMapList = dataImportService.getTitle_ColumnInfo_DataListMap(dataMap);
        List<Map<String,Object>> pkCacheMapList = new ArrayList<Map<String,Object>>();
        for(Map<String, Object> title_ColumnInfo_DataListMap:title_ColumnInfo_DataListMapList){
            Map<String,Object> singlePkCacheMap = new HashMap<String,Object>();
            /**获取titleList*/
            List<String> titleList = (List<String>) title_ColumnInfo_DataListMap.get("titleList");
            /**sheetName*/
            String sheetName = (String) title_ColumnInfo_DataListMap.get("sheetName");
            /**获取columnInfoList*/
            List<ColumnInfo> columnInfoList = (List<ColumnInfo>) title_ColumnInfo_DataListMap.get("columnInfoList");
            /** 获取dataList*/
            List<Object[]> dataList = (List<Object[]>) title_ColumnInfo_DataListMap.get("dataList");
            singlePkCacheMap.put("titleList", titleList);
            try {
                String cacheId = cu.getUUID();
                singlePkCacheMap.put("cacheId", cacheId);
                dataImportService.putUploadDataInCache(dataMap,contentAttributes,cacheId);
            } catch (Exception e) {
                System.out.println("上传数据放入缓存中失败");
                e.printStackTrace();
            }
            Map<String,Object> matchInfoMap = dataImportService.getColumnPk(columnInfoList);
            boolean matchResult = (Boolean) matchInfoMap.get("Match");
            String matchInfoSb = "";
            String pkName ="";
            String dataTableName="";
            Integer pkIndex =0;
            String nullMessage="";
            String repeatMessage="";
            Map<String,Object> checkResultMap;
            if(matchResult){
                pkName = (String) matchInfoMap.get("pkName");
                pkIndex = (Integer)matchInfoMap.get("pkIndex");
                checkResultMap = dataImportService.checkPkNullAndRepeat(sheetName,dataList,pkName,pkIndex);
                nullMessage = (String)checkResultMap.get("nullMessage");
                repeatMessage = (String)checkResultMap.get("repeatMessage");
                dataTableName = (String) matchInfoMap.get("dataTableName");
                matchInfoSb = "已匹配到相同结构的数据表\""+dataTableName+"\"，主键名称是\""+pkName+"\"。";
            }else{
                matchInfoSb ="未匹配到相同结构的数据表。";
            }
            String matchInfo = matchInfoSb.toString();
            if(nullMessage!=null){
                singlePkCacheMap.put("nullMessage", nullMessage);
            }
            if(repeatMessage!=null){
                singlePkCacheMap.put("repeatMessage", repeatMessage);
            }
            singlePkCacheMap.put("dataTableName", dataTableName);
            singlePkCacheMap.put("pkName", pkName);
            singlePkCacheMap.put("sheetName", sheetName);
            singlePkCacheMap.put("matchInfo", matchInfo);
            singlePkCacheMap.put("matchResult", matchResult);
            pkCacheMapList.add(singlePkCacheMap);
        }
        uploadInfoMap.put("pkCacheMapList", pkCacheMapList);
        return uploadInfoMap;
    }
    @Override
    public void afterUploadAllFiles(List<Map<String, Object>> fl,
        Map<String, Object> rqtAttrs, Map<String, Object> rqtParams) {
    }
}
