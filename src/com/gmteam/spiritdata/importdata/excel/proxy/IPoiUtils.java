package com.gmteam.spiritdata.importdata.excel.proxy;

import java.util.Map;
import com.gmteam.spiritdata.importdata.excel.pojo.SheetInfo;

/** 
 * @author mht
 * @version  
 * 类说明 excel接口类
 */
public interface IPoiUtils {
    /**
     * 根据文件类型,文件,返回一个execlWorkBook
     * @param execlFile
     * @return
     */
    public Object getWorkBook()throws Exception;
    /**
     * 返回一个MateDate的集合
     * @return
     * @throws Exception
     */
    public Map<SheetInfo, Object> getMDMap()throws Exception;
    /**
     * 返回抓取的数据
     * @return
     */
    public Object getData();
}
