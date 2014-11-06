package com.gmteam.spiritdata.importdata.excel.pojo;

import java.util.Map;

import com.gmteam.framework.core.model.BaseObject;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;

/**
 * Excel中的结构表信息
 * @author wh
 */
public class ExcelTableInfo extends BaseObject{
    private static final long serialVersionUID = 4360018605351620147L;

    private int bigenX;//数据开始列
    private int endX;//数据结束列
    private int bigenY;//数据开始行
    private int endY;//数据结束行
    /**
     * 标题信息，包括，标题称，即对应的列属性包括:标题开始列X,结束列X,上级列(对于分集的情况)
     */
    private Map<String, Map<String, Object>> titleInfo;
    private SheetInfo sheetInfor; //所在的sheet信息
    private MetadataModel mm; //对应的元数据模式

    public int getBigenX() {
        return bigenX;
    }
    public void setBigenX(int bigenX) {
        this.bigenX = bigenX;
    }
    public int getEndX() {
        return endX;
    }
    public void setEndX(int endX) {
        this.endX = endX;
    }
    public int getBigenY() {
        return bigenY;
    }
    public void setBigenY(int bigenY) {
        this.bigenY = bigenY;
    }
    public int getEndY() {
        return endY;
    }
    public void setEndY(int endY) {
        this.endY = endY;
    }
    public Map<String, Map<String, Object>> getTitleInfo() {
        return titleInfo;
    }
    public void setTitleInfo(Map<String, Map<String, Object>> titleInfo) {
        this.titleInfo = titleInfo;
    }
    public SheetInfo getSheetInfor() {
        return sheetInfor;
    }
    public void setSheetInfor(SheetInfo sheetInfor) {
        this.sheetInfor = sheetInfor;
    }
    public MetadataModel getMm() {
        return mm;
    }
    public void setMm(MetadataModel mm) {
        this.mm = mm;
    }
}