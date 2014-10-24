package com.gmteam.spiritdata.importdata.excel.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
import com.gmteam.spiritdata.importdata.excel.pojo.SheetInfo;
import com.gmteam.spiritdata.importdata.excel.util.pmters.CellPrama;
import com.gmteam.spiritdata.importdata.excel.util.pmters.DTPrama;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataColumn;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;
import com.gmteam.spiritdata.metadata.relation.pojo.TableMapOrg;
/** 
 * @author mht
 * @version  
 * 类说明 用于得到Md
 */
public class PoiUtils {
    public static void saveInDB(Connection conn, SheetInfo sheetInfo,Map<Integer, Integer> delIndexMap, MetadataModel oldMD,MetadataModel newMD, TableMapOrg[] tabMapOrgAry) {
        List<MetadataColumn> oldMdColList = oldMD.getColumnList();
        List<MetadataColumn> newMdColList = newMD.getColumnList();
        /**k=newIndex,val=oldIndex*/
        Map<Integer,Integer> newOldIndexOrgMap = new HashMap<Integer,Integer>();
        int size = oldMdColList.size();
        int[] newIndexAry = new int[size];
        for(int i=0;i<size;i++){
            newIndexAry[i] = newMdColList.get(i).getColumnIndex();
            newOldIndexOrgMap.put(newMdColList.get(i).getColumnIndex(), oldMdColList.get(i).getColumnIndex());
        }
        //总表
        String sumTabName = tabMapOrgAry[0].getTableName();
        //临时表
        String tempTabName = tabMapOrgAry[1].getTableName();
        /**
         * 得到sql
         */
        StringBuffer tempTabSql = new StringBuffer("insert into "+tempTabName+"(");
        StringBuffer sumTabSql = new StringBuffer("insert into "+sumTabName+"(");
        StringBuffer paramSql = new StringBuffer("values(");
        for(int i=0;i<size;i++){
            if(i!=size-1){
                tempTabSql.append(newMdColList.get(i).getColumnName()+",");
                sumTabSql.append(newMdColList.get(i).getColumnName()+","); 
                paramSql.append("?,");
            }else{
                tempTabSql.append(newMdColList.get(i).getColumnName()+")");
                sumTabSql.append(newMdColList.get(i).getColumnName()+")"); 
                paramSql.append("?)");
            }
        }
        tempTabSql.append(paramSql);
        sumTabSql.append(paramSql);
        saveSumData(conn,sheetInfo,delIndexMap,newOldIndexOrgMap,newIndexAry,sumTabSql.toString());
        savetempData(conn,sheetInfo,delIndexMap,newOldIndexOrgMap,newIndexAry,tempTabSql.toString());
    }
    /**
     * 
     * @param conn
     * @param sheetInfo
     * @param delIndexMap
     * @param newOldIndexOrgMap
     * @param newIndexAry
     * @param sumSql
     */
    private static void saveSumData(Connection conn, SheetInfo sheetInfo,Map<Integer,Integer> delIndexMap,Map<Integer,Integer> newOldIndexOrgMap,int[] newIndexAry,String sumSql) {
        Object sheet;
        if(sheetInfo.getSheetType()==ExcelConstants.EXCEL_FILE_TYPE_XSSF){
            sheet = (XSSFSheet)sheetInfo.getSheet();
            //总行数
            int rowNum = ((XSSFSheet) sheet).getLastRowNum()+1;
            XSSFRow xRow = ((XSSFSheet) sheet).getRow(1);
            //每行的有多少个格子
            int cellNum = xRow.getRowNum()-delIndexMap.size();
            PreparedStatement sumPs = null;
            try {
                sumPs = conn.prepareStatement(sumSql);
                for(int i=1;i<rowNum;i++){
                    Object [] rowVal = new Object[cellNum-delIndexMap.size()];
                    for(int k=0;k<cellNum;k++){
                        if(delIndexMap.get(k)==null) {
                            XSSFCell xCell = xRow.getCell(k);
                            Object celVal = getCellValue(xCell);
                            rowVal[k] = celVal;
                        }
                    }
                    for(int k=0;k<cellNum;k++){
                        int key = newIndexAry[i];
                        int val = newOldIndexOrgMap.get(key);
                        sumPs.setObject(k+1, rowVal[val]);
                    }
                    sumPs.execute();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }finally{
                CommonUtils.closeConn(null, sumPs, null);
            }
        }else if(sheetInfo.getSheetType()==ExcelConstants.EXCEL_FILE_TYPE_HSSF){
            sheet = (HSSFSheet)sheetInfo.getSheet();
            //总行数
            int rowNum = ((HSSFSheet) sheet).getLastRowNum()+1;
            HSSFRow xRow = ((HSSFSheet) sheet).getRow(1);
            //每行的有多少个格子
            int cellNum = xRow.getRowNum()-delIndexMap.size();
            PreparedStatement sumPs = null;;
            try {
                sumPs = conn.prepareStatement(sumSql);
                for(int i=1;i<rowNum;i++){
                    Object [] rowVal = new Object[cellNum];
                    for(int k=0;k<cellNum;k++){
                        if(delIndexMap.get(k)==null) {
                            HSSFCell hCell = xRow.getCell(k);
                            Object celVal = getCellValue(hCell);
                            rowVal[k] = celVal;
                        }
                    }
                    for(int k=0;k<cellNum;k++){
                        int key = newIndexAry[i];
                        int val = newOldIndexOrgMap.get(key);
                        sumPs.setObject(k+1, rowVal[val]);
                    }
                    sumPs.execute();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }finally{
                CommonUtils.closeConn(null, sumPs, null);
            }
        }
            
        
    }
    private static void savetempData(Connection conn, SheetInfo sheetInfo,Map<Integer,Integer> delIndexMap,Map<Integer,Integer> newOldIndexOrgMap,int[] newIndexAry,String tempSql) {
        Object sheet;
        if(sheetInfo.getSheetType()==ExcelConstants.EXCEL_FILE_TYPE_XSSF){
            sheet = (XSSFSheet)sheetInfo.getSheet();
            //总行数
            int rowNum = ((XSSFSheet)sheet).getLastRowNum()+1;
            XSSFRow xRow = ((XSSFSheet)sheet).getRow(1);
            //每行的有多少个格子
            int celNum = xRow.getRowNum();
            PreparedStatement tempPs = null;
            try {
                tempPs = conn.prepareStatement(tempSql);
                for(int i=1;i<rowNum;i++){
                    Object [] rowVal = new Object[celNum-delIndexMap.size()];
                    for(int k=0;k<celNum;k++){
                        if(delIndexMap.get(k)==null) {
                            XSSFCell xCell = xRow.getCell(k);
                            Object celVal = getCellValue(xCell);
                            rowVal[k] = celVal;
                        }
                    }
                    for(int k=0;k<celNum;k++){
                        int key = newIndexAry[i];
                        int val = newOldIndexOrgMap.get(key);
                        tempPs.setObject(k+1, rowVal[val]);
                    }
                    tempPs.execute();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }finally{
                CommonUtils.closeConn(null, tempPs, null);
            }
        }else if(sheetInfo.getSheetType()==ExcelConstants.EXCEL_FILE_TYPE_HSSF){
            sheet = sheetInfo.getSheet();
          //总行数
            int rowNum = ((HSSFSheet)sheet).getLastRowNum()+1;
            HSSFRow hRow = ((HSSFSheet)sheet).getRow(1);
            //每行的有多少个格子
            int celNum = hRow.getRowNum();
            PreparedStatement tempPs = null;
            try {
                tempPs = conn.prepareStatement(tempSql);
                for(int i=1;i<rowNum;i++){
                    Object [] rowVal = new Object[celNum-delIndexMap.size()];
                    for(int k=0;k<celNum;k++){
                        if(delIndexMap.get(k)==null) {
                            HSSFCell hCell = hRow.getCell(k);
                            Object celVal = getCellValue(hCell);
                            rowVal[k] = celVal;
                        }
                    }
                    for(int k=0;k<celNum;k++){
                        int key = newIndexAry[i];
                        int val = newOldIndexOrgMap.get(key);
                        tempPs.setObject(k+1, rowVal[val]);
                    }
                    tempPs.execute();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }finally{
                CommonUtils.closeConn(null, tempPs, null);
            }
        }
    }
    /**
     * 得到md，并且 对delColIndexMap初始化
     * @param workbook
     * @param fileType
     * @return
     */
    public static Map<SheetInfo,Object> getMdModelMap(Object workbook,int fileType) {
        Map<SheetInfo,Object> mdModelMap = new HashMap<SheetInfo,Object>();
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
                Map<String, Object> retMap;
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
                    retMap = getMetadata(xSheet,dataRows,rowLength,titleAry); 
                    sheetInfo.setSheet(xSheet);
                    sheetInfo.setSheetType(fileType);
                    mdModelMap.put(sheetInfo, retMap);
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
                    Map<String,Object> retMap;
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
                    sheetInfo.setSheet(hSheet);
                    sheetInfo.setSheetType(fileType);
                    retMap = getMetadata(hSheet,dataRows,rowLength,titleAry); 
                    mdModelMap.put(sheetInfo, retMap);
                }
            }
        }
        return mdModelMap;
    }  
    /**
     * 设定记录结构
     * @return
     */
    public static Map<String,List<CellPrama>> getTypeMap() {
        Map<String,List<CellPrama>> typeMap = new HashMap<String, List<CellPrama>>();
        List<CellPrama> dateList = new ArrayList<CellPrama>();
        typeMap.put(ExcelConstants.DATA_TYPE_DATE,dateList );
        List<CellPrama> stringList = new ArrayList<CellPrama>();
        typeMap.put(ExcelConstants.DATA_TYPE_STRING,stringList );
        List<CellPrama> booleanList = new ArrayList<CellPrama>();
        typeMap.put(ExcelConstants.DATA_TYPE_BOOLEAN, booleanList);
        List<CellPrama> doubleList = new ArrayList<CellPrama>();
        typeMap.put(ExcelConstants.DATA_TYPE_DOUBLE, doubleList);
        List<CellPrama> nullList = new ArrayList<CellPrama>();
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
    private static Map<String,Object> getMetadata(XSSFSheet sheet, int dataRows, int rowLength, String[] titleAry) {
        Map<String,Object> retMap = null;
        /**
         * 首先获得便于得到Md的结构
         */
        Map<Integer,Map<String,List<CellPrama>>> recordMap = new HashMap<Integer,Map<String,List<CellPrama>>>();
        if(dataRows>2&&dataRows<101){
            /**小于100条数据的时候*/
            for(int x=0;x<dataRows-1;x++){
                XSSFRow xRow = sheet.getRow(x);
                for(int y=0;y<xRow.getLastCellNum();y++){
                    Map<String,List<CellPrama>> typeMap = recordMap.get(y);
                    if(typeMap==null) typeMap = getTypeMap();
                    /**cp赋值*/
                    CellPrama cp = new CellPrama();
                    cp.setX(x);
                    cp.setY(y);
                    XSSFCell xCell = xRow.getCell(y);
                    String dataType= getCellValueType(xCell);
                    cp.setDataType(dataType);
                    List<CellPrama> cpList = typeMap.get(dataType);
                    cpList.add(cp);
                    recordMap.put(y, typeMap);
                }
            }
            retMap = getDataTypes(recordMap,dataRows-1,titleAry);
        }else if(dataRows>101){
            /**大于100条数据的时候*/
            int [] randoms = getRandoms(dataRows,ExcelConstants.EXCEL_MD_RANDOM_ROWSIZE);
            for(int x=0;x<randoms.length;x++){
                XSSFRow xRow = sheet.getRow(randoms[x]);
                for(int y=0;y<xRow.getLastCellNum();y++){
                    Map<String,List<CellPrama>> typeMap = recordMap.get(y);
                    if(typeMap==null) typeMap = getTypeMap();
                    /**cp赋值*/
                    CellPrama cp = new CellPrama();
                    cp.setX(randoms[x]);
                    cp.setY(y);
                    XSSFCell xCell = xRow.getCell(y);
                    String dataType= getCellValueType(xCell);
                    cp.setDataType(dataType);
                    List<CellPrama> cpList = typeMap.get(dataType);
                    cpList.add(cp);
                    recordMap.put(y, typeMap);
                }
            }
            retMap = getDataTypes(recordMap,randoms.length,titleAry);
        }else if(dataRows<2){
            return null;
        }
        return retMap;
    }
    /**
     * 得到md,
     * @param sheet 为HSSFSheet
     * @param dataRows 数据行数
     * @param rowLength 每行长度
     * @param titleAry 标题数组
     */
    private static Map<String,Object> getMetadata(HSSFSheet sheet, int dataRows, int rowLength, String[] titleAry) {
        Map<String,Object> retMap = null;
        /**
         * 首先获得便于得到Md的结构
         */
        Map<Integer,Map<String,List<CellPrama>>> recordMap = new HashMap<Integer,Map<String,List<CellPrama>>>();
        if(dataRows>2&&dataRows<101){
            /**小于100条数据的时候*/
            for(int x=1;x<dataRows;x++){
                HSSFRow hRow = sheet.getRow(x);
                for(int y=0;y<hRow.getLastCellNum();y++){
                    Map<String,List<CellPrama>> typeMap = recordMap.get(y);
                    if(typeMap==null) typeMap = getTypeMap();
                    /**cp赋值*/
                    CellPrama cp = new CellPrama();
                    cp.setX(x);
                    cp.setY(y);
                    HSSFCell hCell = hRow.getCell(y);
                    String dataType= getCellValueType(hCell);
                    cp.setDataType(dataType);
                    List<CellPrama> cpList = typeMap.get(dataType);
                    cpList.add(cp);
                    recordMap.put(y, typeMap);
                }
            }
            retMap = getDataTypes(recordMap,dataRows-1,titleAry);
        }else if(dataRows>101){
            /**大于100条数据的时候*/
            int [] randoms = getRandoms(dataRows,ExcelConstants.EXCEL_MD_RANDOM_ROWSIZE);
            for(int x=0;x<randoms.length;x++){
                HSSFRow hRow = sheet.getRow(randoms[x]);
                for(int y=0;y<hRow.getLastCellNum();y++){
                    Map<String,List<CellPrama>> typeMap = recordMap.get(y);
                    if(typeMap==null) typeMap = getTypeMap();
                    /**cp赋值*/
                    CellPrama cp = new CellPrama();
                    cp.setX(randoms[x]);
                    cp.setY(y);
                    HSSFCell hCell = hRow.getCell(y);
                    String dataType= getCellValueType(hCell);
                    cp.setDataType(dataType);
                    List<CellPrama> cpList = typeMap.get(dataType);
                    cpList.add(cp);
                    recordMap.put(y, typeMap);
                }
            }
            retMap = getDataTypes(recordMap,randoms.length,titleAry);
        }else if(dataRows<2){
            return null;
        }
        return retMap;
    }
    /**
     * 得到mD
     * @param recordMap 记录map
     * @param dataRows 代表抽取的条数
     * @param titleAry 标题数组
     */
    private static Map<String,Object> getDataTypes(Map<Integer, Map<String, List<CellPrama>>> recordMap, int dataRows, String[] titleAry) {
        Map<String,Object> retMap = new HashMap<String,Object>();
        MetadataModel metadataModel = new MetadataModel();
        List<MetadataColumn> mdColumnList = new ArrayList<MetadataColumn>();
        Iterator<Integer> recordIt = recordMap.keySet().iterator();
        Map<Integer,Integer> delColInxMap = new HashMap<Integer,Integer>();
        while(recordIt.hasNext()){
            int columnIndex = recordIt.next();
            Map<String, List<CellPrama>> typeMap = recordMap.get(columnIndex);
            DTPrama dtPmters = getMainDataType(typeMap,dataRows);
            if(dtPmters.getProportion()>=ExcelConstants.DATA_TYPE_PROPORTION){
                MetadataColumn mdColumn = new MetadataColumn();
                mdColumn.setColumnIndex(columnIndex);
                mdColumn.setColumnType(dtPmters.getDataType());
                mdColumn.setTitleName(titleAry[columnIndex]);
                mdColumn.setColumnName("column"+columnIndex);
                mdColumnList.add(mdColumn);
            }else{
                delColInxMap.put(columnIndex,columnIndex);
            }
        }
        try {
            metadataModel.setColumnList(mdColumnList);
            retMap.put("metadataModel",metadataModel);
            retMap.put("delIndexMap", delColInxMap);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return retMap;
    }
    /**
     * 得到数量最多的type和比重，空的个数
     * @param typeMap
     * @param dataRows
     * @return
     */
    private static DTPrama getMainDataType(Map<String, List<CellPrama>> typeMap, int dataRows) {
        int doubleTypeSize = typeMap.get(ExcelConstants.DATA_TYPE_DOUBLE).size();
        int dateTypeSize = typeMap.get(ExcelConstants.DATA_TYPE_DATE).size();
        int stringTypeSize = typeMap.get(ExcelConstants.DATA_TYPE_STRING).size();
        int booleanTypeSize = typeMap.get(ExcelConstants.DATA_TYPE_BOOLEAN).size();
        int nullTypeSize = typeMap.get(ExcelConstants.DATA_TYPE_NULL).size();
        DTPrama dtPmters = new DTPrama();
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
class SaveTempData implements Runnable{
    public SaveTempData(Connection conn){
        
    }
    @Override
    public void run() {
    }
}
class SaveSumData implements Runnable{
    public SaveSumData(Connection conn){
        
    }
    @Override
    public void run() {
        
    }
    
}

