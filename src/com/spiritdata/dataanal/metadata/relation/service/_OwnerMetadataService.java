package com.spiritdata.dataanal.metadata.relation.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.UGA.UgaUser;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.core.web.SessionLoader;

import com.spiritdata.dataanal.SDConstants;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataColSemanteme;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataColumn;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel;
import com.spiritdata.dataanal.metadata.relation.pojo._OwnerMetadata;

/**
 * 对[所有者“关系型元数据”的操作。
 * 通过线程方式进行加载，并放入缓存或Session。
 * 
 * @author wh
 */
@Service
public class _OwnerMetadataService implements SessionLoader {
    @Resource
    private MdBasisService mdBasisService;
    public MdBasisService getMdBasisService() {
        return mdBasisService;
    }

    /**
     * 构造所有者元数据信息，并存入Session。构造过程会启动另一个线程处理\
     * @param session session 从这个session中获得ownerType和ownerId
     */
    @Override
    public void loader(HttpSession session) throws Exception {
        String ownerId = session.getId();
        int ownerType = 2;
        UgaUser user = (UgaUser)session.getAttribute(FConstants.SESSION_USER);
        if (user!=null) {
            ownerId = user.getUserId();
            ownerType = 1;
        }
        loadData2Session(ownerId, ownerType, session);
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
        Thread_LoadData lm = new Thread_LoadData(session, this);
        Thread t = new Thread(lm);
        t.start();
    }

    /**
     * 新增模式
     * @param mm 元数据模式
     * @param session
     */
    public void addMetadataModel(MetadataModel mm, HttpSession session) throws Exception {
        _OwnerMetadata _om = (_OwnerMetadata)session.getAttribute(SDConstants.SESSION_OWNER_RMDUNIT);
        //新增数据库-主表
        mdBasisService.addMetadataModel(mm);
        //新增数据库-子表
        List<MetadataColumn> mcList = mm.getColumnList();
        if (mcList!=null&&mcList.size()>0) {
            for (MetadataColumn mc: mcList) {
                if (mc.getId()==null||mc.getId().equals("")) mc.setId(SequenceUUID.getPureUUID());
                mdBasisService.addMetadataColumn(mc);
            }
        }

        //新增缓存
        _om.mdModelMap.put(mm.getId(), mm);
    }
}

class Thread_LoadData implements Runnable {
    private HttpSession session;
    private _OwnerMetadataService caller;

    public Thread_LoadData(HttpSession session, _OwnerMetadataService caller) {
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
            List<MetadataModel> mmList = mdBasisService.getMdMListByOwnerId(ownerId);
            Map<String, MetadataModel> flagMap = null;
            //过滤元数据模式，把可疑数据删除, 并准备元数据模式信息
            if (mmList!=null&&mmList.size()>0) {
                flagMap = new HashMap<String, MetadataModel>();
                for (int i=mmList.size()-1; i>=0; i--) {
                    MetadataModel mm = mmList.get(i);
                    if (mm.getOwnerType()!=ownerType) {
                        mmList.remove(i);
                    } else {
                        flagMap.put(mm.getId(), mm);
                    }
                }
            }

            List<MetadataColumn> mcList = null;
            List<MetadataColSemanteme> mcsList = null;
            if (mmList!=null&&mmList.size()>0) {//这也保证了flagMap有内容
                //准备语义信息
                mcsList = mdBasisService.getMdColSemantemeListByOwnerId(ownerId);
                Map<String, List<MetadataColSemanteme>> _flagMap = null;
                if (mcsList!=null&&mcsList.size()>0) {
                    _flagMap = new HashMap<String, List<MetadataColSemanteme>>();
                    for (MetadataColSemanteme mcs: mcsList) {
                        List<MetadataColSemanteme> _l = _flagMap.get(mcs.getColId());
                        if (_l==null) {
                            _l = new ArrayList<MetadataColSemanteme>();
                            _flagMap.put(mcs.getColId(), _l);
                        }
                        _l.add(mcs);
                    }
                }
                //根据元数据列描述信息构造结构
                boolean reStructMcs = false;//是否需要重构语义列表
                mcList = mdBasisService.getMdColListByOwnerId(ownerId);
                if (mcList!=null&&mcList.size()>0) {
                    for (int i=mcList.size()-1; i>=0; i--) {
                        MetadataColumn mc = mcList.get(i);
                        if (flagMap.get(mc.getMdMId())!=null) {
                            if (_flagMap!=null&&_flagMap.get(mc.getId())!=null) {
                                mc.setColSemList(_flagMap.get(mc.getId()));
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
                            mcsList.addAll(_flagMap.get(mcsId));
                        }
                    }
                } else {
                    mmList = null;
                    _om.mdModelMap.clear();
                }
            }
            _om.setLoadSuccess();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}