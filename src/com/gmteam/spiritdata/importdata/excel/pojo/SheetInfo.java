package com.gmteam.spiritdata.importdata.excel.pojo;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;

import com.gmteam.framework.core.model.BaseObject;

/**
 * Sheet信息
 * @author wh
 */
public class SheetInfo extends BaseObject {
    private static final long serialVersionUID = 7206180301875759481L;

    private String fileName; //所在文件名称
    private int excelType; //excel文件的类型，参看ExcelConstants.EXECL2007_FLAG,ExcelConstants.EXECL2003_FLAG
    private String sheetName; //sheet名称
    private int sheetIndex; //sheet的编号
    private Sheet sheet; //sheet对象
    private List<SheetTableInfo> stiList; //本sheet对应的Excel表结构数据信息列表（一个sheet）

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
    public Sheet getSheet() {
        return sheet;
    }
    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    /**
     * 新增元数据信息。
     * @param mm 元数据信息
     */
    public void addSheetTableInfo(SheetTableInfo sti) {
        if (this.stiList==null) this.stiList = new ArrayList<SheetTableInfo>();
        this.stiList.add(sti);
    }

    public List<SheetTableInfo> getStiList() {
        return stiList;
    }
}