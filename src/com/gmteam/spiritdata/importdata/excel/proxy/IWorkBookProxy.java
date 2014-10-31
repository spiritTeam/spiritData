package com.gmteam.spiritdata.importdata.excel.proxy;

import java.util.List;
import com.gmteam.spiritdata.importdata.excel.pojo.SheetInfo;

/** 
 * @author mht
 * @version  
 * 类说明 excel接口类
 */
public interface IWorkBookProxy {
    /**
     * 根据文件类型,文件,返回一个execlWorkBook
     * @param execlFile
     * @return
     */
    public Object getWorkBook();
    /**
     * 得到一个sheetInfo的List
     */
    public List<SheetInfo> getSheetList();
}
