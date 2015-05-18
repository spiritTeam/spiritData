package com.spiritdata.dataanal.metadata.relation.service;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spiritdata.filemanage.category.ANAL.model.AnalResultFile;
import com.spiritdata.filemanage.category.ANAL.service.AnalResultFileService;
import com.spiritdata.filemanage.core.persistence.pojo.FileIndexPo;
import com.spiritdata.filemanage.util.FileOperUtils;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.jsonD.model.JsonD;
import com.spiritdata.jsonD.model.JsonDAtomData;
import com.spiritdata.jsonD.model.JsonDHead;
import com.spiritdata.jsonD.util.JsonUtils;
import com.spiritdata.dataanal.SDConstants;
import com.spiritdata.dataanal.exceptionC.Dtal0202CException;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataColumn;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel;

/**
 * 元数据主键处理服务
 * @author wh
 */
public class MdKeyService {
    @Resource
    private BasicDataSource dataSource;
    @Resource
    private MdBasisService mdBasisService;
    @Resource
    private AnalResultFileService arfService;

    /**
     * 调整元数据主键。
     * 若元数据主键是不确定的，本方法还会自动调用主键的分析方法。
     * @param mm 元数据信息，注意，这里的元数据信息必须是全的，包括column和语义
     */
    public void adjustMdKey(MetadataModel mm, Map<String, List<String>> noKeyInfo) {
        if (mm.getColumnList()==null||mm.getColumnList().size()==0) return;
        //看目前元数据积累表是否有主键，若有取出(注意这里是从关系型数据库的系统管理信息[metadata]中得到主键)
        String sumTableName = mm.getTableName();
        if (sumTableName==null||sumTableName.trim().length()==0) return ;

        //开始调整
        //0-合并和jsonD中记录的noKeyInfo的信息
        Map<String, List<String>> _noKeyInfo = mergedNoKeyInfo(noKeyInfo, mm);
        //1-获得要调整的主键信息，读取元数据信息，看主键是否是确定的
        String[] keys = needAnalKey(mm)?analMdKey(mm, _noKeyInfo):null;//分析后得到的主键列表，可以作为主键的列组合，目前应该只是单主键，即keys.length=1
        //2-若没有需要调整的主键信息，则从元数据定义中获取主键信息
        if (keys==null) keys=getMMKeys(mm);
        keys = dealKeyWithNoKeyInfo(keys, _noKeyInfo); //根据不能作为主键的列信息，调整需要调整的信息

        Connection conn = null;
        ResultSet rs = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            boolean needCreateKey = false;
            String[] dbKeys = this.getDbKeys(conn, sumTableName, rs); //数据库中的主键
            if (dbKeys==null) { //原数据库没有主键
                if (keys!=null) needCreateKey = true;
            } else { //有主键
                if (twoStringArraySame(dbKeys, keys)) needCreateKey = false;
            }
            if (needCreateKey) {
                String _tabKeyStr = "";
                st = conn.createStatement();
                //先删除
                if (dbKeys!=null) st.execute("ALTER TABLE "+sumTableName+" DROP PRIMARY KEY");
                //再创建
                for (String aKey: keys) _tabKeyStr += ","+aKey;
                st.execute("ALTER TABLE "+sumTableName+" ADD PRIMARY KEY("+_tabKeyStr.substring(1)+")");
            }
            //修改元数据信息，以及缓存内信息
        } catch(Exception e) {
            throw new Dtal0202CException(e);
        } finally {
            try { if (rs!=null) {rs.close();rs = null;} } catch (Exception e) {e.printStackTrace();} finally {rs = null;};
            try { if (st!=null) {st.close();st = null;} } catch (Exception e) {e.printStackTrace();} finally {st = null;};
            try { if (conn!=null) {conn.close();conn = null;} } catch (Exception e) {e.printStackTrace();} finally {conn = null;};
        }
    }

    private boolean needAnalKey(MetadataModel mm) {
        boolean needAnalKey = false;//是否需要分析主键
        int[] keySigns = new int[2];
        for (MetadataColumn mc: mm.getColumnList()) {
            if (mc.getPkSign()>0) {
                if (keySigns[0]==keySigns[1]&&keySigns[0]==0) keySigns[0]=mc.getPkSign();
                else keySigns[1]=mc.getPkSign();
                if (keySigns[0]!=keySigns[1]&&keySigns[0]>0&&keySigns[1]>0) break;
                else {
                    keySigns[0]=keySigns[1];
                    keySigns[1]=0;
                }
            }
        }
        needAnalKey = !(keySigns[0]==1&&(keySigns[1]==1||keySigns[1]==0));
        return needAnalKey;
    }

    /*
     * 通过jsonD分析元数据的key，并返回最有可能作为key的列组合。
     * 同时，此方法还修改了元数据的主键信息，包括传入的参数MM和数据库的持久化信息，但注意，不调整主键信息。
     * 注意：此方法不修改实体表的主键信息，只修改自定义的元数据结构中的信息
     * @param mm 元数据信息
     * @param noKeyInfo 不能作为key的列信息
     * @return 最有可能作为key的列组合，用列名称(String类型)的数组来表示
     * @throws Exception
     */
    private String[] analMdKey(MetadataModel mm, Map<String, List<String>> noKeyInfo) {
        if (mm.getColumnList()==null||mm.getColumnList().size()==0) return null;
        //读取元数据信息，看是否需要对主键进行分析
        String keyStr = null; //主键串，若分析后无主键，此变量为null
        int pkSign = 2; //主键可能性

        if (needAnalKey(mm)) {//若需要分析主键
            List<Map<String, Double>> keyList = new ArrayList<Map<String, Double>>();
            //从文件系统中得到分析的文件
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("analType2", SDConstants.ANAL_MD_KEY);
            m.put("analType3", mm.getId());
            List<FileIndexPo> afl = arfService.getAnalFiles(m);
            if (afl!=null&&afl.size()>0) {
                for (int i=0; i<(afl.size()>10?10:afl.size()); i++) {
                    FileIndexPo fip = afl.get(i);
                    File f = new File(fip.getPath()+File.separator+fip.getFileName());
                    if (f.isFile()) {
                        Map<String, Double> km = parseJsonFile(f, mm);
                        if (km!=null) keyList.add(km);
                    }
                }
                //根据这些分析结果，分析主键
                Map<String, Integer> _cm = new HashMap<String, Integer>();//计数Map
                if (keyList.size()>0) {
                    //做交集
                    Map<String, Double> intersectionKeyM = keyList.get(0);
                    for (String _k: intersectionKeyM.keySet()) {
                        _cm.put(_k, new Integer("1"));
                    }
                    for (int i=1; i<keyList.size(); i++) {
                        Map<String, Double> _tempM = keyList.get(i);
                        for (String _k: _tempM.keySet()) {
                            if (intersectionKeyM.get(_k)!=null) {
                                intersectionKeyM.put(_k, intersectionKeyM.get(_k)+_tempM.get(_k));
                                if (_cm.get(_k)==null) _cm.put(_k, new Integer(0));
                                else _cm.put(_k, _cm.get(_k)+1);
                            } else intersectionKeyM.remove(_k);
                        }
                        if (intersectionKeyM.size()==0) break;
                    }
                    //取最有可能的主键
                    Double f = new Double(0);
                    if (intersectionKeyM.size()>0) {
                        for (String _k: intersectionKeyM.keySet()) {
                            if (intersectionKeyM.get(_k)>f) {
                                f = intersectionKeyM.get(_k);
                                keyStr = _k;
                            }
                        }
                        if (_cm.get(keyStr)!=null&&_cm.get(keyStr)>3&&((f/_cm.get(keyStr))>1.5)) pkSign = 1; 
                    }
                }
            }
        }
        //修改mm
        String [] ret=null;
        if (keyStr!=null) ret = StringUtils.splitString(keyStr, ",");
        ret = dealKeyWithNoKeyInfo(ret, noKeyInfo);
        if (ret==null) keyStr=null;
        else {
            keyStr="";
            for (String s: ret) keyStr += ","+s;
            keyStr = keyStr.substring(1);
        }
        List<MetadataColumn> updateList = new ArrayList<MetadataColumn>();
        if (keyStr!=null) {
            ret = StringUtils.splitString(keyStr, ",");
            for (String keyCn: ret) {
                for (MetadataColumn mc: mm.getColumnList()) {
                    if (mc.getColumnName().equals(keyCn)) {
                        mc.setPkSign(pkSign);
                        updateList.add(mc);
                    }
                }
            }
        }
        //修改数据库
        if (updateList.size()!=0) {
            Connection conn = null;
            PreparedStatement ps = null;
            boolean autoCommit = false;
            try {
                conn = dataSource.getConnection();
                autoCommit = conn.getAutoCommit();
                conn.setAutoCommit(false);
                ps = conn.prepareStatement("update sa_md_column set pkSign="+pkSign+" where id=?");
                for (MetadataColumn mc: updateList) {
                    ps.setString(1, mc.getId());
                    ps.addBatch();
                }
                ps.executeBatch();
                conn.commit();
            } catch(Exception e) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    throw new Dtal0202CException("无法rollback", e1);
                }
            } finally {
                if (conn!=null) {
                    try {
                        conn.setAutoCommit(autoCommit);
                    } catch (SQLException e) {
                        throw new Dtal0202CException("无法setAutoCommit", e);
                    }
                }
                try { if (ps!=null) {ps.close();ps = null;} } catch (Exception e) {e.printStackTrace();} finally {ps = null;};
                try { if (conn!=null) {conn.close();conn = null;} } catch (Exception e) {e.printStackTrace();} finally {conn = null;};
            }
        }
        //至此，Session中的内容已经做了修改！！！！原因是mm参数是通过指针传过来的
        return ret;
    }

    /*
     * 比较两个字符串数组是否一样
     */
    private boolean twoStringArraySame(String[] sa1, String[] sa2) {
        if (sa1==null&&sa2==null) return true;
        if (sa1==null||sa2==null) return false;
        if (sa1.length!=sa2.length) return false;
        for (String s1: sa1) {
            boolean exsit = false;
            for (String s2: sa2) {
                if (s2.endsWith(s1)) {
                    exsit=true;
                    break;
                }
            }
            if (!exsit) return false;
        }
        return true;
    }

    /*
     * 读取文件内容，今后可能会用到JsonD的功能 
     * @param f 文件信息
     * @param mm 元数据模型，用来判断数据是否合规
     * @return key=字段名、value=可行性的Map
     */
    private Map<String, Double> parseJsonFile(File f, MetadataModel mm) {
        Map<String, Double> ret = null;
        FileInputStream fis = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> analKey = null;
            fis = new FileInputStream(f);
            byte[] b=new byte[fis.available()];
            fis.read(b);
            String jsonS = new String(b);
            analKey = (Map<String, Object>)JsonUtils.jsonToObj(jsonS, Map.class);
            Map<String, Object> _HEAD = (Map<String, Object>)analKey.get("_HEAD");
            String _code = (String)_HEAD.get("_code");
            if (_code.equals(SDConstants.JDC_ANAL_KEY)) {
                Map<String, Object> _data = (Map<String, Object>)analKey.get("_DATA");
                Map<String, Object> _temp = (Map<String, Object>)_data.get("_mdMId");
                String tempStr = (String)_temp.get("value");
                if (!tempStr.equals(mm.getId())) return null;
                List<Map<String, Object>> l = (List<Map<String, Object>>)_data.get("_analResults");
                if (l!=null&&l.size()>0) {
                    ret = new HashMap<String, Double>();
                    for (Map<String, Object> elem: l) {
                        _temp = (Map<String, Object>)elem.get("keyCols");
                        tempStr = (String)_temp.get("value");
                        _temp = (Map<String, Object>)elem.get("rate");
                        Double d = (Double)_temp.get("value");
                        ret.put(tempStr, d);
                    }
                    return ret;
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try { if (fis!=null) fis.close(); } catch(Exception e) {}
        }
    }

    /*
     * 获得元数据定义中的主键信息
     * @param mm 元数据模型
     * @return 主键字符串组成的数组
     */
    private String[] getMMKeys(MetadataModel mm) {
        String keyStr = "";
        for (MetadataColumn mc : mm.getColumnList()) {
            if (mc.isPk()) keyStr += ","+mc.getColumnName();
        }
        if (keyStr.trim().length()==0) return null;
        else return StringUtils.splitString(keyStr.substring(1), ",");
    }

    /*
     * 获得元数据定义中的主键信息
     * @param mm 元数据模型
     * @return 主键字符串组成的数组，第一个元数是主键名称
     */
    private String[] getDbKeys(Connection conn, String tableName, ResultSet rs) {
        String keyStr = "";
        try {
            DatabaseMetaData dbMetaData = conn.getMetaData();
            rs = dbMetaData.getPrimaryKeys(null, null, tableName);
            while (rs.next()) {
                keyStr +=","+rs.getString("COLUMN_NAME"); //列名
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (keyStr.trim().length()==0) return null;
        else return StringUtils.splitString(keyStr.substring(1), ",");
    }

    /*
     * 根据不能作为主键的列信息，调整主键信息，若主键信息出现在“不可作为主键信息”中，则主键信息失效
     * @param keys 可作为主键的列
     * @param noKeyInfo 不可作为主键的信息
     * @return 若keys不在noKeyInfo中，返回keys，否则返回null
     */
    private String[] dealKeyWithNoKeyInfo(String[] keys, Map<String, List<String>> noKeyInfo) {
        if (noKeyInfo!=null&&noKeyInfo.size()>0) {
            for (String noKeyType: noKeyInfo.keySet()) {
                List<String> l = noKeyInfo.get(noKeyType);
                for (String _keyStr: l) {
                    if (twoStringArraySame(keys, StringUtils.splitString(_keyStr, ","))) return null;
                }
            }
        }
        return keys;
    }

    /*
     * 合并不允许作为主键的列，包括读JsonD和写入JsonD
     * @param noKeyInfo 未合并前的“不允许作为主键的列的信息”
     * @param mm 元数据信息
     * @return 合并后的不允许作为主键的列的信息
     */
    private Map<String, List<String>> mergedNoKeyInfo(Map<String, List<String>> noKeyInfo, MetadataModel mm) {
        Map<String, List<String>> noKeyInfo_inFile = null, ret = null;
        Map<String, Object> _m = null;
        //读取JsonD数据
        //从文件系统中得到分析的文件
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("analType2", SDConstants.ANAL_MD_NOKEY);
        m.put("analType3", mm.getId());
        List<FileIndexPo> afl = arfService.getAnalFiles(m);
        FileIndexPo fip = null;
        if (afl!=null&&afl.size()>0) { //若有文件，则按文件处理
            fip = afl.get(0); //只取第一个
            File f = new File(fip.getPath()+File.separator+fip.getFileName());
            _m = parseJsonFile4NoKey(f, mm);
            if (_m!=null||_m.size()==2) noKeyInfo_inFile = (Map<String, List<String>>)_m.get("noKeyInfo");
        }
        //如果传入的“不允许作为主键的列的信息”为空，而从文件中读出的“不允许作为主键的列的信息”存在，则返回从文件中读出的信息
        if ((noKeyInfo==null||noKeyInfo.size()==0)&&(noKeyInfo_inFile!=null&&noKeyInfo_inFile.size()>0)) return noKeyInfo_inFile;
        if (noKeyInfo!=null&&noKeyInfo.size()>0) {
            if (noKeyInfo_inFile==null||noKeyInfo_inFile.size()==0) {//若从文件中未读出信息
                ret = noKeyInfo;
            } else {//需要合并
                boolean canAdd = false;
                ret = noKeyInfo_inFile;
                for (String noKeyType: ret.keySet()) {
                    List<String> inP = noKeyInfo.get(noKeyType);
                    if (inP!=null) {
                        List<String> inF = ret.get(noKeyType);
                        canAdd = true;
                        for (String inPs: inP) {
                            for (String inFs: inF) {
                                if (twoStringArraySame(StringUtils.splitString(inPs, ","),StringUtils.splitString(inFs, ","))) {
                                    canAdd = false;
                                    break;
                                }
                            }
                            //如果可合并
                            if (canAdd) inF.add(inPs);
                        }
                    }
                }
                noKeyInfo.putAll(ret);
                ret = noKeyInfo;
            }
        }
        if (ret==null) return ret;

        //更新文件处理
        //1-得到需要写入的串
        String jsonS = _m==null?null:(String)_m.get("jsonStr");
        JsonD analNoKeyJsonD = null;
        //数据体
        Map<String, Object> _DATA_Map = new HashMap<String, Object>();
        JsonDAtomData _dataElement = new JsonDAtomData("_mdMId", "string", mm.getId());
        _DATA_Map.putAll(_dataElement.toJsonMap());
        _DATA_Map.put("_analResults", ret);
        if (jsonS==null) {
            //组织JsonD，并写入文件
            analNoKeyJsonD = new JsonD();
            //头
            JsonDHead jsonDHead = new JsonDHead();
            jsonDHead.setId(SequenceUUID.getPureUUID());
            jsonDHead.setCode(SDConstants.JDC_ANAL_NOKEY);
            jsonDHead.setCTime(new Date());
            jsonDHead.setDesc("分析元数据["+mm.getTitleName()+"("+mm.getId()+")]不能作为主键的列信息");
            //设置JsonD
            analNoKeyJsonD.set_HEAD(jsonDHead);
            analNoKeyJsonD.set_DATA(_DATA_Map);
            jsonS = analNoKeyJsonD.toJson();
        } else {
            jsonS = jsonS.substring(0, jsonS.indexOf("_DATA"))+"_DATA\":"+JsonUtils.objToJson(_DATA_Map)+"}";
        }
        //2-写入
        if (fip==null) { //需要重新写入
            if (analNoKeyJsonD!=null) {
                //分析结果文件种子设置
                AnalResultFile arfSeed = new AnalResultFile();
                arfSeed.setAnalType(SDConstants.ANAL_MD_NOKEY);
                arfSeed.setSubType(mm.getId());
                arfSeed.setObjType("metadata"); //所分析对象
                arfSeed.setObjId("["+mm.getTitleName()+"("+mm.getId()+")]"); //所分析对象的ID
                arfSeed.setFileNameSeed("METADATA"+File.separator+"nokey"+File.separator+"md_"+mm.getId());
                arfSeed.setJsonDCode(SDConstants.JDC_ANAL_NOKEY);
                arfSeed = (AnalResultFile)arfService.write2FileAsJson(analNoKeyJsonD, arfSeed);
                arfService.saveFile(arfSeed);
            }
        } else { //在已有的情况下写入
            FileOperUtils.write2File(JsonUtils.formatJsonStr(jsonS, null), fip.getPath()+File.separator+fip.getFileName());
        }
        return ret;
    }

    /**
     * 读取f中的信息，并返回不能作为主键的列信息对象，及文件中的Json串
     * @param f 文件
     * @param mm 元数据信息
     * @return 不能作为主键的列信息对象及文件中的Json串
     */
    private Map<String, Object> parseJsonFile4NoKey(File f, MetadataModel mm) {
        Map<String, List<String>> noKeyInfo = null;
        Map<String, Object> ret = new HashMap<String, Object>();
        FileInputStream fis = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            fis = new FileInputStream(f);
            byte[] b=new byte[fis.available()];
            fis.read(b);
            String jsonS = new String(b);
            Map<String, Object> analNoKey = (Map<String, Object>)JsonUtils.jsonToObj(jsonS, Map.class);
            Map<String, Object> _HEAD = (Map<String, Object>)analNoKey.get("_HEAD");
            String _code = (String)_HEAD.get("_code");
            if (_code.equals(SDConstants.JDC_ANAL_NOKEY)) {
                Map<String, Object> _data = (Map<String, Object>)analNoKey.get("_DATA");
                Map<String, Object> _temp = (Map<String, Object>)_data.get("_mdMId");
                String tempStr = (String)_temp.get("value");
                if (!tempStr.equals(mm.getId())) return null;
                noKeyInfo = (Map<String, List<String>>)_data.get("_analResults");
            }
            ret.put("jsonStr", jsonS);
            ret.put("noKeyInfo", noKeyInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try { if (fis!=null) fis.close(); } catch(Exception e) {}
        }
        return ret;
    }
}