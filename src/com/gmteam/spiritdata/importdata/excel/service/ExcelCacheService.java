package com.gmteam.spiritdata.importdata.excel.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.stereotype.Service;

import com.gmteam.spiritdata.importdata.excel.pojo.ColumnInfo;
import com.gmteam.spiritdata.importdata.excel.pojo.DataSignOrg;
import com.gmteam.spiritdata.importdata.excel.pojo.TableInfo;
import com.gmteam.spiritdata.importdata.excel.service.pojoservice.ColumnInfoService;
import com.gmteam.spiritdata.importdata.excel.service.pojoservice.DataSignOrgService;
import com.gmteam.spiritdata.importdata.excel.service.pojoservice.DataUploadLogService;
import com.gmteam.spiritdata.importdata.excel.service.pojoservice.TableInfoService;
import com.gmteam.spiritdata.importdata.excel.util.CommonUtils;
import com.gmteam.spiritdata.matedata.relation.MetaColumnInfo;
import com.gmteam.spiritdata.matedata.relation.MetaInfo;

/** 
 * @author 
 * @version  
 * 类说明  DataTools Service
 */
@Service
public class ExcelCacheService {
    @Resource(name="dataSource")
    private BasicDataSource ds;
    @Resource
    private DataUploadLogService dataUploadLogService;
    @Resource
    private ColumnInfoService columnInfoService;
    @Resource
    private TableInfoService tableInfoService;
    CommonUtils cu = new CommonUtils();
    @Resource
    private DataSignOrgService dataSignOrgService;
    public Map<String,Object> getResultMap() throws SQLException{
        Map<String,Object> resultMap = new HashMap<String,Object>();
        /**ColumnInfo部分*/
        List<ColumnInfo> columnInfoList = columnInfoService.getColumnInfoList();
        /** TableInfo部分*/
        List<TableInfo> tableInfoList = tableInfoService.getTableInfoList();
        List<MetaInfo> metaInfoList = new ArrayList<MetaInfo>();
        Map<String,List<DataSignOrg>> dataSignOrgMap = new HashMap<String, List<DataSignOrg>>();
        List<DataSignOrg> dataSignOrgList = dataSignOrgService.getDataSignOrgList();
        if(tableInfoList!=null){
            Map<String,Map<Integer,Map<String,Object>>> pkListMap = new HashMap<String,Map<Integer,Map<String,Object>>>();
            for(int i=0;i<tableInfoList.size();i++){
                TableInfo tableInfo = tableInfoList.get(i);
                String tableId = tableInfo.getId();
                String tableName = tableInfo.getTableName();
                MetaInfo metaInfo = new MetaInfo();
                metaInfo.setTableId(tableInfo.getId());
                metaInfo.setTableName(tableInfo.getTableName());
                Integer pkIndex = 0; 
                List<MetaColumnInfo> metaColumnList = new ArrayList<MetaColumnInfo>();
                for(int k=0;k<columnInfoList.size();k++){
                    MetaColumnInfo metaColumnInfo  = new MetaColumnInfo();
                    ColumnInfo columnInfo = columnInfoList.get(k);
                    if(tableId.equals(columnInfo.getTableId())){
                        if(columnInfo.getPk().equals("Y")){
                            pkIndex =  columnInfo.getColumnIndex();
                        }
                        metaColumnInfo.setColumnName(columnInfo.getColumnName());
                        metaColumnInfo.setColumnType(columnInfo.getColumnType());
                        metaColumnInfo.setColumnIndex(columnInfo.getColumnIndex());
                        metaColumnInfo.setPk(columnInfo.getPk());
                        metaColumnList.add(metaColumnInfo);
                    }
                }
                metaInfo.setMateColumnInfo(metaColumnList);
                metaInfoList.add(metaInfo);
                for(int j=0;j<dataSignOrgList.size();j++){
                    List<DataSignOrg> tab_DataSignOrgList = new ArrayList<DataSignOrg>();
                    for(DataSignOrg dataSingOrg:dataSignOrgList){
                        if(tableId.equals(dataSingOrg.getTableId())){
                            tab_DataSignOrgList.add(dataSingOrg);
                        }
                    }
                    dataSignOrgMap.put(tableId, tab_DataSignOrgList);
                }
                /**
                 * 获得DataIdList
                 */
                String getDataIdSql = "select id from "+tableName+" order by id";
                Connection dataIdConn = ds.getConnection();
                PreparedStatement getDataIdPs = dataIdConn.prepareStatement(getDataIdSql);
                ResultSet dataIdRst = getDataIdPs.executeQuery();
                List<String> dataIdList = new ArrayList<String>();
                while(dataIdRst.next()){
                    dataIdList.add(dataIdRst.getString("id"));
                }
                dataIdConn.close();
                /**
                 * 获得PkList
                 */
                List<Object> pkList = new ArrayList<Object>();
                String pkColumn = "column"+pkIndex;
                String getPkListSql = "select "+pkColumn+" from "+tableName+" order by id";
                Connection pkConn = ds.getConnection();
                PreparedStatement getPkListPs = pkConn.prepareStatement(getPkListSql);
                ResultSet pkListRst = getPkListPs.executeQuery();
                while(pkListRst.next()){
                    pkList.add(pkListRst.getObject(pkColumn)+"");
                }
                pkConn.close();
                /**
                 * 吧PkList和DataIdList组装成一个Map
                 * key=dataIdpk,value=pk
                 */
                Map<String,Object> pkDataIdMap = new HashMap<String, Object>();
                for(int c = 0;c<dataIdList.size();c++){
                    pkDataIdMap.put(dataIdList.get(c), pkList.get(c));
                }
                /**
                 * key = pkindex,value=pkList
                 */
                Map<Integer,Map<String,Object>> pk_IndexListMap = new HashMap<Integer, Map<String,Object>>();
                pk_IndexListMap.put(pkIndex, pkDataIdMap);
                pkListMap.put(tableId, pk_IndexListMap);
            }
            resultMap.put("metaInfoList", metaInfoList);
            resultMap.put("dataSignOrgMap", dataSignOrgMap);
            resultMap.put("pkListMap", pkListMap);
            return resultMap;
        }else{
            return null;
        }
    }
}
