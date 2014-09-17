package com.gmteam.upload.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/** 
 * @author 
 * @version  
 * 类说明 
 */
public class ExcelPoiUtils {
    private HSSFWorkbook hSSFWorkbook;
    private XSSFWorkbook xSSFWorkbook;
    public ExcelPoiUtils(File execlFile) throws IOException, FileNotFoundException {  
        //workbook = new HSSFWorkbook(new FileInputStream(execlFile));  
    }  
    public ExcelPoiUtils() {
        super();
    }
}
