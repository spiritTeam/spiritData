package com.spiritdata.dataanal.metadata.relation.semanteme.func.coord.pojo;

import com.spiritdata.dataanal.metadata.relation.pojo.QuotaColumn;
import com.spiritdata.framework.core.model.BaseObject;

/**
 * 记录列判断结果 有可能是坐标列
 * 
 * @author yfo
 * 
 */
public class SimilarResultBean extends BaseObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2096634509023261552L;

	private QuotaColumn colQuota; // 指标列

	private CoordRegularBean coordReg; // 坐标判断规则BEAN

	private float similarRangeVal; // 列最大值、最小值在指定坐标范围内的相似度,0-无关；1-相关
	private float similarKeyVal; // 列名所对应关键字串的相似度,0-无关；1-相关
	private float excludeKeyVal; // 列名在其它关键字串的相似度,0-无关；1-相关

	private float similarSortVal; // 相似度排序值,综合前3个值计算出相似值，越接近1越有可能，0-无关；1-相关

	public QuotaColumn getColQuota() {
		return colQuota;
	}

	public void setColQuota(QuotaColumn colQuota) {
		this.colQuota = colQuota;
	}

	public float getSimilarRangeVal() {
		return similarRangeVal;
	}

	public void setSimilarRangeVal(float similarRangeVal) {
		this.similarRangeVal = similarRangeVal;
		this.reCacuSimilarSortVal();
	}

	public float getSimilarKeyVal() {
		return similarKeyVal;
	}

	public void setSimilarKeyVal(float similarKeyVal) {
		this.similarKeyVal = similarKeyVal;
		this.reCacuSimilarSortVal();
	}

	public float getExcludeKeyVal() {
		return excludeKeyVal;
	}

	public void setExcludeKeyVal(float excludeKeyVal) {
		this.excludeKeyVal = excludeKeyVal;
		this.reCacuSimilarSortVal();
	}

	public float getSimilarSortVal() {
		return this.similarSortVal;
	}

	public CoordRegularBean getCoordReg() {
		return coordReg;
	}

	public void setCoordReg(CoordRegularBean coordReg) {
		this.coordReg = coordReg;
	}

	/**
	 * 每设置一个相似度值，都需要重新计算相似度排序值 根据权重计算每种相似度值所占比重，得出综合排序值
	 */
	private void reCacuSimilarSortVal() {
		this.similarSortVal = this.similarRangeVal
				* this.coordReg.getWeightSimilarRangeVal() + this.similarKeyVal
				* this.coordReg.getWeightSimilarKeyVal() + this.excludeKeyVal
				* this.coordReg.getWeightExcludeKeyVal();
	}

}
