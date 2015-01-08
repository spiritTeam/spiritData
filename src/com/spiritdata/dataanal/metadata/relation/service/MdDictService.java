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
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spiritdata.filemanage.core.persistence.pojo.FileIndexPo;
import com.spiritdata.filemanage.core.service.FileManageService;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.jsonD.util.JsonUtils;
import com.spiritdata.dataanal.SDConstants;
import com.spiritdata.dataanal.exceptionC.Dtal0202CException;
import com.spiritdata.dataanal.exceptionC.Dtal0203CException;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataColumn;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel;
import com.spiritdata.dataanal.metadata.relation.semanteme.func.AnalDict;

/**
 * 元数据字典项处理服务
 * @author wh
 */
@Component
public class MdDictService {
    @Resource
    private BasicDataSource dataSource;
    @Resource
    private MdBasisService mdBasisService;
    @Resource
    private FileManageService fmService;

    /**
     * 调整元数据字典项
     * @param mdMId 元数据模式Id
     */
    public void adjustMdDict(String mdMId) {
        MetadataModel mm;
        try {
            mm = mdBasisService.getMetadataMode(mdMId);
        } catch (Exception e) {
            throw new Dtal0203CException("无法根据["+mdMId+"]得到元数据信息", e);
        }
        adjustMdDict(mm);
    }

    /**
     * 调整元数据主键。
     * 若元数据主键是不确定的，本方法还会自动调用主键的分析方法。
     * @param mm 元数据信息
     * @throws Exception
     */
    public void adjustMdDict(MetadataModel mm) {
        if (mm.getColumnList()==null||mm.getColumnList().size()==0) throw new Dtal0203CException("元数据模型信息不包含任何列信息，无法分析！");
        
        //从新的文件系统中得到分析的文件
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("analType2", SDConstants.ANAL_MD_DICT);
        m.put("analType3", mm.getId());
        List<FileIndexPo> afl = fmService.getAnalFiles(m);
        if (afl!=null&&afl.size()>0) {
            for (int i=0; i<(afl.size()>10?10:afl.size()); i++) {
                FileIndexPo fip = afl.get(i);
                File f = new File(fip.getPath()+File.separator+fip.getFileName());
                if (f.isFile()) {
                    //Map<String, Double> km = parseJsonFile(f, mm);
                    //if (km!=null) keyList.add(km);
                }
            }
        }
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
            if (_code.equals(AnalDict.jsonDCode)) {
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