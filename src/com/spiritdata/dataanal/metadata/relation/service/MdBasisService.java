package com.spiritdata.dataanal.metadata.relation.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

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
     * 根据元数据Id，得到元数据信息，不包括语义信息
     * @param id 元数据信息
     * @return 该元数据信息
     */
    public MetadataModel getMetadataMode(String id) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("id", id);
        MetadataModel ret = mmDao.getInfoObject(param);
        if (ret==null) return ret;

        param.clear();
        param.put("mdMId", id);
        //查询列
        List<MetadataColumn> clist = mcDao.queryForList(param);
        if (clist==null||clist.size()==0) return null;
        for (MetadataColumn mc: clist) {
            ret.addColumn(mc);
        }
        return ret;
    }

    /**
     * 根据元数据Id，得到元数据信息，包括语义信息
     * @param id
     * @return 该元数据信息
     */
    public MetadataModel getMetadataModeWithColSemanteme(String id) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("id", id);
        MetadataModel ret = mmDao.getInfoObject(param);
        if (ret==null) return ret;

        param.clear();
        param.put("mdMId", id);
        param.put("orderByClause", "columnIndex");
        //查询列
        List<MetadataColumn> clist = mcDao.queryForList(param);
        if (clist==null||clist.size()==0) return null;
        for (MetadataColumn mc: clist) ret.addColumn(mc);

        //查询语义
        param.put("orderByClause", "cId");//按列排序
        List<MetadataColSemanteme> cslist = mcsDao.queryForList(param);
        if (cslist!=null&&cslist.size()>0) {
            String cId = "";
            MetadataColumn mc = null;
            for (MetadataColSemanteme mcs: cslist) {
                if (!mcs.getColId().equals(cId)) {
                    mc = ret.getColumnByColId(mcs.getColId());
                    cId = mcs.getColId();
                }
                if (mc!=null) mc.addColSem(mcs);
            }
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