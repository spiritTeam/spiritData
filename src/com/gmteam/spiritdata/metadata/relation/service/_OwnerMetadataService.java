package com.gmteam.spiritdata.metadata.relation.service;

import java.util.ArrayList;
import java.util.List;
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
 * 对[所有者“关系型元数据”]的操作。
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
    public void setMdBasisService(MdBasisService mdBasisService) {
        this.mdBasisService = mdBasisService;
    }

    /**
     * 构造所有者元数据信息，并存入Session。构造过程会启动另一个线程处理
     * @param ownerId 所有者Id,UserId或SessionId
     * @param ownerType 所有者类型
     * @param session session
     */
    public synchronized void loadData2Session(String ownerId, int ownerType, HttpSession session) {
        _OwnerMetadata _om = new _OwnerMetadata(ownerId, ownerType);
        session.setAttribute(SDConstants.SESSION_OWNERRMDUNIT, _om);
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
    protected void addMetadataModelModel(MetadataModel mm, HttpSession session) {
        _OwnerMetadata _om = (_OwnerMetadata)session.getAttribute(SDConstants.SESSION_OWNERRMDUNIT);
        try {
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
            _om.mmList.add(mm);
            if (mcList!=null&&mcList.size()>0) {
                for (MetadataColumn mc: mcList) {
                    _om.mcList.add(mc);
                }
            }
        } catch(Exception e) {
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
        _OwnerMetadata _om = (_OwnerMetadata)session.getAttribute(SDConstants.SESSION_OWNERRMDUNIT);
        String ownerId = _om.getOnwerId();
        int ownerType = _om.getOnwerType();
        _om.mdModelMap = new ConcurrentHashMap<String, MetadataModel>();
        MdBasisService mdBasisService = caller.getMdBasisService();
        try {
            List<MetadataModel> mmList = mdBasisService.getMdMListByOwnerId(ownerId);
            List<MetadataColumn> mcList = null;
            List<MetadataColSemanteme> mcsList = null;
            
            if (mmList!=null&&mmList.size()>0) {
                mcList = mdBasisService.getMdColListByOwnerId(ownerId);
                mcsList = mdBasisService.getMdColSemantemeListByOwnerId(ownerId);
                for (int i=mmList.size()-1; i>=0; i--) {
                    MetadataModel mm = mmList.get(i);
                    if (mm.getOwnerType()!=ownerType) {
                        mmList.remove(i);
                        continue;
                    }
                    if (mcList!=null&&mcList.size()>0) {
                        for (MetadataColumn mdc: mcList) {
                            if (mdc.getMdMId().equals(mm.getId())) {
                                mm.addColumn(mdc);
                                if (mcsList!=null&&mcsList.size()>0) {
                                    for (MetadataColSemanteme mcs: mcsList) {
                                        if (mcs.getColId().equals(mdc.getId())) mdc.setColSem(mcs);
                                    }
                                }
                            }
                        }
                    }
                    _om.mdModelMap.put(mm.getId(), mm);
                }
                mcList = new ArrayList<MetadataColumn>();
                mcsList = new ArrayList<MetadataColSemanteme>();
                for (String mdMId: _om.mdModelMap.keySet()) {
                    List<MetadataColumn> _mcl = ((MetadataModel)_om.mdModelMap.get(mdMId)).getColumnList();
                    if (_mcl!=null&&_mcl.size()>0) {
                        mcList.addAll(((MetadataModel)_om.mdModelMap.get(mdMId)).getColumnList());
                        for (MetadataColumn mc: mcList) {
                            if (mc.getColSem()!=null)  mcsList.add(mc.getColSem());
                        }
                    }
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