package com.gmteam.spiritdata.metadata.relation.service;

import com.gmteam.spiritdata.metadata.relation.OwnerRmdUnit;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;
import com.gmteam.spiritdata.metadata.relation.pojo.TableMapOrg;

/**
 * 元数据处理服务，所有关于元数据的操作都封装在这里。
 * 基本元数据信息存放在OwnerRmdMemeryUnit中，这个类中存放了登陆用户的基本元数据信息。
 * @author wh
 */
public class MdDealService {
    private OwnerRmdUnit ownerRmdMemery;

    public OwnerRmdUnit getOwnerRmdMemery() {
        return ownerRmdMemery;
    }

    public void setOwnerRmdMemery(OwnerRmdUnit ownerRmdMemery) {
        this.ownerRmdMemery = ownerRmdMemery;
    }

    /**
     * 为导入数据存储元数据信息
     * @param mm 元数据信息
     * @return TableMapOrg数据的第一个元素是积累表，第二个元素是临时表
     */
    public TableMapOrg[] storeMdModel4Import(MetadataModel mm) {
        if (!existMetadataModel(mm)) {
            //添加模型
            //创建积累表
            //
        }
        //获得积累表
        //
        return null;
    }

    /**
     * 在所有者创建的全部元数据模型集合中 比较 元数据模型是否已经存在
     * @param mm 被比较的元数据模型
     * @return 若存在返回true，否则返回false
     */
    private boolean existMetadataModel(MetadataModel mm) {
        
        return false;
    }
}