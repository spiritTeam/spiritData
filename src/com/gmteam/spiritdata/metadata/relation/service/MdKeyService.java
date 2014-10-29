package com.gmteam.spiritdata.metadata.relation.service;

import java.sql.DatabaseMetaData;

import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.stereotype.Component;

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
     * 分析元数据的key
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
     * @param mm 元数据信息
     * @return 最有可能作为key的列组合，用列名称(String类型)的数组来表示
     * @throws Exception
     */
    public String[] analMdKey(MetadataModel mm) throws Exception {
        if (mm.getColumnList()==null||mm.getColumnList().size()==0) return null;
        //读取元数据信息，看是否需要对主键进行分析
        if (needAnalKey(mm)) {//若需要分析主键
            
        }
        //找到对各临时表的分析结果，json文件
        //根据这些分析结果，分析主键
        return null;
    }

    /**
     * 调整元数据主键
     * @param mdMId 元数据模式Id
     * @throws Exception
     */
    public void adjustMdKey(String mdMId) throws Exception {
        MetadataModel mm = mdBasisService.getMetadataMode(mdMId);
        adjustMdKey(mm);
    }

    /**
     * 调整元数据主键
     * @param mm 元数据信息
     * @throws Exception
     */
    public void adjustMdKey(MetadataModel mm) throws Exception {
        if (mm.getColumnList()==null||mm.getColumnList().size()==0) return ;
        
        //读取元数据信息，看主键是否是确定的
        String[] keys = null;
        if (needAnalKey(mm)) { //若不是确定的，分析元数据主键，调用analMdKey
            keys = analMdKey(mm);
        }
        //不管主键是否确定，下面都对主键进行调整
        String keyStrs = "";
        if (keys==null) {
            for (MetadataColumn mc : mm.getColumnList()) {
                if (mc.isPk()) keyStrs += ","+mc.getColumnName();
            }
            if (keyStrs.equals("")) keys=null;
            else {
                keyStrs = keyStrs.substring(1);
                keys = StringUtils.splitString(keyStrs, ",");
            }
        }
        //if (keys==null||keys[0].equals("")) return ;
        //看目前元数据积累表是否有主键，若有取出(注意这里是从关系型数据库的系统管理信息[metadata]中得到主键)
        String sumTableName = mm.getTableName();
        if (sumTableName==null||sumTableName.equals("")) return ;
        //读取关系型数据元数据
        DatabaseMetaData dbMetaData = dataSource.getConnection().getMetaData();
        dbMetaData.getPrimaryKeys(null, "", sumTableName);
      
      //若有主键，比较主键是否和mm中主键一致
      //若一致，调整完成
      //若不一致：删除原有主键，按mm中的主键重新创建主键
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
}