package com.gmteam.importdata.excel.util.proxy;

import java.io.File;
/** 
 * @author mht
 * @version  
 * 类说明 
 */
public class WorkBookProxy implements IPoiUtils{
    private Object excelWorkBook;
    private IPoiUtils iPoiUtils;
    private Integer fileType;
    /**
     * 默认的代理是XSSF的
     */
    public WorkBookProxy(File execlFile,Integer fileType) throws Exception {
//        this.fileType = fileType;
        if (fileType==2)
            this.iPoiUtils = new XSSFWorkBookImpl(execlFile);
        else if (fileType==1)
            this.iPoiUtils = new HSSFWorkBookImpl(execlFile);
        else throw new Exception("不是excel文件");
//        if (execlFile.getAbsolutePath().endsWith(".xlsx"))
//            this.iPoiUtils = new XSSFWorkBookImpl(execlFile);
//        else if (execlFile.getAbsolutePath().endsWith(".xls"))
//            this.iPoiUtils = new HSSFWorkBookImpl(execlFile);
//        else throw new Exception("不是excel文件");
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
