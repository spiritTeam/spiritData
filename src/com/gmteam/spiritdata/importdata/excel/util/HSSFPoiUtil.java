package com.gmteam.spiritdata.importdata.excel.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DateUtil;

/** 
 * 对应2003版本excel
 * 关于poi.jar对execl的简单解析 
 * @author mht 
 * 该类目前只支持对简单ececl的解析，具体操作根据个人要求设计。 
 * 我要做的主要是通过某应用程序导出或者倒入execl文件， 
 * 该类有部分方法属于网络借鉴，再加上个人业务需要改编。 
 * */ 
public class HSSFPoiUtil {
    public HSSFPoiUtil() {  
    }  
    private HSSFWorkbook workbook;  
  
    public HSSFPoiUtil(File execlFile) throws IOException, FileNotFoundException {  
        workbook = new HSSFWorkbook(new FileInputStream(execlFile));  
    }  
    /** 获取表中所有数据 
     * @throws ParseException */  
    @SuppressWarnings("rawtypes")
    public Map<SheetInfo,Object> getDataSheetMap()throws FileNotFoundException, ParseException {
        Map<SheetInfo,Object> sheetDataMap = new HashMap<SheetInfo, Object>();
        //获取sheet个数
        int sheetSize = workbook.getNumberOfSheets();
        HSSFSheet sheet;
        //逐个得到数据和数据类型，最后一行为数据类型
        Integer sheetIndex;
        for(int k=0;k<sheetSize;k++){
            List<List> result = new ArrayList<List>();
            sheet = workbook.getSheetAt(k);
            sheetIndex = k;
            String sheetName = sheet.getSheetName();
            /**sheetInfo,*/
            SheetInfo sheetInfo = new SheetInfo();
            sheetInfo.setSheetIndex(sheetIndex);
            sheetInfo.setSheetName(sheetName);
            // 获取数据总行数，编号是从0开始的  
            int rowcount = sheet.getLastRowNum()+1;
            if(rowcount>2){
                HSSFRow rowSData = sheet.getRow(1);
                List<String> dataTypes = new ArrayList<String>();
                String typeNullIndexStr = "";
                if(rowSData!=null){
                    for(int i=0;i<rowSData.getLastCellNum();i++){
                        HSSFCell _cell = rowSData.getCell(i);
                        String dataType = getCellType(_cell);
                        if(dataType==null){
                            dataType="String";
                            typeNullIndexStr = typeNullIndexStr+i+",";
                        }
                        dataTypes.add(dataType);
                    }
                }
                rowSData = null;
                // 逐行读取数据  
                for (int i = 0; i < rowcount; i++) {  
                    // 获取行对象  
                    if(i==1&&i<rowcount){
                        rowSData = sheet.getRow(i);
                    }
                    HSSFRow row = sheet.getRow(i);
                    if (row != null) {  
                        List<Object> rowData = new ArrayList<Object>();  
                        // 获取本行中单元格个数  
                        int column = row.getLastCellNum();  
                        // 获取本行中各单元格的数据  
                        for (int cindex = 0; cindex < column; cindex++) {  
                            HSSFCell cell = row.getCell(cindex);  
                            // 获得指定单元格中的数据  
                            Object cellstr = getCellValue(cell);
                            if((typeNullIndexStr.indexOf(i+"")!=-1)&&cellstr!=null){
                                cellstr = cellstr+"";
                            }
                            rowData.add(cellstr);  
                        }  
                        result.add(rowData);  
                    }  
                } 
                result.add(dataTypes);
                sheetDataMap.put(sheetInfo, result);
            }
        }
        return sheetDataMap;  
    }
    /** 获取单元格中的内容 ,该方法用于解析各种形式的数据*/  
    private String getCellType(HSSFCell cell) { 
        if (cell == null)
            return null;
        String type = null;  
        if (cell != null) {  
            int cellType = cell.getCellType();  
            switch (cellType) {  
            case HSSFCell.CELL_TYPE_STRING:  
                type = "String";  
                break;  
            case HSSFCell.CELL_TYPE_NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {  
                    type = "Date";
                } else {  
                    type = "Double";  
                }
                break;  
            case HSSFCell.CELL_TYPE_FORMULA:  
                type = "Double";  
                break;  
            case HSSFCell.CELL_TYPE_ERROR:  
                type = null;  
                break;  
            case HSSFCell.CELL_TYPE_BOOLEAN:  
                type = "Boolean";  
                break;  
            case HSSFCell.CELL_TYPE_BLANK:  
                type = null;  
                break;  
            default:  
                break;  
            }  
        }  
        return type;  
    }  
    /** 获取单元格中的内容 ,该方法用于解析各种形式的数据
     * @throws ParseException */  
    private Object getCellValue(HSSFCell cell) throws ParseException {
        if (cell == null)
            return null;
        Object result = null;  
        if (cell != null) {  
            int cellType = cell.getCellType();  
            switch (cellType) {  
            case HSSFCell.CELL_TYPE_STRING:  
                result = cell.getRichStringCellValue().getString();  
                break;  
            case HSSFCell.CELL_TYPE_NUMERIC: 
                if (DateUtil.isCellDateFormatted(cell)) {  
                    Date d = cell.getDateCellValue();
                    result = d;
                } else {  
                    result = cell.getNumericCellValue();  
                }
                break;  
            case HSSFCell.CELL_TYPE_FORMULA:  
                result = cell.getNumericCellValue();  
                break;  
            case HSSFCell.CELL_TYPE_ERROR:  
                result = null;  
                break;  
            case HSSFCell.CELL_TYPE_BOOLEAN:  
                result = cell.getBooleanCellValue();  
                break;  
            case HSSFCell.CELL_TYPE_BLANK:  
                result = null;  
                break;  
            default:  
                break;  
            }  
        }  
        return result;  
    }  
    /**获取数据内容的方法*/  
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public  Map<SheetInfo,Object[][]> getMessageMap(String fname){  
        try {
            Map<SheetInfo,Object[][]> messageMap = new HashMap<SheetInfo, Object[][]>();
            HSSFPoiUtil poi=new HSSFPoiUtil(new File(fname));
            Map<SheetInfo,Object> sheetDataMap = poi.getDataSheetMap();
            Iterator<SheetInfo> it = sheetDataMap.keySet().iterator();
            while(it.hasNext()){
                SheetInfo sheetInfo = (SheetInfo) it.next();
                List<List> sheetData = (List<List>) sheetDataMap.get(sheetInfo);
                Object[][] p=new Object[sheetData.size()][(sheetData.get(0).size())];//二维数组大小，即用户存储表格内容数据  
                for (int i = 0; i < sheetData.size(); i++) {  
                    List row = (List) sheetData.get(i);
                    for (int j = 0; j < row.size(); j++) {
                        Object value = row.get(j); 
                        //p[i-1][j] = String.valueOf(value); 
                        p[i][j] = value;  
                    }  
                }  
                messageMap.put(sheetInfo, p);
            }
            return messageMap;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            return null; 
        } 
    } 
    public List<String> getDateType(){
        List<String> typeList = new ArrayList<String>();
        
        return typeList;
    }
    public static void main(String[] args) {  
        HSSFPoiUtil poi = new HSSFPoiUtil();  
        Map<SheetInfo,Object[][]> dataMap=poi.getMessageMap("C:\\Users\\admi\\Desktop\\aaa\\经纬度转换2010.xlsx");
        System.out.println(dataMap.size());
//        Object[][] p=poi.getmessage("D:\\test1.xls");//导入数据方法  
//        String[] title=poi.gettitles("D:\\test1.xls");  
//        System.out.println(title.length+":"+title[0]+title[1]);  
//        System.out.println(p.length+""+p[0][0]);//这是正确的  
//        poi.createxls(p, title,"D:\\testli.xls");//new String[]{"序号","姓名","成绩"}  
    }  
}
