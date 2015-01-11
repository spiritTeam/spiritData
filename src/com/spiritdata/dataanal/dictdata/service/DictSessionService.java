package com.spiritdata.dataanal.dictdata.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.spiritdata.dataanal.SDConstants;
import com.spiritdata.dataanal.dictdata.pojo.DictDetail;
import com.spiritdata.dataanal.dictdata.pojo.DictMaster;
import com.spiritdata.dataanal.dictdata.pojo.DictModel;
import com.spiritdata.dataanal.dictdata.pojo._OwnerDictionary;
import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.UGA.UgaUser;
import com.spiritdata.framework.core.model.tree.TreeNode;
import com.spiritdata.framework.core.web.SessionLoader;
import com.spiritdata.framework.util.TreeUtils;

/**
 * 所有者字典信息Session处理服务。
 * 字典信息存放在_OwnerDictionary中，这个类中存放了登录用户的字典数据信息。
 * 包括：<br/>
 * 1-Session和持久化存储同步的相关操作。<br/>
 * 2-通过线程方式进行加载，并放入缓存或Session。<br/>
 * @author wh
 */
@Service
public class DictSessionService implements SessionLoader {
    @Resource
    private DictService dictService;
    public DictService getDictService() {
        return dictService;
    }

    /**
     * 加载所有者字典信息到Session。构造过程会启动另一个线程处理
     * @param ownerId 所有者Id,UserId或SessionId
     * @param ownerType 所有者类型
     * @param session session
     */
    public void loadData2Session(String ownerId, int ownerType, HttpSession session) {
        _OwnerDictionary _od = new _OwnerDictionary(ownerId, ownerType);
        session.setAttribute(SDConstants.SESSION_OWNER_DICT, _od);
        //启动加载线程
        Thread_LoadData lm = new Thread_LoadData(session, this);
        Thread t = new Thread(lm);
        t.start();
    }

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
}

class Thread_LoadData implements Runnable {
    private HttpSession session;
    private DictSessionService caller;

    public Thread_LoadData(HttpSession session, DictSessionService caller) {
        this.caller = caller;
        this.session = session;
    }

    @Override
    public void run() {
        _OwnerDictionary _od = (_OwnerDictionary)session.getAttribute(SDConstants.SESSION_OWNER_DICT);
        String ownerId = _od.getOnwerId();
        int ownerType = _od.getOnwerType();
        _od.dictModelMap = new ConcurrentHashMap<String, DictModel>();

        DictService dictService = caller.getDictService();
        try {
            //字典组列表
            _od.dmList = dictService.getDictMListByOwnerId(ownerId);
            //字典项列表，按照层级结果，按照排序的广度遍历树
            if (_od.dmList!=null&&_od.dmList.size()>0) _od.ddList = dictService.getDictDListByOwnerId(ownerId);

            //组装dictModelMap
            List<DictDetail> templ = new ArrayList<DictDetail>();
            String tempDmId = "";
            if (_od.dmList!=null&&_od.dmList.size()>0) {
                for (DictMaster dm: _od.dmList) {
                    if (dm.getOwnerType()==ownerType&&ownerId.equals(dm.getOwnerId())) { //过滤掉不可用的数据
                        _od.dictModelMap.put(dm.getId(), new DictModel(dm));
                    }
                }
                if (_od.ddList!=null&&_od.ddList.size()>0) {
                    for (DictDetail dd: _od.ddList) {
                        if (tempDmId.equals(dd.getMid())) templ.add(dd);
                        else {
                            if (templ.size()>0) {//组成树
                                DictModel dModel = _od.dictModelMap.get(dd.getMid());
                                if (dModel!=null) {
                                    DictDetail _t = new DictDetail();
                                    _t.setId(dModel.getId());
                                    _t.setMid(dModel.getId());
                                    _t.setNodeName(dModel.getDmName());
                                    _t.setIsValidate(1);
                                    _t.setParentId(null);
                                    _t.setOrder(1);
                                    _t.setBCode("root");
                                    TreeNode<DictDetail> root = new TreeNode<DictDetail>(_t);

                                    Map<String, Object> m = TreeUtils.convertFromList(templ);
                                    root.setChildren((List<TreeNode<DictDetail>>)m.get("forest"));
                                    //暂不处理错误记录
                                }
                            }
                            templ.clear();
                            tempDmId=dd.getMid();
                        }
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            _od.setLoadSuccess();
        }
    }
}