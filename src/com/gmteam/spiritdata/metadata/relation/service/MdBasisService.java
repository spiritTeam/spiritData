package com.gmteam.spiritdata.metadata.relation.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.gmteam.framework.core.dao.mybatis.MybatisDAO;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataColSemanteme;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataColumn;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;

/**
 * 元数据基本信息的服务类，主要功能是与关系型数据库交互，类似DAO。
 * 与Dao不同的是，此服务中的方法是按照基础业务逻辑来组合的，即对DAO功能的初步组合。
 * 主要涉及三个数据库表：SA_MD_TABMODULE/SA_MD_COLUMN/SA_MD_COLSEMANTEME，还涉及SA_MD_TABELMAP_ORG
 * @author wh
 */

@Component
public class MdBasisService {
    @Resource(name="defaultDAO")
    private MybatisDAO<MetadataModel> mmDao;
    @Resource(name="defaultDAO")
    private MybatisDAO<MetadataColumn> mcDao;
    @Resource(name="defaultDAO")
    private MybatisDAO<MetadataColSemanteme> mcsDao;

    @PostConstruct
    public void initParam() {
        mmDao.setNamespace("metadataModel");
        mcDao.setNamespace("metadataColumn");
        mcsDao.setNamespace("metadataColSemanteme");
    }

    //以下为元数据模式相关操作
    /**
     * 获得元数据模式列表
     * @param paramMm 元数据模式对象，此对象中的值将是条件，这些条件是And的关系
     * @return 元数据模式列表
     * @throws Exception
     */
    public List<MetadataModel> getMdMList(MetadataModel paramMm) throws Exception {
        return mmDao.queryForList(paramMm.toHashMapAsBean());
    }

    /**
     * 根据所有者Id获得元数据模式列表
     * @param ownerId 所有者Id
     * @return 元数据模式列表
     * @throws Exception
     */
    public List<MetadataModel> getMdMListByOwnerId(String ownerId) throws Exception {
        MetadataModel paramMm = new MetadataModel();
        paramMm.setOwnerId(ownerId);
        return this.getMdMList(paramMm);
    }

    //以下为元数据列描述相关操作
    /**
     * 获得元数据列描述列表
     * @param paramMc 元数据列描述对象，此对象中的值将是条件，这些条件是And的关系
     * @return 元数据列描述列表
     * @throws Exception
     */
    public List<MetadataColumn> getMdColList(MetadataColumn paramMc) throws Exception {
        return mcDao.queryForList(paramMc.toHashMapAsBean());
    }

    /**
     * 获得元数据列描述列表
     * @param ownerId 所有者Id
     * @return 元数据列描述列表
     * @throws Exception
     */
    public List<MetadataColumn> getMdColListByOwnerId(String ownerId) throws Exception {
        return mcDao.queryForList("getListByOwnerId", ownerId);
    }

    //以下为元数据列语义相关操作
    /**
     * 获得元数据列语义列表
     * @param paramMc 元数据列语义对象，此对象中的值将是条件，这些条件是And的关系
     * @return 元数据列语义列表
     * @throws Exception
     */
    public List<MetadataColSemanteme> getMdColSemantemeList(MetadataColSemanteme paramMc) throws Exception {
        return mcsDao.queryForList(paramMc.toHashMapAsBean());
    }

    /**
     * 获得元数据列语义列表
     * @param ownerId 所有者Id
     * @return 元数据列语义列表
     * @throws Exception
     */
    public List<MetadataColSemanteme> getMdColSemantemeListByOwnerId(String ownerId) throws Exception {
        return mcsDao.queryForList("getListByOwnerId", ownerId);
    }

    /**
     * 新增元数据模式
     * @param mm 元数据模式
     * @throws Exception
     */
    public void addMetadataModel(MetadataModel mm) throws Exception {
        mmDao.insert(mm);
    }

    /**
     * 新增元数据列描述
     * @param mm 元数据列描述
     * @throws Exception
     */
    public void addMetadataColumn(MetadataColumn mc) throws Exception {
        mcDao.insert(mc);
    }
}