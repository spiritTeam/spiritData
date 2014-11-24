package com.gmteam.spiritdata.metadata.relation.service;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import com.gmteam.framework.FConstants;
import com.gmteam.framework.UGA.UgaUser;
import com.gmteam.spiritdata.SDConstants;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;
import com.gmteam.spiritdata.metadata.relation.pojo.TableMapRel;
import com.gmteam.spiritdata.metadata.relation.pojo._OwnerMetadata;
import com.gmteam.spiritdata.util.SequenceUUID;

/**
 * 元数据处理服务，关于所有者元数据的操作都封装在这里。
 * 所有者信息存储在Session中，本服务针对Session中的元数据进行处理。
 * 包括Session和持久化存储同步的相关操作。
 * 基本元数据信息存放在_OwnerMetadata中，这个类中存放了登陆用户的基本元数据信息。
 * @author wh
 */
@Component
public class MetadataSessionService {
    private HttpSession session;
    public void setSession(HttpSession session) {
        this.session = session;
    }

    @Resource
    private _OwnerMetadataService _ownerMdService;
    @Resource
    private MdEntityTableService mdTableOrgService;

    /**
     * 为导入数据存储元数据信息，并生成相应的数据表
     * @param mm 元数据信息，从Import文件中分析出的mm信息，此信息不必包含积累表名称
     * @return TableMapOrg数据的第一个元素是积累表，第二个元素是临时表
     */
    public TableMapRel[] storeMdModel4Import(MetadataModel mm) throws Exception {
        if (session==null) throw new NullPointerException("session为空，请通过[setSession]方法设置！");
        TableMapRel accumulationTable=null, tempTable=null;
        MetadataModel _existMm = getExistMetadataModel(mm);
        if (_existMm==null) {
            //生成积累表名称
            String mdMId = mm.getId();
            if (mdMId==null||mdMId.equals("")) {
                mdMId = SequenceUUID.getPureUUID();
                mm.setId(mdMId);
            }
            String accumulationTabName = "tab_"+mdMId;
            //注册积累表
            accumulationTable = mdTableOrgService.registTabOrgMap(accumulationTabName, mm, 1);
            //添加模型
            _ownerMdService.addMetadataModel(mm, session);
        } else {
            accumulationTable = mdTableOrgService.getAccumulationTableMapOrg(_existMm.getId());
            mm=_existMm;
        }
        //创建临时表
        String tempTabName = "tabt_"+SequenceUUID.getPureUUID();
        tempTable = mdTableOrgService.registTabOrgMap(tempTabName, mm, 2);
        //获得积累表
        TableMapRel[] ret = new TableMapRel[2];
        ret[0] = accumulationTable;
        ret[1] = tempTable;
        return ret;
    }

    /**
     * 在所有者创建的全部元数据模型集合中 比较 元数据模型是否已经存在
     * @param mm 被比较的元数据模型
     * @return 若存在返回true，否则返回false
     */
    private MetadataModel getExistMetadataModel(MetadataModel mm) throws Exception {
        String ownerId = mm.getOwnerId();
        int ownerType = mm.getOwnerType();
        if (ownerId==null||ownerId.equals("")) {
            UgaUser user = (UgaUser)session.getAttribute(FConstants.SESSION_USER);
            if (user!=null) {
                ownerId = user.getUserId();
                ownerType = 1;
            } else {
                ownerId = this.session.getId();
                ownerType = 2;
            }
            mm.setOwnerId(ownerId);
            mm.setOwnerType(ownerType);
        }
        _OwnerMetadata _om = (_OwnerMetadata)this.session.getAttribute(SDConstants.SESSION_OWNER_RMDUNIT);
        if (_om==null) {
            _ownerMdService.loadData2Session(ownerId, ownerType, this.session);
            _om = (_OwnerMetadata)this.session.getAttribute(SDConstants.SESSION_OWNER_RMDUNIT);
        }
        if (_om==null) new Exception("从session中不能取得所有者数据模型，未知错误！");
        while (!_om.isLoadSuccess()) {
            Thread.sleep(100);
        }
        //比较是否存储在
        Map<String, MetadataModel> mmMap = _om.mdModelMap;
        
        for (String id: mmMap.keySet()) {
            if ((mmMap.get(id).isSame(mm, 1)).get("type").equals("1")) return mmMap.get(id);
        }
        return null;
    }
}