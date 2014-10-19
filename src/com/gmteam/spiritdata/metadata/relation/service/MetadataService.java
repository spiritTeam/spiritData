package com.gmteam.spiritdata.metadata.relation.service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import com.gmteam.spiritdata.SDConstants;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;
import com.gmteam.spiritdata.metadata.relation.pojo.TableMapOrg;
import com.gmteam.spiritdata.metadata.relation.pojo._OwnerMetadata;
import com.gmteam.spiritdata.util.SequenceUUID;

/**
 * 元数据处理服务，所有关于元数据的操作都封装在这里。
 * 基本元数据信息存放在OwnerRmdMemeryUnit中，这个类中存放了登陆用户的基本元数据信息。
 * @author wh
 */
@Component
public class MetadataService {
    private HttpSession session;

    @Resource
    private _OwnerMetadataService _ownerMdService;
    public _OwnerMetadataService get_ownerMdService() {
        return _ownerMdService;
    }
    public void set_ownerMdService(_OwnerMetadataService _ownerMdService) {
        this._ownerMdService = _ownerMdService;
    }

    public void setSession(HttpSession session) {
    }

    /**
     * 为导入数据存储元数据信息，并生成相应的数据表
     * @param mm 元数据信息，从Import文件中分析出的mm信息，此信息不必包含积累表名称
     * @return TableMapOrg数据的第一个元素是积累表，第二个元素是临时表
     */
    public TableMapOrg[] storeMdModel4Import(MetadataModel mm) throws Exception {
        if (session==null) new NullPointerException("session为空，请通过[setSession]方法设置！");
        TableMapOrg accumulationTable=null, tempTable=null;
        if (!existMetadataModel(mm)) {
            //生成积累表名称
            String accumulationTabName = SequenceUUID.getUUIDSubSegment(4);
            //注册积累表
            accumulationTable = mdTableOrgService.registTabOrgMap(accumulationTabName, mm, 1);
            //添加模型
            _ownerMdService.addMetadataModelModel(mm, session);
        } else {
            accumulationTable = mdTableOrgService.getAccumulationTableMapOrg(mm.getMdMId(), mm.getOwnerId());
        }
        //创建临时表
        String tempTabName = SequenceUUID.getUUIDSubSegment(4);
        tempTable = mdTableOrgService.registTabOrgMap(tempTabName, mm, 2);
        //获得积累表
        TableMapOrg[] ret = new TableMapOrg[2];
        ret[0] = accumulationTable;
        ret[1] = tempTable;
        return ret;
    }

    /**
     * 在所有者创建的全部元数据模型集合中 比较 元数据模型是否已经存在
     * @param mm 被比较的元数据模型
     * @return 若存在返回true，否则返回false
     */
    private boolean existMetadataModel(MetadataModel mm) {
        _OwnerMetadata _om = (_OwnerMetadata)this.session.getAttribute(SDConstants.SESSION_OWNERRMDUNIT);
        if (_om==null) {
            String ownerId = mm.getOwnerId();
            int ownerType = mm.getOwnerType();
            if (ownerType==2) ownerId = this.session.getId();
            _ownerMdService.loadData2Session(ownerId, ownerType, this.session);
        }
        _om = (_OwnerMetadata)this.session.getAttribute(SDConstants.SESSION_OWNERRMDUNIT);
        if (_om==null) new Exception("从session中不能取得所有者数据模型，未知错误！");
        while (!_om.isLoadSuccess()) ; //等待执行，这个用线程间通信更好
        //比较是否存储在
        return false;
    }

    public void storeOwnerMetadate2Session() {
        
    }
    
    @Resource
    private MdEntityTableService mdTableOrgService;
    public MdEntityTableService getMdTableOrgService() {
        return mdTableOrgService;
    }
    public void setMdTableOrgService(MdEntityTableService mdTableOrgService) {
        this.mdTableOrgService = mdTableOrgService;
    }
}