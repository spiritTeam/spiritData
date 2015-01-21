package com.spiritdata.dataanal.importdata.excel.pojo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.spiritdata.dataanal.importdata.excel.ExcelConstants;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataColumn;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel;

/**
 * Excel中的结构表信息
 * @author wh
 */
public class SheetTableInfo implements Serializable {
    private static final long serialVersionUID = 4360018605351620147L;
    
    private boolean threadEnd = false;//线程指标，说明处理线程是否结束了

    private int beginX;//数据开始列
    private int endX;//数据结束列
    private int beginY;//数据开始行
    private int endY;//数据结束行
    /**
     * 标题信息，包括——标题名称；对应的列属性:标题开始列X,结束列X,上级列(对于分级表头的情况)
     */
    private List<Map<String, Object>> titleInfo;
    private SheetInfo sheetInfo; //所在的sheet信息
    private MetadataModel mm; //对应的元数据模式
    private String tableTitleName; //表的名称，一个sheet中可能包含多个表，每个表有不同的名称，目前tableTitleName就与sheetName相同

    /**
     * 分析数据结构的map,其中key和columnTitle相互对应
     * 值是一个Map，为key=数据类型，value=Map{有值计数，有值行号}
     */
    public Map<String, Object> dataStructureAnalMap;

    public int getBeginX() {
        return beginX;
    }
    public void setBeginX(int beginX) {
        this.beginX = beginX;
    }
    public int getEndX() {
        return endX;
    }
    public void setEndX(int endX) {
        this.endX = endX;
    }
    public int getBeginY() {
        return beginY;
    }
    public void setBeginY(int beginY) {
        this.beginY = beginY;
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
    public String getTableTitleName() {
        return tableTitleName;
    }
    public void setTableTitleName(String tableTitleName) {
        this.tableTitleName = tableTitleName;
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

    /**
     * 根据titleInfo，dataStructureAnalMap共同分析，并得出MetadataModel。
     * 分析的结果存储在本对象的mm属性中。
     * 注意，此时mm中的ownerId和ownerType不用关心，会在MetadataSessionService.storeMdModel4Import中进行处理
     */
    public void caculateMetadataModel() throws Exception {
        int allCount;
        MetadataModel mm = new MetadataModel();
        //这里的只对mm的columnList进行处理
        if (titleInfo==null||titleInfo.size()==0) return;
        for (Map<String, Object> tc: titleInfo) {
            String titleName = (String)tc.get("title");
            Map<Integer, Object> _colDataTypeAnalData = (Map<Integer, Object>)dataStructureAnalMap.get(titleName);
            if (_colDataTypeAnalData==null||_colDataTypeAnalData.size()==0) continue; //没有统计数据，不进行处理
            int _thisColType = -1;
            //计算总数
            allCount = 0;
            for (Integer dType: _colDataTypeAnalData.keySet()) {
                if (dType!=ExcelConstants.DATA_TYPE_ERROR&&dType!=ExcelConstants.DATA_TYPE_NULL&&dType!=ExcelConstants.DATA_TYPE_FORMULA) {
                    allCount += (Integer)((Map<String, Object>)_colDataTypeAnalData.get(dType)).get("dCount");
                }
            }
            double b = Double.valueOf(allCount+"");
            double a = 0;
            int numberCount = 0;
            boolean exceptType = false;
            for (Integer dType: _colDataTypeAnalData.keySet()) {
                if (dType==ExcelConstants.DATA_TYPE_ERROR||dType==ExcelConstants.DATA_TYPE_NULL||dType==ExcelConstants.DATA_TYPE_FORMULA) continue;
                if (dType!=ExcelConstants.DATA_TYPE_STRING&&dType!=ExcelConstants.DATA_TYPE_INTEGER) exceptType=true;
                int thisCount = (Integer)((Map<String, Object>)_colDataTypeAnalData.get(dType)).get("dCount");
                a = Double.valueOf(thisCount+"");

                if (a/b>=ExcelConstants.WEIGHT_OF_DATATYPE) _thisColType = dType;

                if (dType==ExcelConstants.DATA_TYPE_NUMERIC||dType==ExcelConstants.DATA_TYPE_DOUBLE||dType==ExcelConstants.DATA_TYPE_INTEGER) numberCount+=thisCount;
            }
            if ((_thisColType==-1||_thisColType==ExcelConstants.DATA_TYPE_NUMERIC||_thisColType==ExcelConstants.DATA_TYPE_DOUBLE||_thisColType==ExcelConstants.DATA_TYPE_INTEGER)
              &&(numberCount/b>=ExcelConstants.WEIGHT_OF_DATATYPE&&numberCount>a)) {
                _thisColType = ExcelConstants.DATA_TYPE_DOUBLE;
            }
            if (_thisColType==-1) { //检查是否都是String或Integer类型
                if (titleName.indexOf("号")!=-1||titleName.indexOf("证")!=-1||titleName.indexOf("码")!=-1) {
                    if (_colDataTypeAnalData.size()==2&&!exceptType) {
                        _thisColType = ExcelConstants.DATA_TYPE_STRING;
                    }
                }
            }
            if (_thisColType!=-1) {//说明已经分析出数据类型
                MetadataColumn mc = new MetadataColumn();
                mc.setTitleName(titleName);
                mc.setColumnType(ExcelConstants.convert2DataTypeString(_thisColType));
                mc.setColumnIndex((Integer)tc.get("firstCol"));
                mm.addColumn(mc);
            }
        }
        if (mm!=null&&mm.getColumnList()!=null&&mm.getColumnList().size()>0) {
            this.setMm(mm);
        }
        //titleName设置，这个可能需要做更详细的分析
        mm.setTitleName(this.getSheetInfo().getSheetName());
    }
}