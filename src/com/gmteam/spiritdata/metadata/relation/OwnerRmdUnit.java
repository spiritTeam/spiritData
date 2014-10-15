package com.gmteam.spiritdata.metadata.relation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;

/**
 * 所有者“关系型元数据”单元。
 * 所有者对“关系型元数据”的存储结构以及对这个存储结构的操作。
 * 主要服务于缓存(或Session)、数据导入、数据质量分析。
 * 
 * 这个类的实例：存储基本元数据信息，并与数据库实现同步。
 * 
 * 这个类提供如下功能：加载存储的信息，与数据库同步，元数据比较等功能。
 * @author wh
 */
public class OwnerRmdUnit {
    private boolean loadSuccess=false; //加载数据是否完成

    private String onwerId; //所有者Id，有可能是用户Id也有可能是SessionId
    private int onwerType; //所有者类型：1=用户；2=Session

    private Map<String, MetadataModel> mdModelMap; //所有者元数据集合

    /**
     * 构造所有者处理单元
     * @param ownerId 所有者类型
     * @param onwerType 所有者Id
     */
    public OwnerRmdUnit(String onwerId, int onwerType) {
        this.onwerId = onwerId;
        this.onwerType = onwerType;
        this.mdModelMap = new ConcurrentHashMap();
        
        //读取数据库内容到mdModelList；采用线程的方式
        
    }

    /**
     * 在所有者创建的全部元数据模型集合中 比较 元数据模型是否已经存在
     * @param mm 被比较的元数据模型
     * @return 若存在返回true，否则返回false
     */
    public boolean compareMetadataModel(MetadataModel mm) {
        
        return false;
    }

    /**
     * 加入新的元数据模型，注意这个方法不判断元数据信息是否意义重复。
     * 这个方法做两个事情：
     * 1-向mdModelMap中加入元数据模型
     * 2-向数据库中插入相应的记录
     * @param mm 新元数据模型
     */
    public void addMetedataModel(MetadataModel mm) {
        
    }

    /**
     * 更改元数据模型，个方法做两个事情：
     * 1-更改mdModelMap中的元数据模型
     * 2-更改数据库中相应的记录
     * 具体怎样修改，还需要不断完善，应该和数据库的修改类似，目前规定：
     * 1-元数据ownerId/mdMId不能修改
     * 2-元数据列数量不能修改，数据类型不能修改，id不能修改，只能修改titleName
     * @param mm 需修改的元数据模式
     */
    public void updateMetedataModel(MetadataModel mm) {
        
    }
}

class loadDataThread implements Runnable{
    public loadDataThread() {
        
    }
    
    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }
    
}