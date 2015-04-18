package com.spiritdata.dataanal.metadata.relation.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.dataanal.metadata.relation.pojo.ImpTableMapRel;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataColumn;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataTableMapRel;

/**
 * 元数据实体表关联、导入文件和实体表关联，功能服务
 * @author wh
 */
public class TableMapService {
    @Resource(name="defaultDAO")
    private MybatisDAO<MetadataTableMapRel> mtmrDao;
    @Resource(name="defaultDAO")
    private MybatisDAO<ImpTableMapRel> itmrDao;

    @PostConstruct
    public void initParam() {
        mtmrDao.setNamespace("mdTableMapRel");
        itmrDao.setNamespace("impTableMapRel");
    }

    //导入文件实体表关联
    public void bindImpTabMap(ImpTableMapRel itmr) {
        if (StringUtils.isNullOrEmptyOrSpace(itmr.getId())) itmr.setId(SequenceUUID.getPureUUID());
        itmrDao.insert(itmr);
    }

    //元数据实体表关联
    /**
     * 注册对应关系，并创建相应的表。
     * 注意此方法会改变参数mm中积累表的名称
     * @param tableName 实体表名称
     * @param mm 所根据的元数据信息
     * @param tableType 表类型
     * @return 映射关系表信息
     * @throws Exception
     */
    public MetadataTableMapRel registTabOrgMap(String tableName, MetadataModel mm, int tableType) {
        if (StringUtils.isNullOrEmptyOrSpace(tableName)) throw new IllegalArgumentException("实体表名称不能为空！");
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
            if (StringUtils.isNullOrEmptyOrSpace(mc.getColumnName())) mc.setColumnName("col_"+(_index++));
            //MySQL的varchar主键只支持不超过768个字节，或者 768/2=384个双字节，或者 768/3=256个三字节的字段。 而 GBK是双字节的，UTF-8是三字节的
            if (mc.getColumnType().equalsIgnoreCase("String")) columnStr += mc.getColumnName()+" varchar(255)";
            else if(mc.getColumnType().equalsIgnoreCase("Long"))columnStr += mc.getColumnName()+" BIGINT"; //将LONG转换成MYSQL的BIGINT型
            else columnStr += mc.getColumnName()+" "+mc.getColumnType();
            if (!StringUtils.isNullOrEmptyOrSpace(mc.getTitleName())) columnStr += " COMMENT '"+mc.getTitleName()+"'";
            if (mc.isPk()) pks.add(mc);
        }
        columnStr = columnStr.substring(1);
        if (pks.size()>0&&tableType==1) { //只有积累表创建主键
            columnStr += ", PRIMARY KEY (";
            for (MetadataColumn mc : pks) {
                columnStr += mc.getColumnName()+",";
            }
            columnStr = columnStr.substring(0, columnStr.length()-1)+") USING BTREE";
        }
        //临时表只创建确认主键
        String _tempS = "";
        if (tableType==2&&pks.size()>0) {
            for (MetadataColumn mc : pks) {
                if (mc.isCertainPk()) _tempS += ","+mc.getColumnName();
            }
            if (_tempS.trim().length()>0) _tempS.substring(1);
            if (_tempS.trim().length()>0) columnStr += ", PRIMARY KEY ("+_tempS+") USING BTREE";
        }
        btm.put("columnStr", columnStr);
        if (!StringUtils.isNullOrEmptyOrSpace(mm.getDesc())) btm.put("tableComment", (tableType==2?"temp::":"")+mm.getDesc());

        mtmrDao.excute("createTable", btm);
        //修改mm中的积累表名称
        if (tableType==1) mm.setTableName(tableName);
        //写入注册信息
        MetadataTableMapRel insertTmo = new MetadataTableMapRel();
        String newkey = SequenceUUID.getPureUUID();
        insertTmo.setId(newkey);
        insertTmo.setOwnerType(mm.getOwnerType());
        insertTmo.setOwnerId(mm.getOwnerId());
        insertTmo.setMdMId(mm.getId());
        insertTmo.setTableName(tableName);
        insertTmo.setTableType(tableType);
        mtmrDao.insert(insertTmo);
        insertTmo.setCTime(new Timestamp(new Date().getTime()));
        return insertTmo;
    }

    /**
     * 得到积累表信息
     * @param mdMId 元数据模式Id
     * @param ownerId 元数据所有者
     * @return 积累表映射信息信息
     * @throws Exception
     */
    public MetadataTableMapRel getAccumulationTableMapOrg(String mdMId) {
        if (StringUtils.isNullOrEmptyOrSpace(mdMId)) throw new IllegalArgumentException("元数据模式Id不能为空！");
        MetadataTableMapRel paramTmo = new MetadataTableMapRel();
        paramTmo.setMdMId(mdMId);
        paramTmo.setTableType(1);
        return mtmrDao.getInfoObject(paramTmo);
    }

    /**
     * 根据模式Id和表名称获得表映射信息
     * @param mdMId 元数据模式Id
     * @param tableName 表名称
     * @return 表映射信息
     * @throws Exception
     */
    public MetadataTableMapRel getTableMapOrg(String mdMId, String tableName) {
        if (StringUtils.isNullOrEmptyOrSpace(tableName)) throw new IllegalArgumentException("表名称不能为空！");
        if (StringUtils.isNullOrEmptyOrSpace(mdMId)) throw new IllegalArgumentException("元数据模式Id不能为空！");

        MetadataTableMapRel paramTmo = new MetadataTableMapRel();
        paramTmo.setMdMId(mdMId);
        paramTmo.setTableName(tableName);
        return mtmrDao.getInfoObject(paramTmo);
    }

    /**
     * 获得列表
     * @param param
     * @return
     * @throws Exception
     */
    public List<MetadataTableMapRel> getList(Map<String, String> param) {
        return mtmrDao.queryForList(param);
    }
}