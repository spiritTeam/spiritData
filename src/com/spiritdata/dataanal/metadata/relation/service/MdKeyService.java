package com.spiritdata.dataanal.metadata.relation.service;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spiritdata.filemanage.ANAL.service.AanlResultFileService;
import com.spiritdata.filemanage.core.persistence.pojo.FileIndexPo;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.jsonD.util.JsonUtils;
import com.spiritdata.dataanal.SDConstants;
import com.spiritdata.dataanal.exceptionC.Dtal0202CException;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataColumn;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel;
import com.spiritdata.dataanal.metadata.relation.semanteme.func.AnalKey;

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
    private AanlResultFileService arfService;

    /**
     * 调整元数据主键。
     * 若元数据主键是不确定的，本方法还会自动调用主键的分析方法。
     * @param mm 元数据信息，注意，这里的元数据信息必须是全的，包括column和语义
     */
    public void adjustMdKey(MetadataModel mm) {
        if (mm.getColumnList()==null||mm.getColumnList().size()==0) return ;
        
        //读取元数据信息，看主键是否是确定的
        String[] keys = (needAnalKey(mm))?analMdKey(mm):null;
        //不管主键是否确定，下面都对主键进行调整
        //以下调整是对实体表(积累表)进行调整的
        String keyStr = "";
        if (keys==null) {
            for (MetadataColumn mc : mm.getColumnList()) {
                if (mc.isPk()) keyStr += ","+mc.getColumnName();
            }
            if (keyStr.equals("")) keys=null;
            else keys = StringUtils.splitString(keyStr, ",");
        } else {
            for (int i=0; i<keys.length; i++) {
                keyStr +="," + keys[i];
            }
        }
        keyStr = keys==null?"":keyStr.substring(1);
        if (keys==null||keys[0].equals("")) return ;
        //看目前元数据积累表是否有主键，若有取出(注意这里是从关系型数据库的系统管理信息[metadata]中得到主键)
        String sumTableName = mm.getTableName();
        if (sumTableName==null||sumTableName.equals("")) return ;
        //读取关系型数据元数据
        Connection conn = null;
        ResultSet rs = null;
        Statement st = null;
        try {
            String[] _tabKeys;
            String _tabKeyStr = "";
            String _tabPkName = null;

            conn = dataSource.getConnection();
            st = conn.createStatement();

            DatabaseMetaData dbMetaData = conn.getMetaData();
            rs = dbMetaData.getPrimaryKeys(null, null, sumTableName);
            while (rs.next()){
                _tabKeyStr +=","+rs.getString("COLUMN_NAME"); //列名
                if (_tabPkName==null) _tabPkName = rs.getString("PK_NAME");//主键名称
            }
            if (_tabKeyStr.equals("")) {
                _tabKeys=null;
            } else {
                _tabKeyStr = _tabKeyStr.substring(1);
                _tabKeys = StringUtils.splitString(_tabKeyStr, ",");
            }
            //检查是否需要创建主键
            boolean needCreateKey = true;
            //若有主键，比较主键是否和mm中主键一致
            if (_tabKeys!=null&&!_tabKeys[0].equals("")) {
                if (!twoStringArraySame(_tabKeys, keys)) {//若不相同，删除原来的主键
                    st.execute("ALTER TABLE "+sumTableName+" DROP PRIMARY KEY");
                } else needCreateKey = false;
            }
            //按照metadata中的内容创建主键
            if (needCreateKey) {
                st.execute("Alter table "+sumTableName+" add primary key("+keyStr+")");
            }
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
     * 分析元数据的key，并返回最有可能作为key的列组合。
     * 同时，此方法还修改了元数据的主键信息，包括传入的参数MM和数据库的持久化信息，但注意，不调整主键信息。
     * 注意：此方法不修改实体表的主键信息，只修改自定义的元数据结构中的信息
     * @param mm 元数据信息
     * @return 最有可能作为key的列组合，用列名称(String类型)的数组来表示
     * @throws Exception
     */
    private String[] analMdKey(MetadataModel mm) {
        if (mm.getColumnList()==null||mm.getColumnList().size()==0) return null;
        //读取元数据信息，看是否需要对主键进行分析
        String keyStr = null; //主键串，若分析后无主键，此变量为null
        int pkSign = 2; //主键可能性

        if (needAnalKey(mm)) {//若需要分析主键
            List<Map<String, Double>> keyList = new ArrayList<Map<String, Double>>();
            //从新的文件系统中得到分析的文件
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
                            } else {
                                intersectionKeyM.remove(_k);
                            }
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
                        if (_cm.get(keyStr)!=null) {
                            if (_cm.get(keyStr)>3&&((f/_cm.get(keyStr))>1.5)) pkSign = 1; 
                        }
                    }
                }
            }
        }
        //修改mm
        String [] ret=null;
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
     * @param f
     * @param mm
     * @return
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
            if (_code.equals(AnalKey.jsonDCode)) {
                Map<String, Object> _data = (Map<String, Object>)analKey.get("_DATA");
                Map<String, Object> _temp = (Map<String, Object>)_data.get("_mdMId");
                String tempStr = (String)_temp.get("value");
                if (!tempStr.equals(mm.getId())) return null;
                List<Map<String, Object>> l = (List<Map<String, Object>>)_data.get("_keyAnals");
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
            try {
                if (fis!=null) fis.close();
            } catch(Exception e) {
            }

        }
    }
}