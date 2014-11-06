package com.gmteam.spiritdata.importdata.excel.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.gmteam.spiritdata.importdata.excel.ExcelConstants;
import com.gmteam.spiritdata.importdata.excel.pojo.SheetInfo;

/**
 * 通过Poi包解析excel文件的公共方法集服务
 * @author wh
 */
public class PoiParseUtils {
    private int excelType=0; //excel文件类型，参看ExcelConstants.EXECL2007_FLAG,ExcelConstants.EXECL2003_FLAG
    private SheetInfo sheetInfo = null; //要解析的Sheet对象
    private Object sheet = null; //sheet对象
    private List<CellRangeAddress> mergedCellList = null; //合的单元格列表

    private boolean is2003() {
        return excelType==ExcelConstants.EXECL2003_FLAG;
    }

    private boolean is2007() {
        return excelType==ExcelConstants.EXECL2007_FLAG;
    }

    /**
     * 构造方法
     * @param excelType excel文件类型
     * @param sheetInfo 要解析的Sheet对象
     */
    public PoiParseUtils(int excelType, SheetInfo sheetInfo) {
        super();
        this.excelType = excelType;
        this.sheetInfo = sheetInfo;
        this.sheet = sheetInfo.getSheet();
        //获得合并的单元格
        int mergedCellCount = 0;
        if (is2003()) {
            mergedCellCount = ((HSSFSheet)sheet).getNumMergedRegions();
        } else if (is2007()) {
            mergedCellCount = ((XSSFSheet)sheet).getNumMergedRegions();
        }
        if (mergedCellCount>0) {
            mergedCellList = new ArrayList<CellRangeAddress>();
            for (int i=0; i<mergedCellCount-1; i++) {
                if (is2003()) {
                    mergedCellList.add(((HSSFSheet)sheet).getMergedRegion(i));
                } else if (is2007()) {
                    mergedCellList.add(((XSSFSheet)sheet).getMergedRegion(i));
                }
            }
        }
    }

    /**
     * 获得sheetinfo信息，此信息中包括元数据的分析结果。主要放在etiList中
     * @return sheetinfo信息
     */
    public SheetInfo getSheetInfo() {
        return this.sheetInfo;
    }

    /**
     * 分析sheet的元数据信息，并把分析的结果存入对象的sheetInfo对象中
     */
    public void analSheetMetadata() {
        //首先分析表头
        int rows = 0, firstRowNum = 0;
        if (is2003()) {
            rows = ((HSSFSheet)sheet).getLastRowNum();
            firstRowNum = ((HSSFSheet)sheet).getFirstRowNum();
        } else if (is2007()){
            rows = ((XSSFSheet)sheet).getLastRowNum();
            firstRowNum = ((XSSFSheet)sheet).getFirstRowNum();
        }

        if (rows==firstRowNum&&rows==0) return; //说明是空sheet
        if (rows==firstRowNum) return; //只有一行数据，没有分析的价值
        //
    }
}