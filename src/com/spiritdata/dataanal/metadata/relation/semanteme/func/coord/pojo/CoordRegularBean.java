package com.spiritdata.dataanal.metadata.relation.semanteme.func.coord.pojo;

import java.util.ArrayList;
import java.util.List;

import com.spiritdata.framework.core.model.BaseObject;
import com.spiritdata.framework.util.StringUtils;

/**
 * 存储坐标列判断的JSON字串所对应的BEAN JSON串可能从配置文件中读取，记录的判断的规则和依据
 * 
 * @author yfo
 * 
 */
public class CoordRegularBean extends BaseObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2542756493739038134L;

	/**
	 * 坐标值范围的最小值
	 */
	private double min;
	/**
	 * 坐标值范围的最大值
	 */
	private double max;

	/**
	 * 范围相似度权重值
	 */
	private float weightSimilarRangeVal;
	/**
	 * 关键字相似度权重值
	 */
	private float weightSimilarKeyVal;
	/**
	 * 在其它种类(非本类)中关键字相似度权重值
	 */
	private float weightExcludeKeyVal;
	/**
	 * 关键字串，该列常用的命名关键字，以逗号隔开
	 */
	private String keywords;
	/**
	 * 关键字串split(",")分割后的关键字列表
	 */
	private List<String> keywordsArr;

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public float getWeightSimilarRangeVal() {
		cacuWeight();
		return weightSimilarRangeVal;
	}

	public void setWeightSimilarRangeVal(float weightSimilarRangeVal) {
		this.weightSimilarRangeVal = weightSimilarRangeVal;
	}

	public float getWeightSimilarKeyVal() {
		cacuWeight();
		return weightSimilarKeyVal;
	}

	public void setWeightSimilarKeyVal(float weightSimilarKeyVal) {
		this.weightSimilarKeyVal = weightSimilarKeyVal;
	}

	public float getWeightExcludeKeyVal() {
		cacuWeight();
		return weightExcludeKeyVal;
	}

	public void setWeightExcludeKeyVal(float weightExcludeKeyVal) {
		this.weightExcludeKeyVal = weightExcludeKeyVal;
	}

	/**
	 * 计算权重值分配情况，如果没分配值，则均分 获取权重值前需要调用此方法 当有新的权重值引入的时候，需要同步修改此方法！！！
	 */
	private void cacuWeight() {
		if (this.weightSimilarRangeVal == 0.0f
				&& this.weightSimilarKeyVal == 0.0f
				&& this.weightExcludeKeyVal == 0.0f) {
			int weightCount = 3; // 有几个权重值，就设置成几
			float aveageVal = 1 / weightCount;
			this.weightSimilarRangeVal = aveageVal;
			this.weightSimilarKeyVal = aveageVal;
			this.weightExcludeKeyVal = aveageVal;
		}
	}

	/**
	 * 获得关键字串数组
	 * 
	 * @return
	 */
	public List<String> getKeywordsArr() {
		if (this.keywordsArr != null) {
			return this.keywordsArr;
		}

		this.keywordsArr = new ArrayList<String>();

		// 关键字符串为空
		if (StringUtils.isNullOrEmptyOrSpace(this.getKeywords())) {
			return this.keywordsArr;
		}

		String[] strs = this.getKeywords().split(",");
		for (String astr : strs) {
			this.keywordsArr.add(astr.trim().toUpperCase());
			// this.keywordsArr.add(astr.trim());
		}

		return keywordsArr;
	}

	/**
	 * 检查所给的范围是否被包含在最大、最小值范围内
	 * 
	 * @param dval
	 * @return
	 */
	public float similarRange(double min, double max) {
		float similarRangeVal = 0.0f;

		if (this.getMin() <= min && min <= this.getMax()) { // 如果最小值在范围内
			if (this.getMin() <= max && max <= this.getMax()) { // 如果最大值也在范围内，则肯定包含
				similarRangeVal = 1.0f;
			} else { // 最小值在范围内，最大值不在范围内
				similarRangeVal = 0.3f;
			}
		} else if (this.getMin() <= max && max <= this.getMax()) {// 最小值不在范围内，最大值在范围内
			similarRangeVal = 0.3f;
		}

		return similarRangeVal;
	}

	/**
	 * 判断指定的字符串和关键字符串的相似度
	 * 1、如果关键字符串为空，则认为无需判断关键字串，直接返回1，不能返回0，否则改字符串会被因为不相符而抛弃掉，导致分析出错；
	 * 2、如果字串和关键字串完全相同，则返回1； 3、如果有包含关系，则根据长度算相似度，minlen/maxlen；
	 * 
	 * @param str
	 * @return
	 */
	public float similarKey(String str) {
		float similarKeyVal = 0.0f; // 相似度
		// 如果给的字符串为空，则相似度=0
		if (StringUtils.isNullOrEmptyOrSpace(str)) {
			return similarKeyVal;
		}
		str = str.trim().toUpperCase();
		// 如果关键字串为空，则相似度=1
		if (this.getKeywordsArr().size() == 0) {
			similarKeyVal = 1.0f;
			return similarKeyVal;
		}

		for (String akey : this.getKeywordsArr()) {
			if (str.equals(akey)) { // 完全相同
				similarKeyVal = 1.0f;
				break;
			}
			// 判断相似度,目前根据长度计算
			float asimilar = 0.0f;
			int strlen = str.length();
			int keylen = akey.length();
			if (strlen > keylen) {
				if (str.indexOf(akey) > -1) {
					asimilar = keylen / strlen;
				}
			} else if (akey.indexOf(str) > -1) {
				asimilar = strlen / keylen;
			}

			// 如果此轮比较的相似度比之前的大，则保留最大的
			if (similarKeyVal < asimilar) {
				similarKeyVal = asimilar;
			}
		}
		return similarKeyVal;
	}

}
