package com.gmteam.spiritdata.dictdata.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.gmteam.framework.core.dao.mybatis.MybatisDAO;
import com.gmteam.framework.core.model.tree.TreeNode;
import com.gmteam.spiritdata.dictdata.pojo.DictDetail;
import com.gmteam.spiritdata.dictdata.pojo.DictMaster;

/**
 * 字典信息服务类，主要功能是与关系型数据库交互，类似DAO。
 * 与Dao不同的是，此服务中的方法是按照基础业务逻辑来组合的，即对DAO功能的初步组合。
 * 主要涉及量个数据库表：PLAT_DICTM/PLAT_DICTD
 * 
 * @author wh
 */

@Component
public class DictService {
    @Resource(name="defaultDAO")
    private MybatisDAO<DictMaster> dictMDao;
    @Resource(name="defaultDAO")
    private MybatisDAO<DictDetail> dictDDao;

    @PostConstruct
    public void initParam() {
        dictMDao.setNamespace("dictMaster");
        dictDDao.setNamespace("dictDetail");
    }

    //以下为字典组相关操作
    /**
     * 获得字典组列表
     * @param paramDm 字典组对象，此对象中的值将是条件，这些条件是And的关系
     * @return 字典组列表
     * @throws Exception
     */
    public List<DictMaster> getDictMList(DictMaster paramDm) throws Exception {
        return dictMDao.queryForList(paramDm.toHashMapAsBean());
    }

    /**
     * 根据所有者Id获得字典组列表
     * @param ownerId 所有者Id
     * @return 字典组列表
     * @throws Exception
     */
    public List<DictMaster> getDictMListByOwnerId(String ownerId) throws Exception {
        DictMaster paramDm = new DictMaster();
        paramDm.setOwnerId(ownerId);
        return this.getDictMList(paramDm);
    }

    /**
     * 新增字典组信息
     * @param dm 字典组信息
     * @throws Exception
     */
    public void addDictMaster(DictMaster dm) throws Exception {
        dictMDao.insert(dm);
    }

    //以下为字典项相关操作
    /**
     * 获得字典项列表
     * @param paramDd 字典项对象，此对象中的值将是条件，这些条件是And的关系
     * @return 字典项列表
     * @throws Exception
     */
    public List<DictDetail> getDictDList(DictDetail paramDd) throws Exception {
        return dictDDao.queryForList(paramDd.toHashMapAsBean());
    }

    /**
     * 根据所有者Id获得字典项列表
     * @param ownerId 所有者Id
     * @return 字典项列表
     * @throws Exception
     */
    public List<DictDetail> getDictDListByOwnerId(String ownerId) throws Exception {
        return dictDDao.queryForList("getListByOwnerId", ownerId);
    }

    /**
     * 新增字典项
     * @param dd 字典项信息
     * @throws Exception
     */
    public void addDictDetail(DictDetail dd) throws Exception {
        dictDDao.insert(dd);
    }

    /**
     * 新增字典项
     * @param dd 字典项信息
     * @throws Exception
     */
    public void addDictDetail(TreeNode<DictDetail> dictTree) throws Exception {
        if (dictTree==null) return;
        insertByTree(dictTree);
    }

    private void insertByTree(TreeNode<DictDetail> dictTree) throws Exception {
        if (dictTree==null) return;
        DictDetail dd = dictTree.getTnEntity();
        dictDDao.insert(dd);
        if (dictTree.getChildren()!=null&&dictTree.getChildren().size()>0) {
            for (TreeNode<DictDetail> child: dictTree.getChildren()) {
                insertByTree(child);
            }
        }
    }
}