package com.spiritdata.dataanal.dictionary.pojo;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 所有者“字典数据”。把一个所有者的所有字典信息按结构进行存储。
 * 主要服务于缓存(或Session)、数据导入、数据质量分析。
 * 这里只对Session中的数据进行处理，不对持久化数据库进行处理。
 * 
 * @author wh
 */
public class _OwnerDictionary {
    public ConcurrentHashMap<String, DictModel> dictModelMap; //所有者字典模型的数据集合
    public List<DictMaster> dmList = null; //所有者字典组列表
    public List<DictDetail> ddList = null; //所有者字典项列表

    protected boolean loadSuccess=false; //加载数据是否完成

    protected String ownerId; //所有者Id，有可能是用户Id也有可能是SessionId
    public String getOwnerId() {
        return ownerId;
    }

    protected int ownerType; //所有者类型：1=用户；2=Session
    public int getOwnerType() {
        return ownerType;
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
     * @param ownerType 所有者Id
     */
    public _OwnerDictionary(String ownerId, int ownerType) {
        this.ownerId = ownerId;
        this.ownerType = ownerType;
        this.loadSuccess = false;
    }

    /**
     * 根据Id得到字典模式
     * @param dictMId 字典组Id
     * @return 元数据信息
     */
    public DictModel getDictModelById(String dictMid) {
        if (dictModelMap==null) return null;
        return dictModelMap.get(dictMid);
    }
}