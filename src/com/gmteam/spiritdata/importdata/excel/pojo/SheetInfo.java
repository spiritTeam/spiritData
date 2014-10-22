package com.gmteam.spiritdata.importdata.excel.pojo;
/** 
 * @author 
 * @version  
 * 类说明 :用于存储sheetName，sheetIndex
 * 等信息
 */
public class SheetInfo {
    private String sheetName;
    private Object sheet;
    private int sheetType;
    private Integer sheetIndex;
    public Object getSheet() {
        return sheet;
    }
    public void setSheet(Object sheet) {
        this.sheet = sheet;
    }
    public int getSheetType() {
        return sheetType;
    }
    public void setSheetType(int sheetType) {
        this.sheetType = sheetType;
    }
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
