package com.spiritdata.dataanal.metadata.relation.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataColSemanteme;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataColumn;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel;

/**
 * 元数据基本信息的服务类，主要功能是与关系型数据库交互，类似DAO。
 * 与Dao不同的是，此服务中的方法是按照基础业务逻辑来组合的，即对DAO功能的初步组合。
 * 主要涉及三个数据库表：SA_MD_TABMODULE/SA_MD_COLUMN/SA_MD_COLSEMANTEME，还涉及SA_MD_TABELMAP_ORG
 * 
 * @author wh
 */

@Service
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
     * 新增元数据模式
     * @param mm 元数据模式
     * @throws Exception
     */
    public void addMetadataModel(MetadataModel mm) {
        mmDao.insert(mm);
    }

    /**
     * 根据元数据Id，得到元数据信息
     * 注意：这个方法不能取到语义信息
     * @param id
     * @return 该元数据信息
     * @throws Exception 
     * 这个目前不全，没有包括语义信息，不能调用
     */
    public MetadataModel getMetadataMode(String id) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("id", id);
        MetadataModel ret = mmDao.getInfoObject(param);
        if (ret==null) return ret;

        param.clear();
        param.put("mdMId", id);
        List<MetadataColumn> clist = mcDao.queryForList(param);
        if (clist==null||clist.size()==0) return null;
        for (MetadataColumn mc: clist) {
            ret.addColumn(mc);
        }
        return ret;
    }

    /**
     * 获得元数据模式列表
     * @param paramMm 元数据模式对象，此对象中的值将是条件，这些条件是And的关系
     * @return 元数据模式列表
     * @throws Exception
     */
    public List<MetadataModel> getMdMList(MetadataModel paramMm) {
        return mmDao.queryForList(paramMm.toHashMapAsBean());
    }

    /**
     * 根据所有者Id获得元数据模式列表
     * @param ownerId 所有者Id
     * @return 元数据模式列表
     * @throws Exception
     */
    public List<MetadataModel> getMdMListByOwnerId(String ownerId) {
        MetadataModel paramMm = new MetadataModel();
        paramMm.setOwnerId(ownerId);
        return this.getMdMList(paramMm);
    }

    /**
     * 根据所有者Id获得元数据模式列表
     * @param ownerId 所有者Id
     * @return 元数据模式列表
     * @throws Exception
     */
    public List<MetadataModel> getMdMListByOwnerId4Session(String ownerId) {
        MetadataModel paramMm = new MetadataModel();
        paramMm.setOwnerId(ownerId);
        return mmDao.queryForList("getList4Session", paramMm.toHashMapAsBean());
    }

    /**
     * 根据所有者Id获得元数据标题列表
     * @param ownerId 所有者Id
     * @return 元数据标题列表
     * @throws Exception
     */
    public List<Map<String, Object>> getMdTitleListByOwnerId(String ownerId) {
        return mmDao.queryForListAutoTranform("getMdTitleList", ownerId);
    }

    /**
     * 修改元数据模式信息
     * @param paramMm 元数据信息
     * @return 修改的条数
     */
    public int updateMdM(MetadataModel paramMm) {
        return mmDao.update(paramMm);
    }

    //以下为元数据列描述相关操作
    /**
     * 新增元数据列描述
     * @param mm 元数据列描述
     * @throws Exception
     */
    public void addMetadataColumn(MetadataColumn mc) {
        mcDao.insert(mc);
    }

    /**
     * 获得元数据列描述列表
     * @param paramMc 元数据列描述对象，此对象中的值将是条件，这些条件是And的关系
     * @return 元数据列描述列表
     * @throws Exception
     */
    public List<MetadataColumn> getMdColList(MetadataColumn paramMc) {
        return mcDao.queryForList(paramMc.toHashMapAsBean());
    }

    /**
     * 根据所有者Id获得元数据列描述列表
     * @param ownerId 所有者Id
     * @return 元数据列描述列表
     * @throws Exception
     */
    public List<MetadataColumn> getMdColListByOwnerId(String ownerId) {
        return mcDao.queryForList("getListByOwnerId", ownerId);
    }

    //以下为元数据列语义相关操作
    /**
     * 新增元数据列语义描述
     * @param mcs 元数据列描述
     * @throws Exception
     */
    public void addMetadataColSemanteme(MetadataColSemanteme mcs) {
        mcsDao.insert(mcs);
    }
    /**
     * 获得元数据列语义列表
     * @param paramMc 元数据列语义对象，此对象中的值将是条件，这些条件是And的关系
     * @return 元数据列语义列表
     * @throws Exception
     */
    public List<MetadataColSemanteme> getMdColSemantemeList(MetadataColSemanteme paramMc) {
        return mcsDao.queryForList(paramMc.toHashMapAsBean());
    }

    /**
     * 根据所有者Id获得元数据列语义列表
     * @param ownerId 所有者Id
     * @return 元数据列语义列表
     * @throws Exception
     */
    public List<MetadataColSemanteme> getMdColSemantemeListByOwnerId(String ownerId) {
        return mcsDao.queryForList("getListByOwnerId", ownerId);
    }
}