package com.gmteam.spiritdata.importdata.proxy;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.gmteam.spiritdata.importdata.excel.util.CommonUtils;
import com.gmteam.spiritdata.importdata.excel.util.SheetInfo;
import com.gmteam.spiritdata.matedata.relation.MetaColumnInfo;
import com.gmteam.spiritdata.matedata.relation.MetaInfo;

/** 
 * @author mht
 * @version   
 * 类说明  适用于2007之前版本的excel(不包含2007)
 */
public class HSSFWorkBookImpl implements IPoiUtils{
    private HSSFWorkbook workbook;
    public HSSFWorkBookImpl() {  
    }
    public HSSFWorkBookImpl(File execlFile) throws Exception{
        workbook = new HSSFWorkbook(new FileInputStream(execlFile)); 
    } 
    @Override
    public Object getWorkBook() throws Exception {
        return workbook;
    }
    @Override
    public Object getMDList() throws Exception {
        int sheetSize = workbook.getNumberOfSheets();
        HSSFSheet sheet;
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
            if(rows<12&&rows>=2){
                 MetaInfo metaInfo =  getMDLessThan10Rows(sheetIndex,sheet,rows);
            }else{
                MetaInfo metaInfo =  getMDMoreThan10Rows(sheetIndex,sheet,rows);
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
    CommonUtils cu = new CommonUtils();
    private MetaInfo getMDMoreThan10Rows(int sheetIndex, HSSFSheet sheet,int rows) {
        MetaColumnInfo metaColumnInfo = new MetaColumnInfo();
        
        List<Integer> randomList = cu.getRandomList(rows);
        for(int i=0;i<randomList.size();i++){
            HSSFRow row = sheet.getRow(i);
            
        }
        return null;
    }
    /**
     * 总条数少于10的
     * @param i
     * @param sheet
     * @param rows
     * @return
     */
    private MetaInfo getMDLessThan10Rows(int sheetIndex, HSSFSheet sheet,int rows) {
        MetaColumnInfo metaColumnInfo = new MetaColumnInfo();
        return null;
    }
}
