package com.gmteam.spiritdata.importdata.excel.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.gmteam.framework.core.cache.CacheEle;
import com.gmteam.framework.core.cache.SystemCache;
import com.gmteam.framework.core.model.BaseObject;
import com.gmteam.spiritdata.cache.ExcelCacheLifecycleUnit;
import com.gmteam.spiritdata.importdata.excel.ExcelConstants;
import com.gmteam.spiritdata.importdata.excel.ExcelContentAttributes;
import com.gmteam.spiritdata.importdata.excel.service.pojoservice.ColumnInfoService;
import com.gmteam.spiritdata.importdata.excel.service.pojoservice.DataSignOrgService;
import com.gmteam.spiritdata.importdata.excel.service.pojoservice.DataUploadLogService;
import com.gmteam.spiritdata.importdata.excel.service.pojoservice.LogTableOrgService;
import com.gmteam.spiritdata.importdata.excel.service.pojoservice.TableInfoService;
import com.gmteam.spiritdata.importdata.excel.storepojo.ColumnInfo;
import com.gmteam.spiritdata.importdata.excel.storepojo.DataSignOrg;
import com.gmteam.spiritdata.importdata.excel.storepojo.DataUploadLog;
import com.gmteam.spiritdata.importdata.excel.storepojo.LogTableOrg;
import com.gmteam.spiritdata.importdata.excel.storepojo.TableInfo;
import com.gmteam.spiritdata.importdata.excel.util.CommonUtils;
import com.gmteam.spiritdata.importdata.excel.util.SheetInfo;
import com.gmteam.spiritdata.matedata.relation.MetaColumnInfo;
import com.gmteam.spiritdata.matedata.relation.MetaInfo;

import org.apache.commons.dbcp.BasicDataSource;
/** 
 * @author mht
 * @version  
 * 类说明 
 */
