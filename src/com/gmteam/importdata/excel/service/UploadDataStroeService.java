package com.gmteam.importdata.excel.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.gmteam.framework.core.cache.CacheEle;
import com.gmteam.framework.core.cache.SystemCache;
import com.gmteam.framework.core.model.BaseObject;
import com.gmteam.importdata.excel.ExcelContentAttributes;
import com.gmteam.importdata.excel.pojo.ColumnInfo;
import com.gmteam.importdata.excel.pojo.DataSignOrg;
import com.gmteam.importdata.excel.pojo.PkNameCacheIdSign;
import com.gmteam.importdata.excel.pojo.SaveResultInfo;
import com.gmteam.importdata.excel.util.SheetInfo;

/** 
 * @author 
 * @version  
 * 类说明 
 */
@SuppressWarnings("serial")
@Component
public class UploadDataStroeService extends BaseObject {
    @SuppressWarnings("unchecked")
    public Map<String,Object> getUploadDataCacheMap(String cacheId) {
        CacheEle<?> uploadDataCacheEle = SystemCache.getCache(cacheId);
        Map<String,Object> uploadDataMap = (Map<String, Object>) uploadDataCacheEle.getContent();
        return uploadDataMap;
    }
    @Resource
    DataImportService dataImportService;
    @SuppressWarnings("unchecked")
    public SaveResultInfo saveInDB(ExcelContentAttributes saveAttributes, Map<String, Object> uploadDataMap, Map<SheetInfo, Object[][]> dataMap){
        SaveResultInfo saveResultInfo = new SaveResultInfo();
        String sheetName = saveAttributes.getSheetName();
        Integer allRows = 0;
        Integer insertRows = 0;
        Map<String,Object> uploadInfoMap = saveAttributes.getUploadInfoMap();
        Integer sheetSize = dataMap.size();
        /**上传日志id*/
        String logId = dataImportService.saveFileUploadLog(uploadInfoMap,sheetSize);
        saveAttributes.setLogId(logId);
        /**数据map*/
        Iterator<SheetInfo> it = dataMap.keySet().iterator();
        while(it.hasNext()){
            SheetInfo sheetInfo = it.next();
            if(sheetName.equals(sheetInfo.getSheetName())){
                Integer sheetIndex = sheetInfo.getSheetIndex();
                saveAttributes.setSheetIndex(sheetIndex);
                Object[][] sheetData = dataMap.get(sheetInfo);
                allRows = sheetData.length-2;
                /**列标题*/
                Object[] tt =  sheetData[0];
                String[] title = new String[tt.length] ;
                for(int i=0;i<tt.length;i++){
                    title[i]=tt[i].toString();
                }
                /**数据*/
                List<Object[]> dataList = new ArrayList<Object[]>();
                /**数据类型集合*/
                Object[] dataTypeAry = new Object[title.length];
                for(int i=1;i<sheetData.length;i++){
                    if(i<sheetData.length-1){
                        Object[] pi = sheetData[i];
                        dataList.add(pi); 
                    }else{
                        dataTypeAry =  sheetData[i];
                    }
                }
                saveAttributes.setDataList(dataList);
                List<ColumnInfo> columnInfoList = new ArrayList<ColumnInfo>();
                for(int i=0;i<title.length;i++){
                    ColumnInfo mci = new ColumnInfo();
                    mci.setTitleIndex(i);
                    mci.setTitleName(title[i]);
                    mci.setTitleType(dataTypeAry[i]+"");
                    columnInfoList.add(mci);
                }
                saveAttributes.setColumnInfo(columnInfoList);
                /***/
                String tableName = "";
                String tableId = "";
                List<String> dataIdList = new ArrayList<String>();
                try {
                    /**
                     * 获取判断结果(判断数据表是否与excel类型一样)
                     * 并编排excel数据的对应顺序
                     */
                    Map<String,Object> matchInfoMap = dataImportService.matchData(saveAttributes);
                    /**
                    * 包含
                    */
                   if((Boolean) matchInfoMap.get("Match")){
                       //获取对应tableId
                       tableId = (String)matchInfoMap.get("tableId");
                       //获取对应dataTableName
                       tableName = (String) matchInfoMap.get("dataTableName");
                       /**
                        * 放入调整顺序后的dataList
                        */
                       List<Object[]> newDataList = (List<Object[]>) matchInfoMap.get("newDataList");
                       saveAttributes.setDataList(newDataList);
                       saveAttributes.setTableName(tableName);
                       saveAttributes.setTableId(tableId);
                       /**
                        * 返回null,标识不包含Sign,返回不是null，包含返回一个map
                        */
                       Map<String,Object> resultMap = includeSign(saveAttributes,matchInfoMap,tableId);
                       if(resultMap!=null){
                           /**
                            * 包含sign，
                            * 如果pk重复update，pk不重复，insert
                            */
                           insertRows = dataImportService.insertData(saveAttributes,resultMap);
                       }else{
                           /**
                            * 不包含sign的，执行insert
                            */
                           //储存Table与log关系信息
                           dataImportService.saveLogTableOrgInfo(saveAttributes);
                           //保存数据
                           dataIdList = dataImportService.insertData(saveAttributes);
                           insertRows = dataIdList.size();
                           saveAttributes.setDataIdList(dataIdList);
                           dataImportService.saveDataSignOrg(saveAttributes);
                       }
                   }else{
                       /**
                        * 不包含
                        */
                       String pkName = saveAttributes.getPkName();
                       Integer pkIndex = 0;
                       for(int i=0;i<title.length;i++){
                           if(title[i].equals(pkName)){
                               pkIndex = i;
                           }
                       }
                       Map<String,Object> checkResultMap = dataImportService.checkPkNullAndRepeat(sheetName,dataList, pkName, pkIndex);
                       String nullMessage = (String) checkResultMap.get("nullMessage");
                       String repeatMessage = (String) checkResultMap.get("repeatMessage");
                       if(nullMessage==null&&repeatMessage==null){
                           //创建数据表，并插入数据
                           tableName = dataImportService.createDataTable(saveAttributes);
                           saveAttributes.setTableName(tableName);
                           //储存表信息
                           tableId = dataImportService.saveTitleInfo(saveAttributes);
                           saveAttributes.setTableId(tableId);
                           //储存列信息
                           dataImportService.saveColumnInfo(saveAttributes);
                           //储存Table与log关系信息
                           dataImportService.saveLogTableOrgInfo(saveAttributes);
                           //储存数据
                           dataIdList = dataImportService.insertData(saveAttributes);
                           insertRows = dataIdList.size();
                           saveAttributes.setDataIdList(dataIdList);
                           dataImportService.saveDataSignOrg(saveAttributes);
                       }else{
                           saveResultInfo.setSheetName(sheetName);
                           saveResultInfo.setSaveResult(false);
                           saveResultInfo.setNullMessage(nullMessage);
                           saveResultInfo.setRepeatMessage(repeatMessage);
                           return saveResultInfo;
                       }
                   }
                   /**
                    * 刷新缓存
                    */
                    dataImportService.refulshCache();
                } catch (Exception e) {
                    dataImportService.refulshCache();
                    e.printStackTrace();
                }
            }
        }
        saveResultInfo.setSheetName(sheetName);
        saveResultInfo.setSaveResult(true);
        saveResultInfo.setAllRows(allRows);
        saveResultInfo.setInsertRows(insertRows);
        return saveResultInfo;
    }
    /**
     * 判断是否包含Sgin
     * @param saveAttributes 
     * @param matchInfoMap 
     * @param tableId 
     */
    @SuppressWarnings("unchecked")
    private Map<String,Object> includeSign(ExcelContentAttributes saveAttributes, Map<String, Object> matchInfoMap, String tableId) {
        Map<String,Object> resultMap = new HashMap<String, Object>();
        String sign = saveAttributes.getSign();
        Map<String,Object> matchDataCacheContentMap = (Map<String, Object>) matchInfoMap.get("matchDataCacheContent");
        Map<String,Map<Integer,Map<String,String>>> pkListMap = (Map<String,Map<Integer,Map<String,String>>>) matchDataCacheContentMap.get("pkListMap");
        Map<String,List<DataSignOrg>> dataSignOrgMap = (Map<String, List<DataSignOrg>>) matchDataCacheContentMap.get("dataSignOrgMap");
        if(pkListMap.size()==0){
            return null;
        }else{
            Iterator<String> it = pkListMap.keySet().iterator();
            while(it.hasNext()){
                String tId = it.next();
                Map<Integer,Map<String,String>> tab_PkListMap = pkListMap.get(tId);
                if(tableId.equals(tId)){
                    List<DataSignOrg> dataSignOrgList = dataSignOrgMap.get(tableId);
                    if(dataSignOrgList==null||dataSignOrgList.size()==0){
                        return null;
                    }else{
                        List<DataSignOrg> targetDataSignOrgList = new ArrayList<DataSignOrg>();
                        for(DataSignOrg dataSignOrg :dataSignOrgList){
                            if(sign.equals(dataSignOrg.getSign())){
                                targetDataSignOrgList.add(dataSignOrg);
                            }
                        }
                        if(targetDataSignOrgList.size()==0){
                            return null;
                        }else{
                            resultMap.put("targetDataSignOrgList", targetDataSignOrgList);
                            Map<Integer, Map<String, String>> targetDataIdPkMap = getTargetDataIdPkMap(tab_PkListMap,targetDataSignOrgList);
                            resultMap.put("targetDataIdPkMap", targetDataIdPkMap);
                            return resultMap;
                        }
                    }
                }
            }
            return null;
        }
    }
    private Map<Integer, Map<String, String>> getTargetDataIdPkMap(Map<Integer, Map<String, String>> tab_PkListMap,List<DataSignOrg> targetDataSignOrgList) {
        Map<Integer, Map<String, String>> targetDataIdPkMap = new HashMap<Integer, Map<String,String>>();
        Iterator<Integer> it = tab_PkListMap.keySet().iterator();
        while(it.hasNext()){
            Integer pkIndex = it.next();
            Map<String, String> dataIdPkMap = tab_PkListMap.get(pkIndex);
            Map<String, String> targetIdPkMap = new HashMap<String, String>();
            for(DataSignOrg dataSignOrg :targetDataSignOrgList){
                String dataId = dataSignOrg.getDataId();
                String pk = dataIdPkMap.get(dataId)+"";
                if(pk!=null){
                    targetIdPkMap.put(dataId, pk);
                }
            }
            targetDataIdPkMap.put(pkIndex, dataIdPkMap);
        }
        return targetDataIdPkMap;
    }
    /**
     * 根据前台得到的字符串，拆分出自己想要的对象，
     * 变成集合返回
     * @param pkCacheIdSignStr
     * @return
     */
    public List<PkNameCacheIdSign> getPkCacheIdSignList(String pkCacheIdSignStr) {
        String pkCacheIdSign[] = pkCacheIdSignStr.split(";");
        List<PkNameCacheIdSign> pkNameCacheIdSignList = new ArrayList<PkNameCacheIdSign>();
        for(String str :pkCacheIdSign){
            PkNameCacheIdSign pcs = new PkNameCacheIdSign();
            String strArry[] = str.split(",");
            for(int i=0;i<strArry.length;i++){
               if(i==0){
                   pcs.setPkName(strArry[i]);
               }else if(i==1){
                   pcs.setSign(strArry[i]);
               }else if(i==2){
                   pcs.setCacheId(strArry[i]);
               }else{
                   pcs.setSheetName(strArry[i]);
               }
            }
            pkNameCacheIdSignList.add(pcs);
        }
        return pkNameCacheIdSignList;
    }
}
