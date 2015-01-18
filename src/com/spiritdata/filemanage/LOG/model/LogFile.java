package com.spiritdata.filemanage.LOG.model;

import java.io.Serializable;

/**
 * 日志信息，日志统一放在一个目录下(可能是分布式存储的一个根)
 * 日志信息通过log4j实现，是顺序记录的信息，其中的信息有两类：<br/>
 * 1-无结构的描述信息，如一般的log日志，包括exception中的类链条<br/>
 * 2-有结构的信息，结构为json<br/>
 * 如：<br/>
 * <code>
 * 2014-12-09 09:46:44,749 DEBUG [D] 描述信息
 * 2014-12-09 09:46:44,749 DEBUG [J] {abc:"", list:[]}
 * </code>
 * @author wh
 */
public class LogFile implements Serializable {
    private static final long serialVersionUID = 956357457822723413L;
    
}