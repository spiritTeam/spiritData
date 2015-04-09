package com.spiritdata.dataanal.metadata.relation.semanteme.func;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Component;

import com.spiritdata.dataanal.SDConstants;
import com.spiritdata.dataanal.exceptionC.Dtal0203CException;
import com.spiritdata.dataanal.exceptionC.DtalCException;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel;
import com.spiritdata.dataanal.metadata.relation.pojo.QuotaColumn;
import com.spiritdata.dataanal.metadata.relation.pojo.QuotaTable;
import com.spiritdata.dataanal.metadata.relation.semanteme.AnalMetadata;
import com.spiritdata.dataanal.metadata.relation.semanteme.func.coord.pojo.CoordRegularBean;
import com.spiritdata.dataanal.metadata.relation.semanteme.func.coord.pojo.SimilarResultBean;
import com.spiritdata.dataanal.metadata.relation.service.MdQuotaService;
import com.spiritdata.filemanage.category.ANAL.model.AnalResultFile;
import com.spiritdata.filemanage.category.ANAL.service.AanlResultFileService;
import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.jsonD.model.JsonD;
import com.spiritdata.jsonD.model.JsonDAtomData;
import com.spiritdata.jsonD.model.JsonDHead;

/**
 * 分析元数据列的坐标信息，记录哪些列可能是X坐标列、哪些列可能是Y坐标列
 * 每次分析后会生成一个元数据坐标列分析结果文件
 * 文件存储位置：WebContent/analData/METADATA/coord
 * 文件命名规则：md_metaTableModelId_当前时间的长整形.json  其中metaTableModelId:SD_MD_TABMODULE 指标表的ID
 * @author yfo
 * 
 */
@Component
public class AnalCoord implements AnalMetadata {
	/**
	 * 相似度值分析结果通过线值
	 * 当相似度值高于通过线值时，才会写入元数据列分析结果文件中
	 */
	public static final float ANAL_COORD_SIMILAR_PASS_VAL = 0.4f;
	
	@Resource
	private MdQuotaService mdQuotaService; //元数据指标列读取服务
    @Resource
    private AanlResultFileService arfService; //分析结果文件存取服务

	// 中国范围的经度判断参数JSON，可以考虑从配置文件中读取,放到param里传到此类进行分析!!! 所有的列判断规则JSON串最好是统一放在一个列规则判断配置文件中
	public static final String BAIDU_CHINA_COORDX = "{\"min\":73.508097,\"max\":135.083849,\"weightSimilarRangeVal\":0.5,\"keywords\":\"x,lon,lng,long,longitude,jd,jingdu,经度\",\"weightSimilarKeyVal\":0.5,\"weightExcludeKeyVal\":0}";
	// 中国范围的纬度判断参数JSON                                                                           
	public static final String BAIDU_CHINA_COORDY = "{\"min\":18.164892,\"max\":53.569653,\"weightSimilarRangeVal\":0.5,\"keywords\":\"y,lat,latitude,wd,weidu,维度\",\"weightSimilarKeyVal\":0.5,\"weightExcludeKeyVal\":0}";

	// X坐标关键字
//	public static final String COORD_TYPE_X = "X";
//	public static final String COORD_TYPE_Y = "Y";

	// X坐标列参数判断BEAN
	private CoordRegularBean coordXBean;
	// Y坐标列参数判断BEAN
	private CoordRegularBean coordYBean;

//	// 暂存的X列名值，可能分析出来的有多个X值列，需要再做进一步的过滤筛选
//	private List<SimilarResultBean> colXList;
//	// 暂存的Y列名值，可能分析出来的有多个Y值列，需要再做进一步的过滤筛选
//	private List<SimilarResultBean> colYList;

