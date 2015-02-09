package com.spiritdata.dataanal.metadata.relation.semanteme.func;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spiritdata.filemanage.ANAL.model.AnalResultFile;
import com.spiritdata.filemanage.ANAL.service.AanlResultFileService;
import com.spiritdata.dataanal.SDConstants;
import com.spiritdata.framework.util.SequenceUUID;

import com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel;
import com.spiritdata.dataanal.metadata.relation.pojo.QuotaColumn;
import com.spiritdata.dataanal.metadata.relation.pojo.QuotaTable;
import com.spiritdata.dataanal.metadata.relation.semanteme.AnalMetadata;
import com.spiritdata.dataanal.metadata.relation.service.MdQuotaService;

import com.spiritdata.jsonD.model.JsonD;
import com.spiritdata.jsonD.model.JsondAtomData;
import com.spiritdata.jsonD.model.JsondHead;

import com.spiritdata.dataanal.exceptionC.Dtal0203CException;

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
    @Resource
    private AanlResultFileService arfService;

    /**
     * 扫描元数据，分析字典语义，把分析结果写入文件
     *
     * @param md 元数据
     * @param param 扩展参数，若分析需要其他参数，可通过这个参数传入
     * @return Map<String, Object> 一个Map对象，这样能返回更丰富的信息
     */
    public Map<String, Object> scanMetadata(MetadataModel mm, Map<String, Object> param) {
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

        //组织JsonD，并写入文件
        JsonD analDictJsond = new JsonD();
        //头
        JsondHead jsondHead = new JsondHead();
        jsondHead.setId(SequenceUUID.getPureUUID());
        jsondHead.setCode(jsonDCode);
        jsondHead.setCTime(new Date());
        jsondHead.setDesc("分析元数据["+mm.getTitleName()+"("+mm.getId()+")]的字典信息");
        //数据体
        Map<String, Object> _DATA_Map = new HashMap<String, Object>();
        JsondAtomData _dataElement = new JsondAtomData("_mdMId", "string", mm.getId());
        _DATA_Map.putAll(_dataElement.toJsonMap());
        _DATA_Map.put("_dictAnals", convertToList(ret));
        //设置JsonD
        analDictJsond.set_HEAD(jsondHead);
        analDictJsond.set_DATA(_DATA_Map);
        //分析结果文件种子设置
        AnalResultFile arfSeed = new AnalResultFile();
        arfSeed.setAnalType(SDConstants.ANAL_MD_DICT); //分析类型
        arfSeed.setSubType(mm.getId()); //下级分类
        arfSeed.setObjType("metadata"); //所分析对象
        arfSeed.setObjId("["+mm.getTitleName()+"("+mm.getId()+")]"); //所分析对象的ID
        arfSeed.setFileNameSeed("METADATA"+File.separator+"dict"+File.separator+"md_"+mm.getId());

        AnalResultFile arf = (AnalResultFile)arfService.write2FileAsJsonD(analDictJsond, arfSeed);
        //回写文件信息到返回值
        ret.put("resultFile", arf);

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