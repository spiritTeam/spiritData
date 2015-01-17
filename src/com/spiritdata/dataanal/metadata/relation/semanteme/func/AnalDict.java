package com.spiritdata.dataanal.metadata.relation.semanteme.func;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spiritdata.dataanal.SDConstants;
import com.spiritdata.dataanal.exceptionC.Dtal0203CException;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel;
import com.spiritdata.dataanal.metadata.relation.pojo.QuotaColumn;
import com.spiritdata.dataanal.metadata.relation.pojo.QuotaTable;
import com.spiritdata.dataanal.metadata.relation.semanteme.AnalMetadata;
import com.spiritdata.dataanal.metadata.relation.service.MdQuotaService;
import com.spiritdata.filemanage.ANAL.model.AnalResultFile;
import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.util.FileNameUtils;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.jsonD.model.JsondAtomData;
import com.spiritdata.jsonD.model.JsondHead;
import com.spiritdata.jsonD.util.JsonUtils;

/**
 * 分析元数据的字典语义
 * @author wh
 */
@Component
public class AnalDict implements AnalMetadata {
    public final static String jsonDCode = "SD.TEAM.ANAL::0002"; 
    private final static float compressThreshold = 0.7f; //压缩率的阀值，当压缩率大于此值，则认为是字典项目

    @Resource
    private MdQuotaService mdQuotaService;

    /**
     * 扫描元数据，分析字典语义，把分析结果写入文件
     */
    public Map<String, Object> scanMetadata(MetadataModel mm, Map<String, Object> param) throws Dtal0203CException {
        if (mm.getColumnList()==null||mm.getColumnList().size()==0) throw new Dtal0203CException("元数据模型信息不包含任何列信息，无法分析！");

        QuotaTable qt = mdQuotaService.getQuotaInfo(mm.getTableName(), mm); //获得指标表
        if (qt==null) qt = mdQuotaService.caculateQuota(mm, mm.getTableName());//为空，则重新计算指标
        if (qt.getAllCount()==0) return null;//返回空，表中没有数据，无法分析
        if (qt.getColQuotaList()==null||qt.getColQuotaList().size()==0) return null;
        
        Map<String, Object> ret  = new HashMap<String, Object>();
        for (QuotaColumn qc: qt.getColQuotaList()) {
            if (qc.getColumn().getColumnType().equals("String")&&qc.getCompressRate()>AnalDict.compressThreshold) {//是字典项
                ret.put(qc.getColumn().getColumnName(), qc.getCompressRate());
            }
        }

        //写jsonD文件，此方法目前为测试方法，今后把他变为一个更好用的包
        JsondHead jsonDHead = new JsondHead();
        jsonDHead.setId(SequenceUUID.getPureUUID());
        jsonDHead.setCode(jsonDCode);
        jsonDHead.setCTime(new Date());
        jsonDHead.setDesc("分析元数据["+mm.getTitleName()+"("+mm.getId()+")]的字典信息");
        //文件名
        String root = (String)(SystemCache.getCache(FConstants.APPOSPATH)).getContent();
        String storeFile = FileNameUtils.concatPath(root, "analData"+File.separator+"METADATA"+File.separator+"dict"+File.separator+"md_"+mm.getId()+".json");
        jsonDHead.setFileName(storeFile.replace("\\", "/"));

        Map<String, Object> _DATA_Map = new HashMap<String, Object>();
        JsondAtomData _dataElement = new JsondAtomData("_mdMId", "string", mm.getId());
        _DATA_Map.putAll(_dataElement.toJsonMap());
        _DATA_Map.put("_dictAnals", convertToList(ret));

        String jsonStr=JsonUtils.formatJsonStr("{"+jsonDHead.toJson()+",\"_DATA\":"+JsonUtils.objToJson(_DATA_Map)+"}", null);

        //写文件
        FileOutputStream fileOutputStream = null;
        try {
            File file = new File(storeFile);
            if (!file.exists()) {
                File dirs = new File(FileNameUtils.getFilePath(storeFile));
                if (!dirs.exists()) dirs.mkdirs();
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(jsonStr.getBytes());
            //回写文件信息到返回值
            AnalResultFile arf = new AnalResultFile();
            arf.setFileName(storeFile);
            arf.setJsonDCode(jsonDCode);
            arf.setAnalType(SDConstants.ANAL_MD_DICT);
            arf.setSubType(mm.getId());
            arf.setObjType("metadata");
            arf.setObjId("["+mm.getTitleName()+"("+mm.getId()+")]");
            ret.put("resultFile", arf);
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

    private List<Map<String, Object>> convertToList(Map<String, Object> dictAnalResultMap) {
        if (dictAnalResultMap==null||dictAnalResultMap.size()==0) return null;

        List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
        for (String cols: dictAnalResultMap.keySet()) {
            Map<String, Object> oneEle = new HashMap<String, Object>();
            JsondAtomData _dataElement = new JsondAtomData("keyCols", "string", cols);
            oneEle.putAll(_dataElement.toJsonMap());
            _dataElement.setAtomData("rate", "double", dictAnalResultMap.get(cols));
            oneEle.putAll(_dataElement.toJsonMap());
            ret.add(oneEle);
        }
        return ret;
    }
}