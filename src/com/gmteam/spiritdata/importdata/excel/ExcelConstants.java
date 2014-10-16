package com.gmteam.spiritdata.importdata.excel;

import com.gmteam.framework.core.model.BaseObject;

/** 
 * @author mht
 * @version  
 * 类说明 处理excel中的常量
 */
public class ExcelConstants extends BaseObject{
    /**抽取随机数的个数,用于得到随机的行数*/
    public final static int EXCEL_MD_RANDOM_ROWSIZE=100;
    /**dataType类型，double*/
    public final static String DATA_TYPE_DOUBLE = "Double";
    /**dataType类型，date*/
    public final static String DATA_TYPE_DATE = "Date";
    /**dataType类型，boolean*/
    public final static String DATA_TYPE_BOOLEAN = "Boolean";
    /**dataType类型，null*/
    public final static String DATA_TYPE_NULL = "Null";
    /**dataType类型，string*/
    public final static String DATA_TYPE_STRING = "String";
    /**比重常量*/
    public final static int DATA_TYPE_PROPORTION = 80;
}
 