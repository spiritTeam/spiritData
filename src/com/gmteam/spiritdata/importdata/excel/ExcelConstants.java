package com.gmteam.spiritdata.importdata.excel;

/** 
 * 处理excel中的常量
 * @author mht
 * @version  
 */
public abstract class ExcelConstants {
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
    public final static double WEIGHT_OF_DATATYPE = 0.8d;

    /**采样临界行，总行数大于此行数，则进行采样抽取*/
    public final static int SAMPLING_CRITICAL_COUNT = 200;

    /**顺序采样临界行，大于此行数，则进行采样抽取*/
    public final static int LIMIT_SEQUENCE_COUNT = 20;

    public static String convert2DataTypeString(int dtype) {
        switch (dtype) {
        case DATA_TYPE_STRING: return "String";
        case DATA_TYPE_NUMERIC: return "Numeric";
        case DATA_TYPE_DOUBLE: return "Double";
        case DATA_TYPE_INTEGER: return "Integer";
        case DATA_TYPE_DATE: return "Date";
        case DATA_TYPE_BOOLEAN: return "Boolean";
        case DATA_TYPE_NULL: return "Null";
        case DATA_TYPE_ERROR: return "Error";
        case DATA_TYPE_FORMULA: return "Formula";
        default: return "unknow";
        }
    }

    public static int convert2DataType(String dtypeStr) {
        if (dtypeStr.equalsIgnoreCase("String")) return DATA_TYPE_STRING;
        else if (dtypeStr.equalsIgnoreCase("Numeric")) return DATA_TYPE_NUMERIC;
        else if (dtypeStr.equalsIgnoreCase("Double")) return DATA_TYPE_DOUBLE;
        else if (dtypeStr.equalsIgnoreCase("Integer")) return DATA_TYPE_INTEGER;
        else if (dtypeStr.equalsIgnoreCase("Boolean")) return DATA_TYPE_BOOLEAN;
        else if (dtypeStr.equalsIgnoreCase("Date")) return DATA_TYPE_DATE;
        else if (dtypeStr.equalsIgnoreCase("Null")) return DATA_TYPE_NULL;
        else if (dtypeStr.equalsIgnoreCase("Error")) return DATA_TYPE_ERROR;
        else if (dtypeStr.equalsIgnoreCase("Formula")) return DATA_TYPE_FORMULA;
        else return -1;
    }
}