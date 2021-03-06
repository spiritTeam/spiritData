package com.spiritdata.dataanal.metadata.relation.semanteme.func;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spiritdata.filemanage.category.ANAL.model.AnalResultFile;
import com.spiritdata.filemanage.category.ANAL.service.AnalResultFileService;
import com.spiritdata.dataanal.SDConstants;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel;
import com.spiritdata.dataanal.metadata.relation.pojo.QuotaColumn;
import com.spiritdata.dataanal.metadata.relation.pojo.QuotaTable;
import com.spiritdata.dataanal.metadata.relation.semanteme.AnalMetadata;
import com.spiritdata.dataanal.metadata.relation.service.MdQuotaService;
import com.spiritdata.jsonD.model.JsonD;
import com.spiritdata.jsonD.model.JsonDAtomData;
import com.spiritdata.jsonD.model.JsonDHead;
import com.spiritdata.dataanal.exceptionC.Dtal0203CException;

/**
 * 分析元数据的字典语义
 * @author wh
 */
@Component
public class AnalDict implements AnalMetadata {
    private final static String dictSemantemeStrs = "班级;类型;类别;型号;分类;type;class";//目前只是中文
    private final static float compressThreshold = 0.7f; //压缩率的阀值，当压缩率大于此值，则认为是字典项目

    @Resource
    private MdQuotaService mdQuotaService;
    @Resource
    private AnalResultFileService arfService;

    /*
     * 判断数值列是否是字典项的列，通过列名称判断
     * @param colName 列名称；
     * @return
     */
    private boolean isDictNumbCol(String colName) {
        String[] flagStrList = dictSemantemeStrs.split(";");
        for (String flagStr: flagStrList) {
            if (colName.toLowerCase().indexOf(flagStr)!=-1) {
                return true;
            }
        }
        return false;
    }
    /**
     * 扫描元数据，分析字典语义，把分析结果写入文件
     *
     * @param md 元数据
     * @param param 扩展参数，若分析需要其他参数，可通过这个参数传入
     * @return Map<String, Object> 一个Map对象，这样能返回更丰富的信息，目前包括分析的结果
     */
    public Map<String, Object> scanMetadata(MetadataModel mm, Map<String, Object> param) {
        if (mm.getColumnList()==null||mm.getColumnList().size()==0) throw new Dtal0203CException("元数据模型信息不包含任何列信息，无法分析！");

        QuotaTable qt = mdQuotaService.getQuotaTable(mm.getTableName(), mm); //获得指标表
        if (qt==null) qt = mdQuotaService.caculateQuota(mm, mm.getTableName());//为空，则重新计算指标
        if (qt.getAllCount()==0) return null;//返回空，表中没有数据，无法分析
        if (qt.getQuotaColList()==null||qt.getQuotaColList().size()==0) return null;

        Map<String, Object> ret  = new HashMap<String, Object>();
        for (QuotaColumn qc: qt.getQuotaColList()) {
            if (qc.getCompressRate()>AnalDict.compressThreshold&&qc.getColumn().getColumnType().equals("String")) {//是字典项
                ret.put(qc.getColumn().getColumnName(), qc.getCompressRate());
            }
            if (qc.getCompressRate()>AnalDict.compressThreshold&&qc.getColumn().getColumnType().equals("Integer")&&isDictNumbCol(qc.getColumn().getTitleName())) {//是字典项
                ret.put(qc.getColumn().getColumnName(), qc.getCompressRate());
            }
        }
        if (ret.size()>0) { //如果没有分析结果，就不写jsonD文件了
            //组织JsonD，并写入文件
            JsonD analDictJsonD = new JsonD();
            //头
            JsonDHead jsonDHead = new JsonDHead();
            jsonDHead.setId(SequenceUUID.getPureUUID());
            jsonDHead.setCode(SDConstants.JDC_ANAL_DICT);
            jsonDHead.setCTime(new Date());
            jsonDHead.setDesc("分析元数据["+mm.getTitleName()+"("+mm.getId()+")]的字典信息");
            //数据体
            Map<String, Object> _DATA_Map = new HashMap<String, Object>();
            JsonDAtomData _dataElement = new JsonDAtomData("_mdMId", "string", mm.getId());
            _DATA_Map.putAll(_dataElement.toJsonMap());
            _DATA_Map.put("_analResults", convertToList(ret));
            //设置JsonD
            analDictJsonD.set_HEAD(jsonDHead);
            analDictJsonD.set_DATA(_DATA_Map);
            //分析结果文件种子设置
            AnalResultFile arfSeed = new AnalResultFile();
            arfSeed.setAnalType(SDConstants.ANAL_MD_DICT); //分析类型
            arfSeed.setSubType(mm.getId()); //下级分类
            arfSeed.setObjType("metadata"); //所分析对象
            arfSeed.setObjId("["+mm.getTitleName()+"("+mm.getId()+")]"); //所分析对象的ID
            arfSeed.setFileNameSeed("METADATA"+File.separator+"dict"+File.separator+"md_"+mm.getId());
            arfSeed.setJsonDCode(SDConstants.JDC_ANAL_DICT);

            AnalResultFile arf = (AnalResultFile)arfService.write2FileAsJson(analDictJsonD, arfSeed);
            //回写文件信息到返回值
            ret.put("resultFile", arf);
        }
        return ret.size()>0?ret:null;
    }

    private List<Map<String, Object>> convertToList(Map<String, Object> dictAnalResultMap) {
        List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
        if (dictAnalResultMap==null||dictAnalResultMap.size()==0) return ret;

        for (String cols: dictAnalResultMap.keySet()) {
            Map<String, Object> oneEle = new HashMap<String, Object>();
            JsonDAtomData _dataElement = new JsonDAtomData("keyCols", "string", cols);
            oneEle.putAll(_dataElement.toJsonMap());
            _dataElement.setAtomData("rate", "double", dictAnalResultMap.get(cols));
            oneEle.putAll(_dataElement.toJsonMap());
            ret.add(oneEle);
        }
        return ret;
    }
}