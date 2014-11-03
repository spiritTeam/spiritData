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
     * @param conn
     * @param sheetInfo
     * @param excelMd
     * @param newMd
     * @param sumTabName
     * @param titleRowIndex
     * @param pkColList
     */
    public static void saveSubTabInDB(Connection conn, SheetInfo sheetInfo,MetadataModel excelMd, MetadataModel newMd, String sumTabName,int titleRowIndex, List<MetadataColumn> pkColList) {
        String updateSql = "", insertSql = "";
        Object sheet = null, row = null, cell = null;
        int rowNum = 0;

        try {
            if(sheetInfo.getSheetType()==ExcelConstants.EXCEL_FILE_TYPE_XSSF) {
                sheet = (XSSFSheet)sheetInfo.getSheet();
                rowNum = ((XSSFSheet)sheet).getLastRowNum()+1;
            } else if(sheetInfo.getSheetType()==ExcelConstants.EXCEL_FILE_TYPE_HSSF) {
                sheet = (HSSFSheet)sheetInfo.getSheet();
                rowNum = ((HSSFSheet)sheet).getLastRowNum()+1;
            }
            if (sheet==null) return ;
            String u_setList = null, u_keyList = null, i_insertColList = null, i_valueList = null;
            Object value = null;
            MetadataColumn _col = null;
            int pkCount = 0, _pkCount = 0;
            for (MetadataColumn mc: newMd.getColumnList()) if (mc.isPk()) pkCount++;

            PreparedStatement ps = null;
            try {
                List<Object> insertParam = new ArrayList<Object>(), updateSetParam = new ArrayList<Object>(), updateKeyParam = new ArrayList<Object>();
                for(int i=1+titleRowIndex;i<rowNum;i++) {
                    if(sheetInfo.getSheetType()==ExcelConstants.EXCEL_FILE_TYPE_XSSF) row = ((XSSFSheet)sheet).getRow(i);
                    else if(sheetInfo.getSheetType()==ExcelConstants.EXCEL_FILE_TYPE_HSSF)  row = ((HSSFSheet)sheet).getRow(i);
                    if (row==null) continue;
                    _pkCount = 0;
                    u_setList = "";
                    u_keyList = "";
                    i_insertColList = "";
                    i_valueList = "";
                    insertParam.clear();
                    updateSetParam.clear();
                    updateKeyParam.clear();

                    for (MetadataColumn mc: excelMd.getColumnList()) {
                        int colIndex = mc.getColumnIndex();
                        _col = newMd.getColumnByTName(mc.getTitleName());
                        if (_col==null) continue;
                        if(sheetInfo.getSheetType()==ExcelConstants.EXCEL_FILE_TYPE_XSSF) {
                            cell = ((XSSFRow)row).getCell(colIndex);
                            value = getCellValue((XSSFCell)cell);
                        } else if(sheetInfo.getSheetType()==ExcelConstants.EXCEL_FILE_TYPE_HSSF) {
                            cell = ((HSSFRow)row).getCell(colIndex);
                            value = getCellValue((HSSFCell)cell);
                        }
                        if (value!=null) {
                            if (!_col.isPk()) {
                                u_setList += ","+_col.getColumnName()+"=?";
                                updateSetParam.add(value);
                            }
                            else {
                                u_keyList += "and "+_col.getColumnName()+"=?";
                                updateKeyParam.add(value);
                                _pkCount++;
                            }
                            i_insertColList += "," +_col.getColumnName();
                            i_valueList += ",?";
                            insertParam.add(value);
                        }
                    }
                    if (_pkCount!=pkCount) {
                        //处理日志
                        continue;
                    }
                    int uc = 0;
                    //先update
                    try {
                        if (u_keyList!=null&&u_keyList.length()>0) {
                            u_keyList = u_keyList.substring(4);
                            u_setList = u_setList.substring(1);
                            updateSql = "update "+sumTabName+" set #u_setList where #u_keyList";
                            updateSql = updateSql.replaceAll("#u_keyList", u_keyList);
                            updateSql = updateSql.replaceAll("#u_setList", u_setList);
                            ps = conn.prepareStatement(updateSql);
                            updateSetParam.addAll(updateKeyParam);
                            for (int j=0; j<updateSetParam.size(); j++) ps.setObject(j+1, updateSetParam.get(j));
                            uc = ps.executeUpdate();
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                    //再insert
                    if (uc==0) {
                        i_insertColList = i_insertColList.substring(1);
                        i_valueList = i_valueList.substring(1);
                        insertSql = "insert into "+sumTabName+"(#i_insertColList) values(#i_valueList) ";
                        insertSql = insertSql.replaceAll("#i_insertColList", i_insertColList);
                        insertSql = insertSql.replaceAll("#i_valueList", i_valueList);
                        ps = conn.prepareStatement(insertSql);
                        for (int j=0; j<insertParam.size(); j++) ps.setObject(j+1, insertParam.get(j));
                        try {
                            uc = ps.executeUpdate();
                        } catch(Exception e) {
                            //记录日志
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try { if (ps!=null) {ps.close();ps = null;} } catch (Exception e) {e.printStackTrace();} finally {ps = null;};
            }
        } catch(Exception e) {
            
        }
    }

    @SuppressWarnings("unchecked")
    private static void saveSumData(Connection conn, SheetInfo sheetInfo,Map<String, Object> sqlMap,int titleRowIndex) {
        String updateSql = (String) sqlMap.get("updateSql");
        String insertSql = (String) sqlMap.get("insertSql");
        PreparedStatement sumInsertPs = null;
        PreparedStatement sumUpdatePs = null;
        Object sheet = null;
        List<Integer> pkIndexList = (List<Integer>) sqlMap.get("pkIndexList");
        List<Integer> updateIndexList = (List<Integer>) sqlMap.get("updateIndexList");
        List<Integer> insertColIndex = (List<Integer>) sqlMap.get("insertColIndex");

        try {
            sumUpdatePs = conn.prepareStatement(updateSql);
            sumInsertPs = conn.prepareStatement(insertSql);
            if(sheetInfo.getSheetType()==ExcelConstants.EXCEL_FILE_TYPE_XSSF){
                sheet = (XSSFSheet)sheetInfo.getSheet();
                int rowNum = ((XSSFSheet)sheet).getLastRowNum()+1;
                for(int i=1+titleRowIndex;i<rowNum;i++){
                    XSSFRow xRow = ((XSSFSheet)sheet).getRow(i);
                    if(xRow!=null){
                        int j=1;
                        for (Integer integer :updateIndexList) {
                            XSSFCell cell = xRow.getCell(integer);
                            sumUpdatePs.setObject(j, getCellValue(cell));
                            j++;
                        }
                        for (Integer integer :pkIndexList) {
                            XSSFCell cell = xRow.getCell(integer);
                            sumUpdatePs.setObject(j, getCellValue(cell));
                            j++;
                        }
                        int updateRow = sumUpdatePs.executeUpdate();
                        if(updateRow==0){
                            int k = 1;
                            for (Integer integer :insertColIndex) {
                                XSSFCell cell = xRow.getCell(integer);
                                sumUpdatePs.setObject(k, getCellValue(cell));
                                k++;
                            }
                        }
                    }
                }
            } else if(sheetInfo.getSheetType()==ExcelConstants.EXCEL_FILE_TYPE_HSSF){
                sheet = (HSSFSheet)sheetInfo.getSheet();
                int rowNum = ((HSSFSheet)sheet).getLastRowNum()+1;
                for(int i=1+titleRowIndex;i<rowNum;i++){
                    HSSFRow hRow = ((HSSFSheet)sheet).getRow(i);
                    if(hRow!=null){
                        int j=1;
                        for (Integer integer :updateIndexList) {
                            HSSFCell cell = hRow.getCell(integer);
                            sumUpdatePs.setObject(j, getCellValue(cell));
                            j++;
                        }
                        for (Integer integer :pkIndexList) {
                            HSSFCell cell = hRow.getCell(integer);
                            sumUpdatePs.setObject(j, getCellValue(cell));
                            j++;
                        }
                        int updateRow = sumUpdatePs.executeUpdate();
                        if(updateRow==0){
                            int k = 1;
                            for (Integer integer :insertColIndex) {
                                HSSFCell cell = hRow.getCell(integer);
                                sumUpdatePs.setObject(k, getCellValue(cell));
                                k++;
                            }
                        }
                    }
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            CommonUtils.closeConn(null, sumUpdatePs, null);
            CommonUtils.closeConn(null, sumInsertPs, null);
        }
    }
    private static Map<String, Object> getSumTabSql(List<MetadataColumn> newMdColList, List<MetadataColumn> oldMdColList, String sumTabName, List<MetadataColumn> pkColList) {
        Map<String, Object> sqlMap = new HashMap<String, Object>();
        //更新sql
        StringBuffer updateSql = new StringBuffer("");
        StringBuffer pkColSb = new StringBuffer();
        StringBuffer updateColSb = new StringBuffer("update "+sumTabName +" set ");     
        List<Integer> pkIndexList = new ArrayList<Integer>();
        List<Integer> updateIndexList = new ArrayList<Integer>();
        Map<Integer,String> updateMdMap = new HashMap<Integer,String>();
        for(MetadataColumn mcPk :pkColList){
            pkColSb.append(","+mcPk.getColumnName()+"=?");
            pkIndexList.add(mcPk.getColumnIndex());
        }
        for(MetadataColumn mcUpdate:newMdColList){
            for(int i:pkIndexList){
                if(i!=mcUpdate.getColumnIndex())
                updateMdMap.put(mcUpdate.getColumnIndex(), mcUpdate.getColumnName());
            }
        }
        Iterator<Integer> updateIt = updateMdMap.keySet().iterator();
        while(updateIt.hasNext()){
            updateColSb.append(","+updateMdMap.get(updateIt.next())+"=?");
            updateIndexList.add(updateIt.next());
        }
        updateSql.append(updateColSb.substring(1)).append("where ").append(pkColSb.substring(1));
        sqlMap.put("updateSql", updateSql.toString());
        sqlMap.put("pkIndexList", pkIndexList);
        sqlMap.put("updateIndexList", updateIndexList);
        //计算对应表
        List<Integer> insertColIndex = new ArrayList<Integer>();
        for (MetadataColumn mcN: newMdColList) {
            for (MetadataColumn mcS: oldMdColList) {
                if (mcN.getTitleName().equals(mcS.getTitleName())&&mcN.getColumnType().equals(mcS.getColumnType())) {
                    insertColIndex.add(mcS.getColumnIndex());
                }
            }
        }
        if (insertColIndex.size()==newMdColList.size()) ;//才能继续处理
        //得到sql
        StringBuffer fieldStr = new StringBuffer(), valueStr=new StringBuffer();
        for (MetadataColumn mc: newMdColList) {
            fieldStr.append(","+mc.getColumnName());
            valueStr.append(",?");
        }
        StringBuffer insertSql=new StringBuffer().append("insert into "+sumTabName+"(").append(fieldStr.substring(1)+") values(").append(valueStr.substring(1)+")");
        sqlMap.put("insertSumTabSql", insertSql);
        sqlMap.put("insertColIndex", insertColIndex);
        return sqlMap;
    }
    public static StringBuffer saveInDB(Connection conn, SheetInfo sheetInfo, MetadataModel excelMD,MetadataModel newMD, String tempTabName, int titleRowIndex) {
        List<MetadataColumn> oldMdColList = excelMD.getColumnList();
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
        //先插临时表，在插积累表，要通过积累表来分析主键
        StringBuffer saveInfo = saveTempData(conn, sheetInfo, l, tempTabSql.toString(),titleRowIndex,tempTabName);
        return saveInfo;
    }
    private static StringBuffer saveTempData(Connection conn, SheetInfo sheetInfo, List<Integer> mapL,String tempSql, int titleRowIndex, String tempTabName) {
        PreparedStatement tempPs = null;
        Object sheet = null;
        StringBuffer saveInfo = new StringBuffer("sheetInfo:[{sheetName:"+sheetInfo.getSheetName()+",targetTab:"+tempTabName+",dataRows:");
        StringBuffer errorInfo = new StringBuffer("{rowIndex:");
        int allRows = 0;
        int inertRows = 0;
        int errorRows = 0;
        try {
            tempPs = conn.prepareStatement(tempSql);
            if(sheetInfo.getSheetType()==ExcelConstants.EXCEL_FILE_TYPE_XSSF){
                sheet = (XSSFSheet)sheetInfo.getSheet();
                int rowNum = ((XSSFSheet)sheet).getLastRowNum()+1;
                allRows = rowNum-titleRowIndex-1;
                for(int i=1+titleRowIndex;i<rowNum;i++){
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
                            errorInfo.append(""+i+",errorMessage:"+eX.getMessage()+"},");
                            errorRows++;
                            eX.printStackTrace();
                        }
                    }else{
                    }
                }
                inertRows = allRows-errorRows;
            } else if(sheetInfo.getSheetType()==ExcelConstants.EXCEL_FILE_TYPE_HSSF){
                sheet = (HSSFSheet)sheetInfo.getSheet();
                int rowNum = ((HSSFSheet)sheet).getLastRowNum()+1;
                allRows = rowNum-titleRowIndex-1;
                for(int i=titleRowIndex+1;i<rowNum;i++){
                    HSSFRow hRow = ((HSSFSheet)sheet).getRow(i);
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
                            errorInfo.append(""+i+",errorMessage:"+eX.getMessage()+"},");
                            errorRows++;
                            eX.printStackTrace();
                        }
                    }
                }
                inertRows = allRows-errorRows;
            }
            //errorInfo.substring(0,errorInfo.lastIndexOf(","));
            //saveInfo.append(""+allRows+",inertRows:"+inertRows+",updateRows:0,errorRows:+"+errorRows+",errorInfo:[").append(errorInfo+"]}]}}");
            return null;
        } catch(SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            CommonUtils.closeConn(null, tempPs, null);
        }
    }
    public static Map<String,Object> getMdModelMap(SheetInfo sheetInfo) {
        Map<String,Object> retMap = new HashMap<String,Object>();
        MetadataModel metadataModel;
        int dataRows;
        if(sheetInfo.getSheetType()==ExcelConstants.EXCEL_FILE_TYPE_XSSF){
            XSSFSheet xSheet = (XSSFSheet) sheetInfo.getSheet();
            int rows = xSheet.getLastRowNum()+1;
            if(rows>=2){
                dataRows = xSheet.getLastRowNum()+1;
                int rowLength = 0;
                //标题行
                XSSFRow titleRow = null;
                //标题所在行
                int titleRowIndex = 0;
                Map<Integer,String> titleMap = null;
                for(int i=0;i<dataRows;i++){
                    titleRow= xSheet.getRow(i);
                    if(titleRow==null) continue;
                    //每行长度
                    rowLength = titleRow.getLastCellNum();
                    titleRowIndex = i;
                    //得到TitleAry
                    int nu = 0;
                    titleMap = new HashMap<Integer,String>();
                    for(int k=0;k<rowLength;k++){
                        XSSFCell xCell = titleRow.getCell(k);
                        if(xCell==null){
                            nu++;
                            continue;
                        }
                        if(xCell.getCellType()==XSSFCell.CELL_TYPE_STRING){
                            String columnName = xCell.getStringCellValue();
                            titleMap.put(k, columnName); 
                        }
                    }
                    if(rowLength-nu==titleMap.size())
                    break;
                }
                //得到dataType
                if(titleMap.size()>0){
                    metadataModel = getMetadata(xSheet,dataRows,rowLength,titleMap,titleRowIndex); 
                    retMap.put("md", metadataModel);
                    retMap.put("titleRowIndex", titleRowIndex);
                    return retMap;
                }
            }else{
                return null;
            }
        }else if(sheetInfo.getSheetType()==ExcelConstants.EXCEL_FILE_TYPE_HSSF){
            HSSFSheet hSheet = (HSSFSheet) sheetInfo.getSheet();
            int rows = hSheet.getLastRowNum()+1;
            if(rows>=2){
                dataRows = hSheet.getLastRowNum()+1;
                int rowLength = 0;
                //标题行
                HSSFRow titleRow = null;
                //标题所在行
                int titleRowIndex = 0;
                Map<Integer,String> titleMap = null;
                for(int i=0;i<dataRows;i++){
                    titleMap = new HashMap<Integer,String>();
                    titleRow= hSheet.getRow(i);
                    if(titleRow==null) continue;
                    //每行长度
                    rowLength = titleRow.getLastCellNum();
                    titleRowIndex = i;
                    //得到TitleAry
                    int nu = 0;
                    for(int k=0;k<rowLength;k++){
                        HSSFCell hCell = titleRow.getCell(k);
                        if(hCell==null){
                            nu++;
                            continue;
                        }
                        if(hCell.getCellType()==XSSFCell.CELL_TYPE_STRING){
                            String columnName = hCell.getStringCellValue();
                            titleMap.put(k, columnName); 
                        }
                    }
                    if(rowLength-nu==titleMap.size())
                    break;
                }
                //得到dataType
                if(titleMap.size()>0){
                    metadataModel = getMetadata(hSheet,dataRows,rowLength,titleMap,titleRowIndex); 
                    retMap.put("md", metadataModel);
                    retMap.put("titleRowIndex", titleRowIndex);
                    return retMap; 
                }
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
        List<CellParam> intList = new ArrayList<CellParam>();
        typeMap.put(ExcelConstants.DATA_TYPE_INTEGER, intList);
        return typeMap;
    }

    /**
     * 得到md,
     * @param sheet 为XSSFSHeet
     * @param dataRows 数据行数
     * @param rowLength 每行长度
     * @param titleAry 标题数组
     */
    private static MetadataModel getMetadata(XSSFSheet sheet, int dataRows, int rowLength, Map<Integer,String> titleMap,int titleRowIndex) {
        MetadataModel metadataModel = null;
        Map<Integer,Map<String,List<CellParam>>> recordMap = new HashMap<Integer,Map<String,List<CellParam>>>();
        //当行数小于等于criticalPoint的时候
        if(dataRows-titleRowIndex>2&&dataRows-titleRowIndex<=ExcelConstants.DATA_ROWS_CRITICAL_POINT){
            for(int x=titleRowIndex+1;x<dataRows;x++){
                XSSFRow xRow = sheet.getRow(x);
                if(xRow!=null){
                    Iterator<Integer> yIt = titleMap.keySet().iterator();
                    while(yIt.hasNext()){
                        int y = yIt.next();
                        Map<String,List<CellParam>> typeMap = recordMap.get(y);
                        if(typeMap==null) typeMap = getTypeMap();
                        //cp赋值
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
            metadataModel = getDataTypes(recordMap,dataRows = dataRows-titleRowIndex-1,titleMap);
          //当行数大于criticalPoint的时候
        }else if(dataRows-titleRowIndex>ExcelConstants.DATA_ROWS_CRITICAL_POINT){
            int [] randoms = getRandoms(dataRows,ExcelConstants.EXCEL_MD_RANDOM_ROWSIZE,titleRowIndex);
            for(int x=0;x<randoms.length;x++){
                XSSFRow xRow = sheet.getRow(randoms[x]);
                if(xRow!=null){
                    Iterator<Integer> yIt = titleMap.keySet().iterator();
                    while(yIt.hasNext()){
                        int y = yIt.next();
                        Map<String,List<CellParam>> typeMap = recordMap.get(y);
                        if(typeMap==null) typeMap = getTypeMap();
                        //cp赋值
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
            metadataModel = getDataTypes(recordMap,randoms.length,titleMap);
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
     * @param titleRowIndex 
     * @param titleAry 标题数组
     */
    private static MetadataModel getMetadata(HSSFSheet sheet, int dataRows, int rowLength, Map<Integer,String> titleMap, int titleRowIndex) {
        MetadataModel metadataModel = null;
        //首先获得便于得到Md的结构
        Map<Integer,Map<String,List<CellParam>>> recordMap = new HashMap<Integer,Map<String,List<CellParam>>>();
        if(dataRows-titleRowIndex>2&&dataRows-titleRowIndex<=ExcelConstants.DATA_ROWS_CRITICAL_POINT){
            //小于100条数据的时候
            for(int x=1+titleRowIndex;x<dataRows;x++){
                HSSFRow hRow = sheet.getRow(x);
                if(hRow!=null){
                    Iterator<Integer> yIt = titleMap.keySet().iterator();
                    while(yIt.hasNext()){
                        int y = yIt.next();
                        Map<String,List<CellParam>> typeMap = recordMap.get(y);
                        if(typeMap==null) typeMap = getTypeMap();
                        //cp赋值
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
            }
            metadataModel = getDataTypes(recordMap,dataRows-1-titleRowIndex,titleMap);
        }else if(dataRows-titleRowIndex>ExcelConstants.DATA_ROWS_CRITICAL_POINT){
            int [] randoms = getRandoms(dataRows,ExcelConstants.EXCEL_MD_RANDOM_ROWSIZE,titleRowIndex);
            for(int x=0;x<randoms.length;x++){
                HSSFRow hRow = sheet.getRow(randoms[x]);
                if(hRow!=null){
                    Iterator<Integer> yIt = titleMap.keySet().iterator();
                    while(yIt.hasNext()){
                        int y = yIt.next();
                        Map<String,List<CellParam>> typeMap = recordMap.get(y);
                        if(typeMap==null) typeMap = getTypeMap();
                        //cp赋值
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
            }
            metadataModel = getDataTypes(recordMap,randoms.length,titleMap);
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
    private static MetadataModel getDataTypes(Map<Integer, Map<String, List<CellParam>>> recordMap, int dataRows, Map<Integer,String> titleMap) {
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
                mdColumn.setTitleName(titleMap.get(columnIndex));
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
        int intTypeSize = typeMap.get(ExcelConstants.DATA_TYPE_INTEGER).size();
        DTParam dtPmters = new DTParam();
        if((nullTypeSize+errorTypeSize)!=dataRows){
            dtPmters.setNullNum(nullTypeSize);
            String type="";
            double max=0;
            if(doubleTypeSize!=0){
                max = doubleTypeSize+intTypeSize;
                type = ExcelConstants.DATA_TYPE_DOUBLE;
            }else{
                max = intTypeSize;
                type = ExcelConstants.DATA_TYPE_INTEGER;
            }
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
     * @param titleRowIndex 
     * @return
     */
    public static int [] getRandoms(int randomRange,int randomSize, int titleRowIndex){
        if(randomSize>=randomRange-titleRowIndex-1)return null;
        int [] rdmAry = new int[randomSize];
        Map<Integer,Integer> randomMap = new HashMap<Integer,Integer>(); 
        Random r = new Random();
        for(int i=0;i<randomSize;i++){
            int k = r.nextInt(randomRange-titleRowIndex-1)+1+titleRowIndex;
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
        String type = null;
        if (hCell != null) {  
            int cellType = hCell.getCellType();  
            switch (cellType) {  
            case XSSFCell.CELL_TYPE_STRING:  
                type = "String";
                result = getParseVal(type,hCell);
                break;  
            case XSSFCell.CELL_TYPE_NUMERIC: 
                if (DateUtil.isCellDateFormatted(hCell)) {  
                    Date d = hCell.getDateCellValue();
                    result = d;
                } else {  
                    type = "Double";
                    result = getParseVal(type,hCell);  
                }
                break;  
            case XSSFCell.CELL_TYPE_FORMULA:  
                type = "Double";
                result = getParseVal(type,hCell); 
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
        String type = null;
        if (xCell != null) {  
            int cellType = xCell.getCellType();  
            switch (cellType) {  
            case XSSFCell.CELL_TYPE_STRING:  
                type = "String";
                result = getParseVal(type,xCell);
                break;  
            case XSSFCell.CELL_TYPE_NUMERIC: 
                if (DateUtil.isCellDateFormatted(xCell)) {  
                    Date d = xCell.getDateCellValue();
                    result = d;
                } else {  
                    type = "Double";
                    result = getParseVal(type,xCell);  
                }
                break;  
            case XSSFCell.CELL_TYPE_FORMULA:  
                type = "Double";
                result = getParseVal(type,xCell);   
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
    private static Object getParseVal(String type, XSSFCell xCell) {
        Object result = null;
        if(type.equals("String")){
            String strVal = xCell.getRichStringCellValue().getString();
            try{
                double dVal = Double.parseDouble(strVal);
                String intStr = ""+dVal;
                intStr = intStr.substring(0,intStr.lastIndexOf("."));
                if(Integer.parseInt(intStr)==dVal){
                    result = Integer.parseInt(intStr);
                }else{
                    result = dVal;
                }
            }catch(Exception e){
                result = strVal;
            }
        }else if(type.equals("Double")){
            double dVal = xCell.getNumericCellValue();
            String strD = ""+dVal;
            strD = strD.substring(0,strD.lastIndexOf("."));
            if(Integer.parseInt(strD)==dVal){
                result = Integer.parseInt(strD);
            } else{
                result = dVal;
            }
        }
        return result;
    }
    private static Object getParseVal(String type, HSSFCell hCell) {
        Object result = null;
        if(type.equals("String")){
            String strVal = hCell.getRichStringCellValue().getString();
            try{
                double dVal = Double.parseDouble(strVal);
                String intStr = ""+dVal;
                intStr = intStr.substring(0,intStr.lastIndexOf("."));
                if(Integer.parseInt(intStr)==dVal){
                    result = Integer.parseInt(intStr);
                }else{
                    result = dVal;
                }
            }catch(Exception e){
                result = strVal;
            }
        }else if(type.equals("Double")){
            double dVal = hCell.getNumericCellValue();
            String strD = ""+dVal;
            strD = strD.substring(0,strD.lastIndexOf("."));
            if(Integer.parseInt(strD)==dVal){
                result = Integer.parseInt(strD);
            } else{
                result = dVal;
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
                type = getParseType(type,xCell);
                break;  
            case XSSFCell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(xCell)) {  
                    type = "Date";
                } else {  
                    type = "Double";
                    type = getParseType(type,xCell);
                }
                break;  
            case XSSFCell.CELL_TYPE_FORMULA:  
                type = "Double";
                type = getParseType(type,xCell);
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
    private static String getParseType(String type, XSSFCell xCell) {
        if(type.equals("String")){
            String strVal = xCell.getStringCellValue();
            try{
                double dVal = Double.parseDouble(strVal);
                type = "Double";
                String strD = ""+dVal;
                strD = strD.substring(0,strD.lastIndexOf("."));
                if(Integer.parseInt(strD)==dVal){
                    type = "Integer";
                }
            }catch(Exception e){
                type = "String";
            }
        }else if(type.equals("Double")){
            double dVal = xCell.getNumericCellValue();
            String strD = ""+dVal;
            strD = strD.substring(0,strD.lastIndexOf("."));
            if(Integer.parseInt(strD)==dVal){
                type = "Integer";
            }
        }
        return type;
    }
    private static String getParseType(String type, HSSFCell hCell) {
        if(type.equals("String")){
            String strVal = hCell.getStringCellValue();
            try{
                double dVal = Double.parseDouble(strVal);
                type = "Double";
                String strD = ""+dVal;
                strD = strD.substring(0,strD.lastIndexOf("."));
                if(Integer.parseInt(strD)==dVal){
                    type = "Integer";
                }
            }catch(Exception e){
                type = "String";
            }
        }else if(type.equals("Double")){
            double dVal = hCell.getNumericCellValue();
            String strD = ""+dVal;
            strD = strD.substring(0,strD.lastIndexOf("."));
            if(Integer.parseInt(strD)==dVal){
                type = "Integer";
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
                type = getParseType(type,hCell);
                break;  
            case XSSFCell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(hCell)) {  
                    type = "Date";
                } else {  
                    type = "Double";  
                    type = getParseType(type,hCell);
                }
                break;  
            case XSSFCell.CELL_TYPE_FORMULA:  
                type = "Double";  
                type = getParseType(type,hCell);
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