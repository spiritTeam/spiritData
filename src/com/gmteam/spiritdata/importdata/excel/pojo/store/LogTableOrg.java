package com.gmteam.spiritdata.importdata.excel.pojo.store;

import com.gmteam.framework.core.model.BaseObject;
/** 
 * @author mht
 * @version  
 * 类说明 
 */
@SuppressWarnings("serial")
public class LogTableOrg extends BaseObject {
    private int sheetIndex;
    private String sheetName;
    private String id;
    private String logId;
    private String tableId;
    public String getSheetName() {
        return sheetName;
    }
    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
    public int getSheetIndex() {
        return sheetIndex;
    }
    public void setSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getLogId() {
        return logId;
    }
    public void setLogId(String logId) {
        this.logId = logId;
    }
    public String getTableId() {
        return tableId;
    }
    public void setTableId(String tableId) {
        this.tableId = tableId;
    }
}