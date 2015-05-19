package com.spiritdata.dataanal.metadata.relation.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.spiritdata.dataanal.SDConstants;
import com.spiritdata.dataanal.exceptionC.Dtal0404CException;
import com.spiritdata.dataanal.metadata.relation.semanteme.SemantemeType;
import com.spiritdata.dataanal.metadata.relation.service.MdBasisService;
import com.spiritdata.dataanal.metadata.relation.service.MdQuotaService;
import com.spiritdata.dataanal.task.process.TaskProcess;
import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.jsonD.model.JsonDAtomData;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataColSemanteme;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataColumn;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel;
import com.spiritdata.dataanal.metadata.relation.pojo.QuotaColumn;
import com.spiritdata.dataanal.metadata.relation.pojo.QuotaTable;
import com.spiritdata.dataanal.metadata.enumeration.DataType;

public class GetMDInfos implements TaskProcess {
    /**
     * 获得一组元数据信息
     */
    @Override
    public Map<String, Object> process(Map<String, Object> param) {
        if (param==null) throw new Dtal0404CException(new NullPointerException("任务执行必须设定参数！"));
        if (param.get("pType")==null)  throw new Dtal0404CException(new NullPointerException("参数中没有“参数类型pType”的数据！"));
        if (!((String)param.get("pType")).toLowerCase().equals("metadatas")) new Dtal0404CException(new IllegalArgumentException("参数中必须指定参数类型pType是元数据id数组[metadatas]"));
        if (param.get("mids")==null)  throw new Dtal0404CException(new NullPointerException("参数中没有“数据id数组”的数据！"));

        ServletContext sc = (ServletContext)SystemCache.getCache(FConstants.SERVLET_CONTEXT).getContent();//通过此获得Spring中定义的对象，这种方法似乎不好

        Map<String, Object> ret = new HashMap<String, Object>();
        Map<String, Object> sysRd = new HashMap<String, Object>();
        sysRd.put("JsonDCode", SDConstants.JDC_MD_INFO);
        ret.put("sysResultData", sysRd);

        //组织为jsond的数据格式
        List<Map<String, Object>> miList = new ArrayList<Map<String, Object>>();//元数据信息的数组
        //获得元数据-包括语义信息，获得元数据的指标(统计)信息
        MdBasisService mdbServcie = (MdBasisService)WebApplicationContextUtils.getWebApplicationContext(sc).getBean("mdBasisService");
        MdQuotaService mdqServcie = (MdQuotaService)WebApplicationContextUtils.getWebApplicationContext(sc).getBean("mdQuotaService");
        String[] ids = StringUtils.splitString(param.get("mids")+"", ",");
        for (int i=0; i<ids.length; i++) {
            MetadataModel mm = mdbServcie.getMetadataModeWithColSemanteme(ids[i]);
            QuotaTable qt = mdqServcie.getMdQuotaInfo(ids[i]);
            if (mm!=null) {
                miList.add(_getJsonDTable_MM(mm, qt));
            }
        }

        if (miList.size()>0) {
            sysRd.put("resultType", 1);
            ret.put("userResultData", miList);

        } else sysRd.put("resultType", 2);

        return ret;
    }

