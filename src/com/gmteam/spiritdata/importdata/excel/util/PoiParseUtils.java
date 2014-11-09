package com.gmteam.spiritdata.importdata.excel.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import com.gmteam.framework.util.DateUtils;
import com.gmteam.spiritdata.importdata.excel.ExcelConstants;
import com.gmteam.spiritdata.importdata.excel.pojo.SheetInfo;

/**
 * 通过Poi包解析excel文件的公共方法集服务
 * @author wh
 */
//TODO 目前此解释类，只完成了单表的解析，若一个sheet中包含多个表，则无法解析
//TODO 目前此解释类，对表头只进行了一级处理，没有分析表头的结构
public class PoiParseUtils {
    private SheetInfo sheetInfo = null; //要解析的Sheet对象
    private Sheet sheet = null; //sheet对象
    private List<CellRangeAddress> mergedCellList = null; //合并单元格列表
    private Map<String, Map<String, Object>> mainMergedCellList = null;//主合并单元格映射表，key是行号和列号的组合，如"23,12"

    /**
     * 构造方法
     * @param excelType excel文件类型
     * @param sheetInfo 要解析的Sheet对象
     */
    public PoiParseUtils(SheetInfo sheetInfo) {
        super();
        this.sheetInfo = sheetInfo;
        this.sheet = sheetInfo.getSheet();
        //获得合并的单元格
        int mergedCellCount = sheet.getNumMergedRegions();
        if (mergedCellCount>0) {
            mergedCellList = new ArrayList<CellRangeAddress>();
            for (int i=0; i<mergedCellCount; i++) {
                mergedCellList.add(sheet.getMergedRegion(i));
            }
        }
    }

    /**
     * 获得sheetinfo信息，此信息中包括元数据的分析结果。主要放在etiList中
     * @return sheetinfo信息
     */
    public SheetInfo getSheetInfo() {
        return this.sheetInfo;
    }

    /**
     * 分析sheet的元数据信息，并把分析的结果存入对象的sheetInfo对象中
     */
    public void analSheetMetadata() {
        //首先分析表头
        int rows = this.sheet.getLastRowNum();
        int firstRowNum = this.sheet.getFirstRowNum();

        if (rows==firstRowNum&&rows==0) return; //说明是空sheet
        if (rows==firstRowNum) return; //只有一行数据，没有分析的价值

        List<Map<String, Object>> rowData;
        //行缓存,key是行号
        Map<Integer, List<Map<String, Object>>> catchRows = new HashMap<Integer, List<Map<String, Object>>>();
        //行号缓存，用于快速索引行缓存
        List<Integer> numList = new ArrayList<Integer>();
        for (int i=firstRowNum; i<rows; i++) { //第一行必然不是空行
            rowData = readOneRow(i);
            if (rowData!=null) {//放弃空行
                catchRows.put(i, rowData);
                numList.add(i);
            }
            if (catchRows.size()>=2) {//至少读了两行，可以进行表头分析了
                //看最后两行是否是相同的列
            }
        }
    }

    /*
     * 按照表头方式读取一行数据。
     * @param rowNum 行号
     * @return 标题的列表，每个元素是一个map包括{"name":"姓名", "firstRow":"范围-开始行", "lastRow":"范围-结束行", "firstCol":"范围-开始列", "lastCol":"范围-结束列", "dataType":"数据类型(此类型不经过转换)"}
     */
    private List<Map<String, Object>> readOneRow(int rowNum) {
        Row rowData = this.sheet.getRow(rowNum); //行数据
        if (rowData==null) return null;

        Cell cell = null; //单元格信息
        Map<String, Object> cellMap = null;
        List<Map<String, Object>> rd = new ArrayList<Map<String, Object>>();

        for (int i=0; i<rowData.getLastCellNum(); i++) {
            cell = rowData.getCell(i);
            if (cell!=null) {
                cellMap = getCellMap(cell);
                rd.add(cellMap);
            }
        }
        
        return rd.size()==0?null:rd;
    }

