package com.spiritdata.dataanal.metadata.relation.semanteme.func;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.stereotype.Component;

import com.spiritdata.filemanage.category.ANAL.model.AnalResultFile;
import com.spiritdata.filemanage.category.ANAL.service.AanlResultFileService;
import com.spiritdata.dataanal.SDConstants;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel;
import com.spiritdata.dataanal.metadata.relation.pojo.QuotaColumn;
import com.spiritdata.dataanal.metadata.relation.pojo.QuotaTable;
import com.spiritdata.dataanal.metadata.relation.semanteme.AnalTable;
import com.spiritdata.dataanal.metadata.relation.service.MdQuotaService;
import com.spiritdata.dataanal.util.Arithmetic;
import com.spiritdata.jsonD.model.JsonD;
import com.spiritdata.jsonD.model.JsonDAtomData;
import com.spiritdata.jsonD.model.JsonDHead;
import com.spiritdata.dataanal.exceptionC.Dtal0202CException;

/**
 * 主键分析器
 * @author wh
 */
@Component
public class AnalKey implements AnalTable {
    public final static String jsonDCode = "SD.TEAM.ANAL::0001";
    @Resource
    private BasicDataSource dataSource;
    @Resource
    private MdQuotaService mdQuotaService;
    @Resource
    private AanlResultFileService arfService;

