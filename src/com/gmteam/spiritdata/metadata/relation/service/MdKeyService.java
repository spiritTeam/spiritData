package com.gmteam.spiritdata.metadata.relation.service;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmteam.framework.FConstants;
import com.gmteam.framework.core.cache.SystemCache;
import com.gmteam.framework.util.FileNameUtils;
import com.gmteam.framework.util.StringUtils;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataColumn;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;

/**
 * 元数据主键处理服务
 * @author wh
 */
@Component
public class MdKeyService {
    @Resource
    private BasicDataSource dataSource;
    @Resource
    private MdBasisService mdBasisService;

    /**
     * 分析元数据的key，并返回最有可能作为key的列组合。
     * 同时，此方法还修改了元数据的主键信息，包括传入的参数MM和数据库的持久化信息，但注意，不调整主键信息
     * @param mdMId 元数据模式Id
     * @return 最有可能作为key的列组合，用列名称(String类型)的数组来表示
     * @throws Exception
     */
    public String[] analMdKey(String mdMId) throws Exception {
        MetadataModel mm = mdBasisService.getMetadataMode(mdMId);
        return analMdKey(mm);
    }

    /**
     * 分析元数据的key，并返回最有可能作为key的列组合。
     * 同时，此方法还修改了元数据的主键信息，包括传入的参数MM和数据库的持久化信息，但注意，不调整主键信息
     * @param mm 元数据信息
     * @return 最有可能作为key的列组合，用列名称(String类型)的数组来表示
     * @throws Exception
     */
    public String[] analMdKey(MetadataModel mm) throws Exception {
        if (mm.getColumnList()==null||mm.getColumnList().size()==0) return null;
        //读取元数据信息，看是否需要对主键进行分析
        String keyStr = null; //主键串，若分析后无主键，此变量为null
        int pkSign = 2; //主键可能性
        int _maxFileCount = 10;//最多只分析近10个文件

        if (needAnalKey(mm)) {//若需要分析主键
            List<Map<String, Double>> keyList = new ArrayList<Map<String, Double>>();
            String root = (String)(SystemCache.getCache(FConstants.APPOSPATH)).getContent();
            //文件格式：analData\{用户名}\MM_{模式Id}\keyAnal\tab_{TABId}.json
            String dirStr = FileNameUtils.concatPath(root, "analData"+File.separator+mm.getOwnerId()+File.separator+"MM_"+mm.getId()+File.separator+"keyAnal"+File.separator);
            File dir = new File(dirStr);
            File[] fl = dir.listFiles();
            if (fl!=null&&fl.length>0) {
                Arrays.sort(fl, new Comparator<File>(){
                    @Override
                    public int compare(File f1, File f2) {
                        long diff = f1.lastModified()-f2.lastModified();
                        if(diff>0) return 1;  
                        else if(diff==0) return 0;  
                        else return -1;
                    }
                });
                for (int i=fl.length-1; i>=0; i--) {
                    if (_maxFileCount==0) break;
                    File f = fl[i];
                    if (f.isFile()) {
                        Map<String, Double> km = parseJsonFile(f, mm);
                        if (km!=null) keyList.add(km);
                        _maxFileCount--;
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
                conn.rollback();
            } finally {
                if (conn!=null) conn.setAutoCommit(autoCommit);
                try { if (ps!=null) {ps.close();ps = null;} } catch (Exception e) {e.printStackTrace();} finally {ps = null;};
                try { if (conn!=null) {conn.close();conn = null;} } catch (Exception e) {e.printStackTrace();} finally {conn = null;};
            }
        }
        //TODO 由于修改了元数据，所有要通知session，修改相关的信息
        return ret;
    }

    /**
     * 调整元数据主键
     * 若元数据主键是不确定的，本方法还会自动调用主键的分析方法。
     * @param mdMId 元数据模式Id
     * @throws Exception
     */
    public void adjustMdKey(String mdMId) throws Exception {
        MetadataModel mm = mdBasisService.getMetadataMode(mdMId);
        adjustMdKey(mm);
    }

    /**
     * 调整元数据主键。
     * 若元数据主键是不确定的，本方法还会自动调用主键的分析方法。
     * @param mm 元数据信息
     * @throws Exception
     */
    public void adjustMdKey(MetadataModel mm) throws Exception {
        if (mm.getColumnList()==null||mm.getColumnList().size()==0) return ;
        
        //读取元数据信息，看主键是否是确定的
        String[] keys = (needAnalKey(mm))?analMdKey(mm):null;
        //不管主键是否确定，下面都对主键进行调整
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

    private Map<String, Double> parseJsonFile(File f, MetadataModel mm) {
        Map<String, Double> ret = null;
        FileInputStream fis = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> analKey = null;
            fis = new FileInputStream(f);
            byte[] b=new byte[fis.available()];
            fis.read(b);
            analKey = mapper.readValue((new String(b)).getBytes("utf-8"), Map.class);
            String _code = (String)analKey.get("_code");
            if (_code.equals("SD.TEAM.ANAL-0001")) {
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