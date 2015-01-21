package com.spiritdata.dataanal.dictionary.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.dataanal.dictionary.pojo.DictDetail;
import com.spiritdata.dataanal.dictionary.pojo.DictMaster;
import com.spiritdata.dataanal.exceptionC.Dtal0301CException;

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
        dictMDao.setNamespace("dMaster");
        dictDDao.setNamespace("dDetail");
    }

    //以下为字典组相关操作
    /**
     * 获得字典组列表
     * @param paramDm 字典组对象，此对象中的值将是条件，这些条件是And的关系
     * @return 字典组列表
     * @throws Exception
     */
    public List<DictMaster> getDictMList(DictMaster paramDm) {
        try {
            return dictMDao.queryForList(paramDm.toHashMapAsBean());
        } catch(Exception e) {
            throw new Dtal0301CException(e);
        }
    }

    /**
     * 根据所有者Id获得字典组列表
     * @param ownerId 所有者Id
     * @return 字典组列表
     * @throws Exception
     */
    public List<DictMaster> getDictMListByOwnerId(String ownerId) {
        DictMaster paramDm = new DictMaster();
        paramDm.setOwnerId(ownerId);
        return this.getDictMList(paramDm);
    }

    /**
     * 新增字典组信息
     * @param dm 字典组信息
     * @throws Exception
     */
    public void addDictMaster(DictMaster dm) {
        try {
            dictMDao.insert(dm);
        } catch(Exception e) {
            throw new Dtal0301CException(e);
        }
    }

    //以下为字典项相关操作
    /**
     * 获得字典项列表
     * @param paramDd 字典项对象，此对象中的值将是条件，这些条件是And的关系
     * @return 字典项列表
     * @throws Exception
     */
    public List<DictDetail> getDictDList(DictDetail paramDd) {
        try {
            return dictDDao.queryForList(paramDd.toHashMapAsBean());
        } catch(Exception e) {
            throw new Dtal0301CException(e);
        }
    }

    /**
     * 根据所有者Id获得字典项列表
     * @param ownerId 所有者Id
     * @return 字典项列表
     * @throws Exception
     */
    public List<DictDetail> getDictDListByOwnerId(String ownerId) {
        try {
            Map<String, String> param = new HashMap<String, String>();
            param.put("ownerId", ownerId);
            return dictDDao.queryForList("getListByOwnerId", param);
        } catch(Exception e) {
            throw new Dtal0301CException(e);
        }
    }

    /**
     * 新增字典项
     * @param dd 字典项信息
     * @throws Exception
     */
    public void addDictDetail(DictDetail dd) {
        try {
            dictDDao.insert(dd);
        } catch(Exception e) {
            throw new Dtal0301CException(e);
        }
    }
}