    /*
     * 把cell转换为Map，map包括：
     * {
     *   "isMerged":"是否合并单元格",
     *   "mainMergedLabel":"主合并单元格的标号",
     *   "firstRow":"范围-开始行",
     *   "lastRow":"范围-结束行",
     *   "firstCol":"范围-开始列",
     *   "lastCol":"范围-结束列",
     *   "nativeDate":{ "dType":"数据类型", "value":"值"},//此类型不经过转换
     *   "transDate":{ "dType":"数据类型", "value":"值"}  //此类型经过转换
     * }
     * @param cell 单元格数据
     * @return cell对应的Map
     */
    private Map<String, Object> getCellMap(Cell cell) {
        Map<String, Object> ret = new HashMap<String, Object>();

        //本单元格的行列号
        int rowIndex = cell.getRowIndex();
        int colIndex = cell.getColumnIndex();
        //合并单元格处理
        CellRangeAddress cra = getMergedRange(cell);
        ret.put("isMerged", cra!=null);
        if (cra!=null) {
            String mergedLabel = rowIndex+","+colIndex;
            ret.put("firstRow", cra.getFirstRow());
            ret.put("lastRow", cra.getLastRow());
            ret.put("firstCol", cra.getFirstColumn());
            ret.put("lastCol", cra.getLastColumn());
            if (cra.getFirstRow()==rowIndex||cra.getFirstColumn()==colIndex) {//是主合并单元格
                if (mainMergedCellList==null) mainMergedCellList=new HashMap<String, Map<String, Object>>();
                mainMergedCellList.put(mergedLabel, ret);
            }
            mergedLabel = cra.getFirstRow()+","+cra.getFirstColumn();
            ret.put("mainMergedLabel", mergedLabel);
            //关联主合并单元格
        } else {
            ret.put("firstRow", rowIndex);
            ret.put("lastRow", rowIndex);
            ret.put("firstCol", colIndex);
            ret.put("lastCol", colIndex);
        }
        //读取原始原始数据
        ret.put("nativeDate", getCellNativeValueMap(cell));
        ret.put("transDate", getCellTransValueMap(cell));
        return ret;
    }

    /*
     * 判断cell是否是合并单元格，若是返回合并单元格信息
     * @param cell 被判断的cell
     * @return 若是合并单元格，返回CellRangeAddress对象，否则，返回空
     */
    private CellRangeAddress getMergedRange(Cell cell) {
        if (mergedCellList==null) {
            CellRangeAddress ret = null;
            CellRangeAddress cra = null;
            //获得合并的单元格
            int mergedCellCount = this.sheet.getNumMergedRegions();

            if (mergedCellCount>0) {
                mergedCellList = new ArrayList<CellRangeAddress>();
                for (int i=0; i<mergedCellCount; i++) {
                    cra = this.sheet.getMergedRegion(i);
                    mergedCellList.add(cra);
                    if (containsMergedCell(cra, cell)) ret=cra;
                }
            }
            return ret;
        } else {
            for (CellRangeAddress cra: mergedCellList) {
                if (containsMergedCell(cra, cell)) return cra;
            }
        }
        return  null;
    }

    /*
     * 判断某个cell是否是合并单元格 
     * @param cra 合并单元格对象
     * @param cell cell对象
     * @return 若cell是合并单元格，返回true，否则，返回false
     */
    private boolean containsMergedCell(CellRangeAddress cra, Cell cell) {
        int rowIndex = cell.getRowIndex();
        int colIndex = cell.getColumnIndex();

        if ((rowIndex>=cra.getFirstRow())&&(rowIndex<=cra.getLastRow())&&(colIndex>=cra.getFirstColumn())&&(colIndex<=cra.getLastColumn())) {
            return true;
        }
        return false;
    }

    /*
     * 通过Poi获得单元原始数据，不进行尝试转换 
     * @param cell cell对象
     * @return valueMap:{"dType":"整数的数据类型", "value":"值的对象"}
     */
    private Map<String, Object> getCellNativeValueMap(Cell cell) {
        Map<String, Object> ret = new HashMap<String, Object>();
        if (cell!=null) {
            Object value = null;
            int _dtype = cell.getCellType();
            switch (_dtype) {
            case 0://数值
                value = cell.getNumericCellValue();
                break;
            case 1://字符串
                value = cell.getStringCellValue();
                break;
            case 2://公式
                //TODO 公式还要进行进一步处理
                value = cell.getCellFormula();
                break;
            case 3://空
                value = null;
                break;
            case 4://布尔
                value = cell.getBooleanCellValue();
                break;
            case 5://错误，相当于空
                value = null;
                break;
            }
            ret.put("dType", _dtype);
            ret.put("value", value);
        }
        return ret.size()==0?null:ret;
    }

