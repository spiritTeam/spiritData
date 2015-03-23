package com.spiritdata.dataanal;

public abstract class SDConstants {
    //-用户及用户管理相关常量-----------------------------------------------------------
    //所有者元数据Session缓存
    public final static String SESSION_OWNER_RMDUNIT="OnwerRmdUnit";
    //所有者字典信息Session缓存
    public final static String SESSION_OWNER_DICT="OnwerDictUnit";

    //以下为分析方法相关的常量
    public final static String ANAL_MD_KEY="METADATA-key";//主键分析标识
    public final static String ANAL_MD_DICT="METADATA-dict";//字典分析标识
    public final static String ANAL_MD_GETINFO="METADATA-get";//获得元数据信息
    public final static String ANAL_MD_SDICT="METADATA-sigleDice";//单项指标分析

    //以下jsonD常量类型信息，JDC=JsonDCode，主要和分析有关
    public final static String JDC_MD_INFO="SD.TEAM.ANAL::0003"; //元数据信息（一个或多个）
    public final static String JDC_MD_SDICT="SD.TEAM.ANAL::0004"; //元数据单项指标分析结果信息

    //以下为报告类型Code常量
    public final static String RP_AFTER_IMP="SD.TEAM.REPORT::0001";//导入结构化数据后的报告

    //以下为Cache中用到的常量
    public final static String CACHE_TASKS="CacheTask";//缓存在内存中的任务信息标识，
}