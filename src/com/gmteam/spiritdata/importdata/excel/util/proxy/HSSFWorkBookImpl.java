package com.gmteam.spiritdata.importdata.excel.util.proxy;

import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/** 
 * @author mht 
 * @version  
 * 类说明 
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
        // TODO Auto-generated method stub
        return null;
    }
}
