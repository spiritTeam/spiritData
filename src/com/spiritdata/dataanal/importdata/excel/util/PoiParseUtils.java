package com.spiritdata.dataanal.importdata.excel.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import com.spiritdata.framework.util.DateUtils;
import com.spiritdata.dataanal.common.util.SpiritRandom;
import com.spiritdata.dataanal.importdata.excel.ExcelConstants;
import com.spiritdata.dataanal.importdata.excel.pojo.SheetTableInfo;
import com.spiritdata.dataanal.importdata.excel.pojo.SheetInfo;

/**
 * 通过Poi包解析excel文件的公共方法集服务
 * @author wh
 */
//TODO 目前此解释类，只完成了单表的解析，若一个sheet中包含多个表，则无法解析
//TODO 目前此解释类，对表头只进行了一级处理，没有分析表头的结构
public class PoiParseUtils {
    Logger logger = Logger.getLogger(PoiParseUtils.class);

    private SheetInfo sheetInfo = null; //要解析的Sheet对象
    private Sheet sheet = null; //sheet对象
    protected List<CellRangeAddress> mergedCellList = null; //合并单元格列表
    protected Map<String, Map<String, Object>> mainMergedCellList = null;//主合并单元格映射表，key是行号和列号的组合，如"23,12"

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
            for (int i=0; i<mergedCellCount; i++) mergedCellList.add(sheet.getMergedRegion(i));
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
     * @param excelType excel文件类型
     * @throws InterruptedException 
     */
    public List<String> analSheetMetadata() throws InterruptedException, Exception {
        List<String> logl = new ArrayList<String>();
        //首先分析表头
        int rows = this.sheet.getLastRowNum();
        int firstRowNum = this.sheet.getFirstRowNum();

        if (rows==firstRowNum&&rows==0) {//说明是空sheet
//            logl.add(StringUtils.convertLogStr("页签("+this.sheetInfo.getSheetName()+"["+this.sheetInfo.getSheetIndex()+"])为空，无法分析"));
            return logl;
        }
        if (rows-firstRowNum<2) {//至少要有三行数据
//            logl.add(StringUtils.convertLogStr("页签("+this.sheetInfo.getSheetName()+"["+this.sheetInfo.getSheetIndex()+"])只有一行有效数据，不具备分析价值"));
            return logl;
        }

        Map<String, Object> _colM;

        //001-表结构分析，生成SheetTableInfo，即把sheet分成各个独立的表区域
        //===以下最好能够分析出一个sheet中的多个表结构
        List<Map<String, Object>> rowData, lastRow, lastButOneRow, perhapsSplitRow;
        //行缓存,key是行号
        Map<Integer, List<Map<String, Object>>> catchRows = new HashMap<Integer, List<Map<String, Object>>>();
        //行号缓存，用于快速索引行缓存
        List<Integer> numList = new ArrayList<Integer>();
        int perhapsNum = -1;//表头和数据之间可能的分割行号
        int splitRowNum = -1;//表头和数据的分割行号
//        logl.add(StringUtils.convertLogStr("页签("+this.sheetInfo.getSheetName()+"["+this.sheetInfo.getSheetIndex()+"])开始分析表头和数据的分割行"));
        for (int i=firstRowNum; i<=rows; i++) {
            //读取一行，并缓存
            rowData = readOneRow(i);
            if (rowData!=null&&!isEmptyRow(rowData)/*放弃空行*/) {
                catchRows.put(i, rowData);
                numList.add(i);
            }
            if (perhapsNum==-1&&numList.size()>1) {//当还没有找到可能的分割行 并且 至少读取了两行
                lastRow = catchRows.get(numList.get(numList.size()-1));
                lastButOneRow = catchRows.get(numList.get(numList.size()-2));
                
                if (isAreaBottomRow(lastButOneRow)) perhapsNum=numList.get(numList.size()-2);//可能的表头行底行
                if (perhapsNum>-1) {
                    //如果最后两行列结构相同,splitRowNum就可能是表头分割行，否则不是
                    if (!compareSimilarColumn2Row(lastButOneRow, lastRow)) perhapsNum=-1;
                }
            }
            if (perhapsNum!=-1&&splitRowNum==-1&&numList.size()>2&&perhapsNum==numList.get(numList.size()-3)) {
                //再多判断一行
                perhapsSplitRow = catchRows.get(numList.get(numList.size()-3));
                lastRow = catchRows.get(numList.get(numList.size()-1));
                if (!compareSimilarColumn2Row(perhapsSplitRow, lastRow)) perhapsNum=-1;
                else splitRowNum=perhapsNum;
            }
            if (perhapsNum!=-1&&splitRowNum!=-1) break;//找到了分割行号
        }
        if (splitRowNum==-1) {//未找到表头，无法进行元数据分析
//            logl.add(StringUtils.convertLogStr("页签("+this.sheetInfo.getSheetName()+"["+this.sheetInfo.getSheetIndex()+"])无法分析出表头分割行。"));
            return logl;
        }
        //得到表头信息
        rowData = readOneRow(splitRowNum);//表头最后一行信息
        SheetTableInfo oneSti = new SheetTableInfo();
        //设置区域
        _colM = rowData.get(0);
        oneSti.setBeginX((Integer)_colM.get("firstCol"));
        _colM = rowData.get(rowData.size()-1);
        oneSti.setEndX((Integer)_colM.get("firstCol"));
        oneSti.setBeginY(splitRowNum+1);
        oneSti.setEndY(rows);
        oneSti.setSheetInfo(sheetInfo);
        oneSti.setTableTitleName(sheetInfo.getSheetName());// TODO tableName是需要重新处理的
        oneSti.dataStructureAnalMap= new HashMap<String, Object>();
        sheetInfo.addSheetTableInfo(oneSti);

        String tempStr = "页签("+this.sheetInfo.getSheetName()+"["+this.sheetInfo.getSheetIndex()+"])分析出表头分割行，并确定表区域范围[{"+oneSti.getBeginX()+","+oneSti.getBeginY()+"}->{"+oneSti.getEndX()+","+oneSti.getEndY()+"}]";
//        logl.add(StringUtils.convertLogStr(tempStr));
        //===以上最好能够分析出一个sheet中的多个数据表结构

        //002-表数据结构分析
        if (sheetInfo.getStiList()==null||sheetInfo.getStiList().size()==0) {//没有任何表结构数据不进行处理
//            logl.add(StringUtils.convertLogStr("页签("+this.sheetInfo.getSheetName()+"["+this.sheetInfo.getSheetIndex()+"])数据不包含表头信息，无法分析表结构。"));
            return logl;
        }

 //       logl.add(StringUtils.convertLogStr("页签("+this.sheetInfo.getSheetName()+"["+this.sheetInfo.getSheetIndex()+"])开始分析表结构的列数据类型"));
        if (sheetInfo.getStiList().size()==1) {
            numList.clear();
            _analSheetMetadata(sheetInfo.getStiList().get(0), numList, logl);
        } else { //用多个线程处理
            //TODO 多线程处理，日志和numlist都需要修改！！！！！
            cleanThreadEnd();
            for (int i=0; i<sheetInfo.getStiList().size(); i++) {
                Thread_anay_MetadataTableInfo thread_a_m = new Thread_anay_MetadataTableInfo(sheetInfo.getStiList().get(i), this, numList, logl);
                Thread t = new Thread(thread_a_m);
                t.start();
            }
            while (!allProcessed()) {
                Thread.sleep(100);
            }
            cleanThreadEnd();
        }
        return logl;//TODO 若是多线程，这个需要修改
    }

