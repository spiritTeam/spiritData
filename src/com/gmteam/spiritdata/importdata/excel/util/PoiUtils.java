package com.gmteam.spiritdata.importdata.excel.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.junit.Test;

import com.gmteam.spiritdata.importdata.excel.ExcelConstants;
import com.gmteam.spiritdata.importdata.excel.util.pmters.CellPmters;
import com.gmteam.spiritdata.importdata.excel.util.pmters.MdPmters;
import com.gmteam.spiritdata.matedata.relation.pojo.MetadataColumn;

/** 
 * @author 
 * @version  
 * 类说明 用于得到Md
 */
public class PoiUtils {
    public static List<MetadataColumn> getMDColumn(MdPmters mdPmters){
        List<MetadataColumn> mdCList = new ArrayList<MetadataColumn>();
        MetadataColumn mdc;
        int dataRows = mdPmters.getRows();
        /**
         * 1代表是2007+，否则代表
         * 2007以下版本
         */
        if(mdPmters.getFileType()==1){
            XSSFSheet sheet = (XSSFSheet) mdPmters.getSheet();
            XSSFRow xRow = sheet.getRow(1);
            int rowLength = xRow.getLastCellNum();
            /**得到Index和Name*/
            for(int i=0;i<rowLength;i++){
                XSSFCell xCell = xRow.getCell(i);
                mdc = new MetadataColumn();
                mdc.setColumnIndex(i);
                String columnName = (String) getCellValue(xCell);
                mdc.setColumnName(columnName);
            }
            /**得到dataType*/
            getDataTypesInfo(sheet,dataRows,rowLength); 
        }else{
            HSSFSheet sheet = (HSSFSheet) mdPmters.getSheet();
        }
        return null;
    }
    /**
     * 设定记录结构
     * @return
     */
    public static Map<String,List<CellPmters>> getTypeMap() {
        Map<String,List<CellPmters>> typeMap = new HashMap<String, List<CellPmters>>();
        List<CellPmters> dateList = new ArrayList<CellPmters>();
        typeMap.put(ExcelConstants.DATA_TYPE_DATE,dateList );
        List<CellPmters> stringList = new ArrayList<CellPmters>();
        typeMap.put(ExcelConstants.DATA_TYPE_STRING,stringList );
        List<CellPmters> booleanList = new ArrayList<CellPmters>();
        typeMap.put(ExcelConstants.DATA_TYPE_BOOLEAN, booleanList);
        List<CellPmters> doubleList = new ArrayList<CellPmters>();
        typeMap.put(ExcelConstants.DATA_TYPE_DOUBLE, doubleList);
        List<CellPmters> nullList = new ArrayList<CellPmters>();
        typeMap.put(ExcelConstants.DATA_TYPE_NULL, nullList);
        return typeMap;
    }
    /**
     * 获得dataTypes，并记录一些关于types信息
     * @param sheet
     * @param dataRows
     * @param rowLength 
     */
    private static Map<Integer,Map<String,List<CellPmters>>> getDataTypesInfo(XSSFSheet sheet, int dataRows, int rowLength) {
        Map<Integer,Map<String,List<CellPmters>>> recordMap = new HashMap<Integer,Map<String,List<CellPmters>>>();
        if(dataRows>2&&dataRows<101){
            /**小于100条数据的时候*/
            for(int x=0;x<dataRows-2;x++){
                XSSFRow xRow = sheet.getRow(x);
                for(int y=0;y<xRow.getLastCellNum();y++){
                    Map<String,List<CellPmters>> typeMap = recordMap.get(y);
                    if(typeMap==null) typeMap = getTypeMap();
                    /**cp赋值*/
                    CellPmters cp = new CellPmters();
                    cp.setX(x);
                    cp.setY(y);
                    XSSFCell xCell = xRow.getCell(y);
                    String dataType= getCellValueType(xCell);
                    cp.setDataType(dataType);
                    List<CellPmters> cpList = typeMap.get(dataType);
                    cpList.add(cp);
                    recordMap.put(y, typeMap);
                }
            }
            getDataTypes(recordMap,dataRows-1);
        }else if(dataRows>101){
            /**大于100条数据的时候*/
            int [] randoms = getRandoms(dataRows,ExcelConstants.EXCEL_MD_RANDOM_ROWSIZE);
            for(int x=0;x<randoms.length;x++){
                XSSFRow xRow = sheet.getRow(randoms[x]);
                for(int y=0;y<xRow.getLastCellNum();y++){
                    Map<String,List<CellPmters>> typeMap = recordMap.get(y);
                    if(typeMap==null) typeMap = getTypeMap();
                    /**cp赋值*/
                    CellPmters cp = new CellPmters();
                    cp.setX(randoms[x]);
                    cp.setY(y);
                    XSSFCell xCell = xRow.getCell(y);
                    String dataType= getCellValueType(xCell);
                    cp.setDataType(dataType);
                    List<CellPmters> cpList = typeMap.get(dataType);
                    cpList.add(cp);
                    recordMap.put(y, typeMap);
                }
            }
            getDataTypes(recordMap,randoms.length);
        }else if(dataRows<2){
            return null;
        }
        return recordMap;
    }
    private static void getDataTypes(Map<Integer, Map<String, List<CellPmters>>> recordMap, int dataRows) {
        Iterator<Integer> recordIt = recordMap.keySet().iterator();
        while(recordIt.hasNext()){
            int columnIndex = recordIt.next();
            Map<String, List<CellPmters>> typeMap = recordMap.get(columnIndex);
            getMainDataType(typeMap);
        }
        
    }
    private static void getMainDataType(Map<String, List<CellPmters>> typeMap) {
        int doubleList = typeMap.get(ExcelConstants.DATA_TYPE_DOUBLE).size();
        int dateList = typeMap.get(ExcelConstants.DATA_TYPE_DATE).size();
        int stringList = typeMap.get(ExcelConstants.DATA_TYPE_STRING).size();
        int booleanList = typeMap.get(ExcelConstants.DATA_TYPE_BOOLEAN).size();
        int nullList = typeMap.get(ExcelConstants.DATA_TYPE_NULL).size();
        
    }
    /**
     * 根据给定随机范围randomRange和给定的随机个数
     * 获取随机数（不包括0）
     * @param randomRange 随机范围
     * @param randomSize  随机个数
     * @return
     */
    public static int [] getRandoms(int randomRange,int randomSize){
        if(randomSize>=randomRange-1)return null;
        int [] rdmAry = new int[randomSize];
        Map<Integer,Integer> randomMap = new HashMap<Integer,Integer>(); 
        Random r = new Random();
        for(int i=0;i<randomSize;i++){
            int k = r.nextInt(randomRange-1)+1;
            if(randomMap.get(k)==null&&k<randomSize){
                randomMap.put(k, k);
                rdmAry[i] = k;
            }
        }
        return rdmAry;
    }
    /** 获取单元格中数据的值*/  
    public static Object getCellValue(XSSFCell xCell) {
        if (xCell == null)
            return null;
        Object result = null;  
        if (xCell != null) {  
            int cellType = xCell.getCellType();  
            switch (cellType) {  
            case XSSFCell.CELL_TYPE_STRING:  
                result = xCell.getRichStringCellValue().getString();  
                break;  
            case XSSFCell.CELL_TYPE_NUMERIC: 
                if (DateUtil.isCellDateFormatted(xCell)) {  
                    Date d = xCell.getDateCellValue();
                    result = d;
                } else {  
                    result = xCell.getNumericCellValue();  
                }
                break;  
            case XSSFCell.CELL_TYPE_FORMULA:  
                result = xCell.getNumericCellValue();  
                break;  
            case XSSFCell.CELL_TYPE_ERROR:  
                result = null;  
                break;  
            case XSSFCell.CELL_TYPE_BOOLEAN:  
                result = xCell.getBooleanCellValue();  
                break;  
            case XSSFCell.CELL_TYPE_BLANK:  
                result = null;  
                break;  
            default:  
                break;  
            }  
        }  
        return result;
    }
    /** 得到单元格的valType*/  
    private static String getCellValueType(XSSFCell xCell) { 
        if (xCell == null)
            return "null";
        String type = null;  
        if (xCell != null) {  
            int cellType = xCell.getCellType();  
            switch (cellType) {  
            case XSSFCell.CELL_TYPE_STRING:  
                type = "String";  
                break;  
            case XSSFCell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(xCell)) {  
                    type = "Date";
                } else {  
                    type = "Double";  
                }
                break;  
            case XSSFCell.CELL_TYPE_FORMULA:  
                type = "Double";  
                break;  
            case XSSFCell.CELL_TYPE_ERROR:  
                type = null;  
                break;  
            case XSSFCell.CELL_TYPE_BOOLEAN:  
                type = "Boolean";  
                break;  
            case XSSFCell.CELL_TYPE_BLANK:  
                type = null;  
                break;  
            default:  
                break;  
            }  
        }  
        return type;  
    }  
}