    /*
     * 按jsonD的table格式，得到元数据信息，包括统计信息
     * @param mm 元数据信息
     * @param qt 元数据指标信息
     * @return 转换完的数据
     */
    private Map<String, Object> _getJsonDTable_MM(MetadataModel mm, QuotaTable qt) {
        Map<String, Object> ret = new HashMap<String, Object>();
        JsonDAtomData _dataElement = new JsonDAtomData("_mdMId", "string", mm.getId());
        ret.putAll(_dataElement.toJsonMap());
        _dataElement.setAtomData("_tableName", "string", mm.getTableName());
        ret.putAll(_dataElement.toJsonMap());
        boolean flag = false;
        if (!StringUtils.isNullOrEmptyOrSpace(mm.getTitleName())) {
            _dataElement.setAtomData("_titleName", "string", mm.getTitleName());
            ret.putAll(_dataElement.toJsonMap());
            flag = true;
        }
        if (qt!=null) {//加入总数
            _dataElement.setAtomData("_allCount", "long", qt.getAllCount());
            ret.putAll(_dataElement.toJsonMap());
        }

        //表数据处理
        Map<String, Object> tableM = new HashMap<String, Object>();
        tableM.put("mdTitle", flag?(mm.getTitleName()+"("+mm.getId()+")"):mm.getId()+"的元数据信息");
        //title
        String titleStr = "[{\"titleName\":\"元数据名称\"},{\"columnType\":\"元数据类型\"},{\"columnName\":\"列名称\"},"
            +"{\"semanteme\":\"元数据语义\"},{\"columnIndex\":\"顺序号\"}, {\"range\":\"范围\"}, {\"compressRate\":\"压缩率\"}, {\"sparseRate\":\"稀疏率\"}}]";
        tableM.put("titles", titleStr);
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        //dataList
        tableM.put("dataList", dataList);
        for (MetadataColumn mc: mm.getColumnList()) {
            Map<String, String> rowM = new HashMap<String, String>();
            rowM.put("titleName", mc.getTitleName());
            rowM.put("columnType", DataType.getDataType(mc.getColumnType()).getName());
            rowM.put("columnName", mc.getColumnName());
            //语义列处理
            String tempStr = "";
            if (mc.isPk()) tempStr+=",<semanteme >主键</semanteme>";
            if (mc.getColSemList()!=null&&mc.getColSemList().size()>0) {
                for (MetadataColSemanteme mcs: mc.getColSemList()) {
                    if (mcs.getSemantemeType()==SemantemeType.DICT) { //字典
                        tempStr+=",<semanteme semType='"+mcs.getSemantemeType()+"' semCode='"+mcs.getSemantemeCode()+"'>字典</semanteme>";
                    } else if (mcs.getSemantemeType()==SemantemeType.PERSON_IDENTIFY) { //
                        tempStr+=",<semanteme semType='"+mcs.getSemantemeType()+"' semCode='"+mcs.getSemantemeCode()+"'>身份证</semanteme>";
                    } else if (mcs.getSemantemeType()==SemantemeType.COORD_X) { //X坐标
                        tempStr+=",<semanteme semType='"+mcs.getSemantemeType()+"' semCode='"+mcs.getSemantemeCode()+"'>X坐标</semanteme>";
                    } else if (mcs.getSemantemeType()==SemantemeType.COORD_Y) { //Y坐标
                        tempStr+=",<semanteme semType='"+mcs.getSemantemeType()+"' semCode='"+mcs.getSemantemeCode()+"'>Y坐标</semanteme>";
                    }
                }
            }
            if (tempStr.length()>0) tempStr.substring(1);
            rowM.put("semanteme", tempStr);
            rowM.put("columnIndex", mc.getColumnIndex()+"");

            QuotaColumn qc = qt==null?null:qt.getColQuotaByColId(mc.getId());
            //范围
            tempStr="";
            if (DataType.getDataType(mc.getColumnType())==DataType.STRING) {
                tempStr=(qc==null?"":"长度("+qc.getMin()+"->"+qc.getMax()+")");
            } else {
                tempStr=(qc==null?"":("从("+qc.getMin()+")到("+qc.getMax()+")"));
            }
            rowM.put("range", tempStr);
            //压缩率
            float tempRate = qc==null?0:(Math.round((qc.getCompressRate()*10000)+5)/100);
            rowM.put("compressRate", qc==null?"":(tempRate+"%"));
            //稀疏率
            tempRate = qc==null?0:(Math.round((qc.getNullRate()*10000)+5)/100);
            rowM.put("sparseRate", qc==null?"":(tempRate+"%"));
            dataList.add(rowM);
        }

        ret.put("tableData", tableM);
        return ret;
    }
}