@Component
@SuppressWarnings("serial")
public  class DataImportService extends BaseObject {
    /**
     * 获得dataSource
     */
    @Resource(name="dataSource")
    private BasicDataSource ds;
    /**
     * 获得上传记录Service
     */
    @Resource
    private DataUploadLogService  dataUploadLogService;
    /**
     * commonutils，用于获取uuid
     * 和截取的序列
     */
    CommonUtils cu = new CommonUtils();
    /**
     * 保存上传记录
     * @param uploadInfoMap
     * @param sheetSize 
     * @return
     */
    public String  saveFileUploadLog(Map<String,Object> uploadInfoMap, Integer sheetSize){
        Iterator<String> it = uploadInfoMap.keySet().iterator();
        while(it.hasNext()){
            String key = (String) it.next();
            Object value = uploadInfoMap.get(key);
            System.out.println(key + "→" + value);
        }
        String uuid = cu.getUUID();
        DataUploadLog dataUploadLog = new DataUploadLog();
        dataUploadLog.setId(uuid);
        dataUploadLog.setUploadUser("aaaa");
        dataUploadLog.setSourceFileName((String)uploadInfoMap.get("storeFilename"));
        dataUploadLog.setUploadDate(new Date());
        dataUploadLog.setSheetSize(sheetSize);
        dataUploadLog.setDescn("测试存入");
        dataUploadLogService.insertUploadLog(dataUploadLog);
        return uuid;
    }
    /**
     * 动态创建Datetable
     * @throws Exception 
     */
    public String createDataTable(ExcelContentAttributes contentAttributes) throws Exception {
        List<ColumnInfo> metaColumnInfoList = contentAttributes.getColumnInfo();
        int columnSize = metaColumnInfoList.size();
        String tableName = "MD_TAB_"+cu.getUUID2TableSeq(contentAttributes.getLogId())+contentAttributes.getSheetIndex();
        StringBuffer sbSQl = new StringBuffer("create table "+tableName+"( id varchar2(200) primary key,");
        for(int i=0;i<columnSize;i++){
            String columnName = "column"+metaColumnInfoList.get(i).getColumnIndex();
            String dataType = metaColumnInfoList.get(i).getColumnType();
            if(dataType.equals("String")){
                dataType = "varchar(200)";
            }else if(dataType.equals("Double")){
                dataType = "binary_double";
            }else if(dataType.equals("Date")){
                dataType = "date";
            }
            if(i<columnSize-1){
                sbSQl.append(columnName+" "+dataType+",");
            }else{
                sbSQl.append(columnName+" "+dataType+")");
            }
        }
        Map<String , Object> map=new HashMap<String , Object>();
        map.put("sql",sbSQl.toString()); 
        dataUploadLogService.createDataTableInfo(map);
        //向新建data表中插入数据
        return tableName;
    }
    /**
     * 保存Title到tableInfo
     */
    @Resource
    private TableInfoService tis;
    @Resource
    private ColumnInfoService cis;
    public String saveTableInfo(ExcelContentAttributes excelContentAttributes) throws SQLException {
        TableInfo ti = new TableInfo();
        String tableId = cu.getUUID();
        ti.setId(tableId);
        ti.setTableName(excelContentAttributes.getTableName());
        tis.insertTableInfo(ti);
        return tableId;
    }
    /**
     *日志与数据表关系，可以根据上传日志找到
     *对应的数据表
     */
    @Resource
    private LogTableOrgService logTableOrgService;
    public void saveLogTableOrgInfo(ExcelContentAttributes excelContentAttributes) {
        LogTableOrg lto = new LogTableOrg();
        lto.setId(cu.getUUID());
        lto.setLogId(excelContentAttributes.getLogId());
        lto.setTableId(excelContentAttributes.getTableId());
        logTableOrgService.insertLogTableOrg(lto);
    }
    /**
     * 向data表中插入数据
     * @param title
     * @param tableName
     * @param lO,metaInfo, "metaDataName"
     */
    public List<String> insertData(ExcelContentAttributes contentAttributes) {
        List<String> dataIdList = new ArrayList<String>();
        Connection insertDataConn = null;
        PreparedStatement ps = null;
        try {
            insertDataConn = ds.getConnection();
            String formatDate;
            for(Object[] o:contentAttributes.getDataList()){
                String dataId = cu.getUUID();
                dataIdList.add(dataId);
                StringBuffer sbSql = new StringBuffer("insert into "+contentAttributes.getTableName()+" values('"+dataId+"',");
                for(int i=0;i<o.length;i++){
                    Object oo = o[i];
                    if(oo!=null&&!oo.equals("")&&(oo.getClass()+"").equals("class java.util.Date")){
                        formatDate = cu.getFormatDate((Date)oo);
                        if(i!=o.length-1){
                            sbSql.append("to_timestamp('"+formatDate+"','yyyy-mm-dd hh24:mi:ss'),");
                        }else{
                            sbSql.append("to_timestamp('"+formatDate+"','yyyy-mm-dd hh24:mi:ss'))");
                        }
                    }else{
                        if(oo==null){
                            if(i!=o.length-1)sbSql.append(oo+",");
                            else sbSql.append(oo+")");
                        }else{
                            if(i!=o.length-1)sbSql.append("'"+oo+"',");
                            else sbSql.append("'"+oo+"')");
                        }
                    }
                }
                ps = insertDataConn.prepareStatement(sbSql.toString());
                ps.executeUpdate();
            }
            cu.closeConn(insertDataConn, ps, null);
            return dataIdList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }finally{
            cu.closeConn(insertDataConn, ps, null);
        }
    }
    /**
     * 保存列信息，如列类型，列
     * @param contentAttributes
     */
    public void saveColumnInfo(ExcelContentAttributes contentAttributes) {
        List<ColumnInfo> metaColumnInfoList = contentAttributes.getColumnInfo(); 
        int columnSize = metaColumnInfoList.size();
        Connection saveColumnInfoConn = null;
        PreparedStatement ps = null;
        try {
            String sql = "insert into MD_COLUMN_INFO(id,tableId,titleIndex,titleName,titleType,pk) values(?,?,?,?,?,?)";
            String pkName = contentAttributes.getPkName();
            saveColumnInfoConn = ds.getConnection();
            ps = saveColumnInfoConn.prepareStatement(sql);
            for(int i=0;i<columnSize;i++){
                ps.setObject(1, cu.getUUID());
                ps.setObject(2, contentAttributes.getTableId());
                ps.setObject(3, metaColumnInfoList.get(i).getColumnIndex()); 
                ps.setObject(4, metaColumnInfoList.get(i).getColumnName());
                ps.setObject(5, metaColumnInfoList.get(i).getColumnType());
                if(pkName.equals(metaColumnInfoList.get(i).getColumnName())){
                    ps.setObject(6, "Y");
                }else{
                    ps.setObject(6, "N");
                }
                ps.addBatch();
            }
            ps.executeBatch();
            cu.closeConn(saveColumnInfoConn, ps, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            cu.closeConn(saveColumnInfoConn, ps, null);
        }
    }
    /**
     * 根据columnInfoList得到pkName
     * 匹配信息，数据表名称，数据表id
     * @param columnInfoList
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String,Object> getColumnPk(List<ColumnInfo> columnInfoList) {
        Map<String,Object> map = new HashMap<String, Object>();
        CacheEle<?> matchDataCacheEle = SystemCache.getCache(ExcelConstants.DATATOOLS_METADATA_CATCH_STORE);
        Map<String,Object> matchDataCacheContent = (Map<String, Object>) matchDataCacheEle.getContent();
        List<MetaInfo> metaInfoList = (List<MetaInfo>) matchDataCacheContent.get("metaInfoList");
        if(metaInfoList==null||metaInfoList.size()==0){
            map.put("Match", false);
            return map;
        }
        /**
         * 获取内存中数据的column属性，匹配titleIndex，titleName，TitleType
         * 找到tableId，根据id分组，然后按组匹配
         * */
        for(int i=0;i<metaInfoList.size();i++){
            MetaInfo metaInfo = metaInfoList.get(i);
            List<MetaColumnInfo> metaColumnInfoList = metaInfo.getMateColumnInfo();
            String pkName = "";
            Integer pkIndex = 0;
            int columnSize = columnInfoList.size();
            if(metaColumnInfoList.size()==columnSize){
                int flag = columnSize;
                for (int k=0;k<columnSize;k++) {
                    for (int j=0; j<columnSize; j++) {
                        if(metaColumnInfoList.get(k).getColumnName().equals(columnInfoList.get(j).getColumnName())
                                    &&metaColumnInfoList.get(k).getColumnType().equals(columnInfoList.get(j).getColumnType())){
                            if(metaColumnInfoList.get(k).getPk().equals("Y")){
                                pkName = metaColumnInfoList.get(k).getColumnName();
                                pkIndex = columnInfoList.get(j).getColumnIndex();
                            }
                            flag--;
                        }
                    }
                    if (flag==0){
                        String tableId =  metaInfo.getTableId();
                        String tableName = metaInfo.getTableName();
                        map.put("Match", true);
                        map.put("dataTableName", tableName);
                        map.put("tableId", tableId);
                        map.put("pkName", pkName);
                        map.put("pkIndex", pkIndex);
                        return map;
                    }
                }
            }
        }
        map.put("Match", false);
        return map; 
    }
    /**
     * 比对是结构，如果结构相符，进行数据的正确排序
     * @param contentAttributes
     * @return
     */
    @SuppressWarnings({ "unchecked" })
    public Map<String,Object> matchData(ExcelContentAttributes contentAttributes) {
        Map<String,Object> map = new HashMap<String, Object>();
        CacheEle<?> matchDataCacheEle = SystemCache.getCache(ExcelConstants.DATATOOLS_METADATA_CATCH_STORE);
        Map<String,Object> matchDataCacheContent = (Map<String, Object>) matchDataCacheEle.getContent();
        List<MetaInfo> metaInfoList = (List<MetaInfo>) matchDataCacheContent.get("metaInfoList");
        int m = metaInfoList.size();
        if(m==0){
            map.put("Match", false);
            return map;
        }
        /**
         * 获取内存中数据的column属性，匹配titleIndex，titleName，TitleType
         * 找到tableId，根据id分组，然后按组匹配
         * */
        for(int i=0;i<metaInfoList.size();i++){
            MetaInfo metaInfo = metaInfoList.get(i);
            List<MetaColumnInfo> metaColumnInfoList = metaInfo.getMateColumnInfo();
            List<ColumnInfo> columnList = contentAttributes.getColumnInfo();
            int columnSize = columnList.size();
            if(metaColumnInfoList.size()==columnSize){
                int flag = columnSize;
                for (int k=0;k<columnSize;k++) {
                    for (int j=0; j<columnSize; j++) {
                        if(metaColumnInfoList.get(k).getColumnName().equals(columnList.get(j).getColumnName())
                                    &&metaColumnInfoList.get(k).getColumnType().equals(columnList.get(j).getColumnType())){
                            flag--;
                        }
                    }
                    if (flag==0){
                        String tableId =  metaInfo.getTableId();
                        String tableName = metaInfo.getTableName();
                        map.put("Match", true);
                        map.put("dataTableName", tableName);
                        map.put("tableId", tableId);
                        map.put("matchDataCacheContent", matchDataCacheContent);
                        List<Object[]> newDataList = reviseDataIndex(metaColumnInfoList,contentAttributes);
                        map.put("newDataList", newDataList);
                        return map;
                    }
                }
            }
        }
        map.put("Match", false);
        return map; 
    }
    /**
     * 调整列的顺序
     * @param metaColumnInfoList
     * @param contentAttributes
     * @return
     */
    private List<Object[]> reviseDataIndex(List<MetaColumnInfo> metaColumnInfoList,ExcelContentAttributes contentAttributes) {
        List<Object[]> newDataList = new ArrayList<Object[]>();
        /**
         * 得到新数据的列信息
         */
        List<ColumnInfo> columnList = contentAttributes.getColumnInfo();
        /**
         * 吧列名和列index放入Map中
         */
        Map<String,Integer> columnInfoMap = new HashMap<String,Integer>();
        for(ColumnInfo ci:columnList){
            columnInfoMap.put(ci.getColumnName(), ci.getColumnIndex());
        }
        List<Integer> indexList = new ArrayList<Integer>();
        for(int i=0;i<metaColumnInfoList.size();i++){
            int k = columnInfoMap.get(metaColumnInfoList.get(i).getColumnName());
            indexList.add(k);
        }
        List<Object[]> dataList = contentAttributes.getDataList();
        for(Object[] oldDataArray: dataList){
            Object[] newDataArray = new Object[oldDataArray.length];
            for(int i=0;i<indexList.size();i++){
                Integer j = indexList.get(i);
                newDataArray[i] = oldDataArray[j];
            }
            newDataList.add(newDataArray);
        }
        return newDataList;
    }
    /**
     * 刷新缓存
     */
    @Resource
    ExcelCacheLifecycleUnit excelCacheLifecycleUnit;
    public void refulshCache() {
        excelCacheLifecycleUnit.refresh(ExcelConstants.DATATOOLS_METADATA_CATCH_STORE);
    }
    /**
     * 保存数据与数据标识关系
     */
    @Resource
    DataSignOrgService dataSignOrgService;
    public void saveDataSignOrg(ExcelContentAttributes contentAttributes) {
        List<String> dataIdList = contentAttributes.getDataIdList();
        for(int i=0;i<dataIdList.size();i++){
            DataSignOrg dataSignOrg = new DataSignOrg();
            dataSignOrg.setId(cu.getUUID());
            dataSignOrg.setTableId(contentAttributes.getTableId());
            dataSignOrg.setDataId(dataIdList.get(i));
            dataSignOrg.setSign(contentAttributes.getSign());
            dataSignOrgService.insertDataSignOrg(dataSignOrg);
        }
    }
    /**
     * 根据读取的数据，得到想要的数据
     * @param dataMap
     * @return
     */
    public List<Map<String, Object>> getTitle_ColumnInfo_DataListMap(Map<SheetInfo, Object[][]> dataMap) {
        /**
         * 返回一个map的集合，每一个map为一个sheet的对应信息
         */
        List<Map<String, Object>> titlePkMapList = new ArrayList<Map<String,Object>>();
        /**迭代数据map，key=sheetInfo,val=dataArray*/
        Iterator<SheetInfo> it = dataMap.keySet().iterator();
        while(it.hasNext()){
            List<String> titleList = new ArrayList<String>();
            Map<String, Object> titlePkMap = new HashMap<String, Object>();
            SheetInfo sheetInfo =  it.next();
            Object[][] dataArray = dataMap.get(sheetInfo);
            /**标题行*/
            Object[] tt = dataArray[0];
            for(Object o :tt){
                titleList.add((String) o);
            }
            /**放入标题*/
            titlePkMap.put("titleList", titleList);
            List<ColumnInfo> columnInfoList = new ArrayList<ColumnInfo>();
            String[] title = new String[tt.length] ;
            for(int i=0;i<tt.length;i++){
                title[i]=tt[i].toString();
            }
            /**组装列信息*/
            Object[] dataTypeAry = dataArray[dataArray.length-1];
            /**sheetName*/
            String sheetName = sheetInfo.getSheetName();
            /**组装columnInfoList，用于匹配缓存中的数据 */
            for(int i=0;i<title.length;i++){
                ColumnInfo mci = new ColumnInfo();
                mci.setColumnIndex(i);
                mci.setColumnName(title[i]);
                mci.setColumnType(dataTypeAry[i]+"");
                columnInfoList.add(mci);
            }
            /**放入columnInfoList，用于匹配缓存中的数据 */
            titlePkMap.put("columnInfoList", columnInfoList);
            /**获得数据*/
            List<Object[]> dataList = new ArrayList<Object[]>();
            for(int i=1;i<dataArray.length-1;i++){
                dataList.add(dataArray[i]);
            }
            titlePkMap.put("dataList", dataList);
            titlePkMap.put("sheetName", sheetName);
            titlePkMapList.add(titlePkMap);
        }
        return titlePkMapList;
    }
    /**
     * 把数据放入缓存中
     * @param dataMap
     * @param contentAttributes
     * @param cacheId
     * @throws Exception
     */
    public void putUploadDataInCache(Map<SheetInfo, Object[][]> dataMap, ExcelContentAttributes contentAttributes, String cacheId) throws Exception {
        try {
            Map<String, Object> moStore = new HashMap<String,Object>();
            moStore.put(ExcelConstants.DATATOOLS_UPLOADDATA_DATAMAP_NAME, dataMap);
            moStore.put(ExcelConstants.DATATOOLS_UPLOADDATA_SAVEATTRIBUTE_NAME, contentAttributes);
            SystemCache.setCache(new CacheEle<Map<String, Object>>(cacheId, "模块", moStore));
        } catch(Exception e) {
            throw new Exception("upload信息放入缓存中失败{DataTools[MateData_UploadData]}失败：", e);
        } 
    }
    /**
     * 
     * @param contentAttributes
     * @param resultMap
     * @throws SQLException 
     */
    @SuppressWarnings({ "unused", "unchecked" })
    public int insertData(ExcelContentAttributes contentAttributes,Map<String, Object> resultMap)  {
        /**
         * 获取比对数据
         * targetDataSignOrgList，
         * targetDataIdPkMap
         */
        int insertRows = 0;
        List<DataSignOrg> targetDataSignOrgList = (List<DataSignOrg>) resultMap.get("targetDataSignOrgList");
        Map<Integer, Map<String, String>> targetDataIdPkMap = (Map<Integer, Map<String, String>>) resultMap.get("targetDataIdPkMap");
        String tableName = contentAttributes.getTableName();
        Iterator<Integer> it = targetDataIdPkMap.keySet().iterator();
        Connection conn =null;
        PreparedStatement ps = null;
        try {
            while(it.hasNext()){
                Integer pkIndex = it.next();
                Map<String, String> dataIdPkMap = targetDataIdPkMap.get(pkIndex);
                Map<String,String> newDataIdPkMap = getNewDataIdPkMap(dataIdPkMap);
                /**
                 * 获取元数据,插入或更新数据
                 */
                List<Object[]> dataList = contentAttributes.getDataList();
                List<String> insetDataIdList = new ArrayList<String>();
                conn = ds.getConnection();
                String formatDate;
                for(Object[] o :dataList){
                    String pk = o[pkIndex]+"";
                    String dataId = newDataIdPkMap.get(pk);
                    if(dataId==null){
                        dataId = cu.getUUID();
                        insetDataIdList.add(dataId);
                        StringBuffer sbInsertSql = new StringBuffer("insert into "+tableName+" values('"+dataId+"',");
                        for(int i=0;i<o.length;i++){
                            Object oo = o[i];
                            if((oo.getClass()+"").equals("class java.util.Date")){
                                formatDate = cu.getFormatDate((Date)oo);
                                if(i!=o.length-1){
                                    sbInsertSql.append("to_timestamp('"+formatDate+"','yyyy-mm-dd hh24:mi:ss'),");
                                }
                                else {
                                    sbInsertSql.append("to_timestamp('"+formatDate+"','yyyy-mm-dd hh24:mi:ss'))");
                                }
                            }else{
                                if(oo==null){
                                    if(i!=o.length-1)sbInsertSql.append(oo+",");
                                    else sbInsertSql.append(oo+")");
                                }else{
                                    if(i!=o.length-1)sbInsertSql.append("'"+oo+"',");
                                    else sbInsertSql.append("'"+oo+"')");
                                }
                            }
                        }
                        ps = conn.prepareStatement(sbInsertSql.toString());
                        ps.executeUpdate();
                        insertRows++;
                    }else{
                        StringBuffer sbUpdateSql = new StringBuffer("update "+tableName+" set ");
                        for(int i=0;i<o.length;i++){
                            Object oo = o[i];
                            if((oo.getClass()+"").equals("class java.util.Date")){
                                formatDate = cu.getFormatDate((Date)oo);
                                if(i!=o.length-1){
                                    sbUpdateSql.append("column"+i+"=to_timestamp('"+formatDate+"','yyyy-mm-dd hh24:mi:ss'),");
                                }else {
                                    sbUpdateSql.append("column"+i+"=to_timestamp('"+formatDate+"','yyyy-mm-dd hh24:mi:ss') where id='"+dataId+"'");
                                }
                            }else{
                                if(oo==null){
                                    if(i!=o.length-1)sbUpdateSql.append("column"+i+"="+oo+",");
                                    else sbUpdateSql.append("column"+i+"="+oo+" where id='"+dataId+"'");
                                }else{
                                    if(i!=o.length-1)sbUpdateSql.append("column"+i+"='"+oo+"',");
                                    else sbUpdateSql.append("column"+i+"='"+oo+"' where id='"+dataId+"'");
                                }
                            }
                        }
                        ps = conn.prepareStatement(sbUpdateSql.toString());
                        ps.executeUpdate();
                    }
                    contentAttributes.setDataIdList(insetDataIdList);
                    saveDataSignOrg(contentAttributes);
                }
            }
            cu.closeConn(conn, ps, null);
            return insertRows;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }finally{
            cu.closeConn(conn, ps, null);
        }
    }
    /**
     * 吧pk和dataId调换位置
     * @param dataIdPkMap
     * @return
     */
    private Map<String, String> getNewDataIdPkMap(Map<String, String> dataIdPkMap) {
        Map<String, String> newDataIdPkMap = new HashMap<String, String>();
        Iterator<String> it = dataIdPkMap.keySet().iterator();
        while(it.hasNext()){
            String dataId = it.next();
            String pk = dataIdPkMap.get(dataId);
            newDataIdPkMap.put(pk, dataId);
        }
        return newDataIdPkMap;
    }
    /**
     * 得到判断数据的结果, 并进行处理
     * @param sheetName 
     * @param dataList
     * @param pkName
     * @param pkIndex 
     * @return
     */
    public Map<String, Object> checkPkNullAndRepeat(String sheetName, List<Object[]> dataList,String pkName, Integer pkIndex) {
        Map<String, Object> checkResultMap = new HashMap<String,Object>();
        StringBuffer nullMessage = new StringBuffer("您新上传的excel文件中名为"+sheetName+"的sheet中所指定的主键列中在");
        int oldLength = nullMessage.length();
        Map<Object,String> repeatMap = new HashMap<Object,String>();
        /**
         * 把空的和重复的位置找出来
         */
        for(int i=0;i<dataList.size();i++){
            Object[] o = dataList.get(i);
            Object oo = o[pkIndex];
            if(oo==null||oo==""){
                nullMessage.append(i+1+",");
            }else{
                String oVal = repeatMap.get(oo);
                if(oVal==null){
                    oVal = (i+1)+"";
                }else{
                    oVal = oVal+","+(i+1);
                }
                repeatMap.put(oo, oVal);
            }
        }
        int newLength = nullMessage.length();
        /**
         * 如果无空，返回null
         */
        if(oldLength!=newLength){
            nullMessage.append("行的位置的值为空");
            checkResultMap.put("nullMessage", nullMessage.toString());
        }else{ 
            checkResultMap.put("nullMessage", null);
        }
        /**
         * 如果无重复，返回null，
         */
        Map<Object,String> newRepeatMap = new HashMap<Object,String>();
        StringBuffer sbRepeat = new StringBuffer("您新上传的excel文件中名为"+sheetName+"的sheet中所指定的主键列在行");
        Iterator<Object> it = repeatMap.keySet().iterator();
        while(it.hasNext()){
            Object k = it.next();
            String v = repeatMap.get(k);
            if(v.split(",").length>1){
                newRepeatMap.put(k, v);
            }
        }
        if(newRepeatMap.size()!=0){
            int count = 0;
            Iterator<Object> oIt = newRepeatMap.keySet().iterator();
            while(oIt.hasNext()){
                Object newKey = oIt.next();
                String newVal = newRepeatMap.get(newKey);
                if(count<newRepeatMap.size()-1){
                    sbRepeat.append(newVal+"处有重复，重复的值为："+newKey+"在");
                }else{
                    sbRepeat.append(newVal+"处有重复，重复的值为："+newKey+"。");
                }
            }
            String repeatMessage = sbRepeat.toString();
            checkResultMap.put("repeatMessage", repeatMessage);
            count++;
        }else{
            checkResultMap.put("repeatMessage", null); 
        }
        return checkResultMap;
    }
}
