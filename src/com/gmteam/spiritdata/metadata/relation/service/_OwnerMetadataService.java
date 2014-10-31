package com.gmteam.spiritdata.metadata.relation.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import com.gmteam.spiritdata.SDConstants;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataColSemanteme;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataColumn;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;
import com.gmteam.spiritdata.metadata.relation.pojo._OwnerMetadata;
import com.gmteam.spiritdata.util.SequenceUUID;

/**
 * 对[所有者“关系型元数据”的操作。
 * 通过线程方式进行加载，并放入缓存或Session。
 * 
 * @author wh
 */
@Component
public class _OwnerMetadataService {
    @Resource
    private MdBasisService mdBasisService;
    public MdBasisService getMdBasisService() {
        return mdBasisService;
    }

    /**
     * 构造所有者元数据信息，并存入Session。构造过程会启动另一个线程处理
     * @param ownerId 所有者Id,UserId或SessionId
     * @param ownerType 所有者类型
     * @param session session
     */
    public void loadData2Session(String ownerId, int ownerType, HttpSession session) {
        _OwnerMetadata _om = new _OwnerMetadata(ownerId, ownerType);
        session.setAttribute(SDConstants.SESSION_OWNER_RMDUNIT, _om);
        //启动加载线程
        loadDataThread lm = new loadDataThread(session, this);
        Thread t = new Thread(lm);
        t.start();
    }

    /**
     * 新增模式
     * @param mm 元数据模式
     * @param session
     */
    protected void addMetadataModelModel(MetadataModel mm, HttpSession session) throws Exception {
        _OwnerMetadata _om = (_OwnerMetadata)session.getAttribute(SDConstants.SESSION_OWNER_RMDUNIT);
        //新增数据库-主表
        mdBasisService.addMetadataModel(mm);
        //新增数据库-字表
        List<MetadataColumn> mcList = mm.getColumnList();
        if (mcList!=null&&mcList.size()>0) {
            for (MetadataColumn mc: mcList) {
                if (mc.getId()==null||mc.getId().equals("")) mc.setId(SequenceUUID.getUUIDSubSegment(4));
                mdBasisService.addMetadataColumn(mc);
            }
        }

        //新增缓存
        _om.mdModelMap.put(mm.getId(), mm);
        if (_om.mmList==null) _om.mmList = new ArrayList<MetadataModel>(); 
        _om.mmList.add(mm);
        if (mcList!=null&&mcList.size()>0) {
            if (_om.mcList==null) _om.mcList = new ArrayList<MetadataColumn>(); 
            for (MetadataColumn mc: mcList) {
                _om.mcList.add(mc);
            }
        }
    }
}

class loadDataThread implements Runnable {
    private HttpSession session;
    private _OwnerMetadataService caller;

    public loadDataThread(HttpSession session, _OwnerMetadataService caller) {
        this.caller = caller;
        this.session = session;
    }

    @Override
    public void run() {
        _OwnerMetadata _om = (_OwnerMetadata)session.getAttribute(SDConstants.SESSION_OWNER_RMDUNIT);
        String ownerId = _om.getOnwerId();
        int ownerType = _om.getOnwerType();
        _om.mdModelMap = new ConcurrentHashMap<String, MetadataModel>();
        MdBasisService mdBasisService = caller.getMdBasisService();

        try {
            //过滤元数据模式，把可疑数据删除
            List<MetadataModel> mmList = mdBasisService.getMdMListByOwnerId(ownerId);
            if (mmList!=null&&mmList.size()>0) {
                for (int i=mmList.size()-1; i>=0; i--) {
                    MetadataModel mm = mmList.get(i);
                    if (mm.getOwnerType()!=ownerType) {
                        mmList.remove(i);
                        continue;
                    }
                }
            }
            List<MetadataColumn> mcList = null;
            List<MetadataColSemanteme> mcsList = null;
            if (mmList!=null&&mmList.size()>0) {
                //准备元数据模式信息
                Map<String, MetadataModel> flagMap = new HashMap<String, MetadataModel>();
                for (MetadataModel mm: mmList) {
                    flagMap.put(mm.getId(), mm);
                }
                //准备语义信息
                mcsList = mdBasisService.getMdColSemantemeListByOwnerId(ownerId);
                Map<String, MetadataColSemanteme> _flagMap = new HashMap<String, MetadataColSemanteme>();
                if (mcsList!=null&&mcsList.size()>0) {
                    for (MetadataColSemanteme mcs: mcsList) {
                        _flagMap.put(mcs.getColId(), mcs);
                    }
                }
                //根据元数据列描述信息构造结构
                boolean reStructMcs = false;//是否需要重构语义列表
                mcList = mdBasisService.getMdColListByOwnerId(ownerId);
                if (mcList!=null&&mcList.size()>0) {
                    for (int i=mcList.size()-1; i>=0; i--) {
                        MetadataColumn mc = mcList.get(i);
                        if (flagMap.get(mc.getMdMId())!=null) {
                            if (_flagMap.get(mc.getId())!=null) {
                                mc.setColSem(_flagMap.get(mc.getId()));
                            }
                            (flagMap.get(mc.getMdMId())).addColumn(mc);
                        } else {
                            mcList.remove(i);
                            reStructMcs = _flagMap.remove(mc.getId())!=null;
                        }
                    }
                    //把元数据模式中没有列信息的元素去掉，并组装返回结构mdModelMap
                    mmList.clear();
                    for (String mmId: flagMap.keySet()) {
                        MetadataModel mm = flagMap.get(mmId);
                        if (mm.getColumnList()!=null&&mm.getColumnList().size()>0) {
                            mmList.add(mm);
                            _om.mdModelMap.put(mm.getId(), mm);
                        }
                    }
                    //重构元数据列语义列表
                    if (reStructMcs) {
                        mcsList.clear();
                        for (String mcsId: _flagMap.keySet()) {
                            mcsList.add(_flagMap.get(mcsId));
                        }
                    }
                } else {
                    mmList = null;
                    _om.mdModelMap.clear();
                }
            }
            _om.mmList = mmList;
            _om.mcList = mcList;
            _om.mcsList = mcsList;

            _om.setLoadSuccess();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
