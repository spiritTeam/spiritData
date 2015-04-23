package com.spiritdata.dataanal.metadata.relation.service;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.dbcp.BasicDataSource;

import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.UGA.UgaUser;
import com.spiritdata.framework.core.web.SessionLoader;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.dataanal.SDConstants;
import com.spiritdata.dataanal.common.util.SessionUtils;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataColSemanteme;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataColumn;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataTableMapRel;
import com.spiritdata.dataanal.metadata.relation.pojo._OwnerMetadata;

/**
 * 所有者元数据Session处理服务。
 * 元数据信息存放在_OwnerMetadata中，这个类中存放了登录用户的基本元数据信息。
 * 包括：<br/>
 * 1-Session和持久化存储同步的相关操作。<br/>
 * 2-通过线程方式进行加载，并放入缓存或Session。<br/>
 * @author wh
 */
public class MdSessionService implements SessionLoader {
    @Resource
    private TableMapService mdTableOrgService;
    @Resource
    private BasicDataSource dataSource;

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
        _OwnerMetadata _om = new _OwnerMetadata(SessionUtils.getOwner(session));
        session.removeAttribute(SDConstants.SESSION_OWNER_RMDUNIT);
        session.setAttribute(SDConstants.SESSION_OWNER_RMDUNIT, _om);
        //启动加载线程
        Thread_LoadData lm = new Thread_LoadData(session, this);
        Thread t = new Thread(lm);
        t.start();
    }

    /**
     * 为导入数据存储元数据信息，并生成相应的数据表
     * @param mm 元数据信息，从Import文件中分析出的mm信息，此信息不必包含积累表名称
     * @return TableMapOrg数据的第一个元素是积累表，第二个元素是临时表，若有第三个元素，则表明本次的元数据是新增的
     */
    public MetadataTableMapRel[] storeMdModel4Import(MetadataModel mm, _OwnerMetadata _om) throws Exception {
        mm.setOwnerId(_om.getOwner().getOwnerId());
        mm.setOwnerType(_om.getOwner().getOwnerType());
        MetadataTableMapRel accumulationTable=null, tempTable=null;
        Map<String, Object> compareMap = getExistMetadataModel(mm, _om);
        MetadataModel _existMm = compareMap==null?null:(MetadataModel)compareMap.get("sameMM");
        if (_existMm==null) {
            //生成积累表名称
            String mdMId = mm.getId();
            if (StringUtils.isNullOrEmptyOrSpace(mdMId)) {
                mdMId = SequenceUUID.getPureUUID();
                mm.setId(mdMId);
            }
            String accumulationTabName = "tab_"+mdMId;
            //注册积累表
            accumulationTable = mdTableOrgService.registTabOrgMap(accumulationTabName, mm, 1);
            //添加模型
            addMetadataModel(mm, _om);
        } else {
            //调整数据类型
            Map<String, String> alterTable = (Map<String, String>)compareMap.get("alterTable");
            if (alterTable!=null&&alterTable.size()>0) {
                Connection conn = null;
                Statement st = null;
                try {
                    conn = dataSource.getConnection();
                    st = conn.createStatement();
                    for (String _k: alterTable.keySet()) {
                        //缓存
                        MetadataModel cm = _om.getMetadataById(_existMm.getId());
                        cm.getColumnByCName(_k).setColumnType(alterTable.get(_k));
                        //表
                        st.execute("ALTER TABLE "+_existMm.getTableName()+" MODIFY COLUMN "+_k+" "+alterTable.get(_k)+" COMMENT '"+cm.getColumnByCName(_k).getTitleName()+"'");
                        st.execute("update sa_md_column set columnType='"+alterTable.get(_k)+"' where tmid="+_existMm.getId()+" and columnName='"+_k+"'");
                    }
                } catch(Exception e) {
                    //???
                } finally {
                    try { if (st!=null) {st.close();st = null;} } catch (Exception e) {e.printStackTrace();} finally {st = null;};
                    try { if (conn!=null) {conn.close();conn = null;} } catch (Exception e) {e.printStackTrace();} finally {conn = null;};
                }
            }
            accumulationTable = mdTableOrgService.getAccumulationTableMapOrg(_existMm.getId());
            mm=_existMm;
        }
        //创建临时表
        String tempTabName = "tabt_"+SequenceUUID.getPureUUID();
        tempTable = mdTableOrgService.registTabOrgMap(tempTabName, mm, 2);

        //处理返回值
        if (_existMm==null) {
            MetadataTableMapRel[] ret = new MetadataTableMapRel[3];
            ret[0] = accumulationTable;
            ret[1] = tempTable;
            ret[2] = null;
            return ret;
        } else {
            MetadataTableMapRel[] ret = new MetadataTableMapRel[2];
            ret[0] = accumulationTable;
            ret[1] = tempTable;
            return ret;
        }
    }

    /**
     * 新增模式
     * @param mm 元数据模式
     * @param session
     */
    public void addMetadataModel(MetadataModel mm, _OwnerMetadata _om) throws Exception {
        //新增数据库-主表
        mdBasisService.addMetadataModel(mm);
        //新增数据库-子表
        List<MetadataColumn> mcList = mm.getColumnList();
        if (mcList!=null&&mcList.size()>0) {
            for (MetadataColumn mc: mcList) {
                if (StringUtils.isNullOrEmptyOrSpace(mc.getId())) mc.setId(SequenceUUID.getPureUUID());
                mdBasisService.addMetadataColumn(mc);
            }
        }
        //新增缓存
        _om.mdModelMap.put(mm.getId(), mm);
    }
    
    /**
     * 在所有者创建的全部元数据模型集合中 比较 元数据模型是否已经存在
     * @param mm 被比较的元数据模型
     * @return 若存在返回true，否则返回false
     */
    private Map<String, Object> getExistMetadataModel(MetadataModel mm, _OwnerMetadata _om) throws Exception {
        //比较是否存储在
        Map<String, MetadataModel> mmMap = _om.mdModelMap;
        for (String id: mmMap.keySet()) {
            Map<String, Object> m = mmMap.get(id).isSame(mm, 1);
            if ((mmMap.get(id).isSame(mm, 1)).get("type").equals("1")) {
                m.put("sameMM", mmMap.get(id));
                return m;
            }
        }
        return null;
    }

    /**
     * 装载并检查数据，先检查是否已经装载，若没有装载则进行装载。。并返回装载的内容
     * @param session 
     * @throws InterruptedException 
     */
    public _OwnerMetadata loadcheckData(HttpSession session) throws Exception {
        _OwnerMetadata _om = (_OwnerMetadata)session.getAttribute(SDConstants.SESSION_OWNER_RMDUNIT);
        if (_om==null) {
            loader(session);
            _om = (_OwnerMetadata)session.getAttribute(SDConstants.SESSION_OWNER_RMDUNIT);
            while (!_om.isLoadSuccess()) {
                Thread.sleep(100);
                _om = (_OwnerMetadata)session.getAttribute(SDConstants.SESSION_OWNER_RMDUNIT);
            }
        }
        return _om;
    }
}

