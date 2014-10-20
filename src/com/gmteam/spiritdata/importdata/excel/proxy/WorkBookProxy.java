package com.gmteam.spiritdata.importdata.excel.proxy;

import java.io.File;

import com.gmteam.spiritdata.importdata.excel.ExcelConstants;
/** 
 * @author mht
 * @version  
 * 类说明 代理类，根据fileType类型，
 * 得到相应的workbook，
 */
public class WorkBookProxy implements IPoiUtils{
    private Object excelWorkBook;
    private IPoiUtils iPoiUtils;
    public WorkBookProxy(File execlFile,int fileType) throws Exception {
        if (fileType==ExcelConstants.EXCEL_FILE_TYPE_XSSF)
            this.iPoiUtils = new XSSFWorkBookImpl(execlFile);
        else if (fileType==ExcelConstants.EXCEL_FILE_TYPE_HSSF)
            this.iPoiUtils = new HSSFWorkBookImpl(execlFile);
        else throw new Exception("不是excel文件");
    }
    @Override
    public Object getWorkBook() throws Exception {
        excelWorkBook = iPoiUtils.getWorkBook();
        return excelWorkBook;
    }
    @Override
    public Object getMDList() throws Exception {
        return iPoiUtils.getMDList();
    }
}
