package com.gmteam.spiritdata.importdata.excel.pojo;

import java.util.ArrayList;
import java.util.List;

import com.gmteam.framework.core.model.BaseObject;

/**
 * Sheet信息
 * @author wh
 */
public class SheetInfor extends BaseObject {
    private static final long serialVersionUID = 7206180301875759481L;

    private String fileName; //所在文件名称
    private int excelType; //excel文件的类型，参看ExcelConstants.EXECL2007_FLAG,ExcelConstants.EXECL2003_FLAG
    private String sheetName; //sheet名称
    private int sheetIndex; //sheet的编号
    private Object sheet; //sheet对象
    private List<ExcelTableInfo> emList; //本SheetInfor对应的Excel元数据信息

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public int getExcelType() {
        return excelType;
    }
    public void setExcelType(int excelType) {
        this.excelType = excelType;
    }
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
    public Object getSheet() {
        return sheet;
    }
    public void setSheet(Object sheet) {
        this.sheet = sheet;
    }

    /**
     * 新增元数据信息。
     * @param mm 元数据信息
     */
    public void addExcelMetadata(ExcelTableInfo em) {
        if (this.emList==null) this.emList = new ArrayList<ExcelTableInfo>();
        this.emList.add(em);
    }

    public List<ExcelTableInfo> getEmList() {
        return emList;
    }
}