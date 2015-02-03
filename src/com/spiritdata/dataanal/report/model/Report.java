package com.spiritdata.dataanal.report.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.spiritdata.dataanal.exceptionC.Dtal1002CException;
import com.spiritdata.filemanage.REPORT.model.ReportFile;
import com.spiritdata.framework.core.model.tree.TreeNode;
import com.spiritdata.jsonD.ConvertJson;
import com.spiritdata.jsonD.util.JsonUtils;

/**
 * 报告类，此报告类只包含报告本身的信息，不包括处理报告相关的信息。<br/>
 * 包括：_HEAD,_DATA,_REPORT
 * @author wh
 */
public class Report implements Serializable, ConvertJson {
    private static final long serialVersionUID = 518670183146944686L;
 
    private String id; //报告id，应和报告头中的id相一致
    private int ownerType; //模式所对应的所有者类型（1=注册用户;2=非注册用户(session);3=系统生成）
    private String ownerId; //所有者标识（可能是用户id，也可能是SessionID，也可能是'Sys'）
    private String reportType; //报告分类
    private String reportName; //报告名称
    private ReportFile reportFile; //报告所对应的文件信息
    private String desc; //文件说明
    private Timestamp CTime; //记录创建时间

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
        //  TODO 
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
    private List<OneJsond> dataList;//jsond数据访问列表
    private Object _REPORT;//报告主题信息，可以是String reportHead 对象

    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
        if (this._HEAD!=null&&(this._HEAD instanceof ReportHead)) {
            ((ReportHead)this._HEAD).setId(id);
        }
    }
    public Object get_HEAD() {
        return _HEAD;
    }
    public void set_HEAD(Object _HEAD) {
        this._HEAD = _HEAD;
        if (this._HEAD!=null&&(this._HEAD instanceof ReportHead)) {
            String _id = ((ReportHead)this._HEAD).getId();
            if (_id!=null&&_id.trim().length()>0) this.setId(((ReportHead)this._HEAD).getId());
        }
    }
    public Object get_REPORT() {
        return _REPORT;
    }
    public void set_REPORT(Object _REPORT) {
        this._REPORT = _REPORT;
    }

    /**
     * 向report中加入一个jsond的访问信息
     * @param one
     */
    public void addOneJsond(OneJsond one) {
        if (dataList==null) dataList=new ArrayList<OneJsond>();
        one.setRdId(dataList.size());
        dataList.add(one);
    }

    /**
     * 根据jsondId获取访问信息在本报告中的id
     * @param jsondId
     * @return
     */
    public int getDid(String jsondId) {
        int ret = -1;
        if (dataList==null||dataList.size()==0) {
            for (int i=0; i<dataList.size(); i++) {
                OneJsond oj = dataList.get(i);
                if (oj.equals(jsondId)) return oj.getRdId();
            }
        }
        return ret;
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
        //转换dataList;报告可以没有任何dataList
        if (dataList!=null&&dataList.size()>0) {
            jsonS += ",\"_DLIST\":[";
            for (int i=0; i<dataList.size(); i++) {
                if (i!=0) jsonS += ",";
                jsonS += dataList.get(i).toJson();
            }
            jsonS += "],";
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
}