	@Override
	public Map<String, Object> scanMetadata(MetadataModel mm,
			Map<String, Object> param) throws DtalCException {
		// TODO Auto-generated method stub
		if (mm.getColumnList() == null || mm.getColumnList().size() == 0)
			throw new Dtal0203CException("元数据模型信息不包含任何列信息，无法分析！");

		QuotaTable qt = mdQuotaService.getQuotaInfo(mm.getTableName(), mm); // 获得指标表
		if (qt == null)
			qt = mdQuotaService.caculateQuota(mm, mm.getTableName());// 为空，则重新计算指标
		if (qt.getAllCount() == 0)
			return null;// 返回空，表中没有数据，无法分析
		if (qt.getColQuotaList() == null || qt.getColQuotaList().size() == 0)
			return null;

		this.initCoordBean();
		if (this.coordXBean == null || this.coordYBean == null) {
			return null;
		}

		List<SimilarResultBean> colXList = new ArrayList<SimilarResultBean>();
		List<SimilarResultBean> colYList = new ArrayList<SimilarResultBean>();		
		//对每一列进行判断是否为坐标列
		for (QuotaColumn qc : qt.getColQuotaList()) {
			if (qc.getColumn().getColumnType().equals("Double")) {
				// 新生成对X相似度判断结果BEAN
				SimilarResultBean srbx = new SimilarResultBean();
				srbx.setColQuota(qc);
				srbx.setCoordReg(this.coordXBean);
				this.SimilarProcess(qc, srbx);
				//对坐标列分析结果按照相似度值进行排序后插入到列表中 
				this.addASimilarResultBean(colXList, srbx);
				
				//新生成对Y相似度判断结果BEAN
				SimilarResultBean srby = new SimilarResultBean();
				srby.setColQuota(qc);
				srby.setCoordReg(this.coordYBean);
				this.SimilarProcess(qc, srby);
				this.addASimilarResultBean(colYList, srby);
			}
		}
		        
		// 存入XY列分析结果
		Map<String, Object> ret = new HashMap<String, Object>();

        //组织JsonD，并写入文件
        JsonD analCoordJsonD = new JsonD();
        //头
        JsonDHead jsonDHead = new JsonDHead();
        jsonDHead.setId(SequenceUUID.getPureUUID());
        jsonDHead.setCode(SDConstants.JDC_ANAL_COORD);
        jsonDHead.setCTime(new Date());
        jsonDHead.setDesc("分析元数据["+mm.getTitleName()+"("+mm.getId()+")]的坐标信息");
        //数据体
        Map<String, Object> _DATA_Map = new HashMap<String, Object>();
        JsonDAtomData _dataElement = new JsonDAtomData("_mdMId", "string", mm.getId());
        _DATA_Map.putAll(_dataElement.toJsonMap());

        //读取X列分析列结果 
        Map<String,List<Map<String,Object>>> colJsonMap = new LinkedHashMap<String,List<Map<String,Object>>>();
        List<SimilarResultBean> colXPassList = filterPassValcolList(colXList,ANAL_COORD_SIMILAR_PASS_VAL);
        List<Map<String,Object>> colXJsonList = colList2JsonStr(colXPassList);
        colJsonMap.put("xCols", colXJsonList);
        //读取Y列分析结果
        List<SimilarResultBean> colYPassList = filterPassValcolList(colYList,ANAL_COORD_SIMILAR_PASS_VAL);
        List<Map<String,Object>> colYJsonList = colList2JsonStr(colYPassList);
        colJsonMap.put("yCols", colYJsonList);

        _DATA_Map.put("_analResults", colJsonMap);
        //设置JsonD
        analCoordJsonD.set_HEAD(jsonDHead);
        analCoordJsonD.set_DATA(_DATA_Map);
        //分析结果文件种子设置
        AnalResultFile arfSeed = new AnalResultFile();
        arfSeed.setAnalType(SDConstants.ANAL_MD_COORD); //分析类型
        arfSeed.setSubType(mm.getId()); //下级分类
        arfSeed.setObjType("metadata"); //所分析对象
        arfSeed.setObjId("["+mm.getTitleName()+"("+mm.getId()+")]"); //所分析对象的ID
        arfSeed.setFileNameSeed("METADATA"+File.separator+"coord"+File.separator+"md_"+mm.getId()+"_"+new Date().getTime());
        arfSeed.setJsonDCode(SDConstants.JDC_ANAL_COORD);

        AnalResultFile arf = (AnalResultFile)arfService.write2FileAsJson(analCoordJsonD, arfSeed);
        //回写文件信息到返回值
        ret.put("resultFile", arf);
        
        
		return ret;
	}

	/**
	 * 对一个指标列的相似度判断处理
	 * 
	 * @param qc
	 *            指标列
	 * @param coordReg
	 *            坐标判断规则BEAN
	 * @param srb
	 *            相似度判断结果BEAN
	 */
	private void SimilarProcess(QuotaColumn qc,	SimilarResultBean similarResult) {
		// 判断最大、最小值
		double min = this.parseDouble(qc.getMin());
		double max = this.parseDouble(qc.getMax());
		float similarRangeVal = 0.0f;
		CoordRegularBean coordReg = similarResult.getCoordReg();
		if (min != Double.MIN_NORMAL && max != Double.MIN_NORMAL) { //如果最大、最小值范围不为空
			similarRangeVal = coordReg.similarRange(min, max);
		}
		similarResult.setSimilarRangeVal(similarRangeVal);
		
		//判断列名是否包含在关键字串规则中
		String colTitleName = qc.getColumn().getTitleName();
		float similarKeyVal = coordReg.similarKey(colTitleName);
		similarResult.setSimilarKeyVal(similarKeyVal);
		
		//判断关键字是否包含在其它种类的关键字串规则中,目前不实现!!!
				
	}
	
