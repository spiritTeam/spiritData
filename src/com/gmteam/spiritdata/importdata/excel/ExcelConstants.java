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

    /**抽取随机数的个数,用于得到随机的行数*/
    public final static int EXCEL_MD_RANDOM_ROWSIZE=100;
    
    /**Excel类型，double*/
    public final static String DATA_TYPE_DOUBLE = "Double";
    /**Excel类型，date*/
    public final static String DATA_TYPE_DATE = "Date";
    /**Excel类型，boolean*/
    public final static String DATA_TYPE_BOOLEAN = "Boolean";
    /**Excel类型，null*/
    public final static String DATA_TYPE_NULL = "Null";
    /**Excel类型，error*/
    public final static String DATA_TYPE_ERROR = "Error";
    /**Excel类型，string*/
    public final static String DATA_TYPE_STRING = "String";
    public static final String DATA_TYPE_INTEGER = "Integer";
    
    /**比重常量*/
    public final static double DATA_TYPE_PROPORTION = 0.8;
    /**Excel文件类型，XSSF，对应2007以上版本包括2007*/
    
    public final static int EXCEL_FILE_TYPE_XSSF = 1;
    /**Excel文件类型，HSSF，对应2003版本*/
    public final static int EXCEL_FILE_TYPE_HSSF = 2;
    /**数据抽取数量常量Critical_Point*/
    public final static int DATA_ROWS_CRITICAL_POINT = 101;

}
 