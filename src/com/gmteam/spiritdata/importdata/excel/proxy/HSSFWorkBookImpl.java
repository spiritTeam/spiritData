package com.gmteam.spiritdata.importdata.excel.proxy;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.gmteam.spiritdata.importdata.excel.ExcelConstants;
import com.gmteam.spiritdata.importdata.excel.pojo.SheetInfo;

/** 
 * @author mht
 * @version   
 * 类说明  适用于2007之前版本的excel(不包含2007)
 */
public class HSSFWorkBookImpl implements IWorkBookProxy{
    private HSSFWorkbook workbook;
    public HSSFWorkBookImpl() {  
    }
    public HSSFWorkBookImpl(File execlFile) throws Exception{
        workbook = new HSSFWorkbook(new FileInputStream(execlFile)); 
    } 
    @Override
    public Object getWorkBook(){
        return workbook;
    }
    @Override
    public List<SheetInfo> getSheetList() {
        List<SheetInfo> sheetInfoList = new ArrayList<SheetInfo>();
        int sheetSize = this.workbook.getNumberOfSheets();
        for(int i=0;i<sheetSize;i++){
            SheetInfo sheetInfo = new SheetInfo();
            HSSFSheet xSheet = workbook.getSheetAt(i);
            sheetInfo.setSheet(xSheet);
            sheetInfo.setSheetIndex(i);
            sheetInfo.setSheetName(xSheet.getSheetName());
            sheetInfo.setSheetType(ExcelConstants.EXCEL_FILE_TYPE_HSSF);
            sheetInfoList.add(sheetInfo);
        }
        return sheetInfoList;
    }
}