    /*
     * 通过Poi获得单元转换数据，进行尝试转换 
     * @param cell cell对象
     * @return valueMap:{"dType":"整数的数据类型", "value":"值的对象"}
     */
    private Map<String, Object> getCellTransValueMap(Cell cell) {
        Map<String, Object> ret = getCellNativeValueMap(cell);
        if (ret!=null&&ret.size()==2) {
            int _dtype = (Integer)ret.get("dType");
            int __dtype = _dtype;
            Object value = null;
            if (_dtype==1) { //字符串
                String sVal = cell.getStringCellValue();
                //尝试转换为整形
                try {
                    value = Integer.parseInt(sVal);
                    __dtype = ExcelConstants.DATA_TYPE_INTEGER;
                } catch (Exception e) {
                    //尝试转换为浮点
                    try {
                        value = Double.parseDouble(sVal);
                        __dtype = ExcelConstants.DATA_TYPE_DOUBLE;
                    } catch(Exception e1) {
                        //尝试转换为日期
                        try {
                            value = DateUtils.getDateTime("yyyy-MM-dd HH:mm:ss S", sVal);
                        } catch(Exception e2) {}
                        if (value==null) {
                            try {
                                value = DateUtils.getDateTime("yyyy年MM月dd日 HH:mm:ss S", sVal);
                            } catch(Exception e2) {}
                        }
                        if (value==null) {
                            try {
                                value = DateUtils.getDateTime("yyyy-MM-dd HH:mm:ss", sVal);
                            } catch(Exception e2) {}
                        }
                        if (value==null) {
                            try {
                                value = DateUtils.getDateTime("yyyy-MM-dd HH:mm:ss S", sVal);
                            } catch(Exception e2) {}
                        }
                        //可能还有其他的模式
                        if (value!=null) {
                            __dtype = ExcelConstants.DATA_TYPE_DATE;
                        }
                    }
                }
            } else if (_dtype==0) { //数值
                //尝试转换为日期
                if (DateUtil.isCellDateFormatted(cell)) {
                    value = cell.getDateCellValue();
                    __dtype = ExcelConstants.DATA_TYPE_DATE;
                } else { //尝试转换为整形
                    Double _d = cell.getNumericCellValue();
                    String _ts = _d.toString();
                    try {
                        if (_ts.indexOf('.')==-1) {
                            value = Integer.parseInt(_ts);
                            __dtype = ExcelConstants.DATA_TYPE_INTEGER;
                        } else if ((Integer.parseInt(_ts.substring(_ts.indexOf('.')+1)))==0) {
                            value = Integer.parseInt(_ts.substring(0, _ts.indexOf('.')));
                            __dtype = ExcelConstants.DATA_TYPE_INTEGER;
                        }
                    } catch(Exception e ) {
                        //尝试转换为浮点
                        value = _d;
                        __dtype = ExcelConstants.DATA_TYPE_DOUBLE;
                    }
                }
            }
            if (__dtype!=_dtype) {
                ret.clear();
                ret.put("dType", __dtype);
                ret.put("value", value);
            }
        }
        return ret;
    }

    //=============以下对单元格判断之间的关系
    /*
     * 判断两个单元格是为相同的列，主要用于合并单元格的处理
     * @param cell1 第一单元格
     * @param cell2 第二单元格
     * @return 若是相同的列，返回true，否则返回false
     */
    private boolean sameColumns(Map<String, Object> cell1, Map<String, Object> cell2) {
        if ((Integer)cell1.get("firstRow")==(Integer)cell2.get("firstRow")&&(Integer)cell1.get("lastRow")==(Integer)cell2.get("lastRow")) return true;
        return false;
    }

    /*
     * 判断两个单元格是为相同的行，主要用于合并单元格的处理
     * @param cell1 第一单元格
     * @param cell2 第二单元格
     * @return 若是相同的行，返回true，否则返回false
     */
    private boolean sameRows(Map<String, Object> cell1, Map<String, Object> cell2) {
        if ((Integer)cell1.get("firstCol")==(Integer)cell2.get("firstCol")&&(Integer)cell1.get("lastCol")==(Integer)cell2.get("lastCol")) return true;
        return false;
    }

    /*
     * 判断第一个单元格列是否包括第二个单元格列，主要用于合并单元格的处理
     * @param cell1 第一单元格
     * @param cell2 第二单元格
     * @return 若是包含的列，返回true，否则返回false
     */
    private boolean containColumns(Map<String, Object> cell1, Map<String, Object> cell2) {
        if ((Integer)cell1.get("firstRow")==(Integer)cell2.get("firstRow")&&(Integer)cell1.get("lastRow")==(Integer)cell2.get("lastRow")) return false;
        if ((Integer)cell1.get("firstRow")>=(Integer)cell2.get("firstRow")&&(Integer)cell1.get("lastRow")<=(Integer)cell2.get("lastRow")) return true;
        return false;
    }

    /*
     * 判断第一个单元格行是否包括第二个单元格行，主要用于合并单元格的处理
     * @param cell1 第一单元格
     * @param cell2 第二单元格
     * @return 若是包含的行，返回true，否则返回false
     */
    private boolean containRows(Map<String, Object> cell1, Map<String, Object> cell2) {
        if ((Integer)cell1.get("firstCol")==(Integer)cell2.get("firstCol")&&(Integer)cell1.get("lastCol")==(Integer)cell2.get("lastCol")) return false;
        if ((Integer)cell1.get("firstCol")>=(Integer)cell2.get("firstCol")&&(Integer)cell1.get("lastCol")<=(Integer)cell2.get("lastCol")) return true;
        return false;
    }

    //以下对行进行判断，包括行内，和行之间的关系
    /*
     * 判断行是否是由原始String类型列组成的行
     * @param rowMap 行数据Map，这里的行数据是由原始数据类型组成的
     * @return 若一样返回true，否则返回false
     */
    private boolean isNativeStingTypeRow(List<Map<String, Object>> rowMap) {
        
        return false;
    }
}