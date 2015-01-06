package com.spiritdata.dataanal.metadata.relation.pojo;

import java.sql.Timestamp;

import com.spiritdata.framework.core.model.BaseObject;
/**
 * 导入文件与实体表对应关联信息
 * 对应持久化中数据库的表为SA_IMP_TABMAP_REL
 * @author wh
 */
public class ImpTableMapRel extends BaseObject {
    private static final long serialVersionUID = 949166772897349204L;

    private String id; //文件/实体对应关系ID
    private String FId; //导入文件ID
    private String tmoId; //对照表Id(元数据实体表对照Id，外键)
    private String mdMId; //元数据模式Id
    private String sheetName; //页签名称
    private int sheetIndex; //页签排序
    private String tableTitleName; //页签中的表名称
    private Timestamp CTime; //本记录创建时间，也是表实体创建时间

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getFId() {
        return FId;
    }
    public void setFId(String fId) {
        this.FId = fId;
    }
    public String getTmoId() {
        return tmoId;
    }
    public void setTmoId(String tmoId) {
        this.tmoId = tmoId;
    }
    public String getMdMId() {
        return mdMId;
    }
    public void setMdMId(String mdMId) {
        this.mdMId = mdMId;
    }
    public String getSheetName() {
        return sheetName;
    }
    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
    public int getSheetIndex() {
        return sheetIndex;
    }
    public void setSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }
    public String getTableTitleName() {
        return tableTitleName;
    }
    public void setTableTitleName(String tableTitleName) {
        this.tableTitleName = tableTitleName;
    }
    public Timestamp getCTime() {
        return CTime;
    }
    public void setCTime(Timestamp cTime) {
        CTime = cTime;
    }
}