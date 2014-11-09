package com.gmteam.spiritdata.importdata.excel;

/** 
 * 处理excel中的常量
 * @author mht
 * @version  
 */
public class ExcelConstants {
    /**
     * excel版本标识，2007及以后版本，用XSSFWorkbook
     */
    public static int EXECL2007_FLAG = 1;
    /**
     * excel版本标识，2003及以前版本，用HSSFWorkbook
     */
    public static int EXECL2003_FLAG = 2;

    /**Excel类型，string*/
    public final static int DATA_TYPE_STRING = 1;
    /**Excel类型，numeric*/
    public final static int DATA_TYPE_NUMERIC = 0;
    /**Excel类型，double*/
    public final static int DATA_TYPE_DOUBLE = 6;
    /**Excel类型，integer*/
    public final static int DATA_TYPE_INTEGER = 7;
    /**Excel类型，date*/
    public final static int DATA_TYPE_DATE = 8;
    /**Excel类型，boolean*/
    public final static int DATA_TYPE_BOOLEAN = 4;
    /**Excel类型，null*/
    public final static int DATA_TYPE_NULL = 3;
    /**Excel类型，error*/
    public final static int DATA_TYPE_ERROR = 5;
    /**Excel类型，formula 公式*/
    public final static int DATA_TYPE_FORMULA = 2;
    
    /**比重常量*/
    public final static double DATA_TYPE_PROPORTION = 0.8;
} 