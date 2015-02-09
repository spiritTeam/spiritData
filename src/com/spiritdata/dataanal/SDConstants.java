package com.spiritdata.dataanal;

public abstract class SDConstants {
    //-用户及用户管理相关常量-----------------------------------------------------------
    //所有者元数据Session缓存
    public final static String SESSION_OWNER_RMDUNIT="OnwerRmdUnit";
    //所有者字典信息Session缓存
    public final static String SESSION_OWNER_DICT="OnwerDictUnit";

    //以下为分析相关的常量
    public final static String ANAL_MD_KEY="METADATA-key";//主键分析标识
    public final static String ANAL_MD_DICT="METADATA-dict";//字典分析标识
    public final static String ANAL_MD_GETINFO="METADATA-get";//字典分析标识

    public final static String JDC_MDINFO="SD.TEAM.ANAL::0003"; //元数据信息

    //以下为报告类型Code常量
    public final static String RP_AFTER_IMP="SD.TEAM.REPORT::0001";//导入结构化数据后的报告
}