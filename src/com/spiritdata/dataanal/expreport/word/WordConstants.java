package com.spiritdata.dataanal.expreport.word;
/**
 * word常量
 * @author mht
 */
public class WordConstants {
	
	//以下为report中的特殊字段========
	/** report _HEAD */
    public final static String REPORT_HEAD = "_HEAD";
    /** report _DLIST */
    public final static String REPORT_DLIST = "_DLIST";
    /** report _REPORT */
    public final static String REPORT_REPORT = "_REPORT";
    /**  report subSeg */
    public final static String REPORT_SUBSEG = "subSeg";
    //以上为report中的特殊字段=========

    /**  report 的服务端保存路径 */
    public final static String REPORT_SAVE_PATH = "/reportDownLoad/";

    //以下 为report 默认字体样式===========
    /**  word 标题字体大小 默认22 */
    public final static Integer REPORT_TITLE_FONT_SIZZE = 22;
    /**  word 正文字体大小 默认12 */
    public final static Integer REPORT_CONTENT_FONT_SIZZE = 12;
    //以上为report 默认字体样式===========

    //以下为 report 大纲参数===========
    /** word 大纲级别1~9对应0~8(越小级别越高) */
    public final static int  REPORT_OUT_LINE_MAX_LEVEL = 8;
    /** word 大纲级别1~9对应0~8(越小级别越高) */
    public final static int  REPORT_OUT_LINE_MIN_LEVEL = 0;
    /** word 大纲级别id前缀 Prefix+int */
    public final static String  REPORT_OUT_LINE_LEVEL_PREFIX = "oTL";
    //以上为 report 大纲参数===========
    
}
