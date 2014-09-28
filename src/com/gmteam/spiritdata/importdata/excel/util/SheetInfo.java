package com.gmteam.spiritdata.importdata.excel.util;
/** 
 * @author 
 * @version  
 * 类说明 :用于存储sheetName，sheetIndex
 * 等信息
 */
public class SheetInfo {
    private String sheetName;
    private Integer sheetIndex;
    public String getSheetName() {
        return sheetName;
    }
    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
    public Integer getSheetIndex() {
        return sheetIndex;
    }
    public void setSheetIndex(Integer sheetIndex) {
        this.sheetIndex = sheetIndex;
    }
    
}
