package com.gmteam.spiritdata.importdata.excel.util.pmters;

import java.util.prefs.BackingStoreException;

import com.gmteam.framework.core.model.BaseObject;
import com.gmteam.spiritdata.importdata.excel.util.SheetInfo;

/** 
 * @author mht
 * @version  
 * 类说明 存一些常量，用于得到md
 */
public class MdPmters extends BaseObject{
    /**文件类型*/
    private int fileType;
    /**sheet*/
    private Object sheet;
    /**条数*/
    private int rows;
    /**sheetInfo*/
    private SheetInfo sheetInfo;
    
    public SheetInfo getSheetInfo() {
        return sheetInfo;
    }
    public void setSheetInfo(SheetInfo sheetInfo) {
        this.sheetInfo = sheetInfo;
    }
    public int getFileType() {
        return fileType;
    }
    public void setFileType(int fileType) {
        this.fileType = fileType;
    }
    public Object getSheet() {
        return sheet;
    }
    public void setSheet(Object sheet) {
        this.sheet = sheet;
    }
    public int getRows() {
        return rows;
    }
    public void setRows(int rows) {
        this.rows = rows;
    }
    
}
