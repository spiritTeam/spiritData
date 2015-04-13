package com.spiritdata.dataanal.report.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.spiritdata.dataanal.common.model.Owner;
import com.spiritdata.dataanal.exceptionC.Dtal1002CException;
import com.spiritdata.dataanal.report.persistence.pojo.ReportPo;
import com.spiritdata.filemanage.category.REPORT.model.ReportFile;
import com.spiritdata.framework.core.model.tree.TreeNode;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.jsonD.Convert2Json;
import com.spiritdata.jsonD.model.AccessJsonD;
import com.spiritdata.jsonD.util.JsonUtils;

/**
 * 报告类，此报告类只包含报告本身的信息，不包括处理报告相关的信息。<br/>
 * 包括：_HEAD,_DATA,_REPORT
 * @author wh
 */
public class Report implements Serializable, Convert2Json {
    private static final long serialVersionUID = 518670183146944686L;
 
    private String id; //报告id，应和报告头中的id相一致
    private Owner owner; //所有者
    private String reportType; //报告分类
    private String reportName; //报告名称
    private ReportFile reportFile; //报告所对应的文件信息
    private String desc; //文件说明
    private Timestamp CTime; //记录创建时间

    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
        if (this._HEAD!=null&&(this._HEAD instanceof ReportHead)) {
            ((ReportHead)this._HEAD).setId(id);
        }
    }
    public Owner getOwner() {
        return owner;
    }
    public void setOwner(Owner owner) {
        this.owner = owner;
    }
    public String getReportType() {
        return reportType;
    }
    public void setReportType(String reportType) {
        this.reportType = reportType;
        if (this._HEAD!=null&&(this._HEAD instanceof ReportHead)) {
            ((ReportHead)this._HEAD).setReportType(reportType);
        }
    }
    public String getReportName() {
        return reportName;
    }
    public void setReportName(String reportName) {
        this.reportName = reportName;
        if (this._HEAD!=null&&(this._HEAD instanceof ReportHead)) {
            ((ReportHead)this._HEAD).setReportName(reportName);
        }
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
    public void setDesc(String desc) {
        this.desc = desc;
        if (this._HEAD!=null&&(this._HEAD instanceof ReportHead)) {
            ((ReportHead)this._HEAD).setDesc(desc);
        }
    }
    public Timestamp getCTime() {
        return CTime;
    }
    public void setCTime(Timestamp cTime) {
        CTime = cTime;
        if (this._HEAD!=null&&(this._HEAD instanceof ReportHead)) {
            ((ReportHead)this._HEAD).setCTime(cTime);
        }
    }

    private Object _HEAD;//头信息，可以是String reportHead 对象
    private List<OneJsonD> _DLIST;//jsonD数据访问列表
    private Object _REPORT;//报告主体信息，可以是String reportHead 对象

    public List<OneJsonD> get_DLIST() {
		return _DLIST;
	}
	public void set_DLIST(List<OneJsonD> _DLIST) {
		this._DLIST = _DLIST;
	}

    public Object get_HEAD() {
        return _HEAD;
    }
    public void set_HEAD(Object _HEAD) {
        this._HEAD = _HEAD;
        if (this._HEAD!=null&&(this._HEAD instanceof ReportHead)) {
            String _id = ((ReportHead)this._HEAD).getId();
            if (!StringUtils.isNullOrEmptyOrSpace(_id)) this.setId(_id);
        }
    }

    /**
     * 向report中加入一个jsonD的访问信息
     * @param one
     */
    public void addOneJsonD(AccessJsonD one) {
        if (_DLIST==null) _DLIST=new ArrayList<OneJsonD>();
        OneJsonD oj = new OneJsonD(one);
        oj.setRdId(_DLIST.size());
        _DLIST.add(oj);
    }

    /**
     * 根据jsonDId获取访问信息在本报告中的id
     * @param jsonDId jsonD的id，就是jsonD文件的id，对应数据库中file_index中的id
     * @return 所对应的report中的jsonD的标识
     */
    public int getDid(String jsonDId) {
        int ret = -1;
        if (_DLIST!=null&&_DLIST.size()>0) {
            for (int i=0; i<_DLIST.size(); i++) {
                OneJsonD oj = _DLIST.get(i);
                if (oj.getJsonDId().equals(jsonDId)) return oj.getRdId();
            }
        }
        return ret;
    }

    public Object get_REPORT() {
        return _REPORT;
    }
    public void set_REPORT(Object _REPORT) {
        this._REPORT = _REPORT;
    }

    /**
     * 把报告对象转换为json字符串
     * @return json字符串
     */
    public String toJson() {
        if (_HEAD==null) throw new Dtal1002CException("reportD不规范：头信息(_HEAD)必须设置！");

        String jsonS = "{";
        //转换头
        if (_HEAD instanceof ReportHead) {
            jsonS += ((ReportHead)_HEAD).toJson();
        } else {
            jsonS += "\"_HEAD\":"+JsonUtils.objToJson(_HEAD);
        }
        //转换dataList;报告可以没有任何_DLIST
        if (_DLIST!=null&&_DLIST.size()>0) {
            jsonS += ","+JsonUtils.objToJson(_DLIST);
        } else jsonS += ",";
        //转换体report
        if (_REPORT==null) jsonS += "\"_REPORT\":\"\"";
        else {
            if (_REPORT instanceof String) {
                jsonS += "\"_REPORT\":"+_REPORT;
            } else if (_REPORT instanceof SegmentList) {
                jsonS += "\"_REPORT\":"+((SegmentList<TreeNode<ReportSegment>>)_REPORT).toJson();
            } else {
                jsonS += "\"_REPORT\":"+JsonUtils.objToJson(_REPORT);
            }
        }
        return jsonS+"}";
    }

    public ReportPo convert2Po() {
        ReportPo ret = new ReportPo();
        //id处理，没有id，自动生成一个
        if (StringUtils.isNullOrEmptyOrSpace(this.id)) ret.setId(SequenceUUID.getPureUUID());
        else ret.setId(this.id);

        //所有者
        ret.setOwnerId(this.owner.getOwnerId());
        ret.setOwnerType(this.owner.getOwnerType());
        //TaskId 在这里不设置
        //文件Id
        if (reportFile!=null) ret.setFId(reportFile.getId());
        //其他
        ret.setReportType(this.getReportType());
        ret.setReportName(this.getReportName());
        ret.setDesc(this.getDesc());
        ret.setCTime(this.CTime);
        return ret;
    }
}