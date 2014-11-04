package com.gmteam.spiritdata.importdata.excel.service;

import java.io.File;
import java.io.FileInputStream;

import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.stereotype.Component;

import com.gmteam.spiritdata.importdata.excel.pojo.SheetInfor;

/**
 * 处理excel文件。
 * 导入系统，并进行初步分析，主要是导入
 * @author wh
 */
@Component
public class DealExcelFileService {
    private static int EXECL2007_FLAG = 1;//2007及以后版本，用XSSFWorkbook
    private static int EXECL2003_FLAG = 2;//2003及以前版本，用HSSFWorkbook

    public void process(String fileName, HttpSession session) {
        File excelFile = new File(fileName);
        Workbook book = null;
        int excelType = 0;
        FileInputStream fis = null;

        try {
            //获得处理excel的workbook
            fis = new FileInputStream(excelFile);
            try {
                book = new XSSFWorkbook(fis);
                excelType = DealExcelFileService.EXECL2007_FLAG;
            } catch (Exception ex) {
                try {
                    book = new HSSFWorkbook(fis);
                    excelType = DealExcelFileService.EXECL2003_FLAG;
                } catch (Exception e) {
                    
                }
            }
            if (excelType==0) {//
                // TODO 记录日志 
                return;
            }
            //根据sheet进行处理
            for (int i=0; i<book.getNumberOfSheets(); i++) {
                Object sheet = book.getSheetAt(i);
                SheetInfor si = getSheetInfo(sheet, excelType);
                si.setSheetIndex(i);
                //1-分析文件结构，得到元数据模式
                
            }
        } catch(Exception e) {
            // TODO 写日志
            e.printStackTrace();
        } finally {
            try {if (fis!=null) fis.close(); } catch (Exception e) {e.printStackTrace();} finally {fis = null;};
        }
    }

    /**
     * 根据sheet或的sheetInfo
     * @param sheet
     * @param excelType
     * @return
     */
    private SheetInfor getSheetInfo(Object sheet, int excelType) {
        SheetInfor ret = new SheetInfor();
        ret.setExcelType(excelType);
        ret.setSheet(sheet);
        if (excelType==DealExcelFileService.EXECL2003_FLAG) {
            ret.setSheetName(((HSSFSheet)sheet).getSheetName());
        } else if (excelType==DealExcelFileService.EXECL2007_FLAG) {
            ret.setSheetName(((XSSFSheet)sheet).getSheetName());
        }
        return ret;
    }
}