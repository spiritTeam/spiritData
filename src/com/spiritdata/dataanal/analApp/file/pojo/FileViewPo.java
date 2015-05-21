package com.spiritdata.dataanal.analApp.file.pojo;

import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.spiritdata.framework.core.model.BaseObject;
import com.spiritdata.framework.util.DateUtils;
import com.spiritdata.framework.util.FileNameUtils;

/**
 * 文件BEAN
 * @author yfo
 *
 */
public class FileViewPo extends BaseObject{
	private static Logger logger = Logger.getLogger(FileViewPo.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 4088944509200826904L;
	
	private String fileIndexId; //sa_file_index文件索引表中的ID
	
	private String fileCategoryId; //sa_file_category文件分类表中的ID
	
	private String clientFileName; //页面显示的文件名称
	
	private String suffix; //文件后缀名
	
	private String fileSize; //文件尺寸
	
	private Timestamp createTime; //创建时间
	
	private String createTimeStr;//创建时间字符串
	
	private String descn; //描述
	
	private String reportId; //文件所对应的即时报告ID，用于查看报告
	
	private String ownerId; //文件所有人ID
	
	private String ownerType; //所有类型
	
	private String tmpTableName; //临时表对应的表名，该表中存储了文件的数据内容
	
	private String tmId; //元数据id

	private String sheetName; //页签名称
	
	public String getFileIndexId() {
		return fileIndexId;
	}

	public void setFileIndexId(String fileIndexId) {
		this.fileIndexId = fileIndexId;
	}

	public String getFileCategoryId() {
		return fileCategoryId;
	}

	public void setFileCategoryId(String fileCategoryId) {
		this.fileCategoryId = fileCategoryId;
	}

	public String getClientFileName() {
		return clientFileName;
	}

	public void setClientFileName(String clientFileName) {
		this.clientFileName = clientFileName;
        this.suffix = FileNameUtils.getExt(clientFileName);
	}

	public String getSuffix() {
		return suffix;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
		try{
			this.createTimeStr = DateUtils.convert2LocalStr("yyyy-MM-dd HH:mm:ss", createTime);
		}catch(Exception ex){
			logger.error("failed to conver timestamp to str. timestamp="+createTime,ex);
		}
	}

	public String getCreateTimeStr() {
		return createTimeStr;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(String ownerType) {
		this.ownerType = ownerType;
	}

	public String getDescn() {
		return descn;
	}

	public void setDescn(String descn) {
		this.descn = descn;
	}

	public String getTmId() {
		return tmId;
	}

	public void setTmId(String tmId) {
		this.tmId = tmId;
	}

	public String getTmpTableName() {
		return tmpTableName;
	}

	public void setTmpTableName(String tmpTableName) {
		this.tmpTableName = tmpTableName;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

}
