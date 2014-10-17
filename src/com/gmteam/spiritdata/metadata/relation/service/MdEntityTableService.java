package com.gmteam.spiritdata.metadata.relation.service;

import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;
import com.gmteam.spiritdata.metadata.relation.pojo.TableMapOrg;

/**
 * 元数据实体表相关功能服务
 * @author wh
 */
public class MdEntityTableService {
    /**
     * 注册对应关系，并创建相应的表。
     * 注意此方法会改变参数mm中积累表的名称
     * @param tableName 实体表名称
     * @param mm 所根据的元数据信息
     * @param tableType 表类型
     * @return 映射关系表信息
     * @throws Exception
     */
    public TableMapOrg registTabOrgMap(String tableName, MetadataModel mm, int tableType)  throws Exception {
        if (tableName==null||tableName.equals("")) throw new IllegalArgumentException("实体表名称不能为空！");
        //创建相应的表
        //修改mm中的积累表名称
        if (tableType==1) mm.setTableName(tableName);
        //写入注册信息
        return null;
    }

    public TableMapOrg getAccumulationTableMapOrg(String mdMId, String ownerId) throws Exception {
        if (mdMId==null||mdMId.equals("")) throw new IllegalArgumentException("元数据模式Id不能为空！");
        if (ownerId==null||ownerId.equals("")) throw new IllegalArgumentException("所有者Id不能为空！");
        return null;
    }
}