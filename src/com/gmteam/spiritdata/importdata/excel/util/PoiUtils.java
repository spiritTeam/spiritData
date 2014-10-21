package com.gmteam.spiritdata.importdata.excel.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.gmteam.spiritdata.importdata.excel.ExcelConstants;
import com.gmteam.spiritdata.importdata.excel.util.pmters.CellPmters;
import com.gmteam.spiritdata.importdata.excel.util.pmters.DTPmters;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataColumn;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;

/** 
 * @author 
 * @version  
 * 类说明 用于得到Md
 */
public class PoiUtils {
    public static Map<SheetInfo,MetadataModel> getMdModelMap(Object workbook,int fileType) {
        //List<Map<SheetInfo,MetadataModel>> mdModelMapList = new ArrayList<Map<SheetInfo,MetadataModel>>();
        Map<SheetInfo,MetadataModel> mdModelMap = new HashMap<SheetInfo, MetadataModel>();
        int dataRows;
        if(fileType==ExcelConstants.EXCEL_FILE_TYPE_XSSF){
            int sheetSize = ((XSSFWorkbook) workbook).getNumberOfSheets();
            /**sheetInfo*/
            SheetInfo sheetInfo = new SheetInfo();
            XSSFSheet sheet;
            for(int i=0;i<sheetSize;i++ ){
                int sheetIndex = i;
                sheet = ((XSSFWorkbook) workbook).getSheetAt(sheetIndex);
                int rows = sheet.getLastRowNum()+1;
                MetadataModel metadataModel = new MetadataModel();
                if(rows+1>=2){
                    XSSFSheet xSheet = (XSSFSheet) sheet;
                    XSSFRow xRow = xSheet.getRow(0);
                    /**init sheetInfo*/
                    sheetInfo.setSheetIndex(sheetIndex);
                    sheetInfo.setSheetName(xSheet.getSheetName());
                    dataRows = xSheet.getLastRowNum()+1;
                    /**每行长度*/
                    int rowLength = xRow.getLastCellNum();
                    /**得到TitleAry*/
                    String [] titleAry = new String[rowLength];
                    for(int k=0;k<rowLength;k++){
                        XSSFCell xCell = xRow.getCell(k);
                        String columnName = (String) getCellValue(xCell);
                        titleAry[k] = columnName;
                    }
                    /**得到dataType*/
                    metadataModel = getMetadata(xSheet,dataRows,rowLength,titleAry); 
                    mdModelMap.put(sheetInfo, metadataModel);
                }
            }
        }else if(fileType==ExcelConstants.EXCEL_FILE_TYPE_HSSF){
            int sheetSize = ((HSSFWorkbook) workbook).getNumberOfSheets();
            SheetInfo sheetInfo = new SheetInfo();
            HSSFSheet sheet;
            boolean isActive;
            for(int i=0;i<sheetSize;i++ ){
                int sheetIndex = i;
                sheet = ((HSSFWorkbook) workbook).getSheetAt(sheetIndex);
                isActive = sheet.isActive();
                int rows = sheet.getLastRowNum()+1;
                if(isActive==false&&rows+1>=2){
                    MetadataModel metadataModel = new MetadataModel();
                    HSSFSheet hSheet = (HSSFSheet) sheet;
                    HSSFRow hRow = hSheet.getRow(0);
                    /**init sheetInfo*/
                    sheetInfo.setSheetIndex(sheetIndex);
                    sheetInfo.setSheetName(hSheet.getSheetName());
                    dataRows = hSheet.getLastRowNum()+1;
                    /**每行长度*/
                    int rowLength = hRow.getLastCellNum();
                    /**得到TitleAry*/
                    String [] titleAry = new String[rowLength];
                    for(int k=0;k<rowLength;i++){
                        HSSFCell hCell = hRow.getCell(k);
                        String columnName = ""+ getCellValue(hCell);
                        titleAry[k] = columnName;
                    }
                    /**得到dataType*/
                    metadataModel = getMetadata(hSheet,dataRows,rowLength,titleAry); 
                    mdModelMap.put(sheetInfo, metadataModel);
                }
            }
        }
        return mdModelMap;
    }  
//    public static Map<SheetInfo,MetadataModel> getMdModelMap(Object sheet,int sheetIndex,int fileTypes){
//        Map<SheetInfo,MetadataModel> mdModelMap = new HashMap<SheetInfo, MetadataModel>();
//        MetadataModel metadataModel = new MetadataModel();
//        int dataRows;
//        /**
//         * 1代表是2007+，否则代表
//         * 2007以下版本
//         */
//        /**sheetInfo*/
//        SheetInfo sheetInfo = new SheetInfo();
//        if(fileTypes==ExcelConstants.EXCEL_FILE_TYPE_XSSF){
//            XSSFSheet xSheet = (XSSFSheet) sheet;
//            XSSFRow xRow = xSheet.getRow(0);
//            /**init sheetInfo*/
//            sheetInfo.setSheetIndex(sheetIndex);
//            sheetInfo.setSheetName(xSheet.getSheetName());
//            dataRows = xSheet.getLastRowNum()+1;
//            /**每行长度*/
//            int rowLength = xRow.getLastCellNum();
//            /**得到TitleAry*/
//            String [] titleAry = new String[rowLength];
//            for(int i=0;i<rowLength;i++){
//                XSSFCell xCell = xRow.getCell(i);
//                String columnName = (String) getCellValue(xCell);
//                titleAry[i] = columnName;
//            }
//            /**得到dataType*/
//            metadataModel = getMetadata(xSheet,dataRows,rowLength,titleAry); 
//            mdModelMap.put(sheetInfo, metadataModel);
//        }else if((fileTypes==ExcelConstants.EXCEL_FILE_TYPE_HSSF)){
//            HSSFSheet hSheet = (HSSFSheet) sheet;
//            HSSFRow hRow = hSheet.getRow(0);
//            /**init sheetInfo*/
//            sheetInfo.setSheetIndex(sheetIndex);
//            sheetInfo.setSheetName(hSheet.getSheetName());
//            dataRows = hSheet.getLastRowNum()+1;
//            /**每行长度*/
//            int rowLength = hRow.getLastCellNum();
//            /**得到TitleAry*/
//            String [] titleAry = new String[rowLength];
//            for(int i=0;i<rowLength;i++){
//                HSSFCell hCell = hRow.getCell(i);
//                String columnName = ""+ getCellValue(hCell);
//                titleAry[i] = columnName;
//            }
//            /**得到dataType*/
//            metadataModel = getMetadata(hSheet,dataRows,rowLength,titleAry); 
//            mdModelMap.put(sheetInfo, metadataModel);
//        }
//        return mdModelMap;
//    }
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
     * 得到md,
     * @param sheet 为XSSFSHeet
     * @param dataRows 数据行数
     * @param rowLength 每行长度
     * @param titleAry 标题数组
     */
    private static MetadataModel getMetadata(XSSFSheet sheet, int dataRows, int rowLength, String[] titleAry) {
        MetadataModel mdModel = null;
        /**
         * 首先获得便于得到Md的结构
         */
        Map<Integer,Map<String,List<CellPmters>>> recordMap = new HashMap<Integer,Map<String,List<CellPmters>>>();
        if(dataRows>2&&dataRows<101){
            /**小于100条数据的时候*/
            for(int x=0;x<dataRows-1;x++){
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
            mdModel = getDataTypes(recordMap,dataRows-1,titleAry);
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
            mdModel = getDataTypes(recordMap,randoms.length,titleAry);
        }else if(dataRows<2){
            return null;
        }
        return mdModel;
    }
    /**
     * 得到md,
     * @param sheet 为HSSFSheet
     * @param dataRows 数据行数
     * @param rowLength 每行长度
     * @param titleAry 标题数组
     */
    private static MetadataModel getMetadata(HSSFSheet sheet, int dataRows, int rowLength, String[] titleAry) {
        MetadataModel mdModel = null;
        /**
         * 首先获得便于得到Md的结构
         */
        Map<Integer,Map<String,List<CellPmters>>> recordMap = new HashMap<Integer,Map<String,List<CellPmters>>>();
        if(dataRows>2&&dataRows<101){
            /**小于100条数据的时候*/
            for(int x=1;x<dataRows;x++){
                HSSFRow hRow = sheet.getRow(x);
                for(int y=0;y<hRow.getLastCellNum();y++){
                    Map<String,List<CellPmters>> typeMap = recordMap.get(y);
                    if(typeMap==null) typeMap = getTypeMap();
                    /**cp赋值*/
                    CellPmters cp = new CellPmters();
                    cp.setX(x);
                    cp.setY(y);
                    HSSFCell hCell = hRow.getCell(y);
                    String dataType= getCellValueType(hCell);
                    cp.setDataType(dataType);
                    List<CellPmters> cpList = typeMap.get(dataType);
                    cpList.add(cp);
                    recordMap.put(y, typeMap);
                }
            }
            mdModel = getDataTypes(recordMap,dataRows-1,titleAry);
        }else if(dataRows>101){
            /**大于100条数据的时候*/
            int [] randoms = getRandoms(dataRows,ExcelConstants.EXCEL_MD_RANDOM_ROWSIZE);
            for(int x=0;x<randoms.length;x++){
                HSSFRow hRow = sheet.getRow(randoms[x]);
                for(int y=0;y<hRow.getLastCellNum();y++){
                    Map<String,List<CellPmters>> typeMap = recordMap.get(y);
                    if(typeMap==null) typeMap = getTypeMap();
                    /**cp赋值*/
                    CellPmters cp = new CellPmters();
                    cp.setX(randoms[x]);
                    cp.setY(y);
                    HSSFCell hCell = hRow.getCell(y);
                    String dataType= getCellValueType(hCell);
                    cp.setDataType(dataType);
                    List<CellPmters> cpList = typeMap.get(dataType);
                    cpList.add(cp);
                    recordMap.put(y, typeMap);
                }
            }
            mdModel = getDataTypes(recordMap,randoms.length,titleAry);
        }else if(dataRows<2){
            return null;
        }
        return mdModel;
    }
    /**
     * 用于记录要删除的列的序号
     */
    public static Map<SheetInfo,List<Integer>> delColIndexList = new HashMap<SheetInfo,List<Integer>>();
    /**
     * 得到mD
     * @param recordMap 记录map
     * @param dataRows 代表抽取的条数
     * @param titleAry 标题数组
     */
    private static MetadataModel getDataTypes(Map<Integer, Map<String, List<CellPmters>>> recordMap, int dataRows, String[] titleAry) {
        MetadataModel metadataModel = new MetadataModel();
        List<MetadataColumn> mdColumnList = new ArrayList<MetadataColumn>();
        Iterator<Integer> recordIt = recordMap.keySet().iterator();
        List<Integer> delColIndexList = new ArrayList<Integer>();
        while(recordIt.hasNext()){
            int columnIndex = recordIt.next();
            Map<String, List<CellPmters>> typeMap = recordMap.get(columnIndex);
            DTPmters dtPmters = getMainDataType(typeMap,dataRows);
            if(dtPmters.getProportion()>=ExcelConstants.DATA_TYPE_PROPORTION){
                MetadataColumn mdColumn = new MetadataColumn();
                mdColumn.setColumnIndex(columnIndex);
                mdColumn.setColumnType(dtPmters.getDataType());
                mdColumn.setTitleName(titleAry[columnIndex]);
                mdColumn.setColumnName("column"+columnIndex);
                mdColumnList.add(mdColumn);
            }else{
                delColIndexList.add(columnIndex);
            }
        }
        try {
            metadataModel.setColumnList(mdColumnList);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return metadataModel;
    }
    /**
     * 得到数量最多的type和比重，空的个数
     * @param typeMap
     * @param dataRows
     * @return
     */
    private static DTPmters getMainDataType(Map<String, List<CellPmters>> typeMap, int dataRows) {
        int doubleTypeSize = typeMap.get(ExcelConstants.DATA_TYPE_DOUBLE).size();
        int dateTypeSize = typeMap.get(ExcelConstants.DATA_TYPE_DATE).size();
        int stringTypeSize = typeMap.get(ExcelConstants.DATA_TYPE_STRING).size();
        int booleanTypeSize = typeMap.get(ExcelConstants.DATA_TYPE_BOOLEAN).size();
        int nullTypeSize = typeMap.get(ExcelConstants.DATA_TYPE_NULL).size();
        DTPmters dtPmters = new DTPmters();
        if(nullTypeSize!=dataRows){
            dtPmters.setNullNum(nullTypeSize);
            String type="";
            int max=0;
            max = doubleTypeSize;
            type = ExcelConstants.DATA_TYPE_DOUBLE;
            if(max<dateTypeSize) {
                max = dateTypeSize;
                type = ExcelConstants.DATA_TYPE_DATE;
            }
            if(max<stringTypeSize){
                max=stringTypeSize;
                type = ExcelConstants.DATA_TYPE_STRING;
            } 
            if(max<booleanTypeSize) {
                max=booleanTypeSize;
                type = ExcelConstants.DATA_TYPE_BOOLEAN;
            }
            int proportion = (100*max)/(dataRows-dtPmters.getNullNum());
            dtPmters.setDataType(type);
            dtPmters.setProportion(proportion);
        }else{
            dtPmters.setNullNum(dataRows);
            dtPmters.setDataType(ExcelConstants.DATA_TYPE_NULL);
            dtPmters.setProportion(0);
        }
        return dtPmters;
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
            if(randomMap.get(k)==null&&k<randomRange){
                randomMap.put(k, k);
                rdmAry[i] = k;
            }
        }
        return rdmAry;
    }
    /** 获取单元格中数据的值对应HCell*/  
    public static Object getCellValue(HSSFCell hCell) {
        if (hCell == null)
            return null;
        Object result = null;  
        if (hCell != null) {  
            int cellType = hCell.getCellType();  
            switch (cellType) {  
            case XSSFCell.CELL_TYPE_STRING:  
                result = hCell.getRichStringCellValue().getString();  
                break;  
            case XSSFCell.CELL_TYPE_NUMERIC: 
                if (DateUtil.isCellDateFormatted(hCell)) {  
                    Date d = hCell.getDateCellValue();
                    result = d;
                } else {  
                    result = hCell.getNumericCellValue();  
                }
                break;  
            case XSSFCell.CELL_TYPE_FORMULA:  
                result = hCell.getNumericCellValue();  
                break;  
            case XSSFCell.CELL_TYPE_ERROR:  
                result = null;  
                break;  
            case XSSFCell.CELL_TYPE_BOOLEAN:  
                result = hCell.getBooleanCellValue();  
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
    /** 获取单元格中数据的值对应XCell*/  
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
    /** 得到单元格类型为XSSFCELL的valType,*/  
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
                type = "Null";  
                break;  
            default:  
                break;  
            }  
        }  
        return type;  
    }  
    /** 得到单元格类型为HSSFCELL的valType,*/  
    private static String getCellValueType(HSSFCell hCell) { 
        if (hCell == null)
            return "null";
        String type = null;  
        if (hCell != null) {  
            int cellType = hCell.getCellType();  
            switch (cellType) {  
            case XSSFCell.CELL_TYPE_STRING:  
                type = "String";  
                break;  
            case XSSFCell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(hCell)) {  
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
                type = "Null";  
                break;  
            default:  
                break;  
            }  
        }  
        return type;  
    }
}
