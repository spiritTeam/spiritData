package com.gmteam.importdata.excel.util;

import java.io.File;

import com.gmteam.importdata.excel.util.proxy.WorkBookProxy;

/** 
 * @author mht
 * @version  
 * 类说明 
 */
public class ExcelPoiUtils{
    public static Object getWorkBook(String fileName,Integer fileType) throws Exception{
        File file = new File(fileName);
        WorkBookProxy workBookProxy= new WorkBookProxy(file,fileType);
        return workBookProxy.getWorkBook();
    }
}
