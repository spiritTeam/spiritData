package com.gmteam.spiritdata.importdata.proxy;

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
    public Object getMDList()throws Exception;
}
