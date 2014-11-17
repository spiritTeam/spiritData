package com.gmteam.spiritdata.metadata.relation.semanteme.func;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

import com.gmteam.framework.FConstants;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;
import com.gmteam.spiritdata.metadata.relation.pojo.QuotaColumn;
import com.gmteam.spiritdata.metadata.relation.pojo.QuotaTable;
import com.gmteam.spiritdata.metadata.relation.semanteme.AnalTable;
import com.gmteam.spiritdata.metadata.relation.service.MdQuotaService;
import com.gmteam.spiritdata.util.Arithmetic;
import com.gmteam.spiritdata.util.SequenceUUID;
import com.gmteam.framework.core.cache.SystemCache;
import com.gmteam.framework.util.FileNameUtils;
import com.gmteam.framework.util.JsonUtils;
import com.gmteam.jsonD.model.AtomData;

/**
 * 主键分析器
 * @author wangxia
 */

@Component
public class AnalKey implements AnalTable {
    @Resource
    private BasicDataSource dataSource;
    @Resource
    private MdQuotaService mdQuotaService;

    /**
     * 分析某一个表主键。
     * 分析方法，查看指标表，看是否有主键可能性，若类型为String或Integer则可能性更大，float可能性%50，Data可能性*10%。
     * 若没有单列主键，则查双列，三列，直到查到为止。
     * 分析结构以json的形式存储在文件中，便于以后查找。
     * @param tableName 表名称
     * @param md 元数据信息
     * @return 是一个Map<String, Float>，其中String是列名，float是主键可能性
     */
    @Override
    public Map<String, Float> scanOneTable(String tableName, MetadataModel mm, Map<String, Object> param) throws Exception {
        if (mm.getColumnList()==null||mm.getColumnList().size()==0) return null;
        //先分析指标表
        QuotaTable qt = mdQuotaService.getQuotaInfo(tableName, mm);
        if (qt==null) {//为空，则重新计算指标
            qt = mdQuotaService.caculateQuota(mm, tableName);
        }
        //按指标分析
        if (qt.getColQuotaList()==null||qt.getColQuotaList().size()==0) return null;
        Map<String, Float> ret  = new HashMap<String, Float>();
        Float one = new Float("1");
        for (QuotaColumn qc: qt.getColQuotaList()) {
            String cType = mm.getColumnByColId(qc.getColId()).getColumnType();
            String cName = mm.getColumnByColId(qc.getColId()).getColumnName();
            String title = mm.getColumnByColId(qc.getColId()).getTitleName().toLowerCase();
            if (one==qc.getCompressRate()) {
                if (cType.equalsIgnoreCase("String")) {
                    ret.put(cName, one);
                } else if (cType.equalsIgnoreCase("Integer")) {
                    ret.put(cName, one);
                } else if (cType.equalsIgnoreCase("Double")) {
                    ret.put(cName, new Float(one*0.5));
                } else if (cType.equalsIgnoreCase("Date")) {
                    ret.put(cName, new Float(one*0.1));
                }
                Float f = ret.get(cName);
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
                            String keyComp = ",";
                            for (int t=0; t<o.length; t++) {
                                keyComp +=((QuotaColumn)o[t]).getColumn().getColumnName();
                            }
                            keyComp = keyComp.substring(1);
                            countSql.replaceAll("#colList", keyComp);
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
                            System.out.println("=============="+keyComp+"");
                            
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
                        System.out.println("=============="+keyComp+"");
                        
                    }
                } else break;
                n++;
            }
        }
        //写json文件，此方法目前为测试方法，今后把他变为一个更好用的包
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("_id", SequenceUUID.getUUID());
        jsonMap.put("_code", "SD.TEAM.ANAL-0001");
        jsonMap.put("_cTime", (new Date()).getTime());
        jsonMap.put("desc", "分析表["+tableName+"]那列或那些列可作为主键的记录文件");
        Map<String, Object> _DATA_Map = new HashMap<String, Object>();
        AtomData _dataElement = new AtomData("string", tableName);
        _DATA_Map.put("_tableName", _dataElement.toJsonMap());
        _dataElement.clean();
        _dataElement.setAtomData("string", mm.getId());
        _DATA_Map.put("_mdMId", _dataElement.toJsonMap());
        _DATA_Map.put("_keyAnals", convertToList(ret));
        jsonMap.put("_DATA", _DATA_Map);
        //写文件
        String root = (String)(SystemCache.getCache(FConstants.APPOSPATH)).getContent();
        //文件格式：analData\{用户名}\MM_{模式Id}\keyAnal\tab_{TABId}.json
        String storeFile = FileNameUtils.concatPath(root, "analData"+File.separator+mm.getOwnerId()+File.separator+"MM_"+mm.getId()+File.separator+"keyAnal"+File.separator+tableName+".json");
        jsonMap.put("_file", storeFile);
        FileOutputStream fileOutputStream = null;
        try {
            File file = new File(storeFile);
            if (!file.exists()) {
                File dirs = new File(FileNameUtils.getFilePath(storeFile));
                if (!dirs.exists()) dirs.mkdirs();
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write((JsonUtils.formatJsonStr(JsonUtils.beanToJson(jsonMap), null)).getBytes()); 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream!=null) {
                try {fileOutputStream.close();}catch(IOException e) {e.printStackTrace();}
            }
        }
        return ret;
    }

    private List<Map<String, Object>> convertToList(Map<String, Float> keyAnalResultMap) {
        if (keyAnalResultMap==null||keyAnalResultMap.size()==0) return null;

        List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
        for (String cols: keyAnalResultMap.keySet()) {
            Map<String, Object> oneEle = new HashMap<String, Object>();
            AtomData _dataElement = new AtomData("string", cols);
            oneEle.put("keyCols", _dataElement.toJsonMap());
            _dataElement.clean();
            _dataElement.setAtomData("double", keyAnalResultMap.get(cols));
            oneEle.put("rate", _dataElement.toJsonMap());
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