package com.gmteam.spiritdata.importdata.proxy;

import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.gmteam.spiritdata.importdata.excel.util.SheetInfo;
import com.gmteam.spiritdata.matedata.relation.MetaColumnInfo;
import com.gmteam.spiritdata.matedata.relation.MetaInfo;

/** 
 * @author mht
 * @version  
 * 类说明  适用于2007之后版本的excel(包含2007)
 */
public class XSSFWorkBookImpl implements IPoiUtils {
    /**workbook*/
    private XSSFWorkbook workbook; 
    public XSSFWorkBookImpl() {  
    }
    public XSSFWorkBookImpl(File execlFile) throws Exception{
        workbook = new XSSFWorkbook(new FileInputStream(execlFile));
    } 
    @Override
    public Object getWorkBook() {
        return workbook;
    }
    @Override
    public Object getMDList() throws Exception {
        int sheetSize = workbook.getNumberOfSheets();
        XSSFSheet sheet;
        for(int i=0;i<sheetSize;i++ ){
            int sheetIndex = i;
            sheet = workbook.getSheetAt(i);
            int rows = sheet.getLastRowNum()+1;
            /**
             * 保存sheetInfo
             */
            SheetInfo sheetInfo = new SheetInfo();
            sheetInfo.setSheetIndex(sheetIndex);
            sheetInfo.setSheetName(sheet.getSheetName());
            /**
             * 根据条数分析MateData
             */
            if(rows<10&&rows>=2){
                 MetaInfo metaInfo =  getMDLess10Rows(sheetIndex,sheet,rows);
            }else{
                MetaInfo metaInfo =  getMDMore10Rows(sheetIndex,sheet,rows);
            }
        }
        return null;
    }
    /**
     * 总条数多于10的
     * @param i
     * @param sheet
     * @param rows
     * @return
     */
    private MetaInfo getMDMore10Rows(int sheetIndex, XSSFSheet sheet,int rows) {
        SheetInfo sheetInfo = new SheetInfo();
        sheetInfo.setSheetIndex(sheetIndex);
        sheetInfo.setSheetName(sheet.getSheetName());
        MetaColumnInfo metaColumnInfo = new MetaColumnInfo();
        return null;
    }
    /**
     * 总条数少于10的
     * @param i
     * @param sheet
     * @param rows
     * @return
     */
    private MetaInfo getMDLess10Rows(int sheetIndex, XSSFSheet sheet,int rows) {
        SheetInfo sheetInfo = new SheetInfo();
        sheetInfo.setSheetIndex(sheetIndex);
        sheetInfo.setSheetName(sheet.getSheetName());
        MetaColumnInfo metaColumnInfo = new MetaColumnInfo();
        return null;
    }

}