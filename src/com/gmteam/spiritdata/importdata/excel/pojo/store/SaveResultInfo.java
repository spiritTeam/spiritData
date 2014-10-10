package com.gmteam.spiritdata.importdata.excel.pojo.store;
/** 
 * @author mht
 * @version  
 * 类说明 ：执行保存之后
 * 所返回的数据
 */
public class SaveResultInfo {
    private String sheetName;
    private boolean saveResult;
    private Integer allRows;
    private Integer insertRows;
    private String nullMessage;
    private String repeatMessage;
    public String getSheetName() {
        return sheetName;
    }
    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
    public boolean isSaveResult() {
        return saveResult;
    }
    public void setSaveResult(boolean saveResult) {
        this.saveResult = saveResult;
    }
    public Integer getAllRows() {
        return allRows;
    }
    public void setAllRows(Integer allRows) {
        this.allRows = allRows;
    }
    public Integer getInsertRows() {
        return insertRows;
    }
    public void setInsertRows(Integer insertRows) {
        this.insertRows = insertRows;
    }
    public String getNullMessage() {
        return nullMessage;
    }
    public void setNullMessage(String nullMessage) {
        this.nullMessage = nullMessage;
    }
    public String getRepeatMessage() {
        return repeatMessage;
    }
    public void setRepeatMessage(String repeatMessage) {
        this.repeatMessage = repeatMessage;
    }
}
