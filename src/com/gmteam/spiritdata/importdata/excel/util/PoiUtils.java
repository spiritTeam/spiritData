package com.gmteam.spiritdata.importdata.excel.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.gmteam.spiritdata.matedata.relation.pojo.MetadataColumn;

/** 
 * @author 
 * @version  
 * 类说明 
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
            XSSFRow sRow = sheet.getRow(1);
            /**得到Index和Name*/
            for(int i=0;i<sRow.getLastCellNum();i++){
                XSSFCell xCell = sRow.getCell(i);
                mdc = new MetadataColumn();
                mdc.setColumnIndex(i);
                String columnName = (String) getCellValue(xCell);
                mdc.setColumnName(columnName);
            }
            /**得到dataType*/
            getDataTypes(sheet,dataRows); 
            //String columnType = (String) getCellValueType(xCell);
        }else{
            HSSFSheet sheet = (HSSFSheet) mdPmters.getSheet();
        }
        return null;
    }
    private static void getDataTypes(XSSFSheet sheet, int dataRows) {
        if(dataRows>2&&dataRows<11){
            for(int i=0;i<dataRows-2;i++){
                XSSFRow xRow = sheet.getRow(i);
                // TODO 该改结构了
            }
        }
    }
    /** 获取单元格中的内容 ,该方法用于解析各种形式的数据
     * @throws ParseException */  
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
    /** 获取单元格中的内容 ,该方法用于解析各种形式的数据*/  
    private static String getCellValueType(XSSFCell xCell) { 
        if (xCell == null)
            return null;
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
