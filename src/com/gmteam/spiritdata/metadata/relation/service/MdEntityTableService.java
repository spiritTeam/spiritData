package com.gmteam.spiritdata.metadata.relation.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.gmteam.framework.core.dao.mybatis.MybatisDAO;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataColumn;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;
import com.gmteam.spiritdata.metadata.relation.pojo.TableMapOrg;
import com.gmteam.spiritdata.util.SequenceUUID;

/**
 * 元数据实体表相关功能服务
 * @author wh
 */
@Component
public class MdEntityTableService {
    @Resource(name="defaultDAO")
    private MybatisDAO<TableMapOrg> tmoDao;

    @PostConstruct
    public void initParam() {
        tmoDao.setNamespace("mdTableMapOrg");
    }
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
        if (mm.getColumnList()==null||mm.getColumnList().size()==0) throw new IllegalArgumentException("元数据模式没有任何列描述信息！");
        //创建相应的表
        Map<String, String> btm = new HashMap<String, String>(); //build table map
        btm.put("tableName", tableName);
        //列名称
        String columnStr = "";
        List<MetadataColumn> pks = new ArrayList<MetadataColumn>();
        int _index = 0;
        for (MetadataColumn mc : mm.getColumnList()) {
            columnStr += ",";
            if (mc.getColumnName()==null||mc.getColumnName().equals("")) mc.setColumnName("col_"+(_index++));
            if (mc.getColumnType().equalsIgnoreCase("String")) {
                columnStr += mc.getColumnName()+" varchar(200)";
            } else {
                columnStr += mc.getColumnName()+" "+mc.getColumnType();
            }
            if (mc.getTitleName()!=null&&!mc.getTitleName().equals("")) columnStr += " COMMENT '"+mc.getTitleName()+"'";
            if (mc.getIsPk()==1) pks.add(mc);
        }
        columnStr = columnStr.substring(1);
        if (pks.size()>0) {
            columnStr += ", PRIMARY KEY (";
            for (MetadataColumn mc : pks) {
                columnStr += mc.getColumnName()+",";
            }
            columnStr = columnStr.substring(0, columnStr.length()-1)+") USING BTREE";
        }
        btm.put("columnStr", columnStr);
        btm.put("tableComment", (tableType==2?"temp::":"")+mm.getDescn());
        tmoDao.excute("createTable", btm);
        //修改mm中的积累表名称
        if (tableType==1) mm.setTableName(tableName);
        //写入注册信息
        TableMapOrg insertTmo = new TableMapOrg();
        String newkey = SequenceUUID.getUUIDSubSegment(4);
        insertTmo.setId(newkey);
        insertTmo.setOwnerId(mm.getOwnerId());
        insertTmo.setMdMId(mm.getId());
        insertTmo.setTableName(tableName);
        insertTmo.setTableType(tableType);
        tmoDao.insert(insertTmo);
        insertTmo.setcTime(new Timestamp(new Date().getTime()));
        return insertTmo;
    }

    /**
     * 得到积累表信息
     * @param mdMId 元数据模式Id
     * @param ownerId 元数据所有者
     * @return 积累表信息
     * @throws Exception
     */
    public TableMapOrg getAccumulationTableMapOrg(String mdMId) throws Exception {
        if (mdMId==null||mdMId.equals("")) throw new IllegalArgumentException("元数据模式Id不能为空！");
        TableMapOrg paramTmo = new TableMapOrg();
        paramTmo.setMdMId(mdMId);
        paramTmo.setTableType(1);
        return tmoDao.getInfoObject(paramTmo);
    }
}