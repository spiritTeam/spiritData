package com.gmteam.spiritdata.dictdata.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import com.gmteam.framework.core.model.tree.TreeNode;
import com.gmteam.framework.util.TreeUtils;
import com.gmteam.spiritdata.SDConstants;
import com.gmteam.spiritdata.dictdata.pojo.DictDetail;
import com.gmteam.spiritdata.dictdata.pojo.DictMaster;
import com.gmteam.spiritdata.dictdata.pojo.DictModel;
import com.gmteam.spiritdata.dictdata.pojo._OwnerDictionary;

/**
 * 对[所有者“字典数据”的操作。
 * 通过线程方式进行加载，并放入缓存或Session。
 * 
 * @author wh
 */
@Component
public class _OnwerDictionaryService {
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

    /**
     * 新增字典模式
     * @param dModel 字典模式
     * @param session
     */
    public void addDictModel(DictModel dModel, HttpSession session) throws Exception {
        _OwnerDictionary _od = (_OwnerDictionary)session.getAttribute(SDConstants.SESSION_OWNER_DICT);
        if (dModel.dictTree!=null) {
            //新增字典组
            dictService.addDictMaster(dModel);
            //新增字典项
            dictService.addDictDetail(dModel.dictTree);

            //新增缓存
            if (_od.dictModelMap==null) _od.dictModelMap = new ConcurrentHashMap<String, DictModel>();
            _od.dictModelMap.put(dModel.getId(), dModel);
            if (_od.dmList==null) _od.dmList = new ArrayList<DictMaster>();
            _od.dmList.add(dModel.getDictMaster());
            if (_od.ddList==null) _od.ddList = new ArrayList<DictDetail>();
            addToList(_od.ddList, dModel.dictTree);
            //缓存中的Map
            if (_od.dictTreeMap==null) _od.dictTreeMap = new ConcurrentHashMap<String, TreeNode<DictDetail>>();
            pubToMap(_od.dictTreeMap, dModel.dictTree);
        }
    }
    private void addToList(List<DictDetail> l, TreeNode<DictDetail> dictTree) {
        if (l==null) throw new IllegalArgumentException("参数l不能是空列表！");
        if (dictTree!=null) {
            l.add(dictTree.getTnEntity());
            if (dictTree.getChildren()!=null&&dictTree.getChildren().size()>0) {
                for (TreeNode<DictDetail> child: dictTree.getChildren()) {
                    addToList(l, child);
                }
            }
        }
    }
    protected void pubToMap(Map<String, TreeNode<DictDetail>> m, TreeNode<DictDetail> dictTree) {
        if (m==null) throw new IllegalArgumentException("参数m不能是空Map！");
        if (dictTree!=null) {
            m.put(dictTree.getId(), dictTree);
            if (dictTree.getChildren()!=null&&dictTree.getChildren().size()>0) {
                for (TreeNode<DictDetail> child: dictTree.getChildren()) {
                    pubToMap(m, child);
                }
            }
        }
    }

    /**
     * 根据字典项列表，修改字典模式
     * @param l 字典项列表
     * @param session
     */
    public void modifyDictBy(List<DictDetail> l, HttpSession session) throws Exception {
        //？？
    }
}

class Thread_LoadData implements Runnable {
    private HttpSession session;
    private _OnwerDictionaryService caller;

    public Thread_LoadData(HttpSession session, _OnwerDictionaryService caller) {
        this.caller = caller;
        this.session = session;
    }

    @Override
    public void run() {
        _OwnerDictionary _od = (_OwnerDictionary)session.getAttribute(SDConstants.SESSION_OWNER_DICT);
        String ownerId = _od.getOnwerId();
        int ownerType = _od.getOnwerType();
        _od.dictModelMap = new ConcurrentHashMap<String, DictModel>();
        _od.dictTreeMap = new ConcurrentHashMap<String, TreeNode<DictDetail>>();
        
        
        DictService dictService = caller.getDictService();
        try {
            List<DictMaster> dmList = dictService.getDictMListByOwnerId(ownerId);
            Map<String, DictMaster> flagMap = null;
            //过滤元数据模式，把可疑数据删除, 并准备元数据模式信息
            if (dmList!=null&&dmList.size()>0) {
                flagMap = new HashMap<String, DictMaster>();
                for (int i=dmList.size()-1; i>=0; i--) {
                    DictMaster dm = dmList.get(i);
                    if (dm.getOwnerType()!=ownerType) {
                        dmList.remove(i);
                    } else {
                        flagMap.put(dm.getId(), dm);
                    }
                }
            }

            List<DictDetail> ddList = null;
            if (dmList!=null&&dmList.size()>0) {//这也保证了flagMap有内容
                ddList = dictService.getDictDListByOwnerId(ownerId); //这是按照组id的排序列表
                if (ddList!=null&&ddList.size()>0) {
                    List<DictDetail> templ = new ArrayList<DictDetail>();
                    String tempDmId = "";
                    for (int i=0; i<ddList.size(); i++) {
                        DictDetail dd = ddList.get(i);
                        if (!dd.getMid().equals(tempDmId)) {
                            if (templ.size()>0) {
                                Map<String, Object> m = TreeUtils.convertFromList(templ);
                                List<TreeNode<DictDetail>> f = (List<TreeNode<DictDetail>>)m.get("forest");
                                if (f!=null&&f.size()>0) {
                                    DictMaster dm = flagMap.get(tempDmId);
                                    if (dm!=null) {
                                        DictModel dModel = new DictModel(dm);
                                        if (f.size()==1) {
                                            dModel.dictTree = f.get(0);
                                        } else {
                                            DictDetail _t = new DictDetail();
                                            TreeNode<DictDetail> root = new TreeNode<DictDetail>(_t);
                                            root.setChildren(f);
                                            dModel.dictTree = root;
                                        }
                                        caller.pubToMap(_od.dictTreeMap, dModel.dictTree);
                                        _od.dictModelMap.put(dModel.getId(), dModel);
                                    }
                                }
                                //暂不处理错误记录
                            } else {
                                templ.clear();
                                tempDmId = dd.getMid();
                                templ.add(dd);
                            }
                        } else templ.add(dd);
                    }
                }
            }

            if (dmList!=null&&dmList.size()>0) _od.dmList = dmList; else _od.dmList=null;
            if (ddList!=null&&ddList.size()>0) _od.ddList = ddList; else _od.ddList=null;

            _od.setLoadSuccess();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