    //===================以下为可功能化的分析方法以及线程处理的相关函数
    //若一个sheet只能分析出一个表结构区域，则线程无用
    /*
     * 针对某一表结构区域进行分析，包括表头分析
     * @param sti Sheet中的表结构区域信息
     * @param numList 行号记录
     * @throws Exception
     */
    protected void _analSheetMetadata(SheetTableInfo sti, List<Integer> numList, List<String> logl) throws Exception {
        sti.cleanThreadEnd();
        try {
            //logl.add(StringUtils.convertLogStr("/t页签("+this.sheetInfo.getSheetName()+"["+this.sheetInfo.getSheetIndex()+"])获取表头信息"));
            //得到表头信息
            int _num = sti.getBeginY()-1; //表头的最后一行标号
            List<Map<String, Object>> rowData = readOneRow(_num);//表头最后一行信息

            if (rowData!=null&&rowData.size()>0&&!isEmptyRow(rowData)) {
                List<Map<String, Object>> titleInfo = new ArrayList<Map<String, Object>>();
                //TODO 目前不分析表头树
                for (Map<String, Object> _cellMap: rowData) {
                    Map<String, Object> titleCol = new HashMap<String, Object>();

                    Map<String, Object> _mergedCellM = (Boolean)_cellMap.get("isMerged")?mainMergedCellList.get(_cellMap.get("mainMergedLabel")):null;
                    //若merged已经处理，则本列不处理
                    if (_mergedCellM!=null&&(Integer)_mergedCellM.get("firstCol")<(Integer)_cellMap.get("cellCol")) continue;

                    if (_mergedCellM!=null) _cellMap = _mergedCellM;
                    titleCol.put("title", ((Map<String, Object>)_cellMap.get("nativeData")).get("value")+"");//标题
                    titleCol.put("firstCol", _cellMap.get("firstCol"));//开始列
                    titleCol.put("lastCol", _cellMap.get("lastCol"));//结束列
                    titleCol.put("parentTitle", null);//目前不分析这部分

                    titleInfo.add(titleCol);
                }
                sti.setTitleInfo(titleInfo);
            }//表头结构分析结束，此时还未分析数据类型

            //分析数据类型，若小于200行，全部读取，否则，采用随机取样的方式
            //采样率应该可以更多规则，目前只采用平均采样，而且，只有一种取样方式，若大于200行，则取样1000行数据（按列取样）
       //     logl.add(StringUtils.convertLogStr("/t页签("+this.sheetInfo.getSheetName()+"["+this.sheetInfo.getSheetIndex()+"])分析列数据类型，顺序采样"));
            boolean _flag = (sti.getEndY()-sti.getBeginY()>ExcelConstants.SAMPLING_CRITICAL_COUNT);//是否需要随机采样，=true(需随机采样)
            int _count=0;
            int i=sti.getBeginY();
            for (; i<=sti.getEndY(); i++) {
                //读取一行，并处理
                rowData = readOneRow(i);
                boolean isDeal = dealOneRowData4DataStructureAnal(rowData, sti, i);
                if (isDeal) {
                    _count++;
                    numList.add(i);
                }
                if (_count>ExcelConstants.LIMIT_SEQUENCE_COUNT&&_flag) break;
            }
            if (i>=sti.getEndY()) {
//                logl.add(StringUtils.convertLogStr("/t页签("+this.sheetInfo.getSheetName()+"["+this.sheetInfo.getSheetIndex()+"])分析列数据类型，采样结束"));
                return; //已经处理完了
            }
            //随机采样
   //         logl.add(StringUtils.convertLogStr("/t页签("+this.sheetInfo.getSheetName()+"["+this.sheetInfo.getSheetIndex()+"])分析列数据类型，顺序采样不充分，开始随机采样"));
            SpiritRandom random = new SpiritRandom(i, sti.getEndY(), ExcelConstants.SAMPLING_CRITICAL_COUNT-ExcelConstants.LIMIT_SEQUENCE_COUNT);
            do {//随机读取一行，并处理
                i = random.getNextRandom();
                rowData = readOneRow(i);
                boolean isDeal = dealOneRowData4DataStructureAnal(rowData, sti, i);
                if (isDeal) {
                    random.setCurrentRandomUsed();
                    numList.add(i);
                }
            } while (!random.isComplete());
 //           logl.add(StringUtils.convertLogStr("/t页签("+this.sheetInfo.getSheetName()+"["+this.sheetInfo.getSheetIndex()+"])分析列数据类型，采样结束"));
        } finally {
  //          logl.add(StringUtils.convertLogStr("页签("+this.sheetInfo.getSheetName()+"["+this.sheetInfo.getSheetIndex()+"])开始计算元数据"));
            sti.caculateMetadataModel();
   //         logl.add(StringUtils.convertLogStr("页签("+this.sheetInfo.getSheetName()+"["+this.sheetInfo.getSheetIndex()+"])元数据计算结束"));
            sti.setThreadEnd();
   //         logl.add("");
        }
    }

