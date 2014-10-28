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
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.gmteam.spiritdata.importdata.excel.ExcelConstants;
import com.gmteam.spiritdata.importdata.excel.pojo.SheetInfo;
import com.gmteam.spiritdata.importdata.excel.util.pmters.CellParam;
import com.gmteam.spiritdata.importdata.excel.util.pmters.DTParam;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataColumn;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;
/** 
 * @author mht
 * @version  
 * 类说明 poi工具类
 */
public class PoiUtils {
    /**
     * 
     * @param conn链接
     * @param sheetInfo
     * @param oldMD
     * @param newMD
     * @param tabMapOrgAry
     * @return
     */
    public static Map<String,Object> saveInDB(Connection conn, SheetInfo sheetInfo, MetadataModel oldMD,MetadataModel newMD, String tempTabName) {
        List<MetadataColumn> oldMdColList = oldMD.getColumnList();
        List<MetadataColumn> newMdColList = newMD.getColumnList();
        //计算对应表
        List<Integer> l = new ArrayList<Integer>();
        for (MetadataColumn mcN: newMdColList) {
            for (MetadataColumn mcS: oldMdColList) {
                if (mcN.getTitleName().equals(mcS.getTitleName())&&mcN.getColumnType().equals(mcS.getColumnType())) {
                    l.add(mcS.getColumnIndex());
                }
            }
        }
        if (l.size()==newMdColList.size()) ;//才能继续处理
        //得到sql
        StringBuffer fieldStr = new StringBuffer(), valueStr=new StringBuffer();
        for (MetadataColumn mc: newMdColList) {
            fieldStr.append(","+mc.getColumnName());
            valueStr.append(",?");
        }
        StringBuffer tempTabSql=new StringBuffer().append("insert into "+tempTabName+"(").append(fieldStr.substring(1)+") values(").append(valueStr.substring(1)+")");
        //StringBuffer sumTabSql=new StringBuffer().append("insert into "+sumTabName+"(").append(fieldStr.substring(1)+") values(").append(valueStr.substring(1)+")");
        //先插临时表，在插积累表，要通过积累表来分析主键
        saveTempData(conn, sheetInfo, l, tempTabSql.toString());
       // saveSumData(conn, sheetInfo, l, sumTabSql.toString());
        return null;
    }
    /**
     * 
     * @param conn
     * @param sheetInfo
     * @param mapL
     * @param sumTabSql
     */
    private static void saveSumData(Connection conn, SheetInfo sheetInfo, List<Integer> mapL,String sumTabSql) {
        Object sheet = null;
        PreparedStatement sumPs = null;
        try {
            sumPs = conn.prepareStatement(sumTabSql);
            if(sheetInfo.getSheetType()==ExcelConstants.EXCEL_FILE_TYPE_XSSF){
                sheet = (XSSFSheet)sheetInfo.getSheet();
                int rowNum = ((XSSFSheet)sheet).getLastRowNum()+1;
                for(int i=1;i<rowNum;i++) {
                    XSSFRow xRow = ((XSSFSheet)sheet).getRow(i);
                    if(xRow!=null){
                        try {
                            int j=1;
                            for (Integer integer :mapL) {
                                XSSFCell cell = xRow.getCell(integer);
                                sumPs.setObject(j, getCellValue(cell));
                                j++;
                            }
                            sumPs.execute();
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if(sheetInfo.getSheetType()==ExcelConstants.EXCEL_FILE_TYPE_HSSF){
                sheet = (HSSFSheet)sheetInfo.getSheet();
                int rowNum = ((HSSFSheet)sheet).getLastRowNum()+1;
                for(int i=1;i<rowNum;i++){
                    HSSFRow hRow = ((HSSFSheet)sheet).getRow(i);
                    if(hRow!=null){
                        try {
                            int j=1;
                            for (Integer integer :mapL) {
                                HSSFCell cell = hRow.getCell(integer);
                                sumPs.setObject(j, getCellValue(cell));
                                j++;
                            }
                            sumPs.execute();
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            CommonUtils.closeConn(null, sumPs, null);
        }
    }
    private static void saveTempData(Connection conn, SheetInfo sheetInfo, List<Integer> mapL,String tempSql) {
        PreparedStatement tempPs = null;
        Object sheet = null;
        try {
            tempPs = conn.prepareStatement(tempSql);
            if(sheetInfo.getSheetType()==ExcelConstants.EXCEL_FILE_TYPE_XSSF){
                sheet = (XSSFSheet)sheetInfo.getSheet();
                int rowNum = ((XSSFSheet)sheet).getLastRowNum()+1;
                for(int i=1;i<rowNum;i++){
                    XSSFRow xRow = ((XSSFSheet)sheet).getRow(i);
                    if(xRow!=null){
                        try{
                            int j=1;
                            for (Integer integer :mapL) {
                                XSSFCell cell = xRow.getCell(integer);
                                tempPs.setObject(j, getCellValue(cell));
                                j++;
                            }
                            tempPs.execute();
                        }catch(Exception eX){
                            eX.printStackTrace();
                        }
                    }else{
                    }
                }
                tempPs.executeBatch();
                tempPs.clearBatch();
            } else if(sheetInfo.getSheetType()==ExcelConstants.EXCEL_FILE_TYPE_HSSF){
                sheet = (HSSFSheet)sheetInfo.getSheet();
                int rowNum = ((HSSFSheet)sheet).getLastRowNum()+1;
                for(int i=1;i<rowNum;i++){
                    HSSFRow hRow = ((HSSFSheet)sheet).getRow(1);
                    if(hRow!=null){
                        try{
                            int j=1;
                            for (Integer integer :mapL) {
                                HSSFCell cell = hRow.getCell(integer);
                                tempPs.setObject(j, getCellValue(cell));
                                j++;
                            }
                            tempPs.execute();
                        }catch(Exception eX){
                            eX.printStackTrace();
                        }
                    }
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            CommonUtils.closeConn(null, tempPs, null);
        }
    }
    public static MetadataModel getMdModelMap(SheetInfo sheetInfo) {
        MetadataModel metadataModel;
        int dataRows;
        if(sheetInfo.getSheetType()==ExcelConstants.EXCEL_FILE_TYPE_XSSF){
            XSSFSheet xSheet = (XSSFSheet) sheetInfo.getSheet();
            int rows = xSheet.getLastRowNum()+1;
            if(rows>=2){
                XSSFRow xRow = xSheet.getRow(0);
                dataRows = xSheet.getLastRowNum()+1;
                /**每行长度*/
                int rowLength = xRow.getLastCellNum();
                /**得到TitleAry*/
                String [] titleAry = new String[rowLength];
                for(int k=0;k<rowLength;k++){
                    if (xRow==null) continue;
                    XSSFCell xCell = xRow.getCell(k);
                    String columnName = (String) getCellValue(xCell);
                    titleAry[k] = columnName;
                }
                /**得到dataType*/
                metadataModel = getMetadata(xSheet,dataRows,rowLength,titleAry); 
                return metadataModel;
            }else{
                return null;
            }
        }else if(sheetInfo.getSheetType()==ExcelConstants.EXCEL_FILE_TYPE_HSSF){
            HSSFSheet hSheet = (HSSFSheet) sheetInfo.getSheet();
            int rows = hSheet.getLastRowNum()+1;
            if(rows>=2){
                HSSFRow hRow = hSheet.getRow(0);
                dataRows = hSheet.getLastRowNum()+1;
                /**每行长度*/
                int rowLength = hRow.getLastCellNum();
                /**得到TitleAry*/
                String [] titleAry = new String[rowLength];
                for(int k=0;k<rowLength;k++){
                    if (hRow==null) continue;
                    HSSFCell hCell = hRow.getCell(k);
                    String columnName = (String) getCellValue(hCell);
                    titleAry[k] = columnName;
                }
                /**得到dataType*/
                metadataModel = getMetadata(hSheet,dataRows,rowLength,titleAry); 
                return metadataModel;
            }else{
                return null;
            }
        }
        return null;
    }  
    /**
     * 设定记录结构
     * @return
     */
    public static Map<String,List<CellParam>> getTypeMap() {
        Map<String,List<CellParam>> typeMap = new HashMap<String, List<CellParam>>();
        List<CellParam> dateList = new ArrayList<CellParam>();
        typeMap.put(ExcelConstants.DATA_TYPE_DATE,dateList );
        List<CellParam> stringList = new ArrayList<CellParam>();
        typeMap.put(ExcelConstants.DATA_TYPE_STRING,stringList );
        List<CellParam> booleanList = new ArrayList<CellParam>();
        typeMap.put(ExcelConstants.DATA_TYPE_BOOLEAN, booleanList);
        List<CellParam> doubleList = new ArrayList<CellParam>();
        typeMap.put(ExcelConstants.DATA_TYPE_DOUBLE, doubleList);
        List<CellParam> nullList = new ArrayList<CellParam>();
        typeMap.put(ExcelConstants.DATA_TYPE_NULL, nullList);
        List<CellParam> errorList = new ArrayList<CellParam>();
        typeMap.put(ExcelConstants.DATA_TYPE_ERROR, errorList);
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
        MetadataModel metadataModel = null;
        /**
         * 首先获得便于得到Md的结构
         */
        Map<Integer,Map<String,List<CellParam>>> recordMap = new HashMap<Integer,Map<String,List<CellParam>>>();
        if(dataRows>2&&dataRows<=ExcelConstants.DATA_ROWS_CRITICAL_POINT){
            /**小于100条数据的时候*/
            for(int x=0;x<dataRows-1;x++){
                XSSFRow xRow = sheet.getRow(x);
                if(xRow!=null){
                    for(int y=0;y<xRow.getLastCellNum();y++){
                        Map<String,List<CellParam>> typeMap = recordMap.get(y);
                        if(typeMap==null) typeMap = getTypeMap();
                        /**cp赋值*/
                        CellParam cp = new CellParam();
                        cp.setX(x);
                        cp.setY(y);
                        XSSFCell xCell = xRow.getCell(y);
                        String dataType= getCellValueType(xCell);
                        cp.setDataType(dataType);
                        List<CellParam> cpList = typeMap.get(dataType);
                        cpList.add(cp);
                        recordMap.put(y, typeMap);
                    }
                }
            }
            metadataModel = getDataTypes(recordMap,dataRows-1,titleAry);
        }else if(dataRows>ExcelConstants.DATA_ROWS_CRITICAL_POINT){
            /**大于100条数据的时候*/
            int [] randoms = getRandoms(dataRows,ExcelConstants.EXCEL_MD_RANDOM_ROWSIZE);
            for(int x=0;x<randoms.length;x++){
                XSSFRow xRow = sheet.getRow(randoms[x]);
                if(xRow!=null){
                    for(int y=0;y<xRow.getLastCellNum();y++){
                        Map<String,List<CellParam>> typeMap = recordMap.get(y);
                        if(typeMap==null) typeMap = getTypeMap();
                        /**cp赋值*/
                        CellParam cp = new CellParam();
                        cp.setX(randoms[x]);
                        cp.setY(y);
                        XSSFCell xCell = xRow.getCell(y);
                        String dataType= getCellValueType(xCell);
                        cp.setDataType(dataType);
                        List<CellParam> cpList = typeMap.get(dataType);
                        cpList.add(cp);
                        recordMap.put(y, typeMap);
                    }
                }
            }
            metadataModel = getDataTypes(recordMap,randoms.length,titleAry);
        }else if(dataRows<2){
            return null;
        }
        return metadataModel;
    }
    /**
     * 得到md,
     * @param sheet 为HSSFSheet
     * @param dataRows 数据行数
     * @param rowLength 每行长度
     * @param titleAry 标题数组
     */
    private static MetadataModel getMetadata(HSSFSheet sheet, int dataRows, int rowLength, String[] titleAry) {
        MetadataModel metadataModel = null;
        /**
         * 首先获得便于得到Md的结构
         */
        Map<Integer,Map<String,List<CellParam>>> recordMap = new HashMap<Integer,Map<String,List<CellParam>>>();
        if(dataRows>2&&dataRows<=ExcelConstants.DATA_ROWS_CRITICAL_POINT){
            /**小于100条数据的时候*/
            for(int x=1;x<dataRows;x++){
                HSSFRow hRow = sheet.getRow(x);
                if(hRow!=null)
                for(int y=0;y<hRow.getLastCellNum();y++){
                    Map<String,List<CellParam>> typeMap = recordMap.get(y);
                    if(typeMap==null) typeMap = getTypeMap();
                    /**cp赋值*/
                    CellParam cp = new CellParam();
                    cp.setX(x);
                    cp.setY(y);
                    HSSFCell hCell = hRow.getCell(y);
                    String dataType= getCellValueType(hCell);
                    cp.setDataType(dataType);
                    List<CellParam> cpList = typeMap.get(dataType);
                    cpList.add(cp);
                    recordMap.put(y, typeMap);
                }
            }
            metadataModel = getDataTypes(recordMap,dataRows-1,titleAry);
        }else if(dataRows>ExcelConstants.DATA_ROWS_CRITICAL_POINT){
            /**大于100条数据的时候*/
            int [] randoms = getRandoms(dataRows,ExcelConstants.EXCEL_MD_RANDOM_ROWSIZE);
            for(int x=0;x<randoms.length;x++){
                HSSFRow hRow = sheet.getRow(randoms[x]);
                if(hRow!=null)
                for(int y=0;y<hRow.getLastCellNum();y++){
                    Map<String,List<CellParam>> typeMap = recordMap.get(y);
                    if(typeMap==null) typeMap = getTypeMap();
                    /**cp赋值*/
                    CellParam cp = new CellParam();
                    cp.setX(randoms[x]);
                    cp.setY(y);
                    HSSFCell hCell = hRow.getCell(y);
                    String dataType= getCellValueType(hCell);
                    cp.setDataType(dataType);
                    List<CellParam> cpList = typeMap.get(dataType);
                    cpList.add(cp);
                    recordMap.put(y, typeMap);
                }
            }
            metadataModel = getDataTypes(recordMap,randoms.length,titleAry);
        }else if(dataRows<2){
            return null;
        }
        return metadataModel;
    }
    /**
     * 得到mD
     * @param recordMap 记录map
     * @param dataRows 代表抽取的条数
     * @param titleAry 标题数组
     */
    private static MetadataModel getDataTypes(Map<Integer, Map<String, List<CellParam>>> recordMap, int dataRows, String[] titleAry) {
        MetadataModel metadataModel = new MetadataModel();
        List<MetadataColumn> mdColumnList = new ArrayList<MetadataColumn>();
        Iterator<Integer> recordIt = recordMap.keySet().iterator();
        while(recordIt.hasNext()){
            int columnIndex = recordIt.next();
            Map<String, List<CellParam>> typeMap = recordMap.get(columnIndex);
            DTParam dtPmters = getMainDataType(typeMap,dataRows);
            if(dtPmters.getProportion()>=ExcelConstants.DATA_TYPE_PROPORTION){
                MetadataColumn mdColumn = new MetadataColumn();
                mdColumn.setColumnIndex(columnIndex);
                mdColumn.setColumnType(dtPmters.getDataType());
                mdColumn.setTitleName(titleAry[columnIndex]);
                mdColumn.setColumnName("column"+columnIndex);
                mdColumnList.add(mdColumn);
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
    private static DTParam getMainDataType(Map<String, List<CellParam>> typeMap, int dataRows) {
        int doubleTypeSize = typeMap.get(ExcelConstants.DATA_TYPE_DOUBLE).size();
        int dateTypeSize = typeMap.get(ExcelConstants.DATA_TYPE_DATE).size();
        int stringTypeSize = typeMap.get(ExcelConstants.DATA_TYPE_STRING).size();
        int booleanTypeSize = typeMap.get(ExcelConstants.DATA_TYPE_BOOLEAN).size();
        int nullTypeSize = typeMap.get(ExcelConstants.DATA_TYPE_NULL).size();
        int errorTypeSize = typeMap.get(ExcelConstants.DATA_TYPE_ERROR).size();
        DTParam dtPmters = new DTParam();
        if((nullTypeSize+errorTypeSize)!=dataRows){
            dtPmters.setNullNum(nullTypeSize);
            String type="";
            double max=0;
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
            double proportion = max/(dataRows-nullTypeSize+errorTypeSize);
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
            while (randomMap.get(k)!=null||k>randomRange) {
                k = r.nextInt(randomRange-1)+1; 
            }
            rdmAry[i] = k;
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
            return "Null";
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
                type = "Error";
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
            return "Null";
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
                type = "Error";  
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

