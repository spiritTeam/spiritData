package com.gmteam.spiritdata.importdata.excel.pojo;

import java.util.List;
import java.util.Map;

import com.gmteam.framework.core.model.BaseObject;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;

/**
 * Excel中的结构表信息
 * @author wh
 */
public class SheetTableInfo extends BaseObject{
    private static final long serialVersionUID = 4360018605351620147L;
    
    private boolean threadEnd = false;//线程指标，说明处理线程是否结束了

    private int bigenX;//数据开始列
    private int endX;//数据结束列
    private int bigenY;//数据开始行
    private int endY;//数据结束行
    /**
     * 标题信息，包括——标题名称；对应的列属性:标题开始列X,结束列X,上级列(对于分级表头的情况)
     */
    private List<Map<String, Object>> titleInfo;
    private SheetInfo sheetInfo; //所在的sheet信息
    private MetadataModel mm; //对应的元数据模式
    /**
     * 分析数据结构的map,其中key和columnTitle相互对应
     * 值是一个Map，为key=数据类型，value=Map{有值计数:,空值行号，有值行号}
     */
    public Map<String, Object> dataStructureAnalMap;

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
    public List<Map<String, Object>> getTitleInfo() {
        return titleInfo;
    }
    public void setTitleInfo(List<Map<String, Object>> titleInfo) {
        this.titleInfo = titleInfo;
    }
    public SheetInfo getSheetInfo() {
        return sheetInfo;
    }
    public void setSheetInfo(SheetInfo sheetInfo) {
        this.sheetInfo = sheetInfo;
    }
    public MetadataModel getMm() {
        return mm;
    }
    public void setMm(MetadataModel mm) {
        this.mm = mm;
    }

    public void setThreadEnd() {
        this.threadEnd=true;
    }

    public void cleanThreadEnd() {
        this.threadEnd=false;
    }

    public boolean threadIsEnd() {
        return this.threadEnd;
    }
}