    /**
     * 分析某一个表主键。
     * 分析方法，查看指标表，看是否有主键可能性，若类型为String或Integer则可能性更大，float可能性%50，Data可能性*10%。
     * 若没有单列主键，则查双列，三列，直到查到为止。
     * 分析结构以json的形式存储在文件中，便于以后查找。
     * @param tableName 表名称
     * @param md 元数据信息
     * @return 是一个Map<String, Object> 其中若String是列名，Object=float是主键可能性；若String是"resultFile"，Object=AnalResultFile是文件信息
     */
    @Override
    public Map<String, Object> scanOneTable(String tableName, MetadataModel mm, Map<String, Object> param) throws Dtal0202CException {
        if (mm.getColumnList()==null||mm.getColumnList().size()==0) throw new Dtal0202CException("元数据模型信息不包含任何列信息，无法分析！");

        QuotaTable qt = mdQuotaService.getQuotaInfo(tableName, mm); //获得指标表
        if (qt==null) qt = mdQuotaService.caculateQuota(mm, tableName);//为空，则重新计算指标
        if (qt.getAllCount()==0) return null;//返回空，表中没有数据，无法分析
        if (qt.getColQuotaList()==null||qt.getColQuotaList().size()==0) return null;

        //按指标分析
        Map<String, Object> ret  = new HashMap<String, Object>();
        Float one = new Float("1");
        for (QuotaColumn qc: qt.getColQuotaList()) {
            String cType = mm.getColumnByColId(qc.getColId()).getColumnType();
            String cName = mm.getColumnByColId(qc.getColId()).getColumnName();
            String title = mm.getColumnByColId(qc.getColId()).getTitleName().toLowerCase();
            if (one==(1-qc.getCompressRate())&&qc.getNullCount()<2) {
                if (cType.equalsIgnoreCase("String")) {
                    ret.put(cName, one);
                } else if (cType.equalsIgnoreCase("Integer")) {
                    ret.put(cName, one);
                } else if (cType.equalsIgnoreCase("Double")) {
                    ret.put(cName, new Float(one*0.5));
                } else if (cType.equalsIgnoreCase("Date")) {
                    ret.put(cName, new Float(one*0.1));
                }
                Float f = (Float)ret.get(cName);
                if (f!=null) {
                    if (title.indexOf("id")!=-1) ret.put(cName, new Float(f*2));
                    else if (title.indexOf("ident")!=-1) ret.put(cName, new Float(f*1.5));
                    else if (title.indexOf("num")!=-1)   ret.put(cName, new Float(f*1.2));
                    else if (title.indexOf("key")!=-1)   ret.put(cName, new Float(f*1.1));
                    else if (title.indexOf("主键")!=-1)  ret.put(cName, new Float(f*2));
                    else if (title.indexOf("键")!=-1)    ret.put(cName, new Float(f*1.6));
                    else if (title.indexOf("编号")!=-1)  ret.put(cName, new Float(f*1.8));
                    else if (title.indexOf("编码")!=-1)  ret.put(cName, new Float(f*1.8));
                    else if (title.indexOf("号")!=-1)    ret.put(cName, new Float(f*1.3));
                    else if (title.indexOf("序号")!=-1)  ret.remove(cName);//若是序号，则一定不能作为主键
                }
            }
        }
        if (ret.size()==0) {//说明一列不能满足要求，主键可能是多列
            int n = 2;//列组合个数
            int _nLimit = 3;//列组和限制，最多查找到多少列的组合
            if (param!=null) {
                if (param.get("maxKeyColumnsCount")!=null) {//参数中给出组合数
                    try {
                        _nLimit = Integer.parseInt((String)param.get("maxKeyColumnsCount"));
                    } catch(Exception e) {}
                }
            }
            //找出可能的列，不包括浮点列，URL列，若是字符串，长度大于128的字符串列;URL列不处理
            List<QuotaColumn> l = new ArrayList<QuotaColumn>();
            for (QuotaColumn qc: qt.getColQuotaList()) {
                if (isWaitKeyCol(qc)>0) l.add(qc);
            }
            //找出列组合
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            Map<Integer, List<Object[]>> CompagesMap = Arithmetic.AllCompages(l.toArray());
            try {
                conn = dataSource.getConnection();
                String countSql = "select count(distinct #colList) from "+tableName;
                long count = 0;
                while (ret.size()==0&&n<=_nLimit) {
                    List<Object[]> _keyL = CompagesMap.get(new Integer(n));
                    if (_keyL!=null&&_keyL.size()>0) {
                        for (Object[] o :_keyL) {
                            String keyComp = "";
                            for (int t=0; t<o.length; t++) {
                                keyComp +=","+((QuotaColumn)o[t]).getColumn().getColumnName();
                            }
                            keyComp = keyComp.substring(1);
                            countSql = countSql.replaceAll("#colList", keyComp);
                            ps = conn.prepareStatement(countSql);
                            rs = ps.executeQuery();
                            if (rs.next()) {
                                count = rs.getLong(1);
                            } else {
                                count = -1;
                            }
                            if (count/qt.getAllCount()==1) {
                                ret.put(keyComp, one);
                                //再根据列情况进行权重的调整
                            }
                            rs.close();
                            ps.close();
                            
                        }
                    } else break;
                    n++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try { if (rs!=null) {rs.close();rs = null;} } catch (Exception e) {e.printStackTrace();} finally {rs = null;};
                try { if (ps!=null) {ps.close();ps = null;} } catch (Exception e) {e.printStackTrace();} finally {ps = null;};
                try { if (conn!=null) {conn.close();conn = null;} } catch (Exception e) {e.printStackTrace();} finally {conn = null;};
            }

            while (ret.size()==0&&n<=_nLimit) {
                List<Object[]> _keyL = CompagesMap.get(new Integer(n));
                if (_keyL!=null&&_keyL.size()>0) {
                    for (Object[] o :_keyL) {
                        String keyComp = ",";
                        for (int t=0; t<o.length; t++) {
                            keyComp +=((QuotaColumn)o[t]).getColumn().getColumnName();
                        }
                        keyComp = keyComp.substring(1);
                    }
                } else break;
                n++;
            }
        }

        //组织JsonD，并写入文件
        JsonD analKeyJsonD = new JsonD();
        //头
        JsonDHead jsonDHead = new JsonDHead();
        jsonDHead.setId(SequenceUUID.getPureUUID());
        jsonDHead.setCode(jsonDCode);
        jsonDHead.setCTime(new Date());
        jsonDHead.setDesc("分析表["+tableName+"]那列或那些列可作为主键");
        //数据体
        Map<String, Object> _DATA_Map = new HashMap<String, Object>();
        JsonDAtomData _dataElement = new JsonDAtomData("_tableName", "string", tableName);
        _DATA_Map.putAll(_dataElement.toJsonMap());
        _dataElement.setAtomData("_mdMId", "string", mm.getId());
        _DATA_Map.putAll(_dataElement.toJsonMap());
        _DATA_Map.put("_keyAnals", convertToList(ret));
        //设置JsonD
        analKeyJsonD.set_HEAD(jsonDHead);
        analKeyJsonD.set_DATA(_DATA_Map);
        //分析结果文件种子设置
        AnalResultFile arfSeed = new AnalResultFile();
        arfSeed.setAnalType(SDConstants.ANAL_MD_KEY);
        arfSeed.setSubType(mm.getId());
        arfSeed.setObjType("table");
        arfSeed.setObjId(tableName);
        arfSeed.setFileNameSeed("METADATA"+File.separator+"key"+File.separator+"md_"+mm.getId());
        arfSeed.setJsonDCode(jsonDCode);

        AnalResultFile arf = (AnalResultFile)arfService.write2FileAsJson(analKeyJsonD, arfSeed);
        //回写文件信息到返回值
        ret.put("resultFile", arf);

        return ret;
    }

    private List<Map<String, Object>> convertToList(Map<String, Object> keyAnalResultMap) {
        if (keyAnalResultMap==null||keyAnalResultMap.size()==0) return null;

        List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
        for (String cols: keyAnalResultMap.keySet()) {
            Map<String, Object> oneEle = new HashMap<String, Object>();
            JsonDAtomData _dataElement = new JsonDAtomData("keyCols", "string", cols);
            oneEle.putAll(_dataElement.toJsonMap());
            _dataElement.setAtomData("rate", "double", keyAnalResultMap.get(cols));
            oneEle.putAll(_dataElement.toJsonMap());
            ret.add(oneEle);
        }
        return ret;
    }

    /*
     * 根据列指标信息，判断其是否可能是多列Key组合中的列。
     * 不包括浮点列，URL列，若是字符串，长度大于128的字符串列;URL列不处理
     * @param qc 列指标信息
     * @return 是key列的可能性
     */
    private float isWaitKeyCol(QuotaColumn qc) {
        if (qc.getColumn().getColumnType().equalsIgnoreCase("Double")) return 0f;
        try {
            if (qc.getColumn().getColumnType().equalsIgnoreCase("Double")&&Integer.parseInt(qc.getMax())>128) return 0f;
        } catch(Exception e) {}
        if (qc.getColumn().getColumnType().equalsIgnoreCase("Date")) return 0.5f;

        return 1f;
    }
}