class Thread_LoadData implements Runnable {
    private HttpSession session;
    private MdSessionService caller;

    public Thread_LoadData(HttpSession session, MdSessionService caller) {
        this.caller = caller;
        this.session = session;
    }

    @Override
    public void run() {
        _OwnerMetadata _om = (_OwnerMetadata)session.getAttribute(SDConstants.SESSION_OWNER_RMDUNIT);
        String ownerId = _om.getOwner().getOwnerId();
        int ownerType = _om.getOwner().getOwnerType();
        _om.mdModelMap = new ConcurrentHashMap<String, MetadataModel>();
        MdBasisService mdBasisService = caller.getMdBasisService();

        try {
            List<MetadataModel> mmList = mdBasisService.getMdMListByOwnerId4Session(ownerId);
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
            List<Map<String, Object>> mTitleList =  null;//名称处理

            if (mmList!=null&&mmList.size()>0) {//这保证了flagMap有内容
                //准备语义信息
                mcsList = mdBasisService.getMdColSemantemeListByOwnerId(ownerId);
                Map<String, List<MetadataColSemanteme>> _flagMap = new HashMap<String, List<MetadataColSemanteme>>();
                if (mcsList!=null&&mcsList.size()>0) {
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
                    //名称处理
                    mTitleList = mdBasisService.getMdTitleListByOwnerId(ownerId);
                    if (mTitleList!=null&&mTitleList.size()>0) {
                        for (Map<String, Object> report: mTitleList) {
                            String tmId=(String)report.get("tmId");
                            if (_om.mdModelMap.get(tmId)!=null) {
                                if (_om.mdModelMap.get(tmId).titleMap==null) _om.mdModelMap.get(tmId).titleMap = new HashMap<String, Integer>();
                                _om.mdModelMap.get(tmId).titleMap.put((String)report.get("tableTitleName"), Integer.parseInt(report.get("size")+""));
                            }
                        }
                    }
                } else {
                    mmList = null;
                    _om.mdModelMap.clear();
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            _om.setLoadSuccess();
        }
    }
}