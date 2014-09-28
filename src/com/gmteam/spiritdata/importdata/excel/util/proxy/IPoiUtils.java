package com.gmteam.spiritdata.importdata.excel.util.proxy;

/** 
 * @author mht
 * @version  
 * 类说明 代理类接口
 */
public interface IPoiUtils {
    /**
     * 根据文件类型,文件,返回一个execlWorkBook
     * @param execlFile
     * @return
     */
    public Object getWorkBook()throws Exception;
    public Object getMDList()throws Exception;
}
