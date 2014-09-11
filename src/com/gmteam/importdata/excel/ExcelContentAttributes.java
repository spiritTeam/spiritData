package com.gmteam.importdata.excel;

import java.util.List;
import java.util.Map;

import com.gmteam.importdata.excel.pojo.ColumnInfo;


/** 
 * @author mht
 * @version  
 * 类说明 ：一个集合了所有信息的类
 * table信息,column信息,logId,
 */
public class ExcelContentAttributes {
    private String sheetName;
    private String logId;
    private String tableName;
    private String tableId;
    private String savePath;
    private List<ColumnInfo> columnInfo;
    private List<Object[]> dataList;
    private Integer sheetIndex;
    private Map<String,Object> uploadInfoMap;
    private String pkName;
    private List<String> dataIdList;
    private String sign;
    public String getSheetName() {
        return sheetName;
    }
    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
    public String getSign() {
        return sign;
    }
    public void setSign(String sign) {
        this.sign = sign;
    }
    public String getPkName() {
        return pkName;
    }
    public void setPkName(String pkName) {
        this.pkName = pkName;
    }
    public Map<String, Object> getUploadInfoMap() {
        return uploadInfoMap;
    }
    public void setUploadInfoMap(Map<String, Object> uploadInfoMap) {
        this.uploadInfoMap = uploadInfoMap;
    }
    public List<String> getDataIdList() {
        return dataIdList;
    }
    public void setDataIdList(List<String> dataIdList) {
        this.dataIdList = dataIdList;
    }
    public Integer getSheetIndex() {
        return sheetIndex;
    }
    public void setSheetIndex(Integer sheetIndex) {
        this.sheetIndex = sheetIndex;
    }
    public List<Object[]> getDataList() {
        return dataList;
    }
    public void setDataList(List<Object[]> dataList) {
        this.dataList = dataList;
    }
    public String getLogId() {
        return logId;
    }
    public void setLogId(String logId) {
        this.logId = logId;
    }
    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public String getTableId() {
        return tableId;
    }
    public void setTableId(String tableId) {
        this.tableId = tableId;
    }
    public String getSavePath() {
        return savePath;
    }
    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }
    public List<ColumnInfo> getColumnInfo() {
        return columnInfo;
    }
    public void setColumnInfo(List<ColumnInfo> columnInfo) {
        this.columnInfo = columnInfo;
    }
    
}
