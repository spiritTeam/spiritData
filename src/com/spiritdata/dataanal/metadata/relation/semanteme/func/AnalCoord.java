package com.spiritdata.dataanal.metadata.relation.semanteme.func;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Component;

import com.spiritdata.dataanal.exceptionC.Dtal0203CException;
import com.spiritdata.dataanal.exceptionC.DtalCException;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel;
import com.spiritdata.dataanal.metadata.relation.pojo.QuotaColumn;
import com.spiritdata.dataanal.metadata.relation.pojo.QuotaTable;
import com.spiritdata.dataanal.metadata.relation.semanteme.AnalMetadata;
import com.spiritdata.dataanal.metadata.relation.semanteme.func.coord.pojo.CoordRegularBean;
import com.spiritdata.dataanal.metadata.relation.semanteme.func.coord.pojo.SimilarResultBean;
import com.spiritdata.dataanal.metadata.relation.service.MdQuotaService;
import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.jsonD.model.JsonD;

/**
 * 分析元数据的坐标信息
 * 
 * @author yfo
 * 
 */
@Component
public class AnalCoord implements AnalMetadata {
	@Resource
	private MdQuotaService mdQuotaService;

	// 中国范围的经度判断参数JSON，可以考虑从配置文件中读取!!!
	public static final String BAIDU_CHINA_COORDX = "{min:73.508097,max:135.083849,keywords:'x,lon, longitude,jd,jingdu,经度'}";
	// 中国范围的纬度判断参数JSON
	public static final String BAIDU_CHINA_COORDY = "{min:18.164892,max:53.569653,keywords:'y,lat, latitude,jd,weidu,维度'}";

	// X坐标关键字
	public static final String COORD_TYPE_X = "X";
	public static final String COORD_TYPE_Y = "Y";

	// X坐标列参数判断BEAN
	private CoordRegularBean coordXBean;
	// Y坐标列参数判断BEAN
	private CoordRegularBean coordYBean;

	// 暂存的X列名值，可能分析出来的有多个X值列，需要再做进一步的过滤筛选
	private List<SimilarResultBean> colXList = new ArrayList<SimilarResultBean>();
	// 暂存的Y列名值，可能分析出来的有多个Y值列，需要再做进一步的过滤筛选
	private List<SimilarResultBean> colYList = new ArrayList<SimilarResultBean>();

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

		//对每一列进行判断是否为坐标列
		for (QuotaColumn qc : qt.getColQuotaList()) {
			if (qc.getColumn().getColumnType().equals("Double")) {
				// 新生成对X相似度判断结果BEAN
				SimilarResultBean srbx = new SimilarResultBean();
				srbx.setColQuota(qc);
				this.SimilarProcess(qc, coordXBean, srbx);
				//对坐标列分析结果按照相似度值进行排序后插入到列表中 
				this.addASimilarResultBean(colXList, srbx);
				
				//新生成对Y相似度判断结果BEAN
				SimilarResultBean srby = new SimilarResultBean();
				srby.setColQuota(qc);
				this.SimilarProcess(qc, coordYBean, srby);
				this.addASimilarResultBean(colYList, srby);
			}
		}
		        
		// 存入XY列分析结果
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(COORD_TYPE_X, colXList);
		ret.put(COORD_TYPE_Y, colYList);

        //组织JsonD，并写入文件
        JsonD analDictJsonD = new JsonD();
        
        
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
	private void SimilarProcess(QuotaColumn qc, CoordRegularBean coordReg,
			SimilarResultBean similarResult) {
		// 判断最大、最小值
		double min = this.parseDouble(qc.getMin());
		double max = this.parseDouble(qc.getMax());
		float similarRangeVal = 0.0f;
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
		if (this.coordXBean == null) {
			this.coordXBean = (CoordRegularBean) JsonUtils.jsonToObj(
					BAIDU_CHINA_COORDX, CoordRegularBean.class);
		}
		if (this.coordYBean == null) {
			this.coordYBean = (CoordRegularBean) JsonUtils.jsonToObj(
					BAIDU_CHINA_COORDY, CoordRegularBean.class);
		}
	}

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

}
