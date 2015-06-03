package com.spiritdata.dataanal.analApp.report.pojo;

import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.spiritdata.framework.core.model.BaseObject;
import com.spiritdata.framework.util.DateUtils;

/**
 * 报告BEAN
 * @author yfo
 *
 */
public class ReportViewPo extends BaseObject {
	private static Logger logger = Logger.getLogger(ReportViewPo.class);

	private static final long serialVersionUID = 9171780470095084128L;
	
	private String id; //报告ID
	
	private String fileId; //报告JSON文件的ID
	
	private String ownerId; //用户ID或SESSION ID
	
	private Integer ownerType; //用户类型		
			
	private String reportType; //报告分类
	
	private String reportName; //报告名称
	
	private String descn; //报告描述
	
	private Timestamp createTime; //创建时间

	private String createTimeStr;//创建时间字符串
	
	private boolean unRead; //是否未读过
	
	private String thumbUrl;//缩略图显示的图片
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public Integer getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(Integer ownerType) {
		this.ownerType = ownerType;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getDescn() {
		return descn;
	}

	public void setDescn(String descn) {
		this.descn = descn;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
		try{
			this.createTimeStr = DateUtils.convert2TimeChineseStr(createTime);
//			this.createTimeStr = DateUtils.convert2LocalStr("yyyy-MM-dd HH:mm:ss", createTime);
		}catch(Exception ex){
			logger.error("failed to conver timestamp to str. timestamp="+createTime,ex);
		}
	}

	public String getCreateTimeStr() {
		return createTimeStr;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public boolean isUnRead() {
		return unRead;
	}

	public void setUnRead(boolean unRead) {
		this.unRead = unRead;
	}

	public String getThumbUrl() {
		return thumbUrl;
	}

	public void setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}
	
}
