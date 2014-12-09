package com.gmteam.spiritdata.filemanage.LOG.model;

import com.gmteam.framework.core.model.BaseObject;

/**
 * 日志信息，日志统一放在一个目录下(可能是分布式存储的一个根)
 * 日志信息通过log4j实现，是顺序记录的信息，其中的信息有两类：
 * 1-无结构的描述信息，如一般的log日志，包括exception中的类链条
 * 2-有结构的信息，结构为json
 * 如：
 * <prep>
 * 2014-12-09 09:46:44,749 DEBUG <D> 描述信心
 * </prep>
 * @author wh
 */
public class LogFile extends BaseObject {
    private static final long serialVersionUID = 956357457822723413L;

}