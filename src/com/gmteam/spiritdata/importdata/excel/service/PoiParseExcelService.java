package com.gmteam.spiritdata.importdata.excel.service;

import com.gmteam.spiritdata.importdata.excel.pojo.SheetInfo;

/**
 * 通过Poi包解析excel文件的公共方法集服务
 * @author wh
 */
public class PoiParseExcelService {
    private int excelType; //excel文件类型，参看ExcelConstants.EXECL2007_FLAG,ExcelConstants.EXECL2003_FLAG
    private SheetInfo sheetInfo; //要解析的Sheet对象

    /**
     * 构造方法
     * @param excelType excel文件类型
     * @param sheetInfo 要解析的Sheet对象
     */
    public PoiParseExcelService(int excelType, SheetInfo sheetInfo) {
        super();
        this.excelType = excelType;
        this.sheetInfo = sheetInfo;
    }

    /**
     * 
     * @return
     */
    public SheetInfo getSheetInfo() {
        return this.sheetInfo;
    }

    /**
     * 分析sheet的元数据信息，并把分析的结果存入对象的sheetInfo对象中
     */
    public void analSheetMetadata() {
        
    }
}