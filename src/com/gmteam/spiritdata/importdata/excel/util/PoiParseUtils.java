package com.gmteam.spiritdata.importdata.excel.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
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
            for (int i=0; i<mergedCellCount; i++) {
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

        List<Map<String, Object>> rowData;
        List<List<Map<String, Object>>> catchRows; //行缓存
        for (int i=firstRowNum; i<rows; i++) {
            rowData = readOneRow(i, 1);
        }
    }

    private boolean is2003() {
        return excelType==ExcelConstants.EXECL2003_FLAG;
    }

    private boolean is2007() {
        return excelType==ExcelConstants.EXECL2007_FLAG;
    }

    /*
     * 按照表头方式读取一行数据。
     * @param rowNum 行号
     * @param readType 类型 1:按表头读取，数据值不进行尝试转换；2:按数据读取，数据值不进行尝试转换
     * @return 标题的列表，每个元素是一个map包括{"name":"姓名", "firstRow":"范围-开始行", "lastRow":"范围-结束行", "firstCol":"范围-开始列", "lastCol":"范围-结束列", "dataType":"数据类型(此类型不经过转换)"}
     */
    private List<Map<String, Object>> readOneRow(int rowNum, int readType) {
        Object rowData = null; //行信息
        Object cell = null; //单元格信息
        Map<String, Object> cellMap = null;

        int lastCellNum = 0;

        if (is2003()) {
            rowData = ((HSSFSheet)sheet).getRow(rowNum);
            lastCellNum = ((HSSFRow)rowData).getLastCellNum();
        } else if (is2007()){
            rowData = ((XSSFSheet)sheet).getRow(rowNum);
            lastCellNum = ((XSSFRow)rowData).getLastCellNum();
        }
        if (rowData==null) return null;

        List<Map<String, Object>> rd = new ArrayList<Map<String, Object>>();
        for (int i=0; i<lastCellNum; i++) {
            cell = getCell(rowData, i);
            cellMap = cellConvertToMap4Title(cell);
            
        }
        
        return rd.size()==0?null:rd;
    }

    /*
     * 得到某一行的单元格信息数据
     * @param rowData 行数据
     * @param cellNum 单元格标号
     * @param excelType excel文件类型
     * @return 单元格信息数据
     */
    private Object getCell(Object rowData, int cellNum) {
        Object cell = null;
        if (is2003()) {
            cell = ((HSSFRow)rowData).getCell(cellNum);
        } else if (is2007()){
            cell = ((XSSFRow)rowData).getCell(cellNum);
        }
        return cell;
    }

    /*
     * 以表头模式把cell转换为Map，map包括{"name":"姓名", "firstRow":"范围-开始行", "lastRow":"范围-结束行", "firstCol":"范围-开始列", "lastCol":"范围-结束列", "dataType":"数据类型(此类型不经过转换)"}
     * @param cell 单元格数据
     * @return
     */
    private Map<String, Object> cellConvertToMap4Title(Object cell) {
        return null;
    }

    /*
     * 以数据模式把cell转换为Map，map包括{"name":"姓名", "firstRow":"范围-开始行", "lastRow":"范围-结束行", "firstCol":"范围-开始列", "lastCol":"范围-结束列", "dataType":"数据类型(此类型不经过转换)"}
     * @param cell 单元格数据
     * @return
     */
    private Map<String, Object> cellConvertToMap4Data(Object cell) {
        return null;
    }

    /*
     * 判断cell是否是合并单元格信息
     * @param cell 
     */
    private CellRangeAddress getMergedRange(Object cell) {
        if (mergedCellList==null) {
            //获得合并的单元格
            int mergedCellCount = 0;
            if (is2003()) {
                mergedCellCount = ((HSSFSheet)sheet).getNumMergedRegions();
            } else if (is2007()) {
                mergedCellCount = ((XSSFSheet)sheet).getNumMergedRegions();
            }
            if (mergedCellCount>0) {
                mergedCellList = new ArrayList<CellRangeAddress>();
                for (int i=0; i<mergedCellCount; i++) {
                    if (is2003()) {
                        mergedCellList.add(((HSSFSheet)sheet).getMergedRegion(i));
                    } else if (is2007()) {
                        mergedCellList.add(((XSSFSheet)sheet).getMergedRegion(i));
                    }
                }
            }
        }
        
        return  null;
    }
}