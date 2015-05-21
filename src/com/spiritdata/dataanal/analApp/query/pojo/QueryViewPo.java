package com.spiritdata.dataanal.analApp.query.pojo;

import java.sql.Timestamp;
import java.util.Map;

import org.apache.log4j.Logger;

import com.spiritdata.framework.core.model.BaseObject;
import com.spiritdata.jsonD.util.JsonUtils;

/**
 * 通用查询结果BEAN
 * @author yfo
 *
 */
public class QueryViewPo extends BaseObject {
	private static Logger logger = Logger.getLogger(QueryViewPo.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -5550828974736998928L;

	private Timestamp createTime; //创建时间
	
	private Map<String, Object> aRowJsonStr; //一行值的JSON字符串

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Map<String, Object> getaRowJsonStr() {
		return aRowJsonStr;
	}

	public void setaRowJsonStr(String aRowJsonStr) {
		this.aRowJsonStr = (Map<String, Object>)JsonUtils.jsonToObj(aRowJsonStr, Map.class);
	}
	
	
}