	/**
	 * 对坐标列分析结果按照相似度值进行排序后插入到列表对应位置中
	 * 相似度值越大越靠前 
	 * @param colList
	 * @param srb
	 */
	private void addASimilarResultBean(List<SimilarResultBean> colSimularList,SimilarResultBean srb){
		float srb_similarSortVal = srb.getSimilarSortVal();
		int colCount = colSimularList.size();
		int idx = colCount;
		for(int i=0;i<colCount;i++){
			SimilarResultBean asrb = (SimilarResultBean)colSimularList.get(i);
			float asrb_similarSortVal = asrb.getSimilarSortVal();
			if(srb_similarSortVal > asrb_similarSortVal){
				idx = i;
				break;
			}
		}
		colSimularList.add(idx, srb);
	}

	/**
	 * 初始化坐标列判断BEAN，将参数JSON字符串转换为COORDBEAN
	 */
	private void initCoordBean() {
//		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		if (this.coordXBean == null) {
			this.coordXBean = (CoordRegularBean) JsonUtils.jsonToObj(
					BAIDU_CHINA_COORDX, CoordRegularBean.class);
		}
		if (this.coordYBean == null) {
			this.coordYBean = (CoordRegularBean) JsonUtils.jsonToObj(
					BAIDU_CHINA_COORDY, CoordRegularBean.class);
		}
	}
	
//	public static void main(String[] args){
//		AnalCoord ac = new AnalCoord();
//		ac.initCoordBean();
//	}

	/**
	 * 解析数值型字符串
	 * 
	 * @param str
	 * @return
	 */
	private double parseDouble(String str) {
		double ret = Double.MIN_NORMAL;
		if (!StringUtils.isNullOrEmptyOrSpace(str)) {
			try {
				ret = Double.parseDouble(str);
			} catch (Exception ex) {
				ex.printStackTrace(); // 这种异常如何处理，用现有的还是写新的???
			}
		}
		return ret;
	}

	/**
	 * 将坐标分析结果MAP转换成LIST
	 * @param coordAnalResultMap
	 * @return
	 */
//    private Map<String,List<Map<String,Object>>> colsListToJsonMap() {        
//        Map<String,List<Map<String,Object>>> ret = new LinkedHashMap<String,List<Map<String,Object>>>();
//        
//        //读取X列分析列结果 
//        List<SimilarResultBean> colXPassList = filterPassValcolList(colXList,ANAL_COORD_SIMILAR_PASS_VAL);
//        List<Map<String,Object>> colXJsonList = colList2JsonStr(colXPassList);
//        ret.put("xCols", colXJsonList);
//        //读取Y列分析结果
//        List<SimilarResultBean> colYPassList = filterPassValcolList(colYList,ANAL_COORD_SIMILAR_PASS_VAL);
//        List<Map<String,Object>> colYJsonList = colList2JsonStr(colYPassList);
//        ret.put("yCols", colYJsonList);
//
//        return ret;
//    }
    
    /**
     * 根据通过值,过滤出相似度值>通过值的列信息
     * @param colList
     * @param passVal
     * @return
     */
    private List<SimilarResultBean> filterPassValcolList(List<SimilarResultBean> colList, float passVal){
    	List<SimilarResultBean> retList = new ArrayList<SimilarResultBean> ();
    	for(SimilarResultBean asrb : colList){
    		if(asrb.getSimilarSortVal()>=passVal){
    			retList.add(asrb);
    		}else{ //因为之前已经做过了值从大到小的排序，所以如果当前值小于指定的通过值，则其后的值肯定都小
    			break;
    		}
    	}
    	
    	return retList;
    }
    
    /**
     * 将一个colList转换成JSON串
     * @param colList
     * @return
     */
    private List<Map<String,Object>> colList2JsonStr(List<SimilarResultBean> colList){
    	List<Map<String,Object>> retList = new ArrayList<Map<String,Object>>();
    	for(SimilarResultBean asrb : colList){
    		Map<String, Object> oneMap = new LinkedHashMap<String, Object>();
    		oneMap.put("colName", asrb.getColQuota().getColumn().getTitleName());
    		Map<String, Object> oneSilimarValMap = new LinkedHashMap<String, Object>();
    		oneSilimarValMap.put("similarSortVal", asrb.getSimilarSortVal());
    		oneSilimarValMap.put("similarRangeVal", asrb.getSimilarRangeVal());
    		oneSilimarValMap.put("similarKeyVal", asrb.getSimilarKeyVal());
    		oneSilimarValMap.put("excludeKeyVal", asrb.getExcludeKeyVal());
    		oneMap.put("similarVals", oneSilimarValMap);
    		retList.add(oneMap);
    	}
    	
    	return retList;
    }
}
