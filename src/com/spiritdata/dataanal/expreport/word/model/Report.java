package com.spiritdata.dataanal.expreport.word.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import com.spiritdata.filemanage.category.REPORT.model.ReportFile;

/**
 * 报告类，此报告类只包含报告本身的信息，不包括处理报告相关的信息。<br/>
 * 包括：_HEAD,_DATA,_REPORT
 * @author wh
 */
public class Report implements Serializable {
    private static final long serialVersionUID = 518670183146944686L;
 
    private String id; //报告id，应和报告头中的id相一致
    private int ownerType; //模式所对应的所有者类型（1=注册用户;2=非注册用户(session);3=系统生成）
    private String ownerId; //所有者标识（可能是用户id，也可能是SessionID，也可能是'Sys'）
    private String reportType; //报告分类
    private String reportName; //报告名称
    private ReportFile reportFile; //报告所对应的文件信息
    private String desc; //文件说明
    private Timestamp CTime; //记录创建时间
    private Object _HEAD;//头信息，可以是String reportHead 对象
    private List<String> _DLIST;//jsonD数据访问列表
    private _REPORT _REPORT;//报告主题信息，可以是String reportHead 对象
    
	public _REPORT get_REPORT() {
		return _REPORT;
	}
	public void set_REPORT(_REPORT _REPORT) {
		this._REPORT = _REPORT;
	}
	public int getOwnerType() {
        return ownerType;
    }
    public void setOwnerType(int ownerType) {
        this.ownerType = ownerType;
    }
    public String getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
    public String getReportType() {
        return reportType;
    }
    public String getReportName() {
        return reportName;
    }
    public ReportFile getReportFile() {
        return reportFile;
    }
    public void setReportFile(ReportFile reportFile) {
        this.reportFile = reportFile;
    }
    public String getDesc() {
        return desc;
    }
    public Timestamp getCTime() {
        return CTime;
    }

	public List<String> get_DLIST() {
		return _DLIST;
	}
	public void set_DLIST(List<String> _DLIST) {
		this._DLIST = _DLIST;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public void setCTime(Timestamp cTime) {
		CTime = cTime;
	}
	public void set_HEAD(Object _HEAD) {
		this._HEAD = _HEAD;
	}
	public String getId() {
        return this.id;
    }
    public Object get_HEAD() {
        return _HEAD;
    }
}