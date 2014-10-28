package com.gmteam.spiritdata.importdata.excel.proxy;

import java.io.File;
import java.util.List;

import com.gmteam.spiritdata.importdata.excel.ExcelConstants;
import com.gmteam.spiritdata.importdata.excel.pojo.SheetInfo;
/** 
 * @author mht
 * @version  
 * 类说明 代理类，根据fileType类型，
 * 得到相应的workbook，
 */
public class WorkBookProxy implements IWorkBookProxy{
    private Object excelWorkBook;
    private IWorkBookProxy iWorkBookProxy;
    public WorkBookProxy(File execlFile,int fileType) throws Exception {
        if (fileType==ExcelConstants.EXCEL_FILE_TYPE_XSSF)
            this.iWorkBookProxy = new XSSFWorkBookImpl(execlFile);
        else if (fileType==ExcelConstants.EXCEL_FILE_TYPE_HSSF)
            this.iWorkBookProxy = new HSSFWorkBookImpl(execlFile);
        else throw new Exception("不是excel文件");
    }
    @Override
    public Object getWorkBook() throws Exception {
        excelWorkBook = iWorkBookProxy.getWorkBook();
        return excelWorkBook;
    }
    @Override
    public List<SheetInfo> getSheetList() {
        return iWorkBookProxy.getSheetList();
    }
}
