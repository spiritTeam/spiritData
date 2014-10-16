package com.gmteam.spiritdata.metadata.relation;

import java.util.Map;

import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;

/**
 * 所有者“关系型元数据”处理单元。
 * 所有者对“关系型元数据”的存储结构以及对这个存储结构的操作的集合。
 * 主要服务于缓存(或Session)、数据导入、数据质量分析。
 * 
 * 这个类的实例：存储基本元数据信息，并与数据库实现同步。
 * 
 * 这个类提供如下功能：加载存储的信息，与数据库同步，元数据比较等功能。
 * @author wh
 */
public class OwnerRmdUnit {
    private String onwerId; //所有者Id，有可能是用户Id也有可能是SessionId
    private Map<String, MetadataModel> metadataModelList; //
}
