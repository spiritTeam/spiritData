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
 * 类说明  2007 +
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
            sheet = workbook.getSheetAt(i);
            int rows = sheet.getLastRowNum()+1;
            if(rows<10&&rows>=2){
                 MetaInfo metaInfo =  getMateDataLessRows(i,sheet,rows);
            }else{
                MetaInfo metaInfo =  getMateDataMoreRows(i,sheet,rows);
            }
        }
        return null;
    }
    private MetaInfo getMateDataMoreRows(int i, XSSFSheet sheet,int rows) {
        SheetInfo sheetInfo = new SheetInfo();
        sheetInfo.setSheetIndex(i);
        sheetInfo.setSheetName(sheet.getSheetName());
        MetaColumnInfo metaColumnInfo = new MetaColumnInfo();
        return null;
    }
    private MetaInfo getMateDataLessRows(int i, XSSFSheet sheet,int rows) {
        SheetInfo sheetInfo = new SheetInfo();
        sheetInfo.setSheetIndex(i);
        sheetInfo.setSheetName(sheet.getSheetName());
        MetaColumnInfo metaColumnInfo = new MetaColumnInfo();
        return null;
    }

}
