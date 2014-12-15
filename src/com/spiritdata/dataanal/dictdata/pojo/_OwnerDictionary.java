package com.spiritdata.dataanal.dictdata.pojo;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.spiritdata.framework.core.model.tree.TreeNode;

/**
 * 所有者“字典数据”。把一个所有者的所有字典信息按结构进行存储。
 * 主要服务于缓存(或Session)、数据导入、数据质量分析。
 * 
 * @author wh
 */
public class _OwnerDictionary {
    public ConcurrentHashMap<String, DictModel> dictModelMap; //所有者字典数据集合
    public ConcurrentHashMap<String, TreeNode<DictDetail>> dictTreeMap; //所有者字典项索引树
    public List<DictMaster> dmList = null; //所有者字典组列表
    public List<DictDetail> ddList = null; //所有者字典项列表

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
    public _OwnerDictionary(String onwerId, int onwerType) {
        this.onwerId = onwerId;
        this.onwerType = onwerType;
    }

    /**
     * 根据Id得到字典模型
     * @param dictMId 字典组Id
     * @return 元数据信息
     */
    public DictModel getDictModelById(String dictMid) {
        if (dictModelMap==null) return null;
        return dictModelMap.get(dictMid);
    }
}