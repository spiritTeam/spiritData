package com.gmteam.spiritdata.metadata.relation.pojo;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 所有者“关系型元数据”。把一个所有者的所有基础元数据信息按结构进行存储。
 * 主要服务于缓存(或Session)、数据导入、数据质量分析。
 * 
 * @author wh
 */
public class _OwnerMetadata {
    public ConcurrentHashMap<String, MetadataModel> mdModelMap; //所有者元数据集合

    protected boolean loadSuccess=false; //加载数据是否完成

    protected String onwerId; //所有者Id，有可能是用户Id也有可能是SessionId
    public String getOnwerId() {
        return onwerId;
    }

    protected int onwerType; //所有者类型：1=用户；2=Session
    public int getOnwerType() {
        return onwerType;
    }

    public void setLoadSuccess() {
        this.loadSuccess=true;
    }
    public boolean isLoadSuccess() {
        return this.loadSuccess;
    }

    /**
     * 构造所有者处理单元
     * @param ownerId 所有者类型
     * @param onwerType 所有者Id
     */
    public _OwnerMetadata(String onwerId, int onwerType) {
        this.onwerId = onwerId;
        this.onwerType = onwerType;
    }

    /**
     * 根据Id得某一元数据
     * @param mdMId 元数据Id
     * @return 元数据信息
     */
    public MetadataModel getMetadataById(String mdMId) {
        if (mdModelMap==null) return null;
        return mdModelMap.get(mdMId);
    }

    /**
     * 根据积累表名称得某一元数据
     * @param tableName 积累表名称
     * @return 元数据信息
     */
    public MetadataModel getMetadataByTableName(String tableName) {
        if (mdModelMap==null) return null;
        MetadataModel ret = null;
        for (String key: mdModelMap.keySet()) {
            ret = mdModelMap.get(key);
            if (ret.getTableName().equals(tableName)) break;
        }
        return ret;
    }
}