    /**
     * 为数据结构分析处理一航数据，若进行了处理，返回true，否则返回false
     * @param rowData 行数据
     * @param sti Sheet中的表结构区域信息
     * @param rowNum 行号
     * @return 若进行了处理，返回true，否则返回false
     */
    private boolean dealOneRowData4DataStructureAnal(List<Map<String, Object>> rowData, SheetTableInfo sti, int rowNum) { //
        try {
            if (rowData!=null&&!isEmptyRow(rowData)/*放弃空行*/) {
                List<Map<String, Object>> _rowData = convert2DataRow(rowData);//转换为——为数据处理的行
                if (_rowData.size()<=sti.getTitleInfo().size()) {//若与title类似，则进行处理，否则不进行处理
                    for (int j=0; j<_rowData.size(); j++) {
                        Map<String, Object> _cellMap = _rowData.get(j);
                        Map<String, Object> titleColumnMap = sti.getTitleInfo().get(j);
                        String titleName = (String)titleColumnMap.get("title");
                        Map<Integer, Object> colAnalData = (Map<Integer, Object>)sti.dataStructureAnalMap.get(titleName);
                        if (colAnalData==null) {
                            colAnalData = new HashMap<Integer, Object>();
                            sti.dataStructureAnalMap.put(titleName, colAnalData);
                        }
                        int dType = (Integer)((Map<String, Object>)_cellMap.get("transData")).get("dType");
                        int _dType = -1;
                        if (titleName.indexOf("号")!=-1||titleName.indexOf("证")!=-1||titleName.indexOf("码")!=-1) {
                            if (dType!=ExcelConstants.DATA_TYPE_DATE) {
                                _dType = (Integer)((Map<String, Object>)_cellMap.get("nativeData")).get("dType");
                                if (_dType==ExcelConstants.DATA_TYPE_NUMERIC) dType = (Integer)((Map<String, Object>)_cellMap.get("transData")).get("dType");
                                if (_dType==ExcelConstants.DATA_TYPE_NUMERIC||_dType==ExcelConstants.DATA_TYPE_STRING) {
                                    dType = (Integer)((Map<String, Object>)_cellMap.get("transData")).get("dType");
                                }
                            }
                        }
                        Map<String, Object> colDtypeAnalData = (Map<String, Object>)colAnalData.get(dType);
                        if (colDtypeAnalData==null) {
                            colDtypeAnalData = new HashMap<String, Object>();
                            colAnalData.put(dType, colDtypeAnalData);
                        }
                        Object o = (Integer)colDtypeAnalData.get("dCount");
                        o = (o==null?new Integer(1):(Integer)o+1);
                        colDtypeAnalData.put("dCount", o);
                        o = (String)colDtypeAnalData.get("rowIndex");
                        o = (o==null?rowNum+"":(String)o+","+rowNum);
                        colDtypeAnalData.put("rowIndex", o);
                    }
                    return true;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
     * 是否所有线程都已处理完，判断sheetInfo中的stiList中各元素，线程处理标志是否都已完成
     */
    private boolean allProcessed() {
        if (this.sheetInfo.getStiList()==null||this.sheetInfo.getStiList().size()==0) return true;
        for (SheetTableInfo sti: this.sheetInfo.getStiList()) {
            if (!sti.threadIsEnd()) return false;
        }
        return true;
    }

    /*
     * 清除线程处理标志，为下一次线程处理做准备 
     */
    private void cleanThreadEnd() {
        if (this.sheetInfo.getStiList()==null||this.sheetInfo.getStiList().size()==0) return ;
        for (SheetTableInfo sti: this.sheetInfo.getStiList()) {
            sti.cleanThreadEnd();
        }
    }
    //===================以上为可功能化的分析方法以及线程处理的相关函数

    /**
     * 读取一行数据
     * @param rowNum 行号
     * @return 标题的列表，每个元素是一个map包括{"name":"姓名", "firstRow":"范围-开始行", "lastRow":"范围-结束行", "firstCol":"范围-开始列", "lastCol":"范围-结束列", "dataType":"数据类型(此类型不经过转换)"}
     */
    public List<Map<String, Object>> readOneRow(int rowNum) {
        Row rowData = this.sheet.getRow(rowNum); //行数据
        if (rowData==null) return null;

        Cell cell = null; //单元格信息
        Map<String, Object> _cellMap = null;
        List<Map<String, Object>> rd = new ArrayList<Map<String, Object>>();
        int i=0;
        for (; i<rowData.getLastCellNum(); i++) {
            cell = rowData.getCell(i);
            if (cell!=null) {
                _cellMap = getCellMap(cell);
                rd.add(_cellMap);
            }
        }
        //把最后一列的空去掉
        for (i=rd.size()-1; i>=0; i--) {
            Map<String, Object> nd = (Map<String, Object>)rd.get(i).get("nativeData");
            if ((Integer)nd.get("dType")==3) {
                rd.remove(i);
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
     *   "nativeData":{ "dType":"数据类型", "value":"值"},//此类型不经过转换
     *   "transData":{ "dType":"数据类型", "value":"值"}  //此类型经过转换
     * }
     * @param cell 单元格数据
     * @return cell对应的Map
     */
    private Map<String, Object> getCellMap(Cell cell) {
        Map<String, Object> ret = new HashMap<String, Object>();

        //本单元格的行列号
        int rowIndex = cell.getRowIndex();
        int colIndex = cell.getColumnIndex();
        ret.put("cellRow", rowIndex);
        ret.put("cellCol", colIndex);
        //合并单元格处理
        CellRangeAddress cra = getMergedRange(cell);
        ret.put("isMerged", cra!=null);
        if (cra!=null) {
            String mergedLabel = rowIndex+","+colIndex;
            ret.put("firstRow", cra.getFirstRow());
            ret.put("lastRow", cra.getLastRow());
            ret.put("firstCol", cra.getFirstColumn());
            ret.put("lastCol", cra.getLastColumn());
            //关联主合并单元格
            if (cra.getFirstRow()==rowIndex||cra.getFirstColumn()==colIndex) {//是主合并单元格
                if (mainMergedCellList==null) mainMergedCellList=new HashMap<String, Map<String, Object>>();
                mainMergedCellList.put(mergedLabel, ret);
            }
            mergedLabel = cra.getFirstRow()+","+cra.getFirstColumn();
            ret.put("mainMergedLabel", mergedLabel);
        } else {
            ret.put("firstRow", rowIndex);
            ret.put("lastRow", rowIndex);
            ret.put("firstCol", colIndex);
            ret.put("lastCol", colIndex);
        }
        //读取原始原始数据
        ret.put("nativeData", getCellNativeValueMap(cell));
        ret.put("transData", getCellTransValueMap(cell));
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
                	//尝试转换为长整形
                	try{
                        value = Long.parseLong(sVal);
                        __dtype = ExcelConstants.DATA_TYPE_LONG;                		
                	}catch(Exception ex){
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
                        	try{
	                            value = Integer.parseInt(_ts);
	                            __dtype = ExcelConstants.DATA_TYPE_INTEGER;
                        	}catch(Exception ex){
                                value = Long.parseLong(_ts);
                                __dtype = ExcelConstants.DATA_TYPE_LONG;
                        	}
                        } else if ((Integer.parseInt(_ts.substring(_ts.indexOf('.')+1)))==0) {
                        	try{
	                            value = Integer.parseInt(_ts.substring(0, _ts.indexOf('.')));
	                            __dtype = ExcelConstants.DATA_TYPE_INTEGER;
                        	}catch(Exception ex){
                                value = Long.parseLong(_ts.substring(0, _ts.indexOf('.')));
                                __dtype = ExcelConstants.DATA_TYPE_LONG;                        		
                        	}
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

    /**
     * 把现有的行转换为数据处理的行，主要是处理合并单元格，若行合并，则合并数据列，若列合并，看主合并单元格的数据类型，若是字符串则该列值同主单元格，若是数值，则为空
     * @param rowData 某一行数据
     * @return 数据处理的行信息
     */
    public List<Map<String, Object>> convert2DataRow(List<Map<String, Object>> rowData) {
        if (rowData==null||rowData.size()==0) return null;
        List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
        Map<String, Object> mainMergedCellMap = null;
        for (Map<String, Object> _cellMap: rowData) {
            mainMergedCellMap = (Boolean)_cellMap.get("isMerged")?mainMergedCellList.get(_cellMap.get("mainMergedLabel")):null;
            if (mainMergedCellMap==null) ret.add(_cellMap);
            else {
                if ((Integer)mainMergedCellMap.get("firstCol")<(Integer)_cellMap.get("cellCol")) continue; //若列不同，则不进行处理
                if ((Integer)mainMergedCellMap.get("firstRow")<(Integer)_cellMap.get("cellRow")) {//若本单元格不是主合并单元格
                    //根据主单元格数据类型判断是否需要填充本单元格信息，注意这样的单元格应该单独处理
                    if ((Integer)((Map<String, Object>)mainMergedCellMap.get("transData")).get("dType")==ExcelConstants.DATA_TYPE_STRING) {
                        _cellMap.put("transData", mainMergedCellMap.get("transData"));
                        _cellMap.put("nativeData", mainMergedCellMap.get("nativeData"));
                    }
                }
                ret.add(_cellMap);
            }
        }
        return ret.size()>0?ret:null;
    }
    
    //=============以下对单元格判断之间的关系
    /*
     * 判断两个单元格是为相同的列，主要用于合并单元格的处理
     * @param cell1 第一单元格
     * @param cell2 第二单元格
     * @return 若是相同的列，返回true，否则返回false
     */
    private boolean sameColumn(Map<String, Object> cell1, Map<String, Object> cell2) {
        return ((Integer)cell1.get("firstCol")==(Integer)cell2.get("firstCol")&&(Integer)cell1.get("lastCol")==(Integer)cell2.get("lastCol"));
    }

    /*
     * 判断两个单元格是为相同的行，主要用于合并单元格的处理
     * @param cell1 第一单元格
     * @param cell2 第二单元格
     * @return 若是相同的行，返回true，否则返回false
    private boolean sameRow(Map<String, Object> cell1, Map<String, Object> cell2) {
        return ((Integer)cell1.get("firstRow")==(Integer)cell2.get("firstRow")&&(Integer)cell1.get("lastRow")==(Integer)cell2.get("lastRow"));
    }
     */

    /*
     * 判断第一个单元格列是否包括第二个单元格列，主要用于合并单元格的处理
     * @param cell1 第一单元格
     * @param cell2 第二单元格
     * @return 若是包含的列，返回true，否则返回false
    private boolean containColumns(Map<String, Object> cell1, Map<String, Object> cell2) {
        if ((Integer)cell1.get("firstRow")==(Integer)cell2.get("firstRow")&&(Integer)cell1.get("lastRow")==(Integer)cell2.get("lastRow")) return false;
        if ((Integer)cell1.get("firstRow")>=(Integer)cell2.get("firstRow")&&(Integer)cell1.get("lastRow")<=(Integer)cell2.get("lastRow")) return true;
        return false;
    }
     */

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

    /*
     * 判断第一个单元格是否和第二个单元格是否相同的列结构，主要用于合并单元格的处理
     * @param cell1 第一单元格
     * @param cell2 第二单元格
     * @return 若是包含的行，返回true，否则返回false
     */
    private boolean sameColumnStruct(Map<String, Object> cell1, Map<String, Object> cell2) {
        if ((Integer)cell1.get("firstCol")==(Integer)cell2.get("firstCol")&&(Integer)cell1.get("lastCol")==(Integer)cell2.get("lastCol")) return false;
        if ((Integer)cell1.get("firstCol")>=(Integer)cell2.get("firstCol")&&(Integer)cell1.get("lastCol")<=(Integer)cell2.get("lastCol")) return true;
        return false;
    }

    //=============以下对行进行判断，包括行内，和行之间的关系
    /**
     * 判断某一行是否为空，其单元格内的信息都为空
     * @param rowList 行数据，以cellMap为list中的元素
     * @return
     */
    public boolean isEmptyRow(List<Map<String, Object>> rowList) {
        if (rowList==null||rowList.size()==0) return true;
        for(Map<String, Object> _cellMap: rowList) {
            Map<String, Object> nativeData = (Map<String, Object>)_cellMap.get("nativeData");
            if (nativeData.get("value")!=null) return false;
        }
        return true;
    }

    /*
     * 判断行是否是由原始String类型列组成的行
     * @param row 行数据，以cellMap为list中的元素
     * @return 若一样返回true，否则返回false
    private boolean isNativeStingTypeRow(List<Map<String, Object>> row) {
        if (row==null||row.size()==0) return false;
        Map<String, Object> compareCellMap = null;//用于判断的map
        for(Map<String, Object> _cellMap: row) {
            compareCellMap = _cellMap;
            if ((Boolean)_cellMap.get("isMerged")) {
                compareCellMap = mainMergedCellList.get(_cellMap.get("mainMergedLabel"));
            }
            Map<String, Object> nativeData = (Map<String, Object>)compareCellMap.get("nativeData");
            int dType = (Integer)nativeData.get("dType");
            if (dType!=ExcelConstants.DATA_TYPE_STRING&&dType!=ExcelConstants.DATA_TYPE_NULL) return false;
        }
        return true;
    }
     */

    /**
     * 判断行是否为某一区域的最后一行，用于判断是否是表头
     * @param row 行数据，以cellMap为list中的元素
     * @return 若是最后一行返回true，否则返回false
     */
    private boolean isAreaBottomRow(List<Map<String, Object>> row) {
        if (row==null||row.size()==0) return false;
        for(Map<String, Object> _cellMap: row) {
            if (((Boolean)_cellMap.get("isMerged"))==Boolean.TRUE) {
                int cellRow = (Integer)_cellMap.get("cellRow");
                int mergedLastRow = (Integer)_cellMap.get("lastRow");
                if (cellRow!=mergedLastRow) return false;
            }
        }
        return true;
    }
    /*
     * 比较两行是否列结构相似，以第一行为准
     * @param firstRow 第一行数据，以cellMap为list中的元素
     * @param secondRow 第二行数据，以cellMap为list中的元素
     * @return 若一样返回true，否则返回false
     */
    private boolean compareSimilarColumn2Row(List<Map<String, Object>> firstRow, List<Map<String, Object>> secondRow) {
        if (firstRow==null||firstRow.size()==0) return false;
        if (secondRow==null||secondRow.size()==0) return false;
        if (firstRow.size()<secondRow.size()) return false;
        for (int i=0; i<secondRow.size(); i++) {
            Map<String, Object> cell1 = firstRow.get(i);
            Map<String, Object> cell2 = secondRow.get(i);
            if (!sameColumn(cell1, cell2)) return false;
        }
        return true;
    }

    /**
     * 找到匹配的列表头信息
     * @param cell 单元格信息
     * @param sti Sheet中的表结构区域信息
     * @return 匹配的列表头信息
     */
    public Map<String, Object> findMatchTitle(Map<String, Object> cellMap, SheetTableInfo sti) {
        if (sti==null||sti.getTitleInfo()==null||sti.getTitleInfo().size()==0) throw new IllegalArgumentException("Sheet中的表结构区域信息必须设置，且表头信息不能为空");
        if (this.getSheetInfo()==null||this.getSheetInfo().getStiList()==null) return null;
        else {
            boolean isMate = false;
            for (SheetTableInfo _sti: this.getSheetInfo().getStiList()) {
                if (_sti.equals(sti)) {
                    isMate=true;
                    break;
                }
            }
            if (!isMate) throw new IllegalArgumentException("参数：sti(表结构区域信息)必须与本解析器表结构区域信息列表相匹配");
        }
        for (Map<String, Object> titleCol: sti.getTitleInfo()) {
            if ((Integer)titleCol.get("firstCol")==(Integer)cellMap.get("firstCol")) return titleCol;
        }

        return null;
    }
}

/**
 * 分析表结构，得到元数据信息
 * @author wh
 */
class Thread_anay_MetadataTableInfo implements Runnable {
    private PoiParseUtils caller;
    private SheetTableInfo _anayData;
    private List<Integer> numList;
    private List<String> logl;

    public Thread_anay_MetadataTableInfo(SheetTableInfo anayData, PoiParseUtils caller, List<Integer> numList, List<String> logl) {
        this.caller = caller;
        this._anayData = anayData;
        this.numList = numList;
        this.logl = logl;
    }

    @Override
    public void run() {
        try {
            this.caller._analSheetMetadata(_anayData, this.numList, this.logl);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}