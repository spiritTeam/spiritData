package com.spiritdata.dataanal.metadata.relation.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.stereotype.Service;

import com.spiritdata.dataanal.dictionary.pojo.DictDetail;
import com.spiritdata.dataanal.dictionary.pojo.DictMaster;
import com.spiritdata.dataanal.dictionary.pojo.DictModel;
import com.spiritdata.dataanal.dictionary.pojo._OwnerDictionary;
import com.spiritdata.dataanal.dictionary.service.DictService;
import com.spiritdata.dataanal.exceptionC.Dtal0203CException;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataColSemanteme;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataColumn;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel;
import com.spiritdata.dataanal.metadata.relation.semanteme.SemantemeType;
import com.spiritdata.framework.core.model.tree.TreeNode;
import com.spiritdata.framework.util.ChineseCharactersUtils;
import com.spiritdata.framework.util.SequenceUUID;

/**
 * 元数据字典项调整处理服务
 * @author wh
 */

@Service
//目前有这样一个假设：
public class MdDictService {
    @Resource
    private BasicDataSource dataSource;
    @Resource
    private MdBasisService mdBasisService;
    @Resource
    private DictService dictService;

    /**
     * 调整元数据的字典信息，注意，此调整也调整了Session或缓存中的内容。
     * 若元数据主键是不确定的，本方法还会自动调用主键的分析方法。
     * @param mm 元数据信息，注意，这里的元数据信息必须是全的，包括column和语义
     * @param dictMap 标识列字典信息的Map
     * @param _dictCache 字典缓存
     * @param tTableName 临时表，用于调整数据
     */
    public void adjustMdDict(MetadataModel mm, Map<String, Object> dictMap, String tTableName, _OwnerDictionary _dictCache) {
        if (mm.getColumnList()==null||mm.getColumnList().size()==0) throw new Dtal0203CException("元数据模型信息不包含任何列信息，无法分析！");

        Map<String, Object> m = new HashMap<String, Object>();//记录那列是字典项
        for (MetadataColumn mc: mm.getColumnList()) {
            List<MetadataColSemanteme> mcsL = mc.getColSemList();
            if (mcsL!=null&&mcsL.size()>0) {
                for (MetadataColSemanteme mcs: mcsL) {
                    if (mcs.getSemantemeType()==SemantemeType.DICT) m.put(mc.getColumnName(), mcs);
                }
            }
        }
        if (dictMap.size()>0) {
            for (String key :dictMap.keySet()) {
                if (m.get(key)==null) m.put(key, dictMap.get(key));
            }
        }
        
        Connection conn = null;
        ResultSet rs = null;
        Statement st = null;
        try {
            if (m.size()>0) {
                String _tableName = "";
                for (String key :m.keySet()) {
                    Object v = m.get(key); //每个字典组分别处理
                    DictModel dModel = null;
                    if (v instanceof MetadataColSemanteme) { //已引用的字典组
                        dModel=_dictCache.getDictModelById(((MetadataColSemanteme) v).getSemantemeCode());
                        _tableName = tTableName;
                    } else { //未引用的字典组，加入新的字典组，包括Session和数据库
                        //准备数据
                        //1准备数据-字典组
                        DictMaster dm = new DictMaster();
                        dm.setId(SequenceUUID.getUUIDSubSegment(4));
                        dm.setOwnerId(_dictCache.getOwnerId());
                        dm.setOwnerType(_dictCache.getOwnerType());
                        dm.setDmName(mm.getColumnByCName(key).getTitleName());//字典组名称mm.getTitleName()+"::"+
                        dm.setNPy(ChineseCharactersUtils.getFullSpellFirstUp(dm.getDmName())); //汉语拼音
                        dm.setSort(1); //排序，排序需要由人来处理
                        dm.setIsValidate(1);
                        dm.setMType(2); //系统生成的
                        //2准备数据-字典语义
                        MetadataColSemanteme mcs = new MetadataColSemanteme();
                        mcs.setId(SequenceUUID.getPureUUID());
                        mcs.setColId(mm.getColumnByCName(key).getId());
                        mcs.setMdMId(mm.getId());
                        mcs.setSemantemeType(1);//字典型
                        mcs.setSemantemeCode(dm.getId());
                        mcs.setSemantemeWeight((Float)v);
                        //数据库处理
                        dictService.addDictMaster(dm);//字典
                        mdBasisService.addMetadataColSemanteme(mcs);//语义
                        //缓存处理
                        //1缓存处理-字典
                        dModel = new DictModel(dm);
                        //1-1创建根结点
                        DictDetail _t = new DictDetail();
                        _t.setId(dModel.getId());
                        _t.setMid(dModel.getId());
                        _t.setNodeName(dModel.getDmName());
                        _t.setIsValidate(1);
                        _t.setParentId(null);
                        _t.setOrder(1);
                        _t.setBCode("root");
                        TreeNode<DictDetail> root = new TreeNode<DictDetail>(_t);
                        dModel.dictTree = root;
                        if (_dictCache.dmList==null) _dictCache.dmList = new ArrayList<DictMaster>();
                        _dictCache.dmList.add(dm);
                        _dictCache.dictModelMap.put(dm.getId(), dModel);
                        //2缓存处理-源数据
                        mm.getColumnByCName(key).addColSem(mcs);

                        _tableName = mm.getTableName();
                    }
                    //字典项的具体处理
                    conn = dataSource.getConnection();
                    st = conn.createStatement();
                    rs = st.executeQuery("select distinct "+key+" from "+_tableName);
                    boolean find = false;
                    while (rs.next()) {//处理每个字典项
                        String dictDetailName = rs.getString(1);
                        find=false;
                        //找看看是否有相同的
                        if (_dictCache.ddList!=null&&_dictCache.ddList.size()>0) {
                            for (DictDetail dd: _dictCache.ddList) {
                                if (dd.getMid().equals(dModel.getId())&&dd.getNodeName().equals(dictDetailName)) {
                                    find=true;
                                    break;
                                }
                            }
                        }
                        if (!find) {//需要新增
                            //准备数据
                            DictDetail dd = new DictDetail();
                            dd.setId(SequenceUUID.getUUIDSubSegment(4));
                            dd.setMid(dModel.getId());
                            dd.setNodeName(dictDetailName);
                            dd.setNPy(ChineseCharactersUtils.getFullSpellFirstUp(dictDetailName));
                            dd.setBCode(dd.getId());
                            dd.setDType(2); //系统生成的
                            dd.setParentId("0");
                            dd.setOrder(1); //由于是单级树，没有排序
                            //数据库新增
                            dictService.addDictDetail(dd);
                            //缓存处理
                            dd.setParentId(dModel.getId()); //在目前字典项只能是单级的，是一个单级树
                            TreeNode<DictDetail> treeDD = new TreeNode<DictDetail>(dd);
                            dModel.dictTree.addChild(treeDD);
                            if (_dictCache.ddList==null) _dictCache.ddList = new ArrayList<DictDetail>();
                            _dictCache.ddList.add(dd);
                        }
                    }
                }
            }
        } catch(Exception e) {
            throw new Dtal0203CException(e);
        } finally {
            try { if (rs!=null) {rs.close();rs = null;} } catch (Exception e) {e.printStackTrace();} finally {rs = null;};
            try { if (st!=null) {st.close();st = null;} } catch (Exception e) {e.printStackTrace();} finally {st = null;};
            try { if (conn!=null) {conn.close();conn = null;} } catch (Exception e) {e.printStackTrace();} finally {conn = null;};
        }
    }
}