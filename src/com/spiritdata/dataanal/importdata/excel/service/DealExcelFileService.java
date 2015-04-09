package com.spiritdata.dataanal.importdata.excel.service;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.spiritdata.filemanage.category.ANAL.model.AnalResultFile;
import com.spiritdata.filemanage.category.ANAL.service.AanlResultFileService;
import com.spiritdata.filemanage.core.enumeration.RelType1;
import com.spiritdata.filemanage.core.model.FileInfo;
import com.spiritdata.filemanage.core.model.FileRelation;
import com.spiritdata.filemanage.core.service.FileManageService;
import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.UGA.UgaUser;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.dataanal.common.util.SessionUtils;
import com.spiritdata.dataanal.dictionary.model._OwnerDictionary;
import com.spiritdata.dataanal.dictionary.service.DictSessionService;
import com.spiritdata.dataanal.importdata.excel.ExcelConstants;
import com.spiritdata.dataanal.importdata.excel.pojo.SheetTableInfo;
import com.spiritdata.dataanal.importdata.excel.pojo.SheetInfo;
import com.spiritdata.dataanal.importdata.excel.util.PoiParseUtils;
import com.spiritdata.dataanal.metadata.relation.pojo.ImpTableMapRel;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataColumn;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataTableMapRel;
import com.spiritdata.dataanal.metadata.relation.pojo._OwnerMetadata;
import com.spiritdata.dataanal.metadata.relation.semanteme.func.AnalCoord;
import com.spiritdata.dataanal.metadata.relation.semanteme.func.AnalDict;
import com.spiritdata.dataanal.metadata.relation.semanteme.func.AnalKey;
import com.spiritdata.dataanal.metadata.relation.service.MdBasisService;
import com.spiritdata.dataanal.metadata.relation.service.MdDictService;
import com.spiritdata.dataanal.metadata.relation.service.MdKeyService;
import com.spiritdata.dataanal.metadata.relation.service.MdQuotaService;
import com.spiritdata.dataanal.metadata.relation.service.MdSessionService;
import com.spiritdata.dataanal.metadata.relation.service.TableMapService;

//import java.util.Enumeration;
//import org.apache.log4j.FileAppender;


/**
 * 处理excel文件。
 * 导入系统，并进行初步分析，主要是导入
 * @author wh
 */
public class DealExcelFileService {
    private Logger logger = Logger.getLogger(DealExcelFileService.class);

    @Resource
    private MdSessionService mdSessionService;
    @Resource
    private MdQuotaService mdQutotaService;
    @Resource
    private DataSource dataSource;
    @Resource
    private TableMapService tmServier;
    @Resource
    private MdBasisService mdBasisServcie;
    //key分析
    @Resource
    private AnalKey analKey;//只分析,并计入文件
    @Resource
    private MdKeyService mdKeyService;//调整表
    //dict分析
    @Resource
    private DictSessionService dictSessionService;
    @Resource
    private AnalDict analDict;//只分析,并计入文件
    @Resource
    private MdDictService mdDictService;//只分析,并计入文件
    @Resource
    private BuildReportAfterUploadService buildReport;
    //文件操作
    @Resource
    private FileManageService fmService;
    @Resource
    private AanlResultFileService arFileService;
    @Resource
    private AnalCoord analCoord;//只分析可能坐标列,并计入文件
    
    /**
     * 处理Excel文件
     * @param fi 导入的文件的fileInfo信息
     * @param session 用户Session
     */
    public void process(FileInfo fi, HttpSession session) {
        /*
        Enumeration ea = logger.getAllAppenders();
        Logger l = logger.getRootLogger();
        ea = l.getAllAppenders();
        FileAppender myLogAppender = new FileAppender();
        myLogAppender.setName("test");
        myLogAppender.setFile("d:\\test.log");
        org.apache.log4j.PatternLayout lay = new org.apache.log4j.PatternLayout();
        lay.setConversionPattern("%d{yyyy-MM-dd HH:mm:ss} %5p %c{1}:%L : %m%n");
        myLogAppender.setLayout(lay);
        logger.addAppender(myLogAppender);
        ea = logger.getAllAppenders();
        myLogAppender.activateOptions();
        logger.info("abcd");
        */

        File excelFile = new File(fi.getAllFileName());
        Workbook book = null;
        int excelType = 0;
        FileInputStream fis = null;
        try {
            //获得处理excel的workbook
        	logger.info("start process excel file ...");
            try {
                fis = new FileInputStream(excelFile);
                book = new HSSFWorkbook(fis);
                excelType = ExcelConstants.EXECL2003_FLAG;
            } catch (Exception e) {
//                e.printStackTrace();
            }
            if (book==null) {
                try {
                    fis = new FileInputStream(excelFile);
                    book = new XSSFWorkbook(fis);
                    excelType = ExcelConstants.EXECL2007_FLAG;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (excelType==0) {
                logger.info("以excel格式读取文件["+fi.getAllFileName()+"]失败");
                //TODO 不支持的版本信息要提示到用户界面去
                return;
            }

            //根据sheet进行处理
            int i=0;
            PoiParseUtils parseExcel;
            SheetInfo si;

            //1-分析文件，得到元数据信息，并把分析结果存入si
            logger.info("start analysis meta data info ...");
            List<PoiParseUtils> excelParseList = new ArrayList<PoiParseUtils>();

//          Map<SheetInfo, Object> sheetLogMap = new HashMap<SheetInfo, Object>();
            for (; i<book.getNumberOfSheets(); i++) {
                try {//处理每个Sheet，并保证某个Sheet处理失败后，继续处理后续Sheet
                    Sheet sheet = book.getSheetAt(i);
                    si = initSheetInfo(sheet, excelType, i);
                    si.setFileName(fi.getAllFileName());
                    parseExcel = new PoiParseUtils(si);
                    List<String> logl = parseExcel.analSheetMetadata();
                    excelParseList.add(parseExcel);
                } catch(Exception e) {
                    
                }
            }
            //记录日志
            /**
            //写json文件，此方法目前为测试方法，今后把他变为一个更好用的包
            Map<String, Object> jsonMap = new HashMap<String, Object>();
            jsonMap.put("_id", SequenceUUID.getUUID());
            jsonMap.put("_code", "SD.TEAM.LOG-0001");
            jsonMap.put("_cTime", (new Date()).getTime());
            jsonMap.put("desc", "分析文件["+fileName+"]元数据结构的日志文件");
            Map<String, Object> _DATA_Map = new HashMap<String, Object>();
            AtomData _dataElement = new AtomData("string", fileName);
            _DATA_Map.put("_tableName", _dataElement.toJsonMap());
            _dataElement.clean();
            _dataElement.setAtomData("string", mm.getId());
            _DATA_Map.put("_mdMId", _dataElement.toJsonMap());
            _DATA_Map.put("_keyAnals", convertToList(ret));
            jsonMap.put("_DATA", _DATA_Map);
            //写文件
            String root = (String)(SystemCache.getCache(FConstants.APPOSPATH)).getContent();
            //文件格式：analData\{用户名}\MM_{模式Id}\keyAnal\tab_{TABId}.json
            String storeFile = FileNameUtils.concatPath(root, "analData"+File.separator+mm.getOwnerId()+File.separator+"MM_"+mm.getId()+File.separator+"keyAnal"+File.separator+tableName+".json");
            jsonMap.put("_file", storeFile);
            FileOutputStream fileOutputStream = null;
            try {
                File file = new File(storeFile);
                if (!file.exists()) {
                    File dirs = new File(FileNameUtils.getFilePath(storeFile));
                    if (!dirs.exists()) dirs.mkdirs();
                    file.createNewFile();
                }
                fileOutputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileOutputStream!=null) {
                    try {fileOutputStream.close();}catch(IOException e) {e.printStackTrace();}
                }
            }
*/
            //2-获得元数据后，数据存储及语义分析功能
            logger.info("start data save and semantic analysis ...");
            if (excelParseList.size()>0) {
                //准备缓存或Session
                _OwnerMetadata _om = mdSessionService.loadcheckData(session);
                _OwnerDictionary _od = dictSessionService.loadcheckData(session);
                //为生成报告准备的数据
                Map<SheetInfo, Map<SheetTableInfo, Map<String, Object>>> reportParam=null;

                for (i=0; i<excelParseList.size(); i++) {
                    if (reportParam==null) reportParam = new HashMap<SheetInfo, Map<SheetTableInfo, Map<String, Object>>>();

                    parseExcel = excelParseList.get(i);
                    si = parseExcel.getSheetInfo();
                    if (si.getStiList()==null||si.getStiList().size()==0) continue;
//                    for (SheetTableInfo sti: si.getStiList()) {
                    for(int j=0;j<si.getStiList().size();j++){
                    	SheetTableInfo sti = si.getStiList().get(j);
                        //准备报告数据:1:begin
                        Map<SheetTableInfo, Map<String, Object>> m = reportParam.get(si);
                        if (m==null) {
                            m = new HashMap<SheetTableInfo, Map<String, Object>>();
                            reportParam.put(si, m);
                        }
                        //准备报告数据:1:end

                        try {//--处理sheet中的每个元数据
                            //--保存分析后的元数据信息，包括数据表的注册与创建
                            //-- 若元数据信息在系统中已经存在，则只生成临时表
                            //-- 否则，创建新的元数据，并生成积累表和临时表
                            MetadataTableMapRel[] tabMapOrgAry = mdSessionService.storeMdModel4Import(sti.getMm(), _om);
                            //处理sa_imp_tablog_rel表，只记录临时表，不记录积累表，通过临时表或元数据ID可以查到积累表
                            ImpTableMapRel itmr = new ImpTableMapRel();
                            itmr.setFId(fi.getId());
                            itmr.setTmoId(tabMapOrgAry[1].getId());
                            itmr.setMdMId(tabMapOrgAry[0].getMdMId());
                            itmr.setSheetName(si.getSheetName());
                            itmr.setSheetIndex(si.getSheetIndex());
                            itmr.setTableTitleName(sti.getTableTitleName());
                            tmServier.bindImpTabMap(itmr);

                            //准备报告数据:2:begin
                            Map<String, Object> m2 = m.get(sti);
                            if (m2==null) {
                                m2 = new HashMap<String, Object>();
                                m.put(sti, m2);
                            }
                            m2.put("tabMapOrgAry", tabMapOrgAry);
                            //准备报告数据:2:end

                            // TODO 为了有更好的处理响应时间，以下逻辑可以采用多线程处理

                            //--获得系统保存的与当前Excel元数据信息匹配的元数据信息
                            MetadataModel sysMd = _om.getMetadataById(tabMapOrgAry[0].getMdMId());

                            //准备报告数据:3:begin
                            m2.put("sysMd", sysMd);
                            //准备报告数据:3:end

                            //处理Title内容
                            String maxTitle = sti.getTableTitleName();
                            if (sysMd.titleMap==null) sysMd.titleMap= new HashMap<String, Integer>();
                            if (sysMd.titleMap.get(maxTitle)==null) sysMd.titleMap.put(maxTitle, 1);
                            else sysMd.titleMap.put(maxTitle, sysMd.titleMap.get(maxTitle)+1);
                            Integer flagI = 0, mV;
                            for (Entry<String, Integer> entry: sysMd.titleMap.entrySet()) {
                                mV = entry.getValue();
                                if (mV>flagI) {
                                    flagI = mV;
                                    maxTitle = entry.getKey();
                                }
                            }
                            if (!maxTitle.equals(sysMd.getTitleName())) {//改写内容，包括数据库
                                sysMd.setTitleName(maxTitle);
                                MetadataModel uMM = new MetadataModel();
                                uMM.setId(sysMd.getId());
                                uMM.setTitleName(maxTitle);
                                mdBasisServcie.updateMdM(uMM);
                            }
                            //2-储存临时表
                            String logPreStr = " (sheet:"+i+" table:"+j+")";
                            logger.info(logPreStr + " start save data to tmpTable ...");
                            saveDataToTempTab(sti, sysMd, tabMapOrgAry[1].getTableName(), parseExcel);
                            //3-临时表指标分析
                            logger.info(logPreStr + " start analysis quota table ...");
                            mdQutotaService.caculateQuota(tabMapOrgAry[1]); //分析临时表指标
                            //4-主键分析
                            //4.1-临时表主键分析
                            logger.info(logPreStr + " start analysis primary key ...");
                            Map<String, Object> keyMap = analKey.scanOneTable(tabMapOrgAry[1].getTableName(), sysMd, null);
                            if (keyMap!=null) {
                                AnalResultFile arf = (AnalResultFile)keyMap.get("resultFile");
                                FileInfo arFi = arFileService.saveFile(arf);//分析jsonD存储
                                //4.2-文件关系存储
                                FileRelation fr = new FileRelation();
                                fr.setElement1(fi.getFileCategoryList().get(0));
                                fr.setElement2(arFi.getFileCategoryList().get(0));
                                fr.setCTime(new Timestamp((new Date()).getTime()));
                                fr.setRType1(RelType1.POSITIVE);
                                fr.setRType2("语义分析-主键");
                                fr.setDesc("分析["+si.getSheetName()+"(sheet"+si.getSheetIndex()+")("+sti.getTableTitleName()+")]的主键");
                                fmService.saveFileRelation(fr);//文件关联存储
                                //4.3-主键分析结果应用
                                try{
                                	mdKeyService.adjustMdKey(sysMd); //分析主键，此方法执行后，若分析出主键，则已经修改了模式对应的积累表的主键信息
                                }catch(Exception ex){
                                	ex.printStackTrace();
                                }
                            }
                            //5-存储积累表
                            logger.info(logPreStr + " start save accumulate table ...");
                            saveDataToAccumulationTab(sti, sysMd, parseExcel);
                            //6-积累表指标分析
                            logger.info(logPreStr + " start analysis accumulate table quota ...");
                            mdQutotaService.caculateQuota(tabMapOrgAry[0]); //分析积累表指标
                            //7-元数据语义分析
                            // TODO 分析元数据语义，目前想到——字典项/身份证/经纬度/URL分析/mail地址分析/姓名分析；另外（列之间关系，如数值的比例等）
                            //7.1-分析字典
                            //7.1.1-积累表字典分析
                            logger.info(logPreStr + " start analysis dict key ...");
                            keyMap = analDict.scanMetadata(sysMd, null);
                            if (keyMap!=null) {
                                AnalResultFile arf = (AnalResultFile)keyMap.get("resultFile");
                                FileInfo arFi = arFileService.saveFile(arf);//分析jsonD存储
                                //7.1.2-文件关系存储
                                FileRelation fr = new FileRelation();
                                fr.setElement1(fi.getFileCategoryList().get(0));
                                fr.setElement2(arFi.getFileCategoryList().get(0));
                                fr.setCTime(new Timestamp((new Date()).getTime()));
                                fr.setRType1(RelType1.POSITIVE);
                                fr.setRType2("语义分析-字典项");
                                fr.setDesc("分析["+si.getSheetName()+"(sheet"+si.getSheetIndex()+")("+sti.getTableTitleName()+")]的字典项");
                                fmService.saveFileRelation(fr);//文件关联存储
                                keyMap.remove("resultFile");
                            }
                            //7.1.3-字典分析结果调整
                            //--获得系统保存的与当前Excel元数据信息匹配的元数据信息
                            logger.info(logPreStr + " start adjust dict key ...");
                            mdDictService.adjustMdDict(sysMd, keyMap, tabMapOrgAry[1].getTableName(), _od); //分析主键，此时，若分析出主键，则已经修改了模式对应的积累表的主键信息
                            
                            //7.2-分析坐标列
                            //7.2.1-积累表坐标列分析
                            logger.info(logPreStr + " start analysis coord key ...");
                            Map<String, Object> coordMap = analCoord.scanMetadata(sysMd, null);
                        } catch(Exception e) {
                            // TODO 记录日志 
                            e.printStackTrace();
                        }
                    }
                }
                //8-生成report，这个也可以不在这里处理，而通过任务启动
                //这个报告是对整个excel文件的，而不是对文件中的某一个表或Seet的
                logger.info("start genrate report ...");
                Map<String, Object> param = new HashMap<String, Object>();
                Map<String, Object> preTreadParam = new HashMap<String, Object>();
                preTreadParam.put("reportParam", reportParam);
                preTreadParam.put("owner", SessionUtils.getOwner(session));
                preTreadParam.put("impFileInfo", fi);
                preTreadParam.put("ownerDict", _od);
                param.put("preTreadParam", preTreadParam);
                buildReport.buildANDprocess(param);
            }
        } catch(Exception e) {
            // TODO 写日志
            e.printStackTrace();
        } finally {
            try {if (fis!=null) fis.close(); } catch (Exception e) {e.printStackTrace();} finally {fis = null;};
        }
    }

    /*
     * 根据sheet或的sheetInfo
     * @param sheet
     * @param excelType
     * @return
     */
    private SheetInfo initSheetInfo(Sheet sheet, int excelType, int sheetIndex) {
        SheetInfo ret = new SheetInfo();
        ret.setExcelType(excelType);
        ret.setSheet(sheet);
        ret.setSheetIndex(sheetIndex);
        ret.setSheetName(sheet.getSheetName());
        return ret;
    }

    /*
     * 保存临时表信息
     * @param sti Sheet中的表结构区域信息
     * @param sysMm 元数据信息（已在系统注册过的）
     * @param tempTableName 临时表名称
     * @param parse excel解析器
     */
    private void saveDataToTempTab(SheetTableInfo sti, MetadataModel sysMm, String tempTableName, PoiParseUtils parse) {
        if (StringUtils.isNullOrEmptyOrSpace(tempTableName)) throw new IllegalArgumentException("临时表名称必须确定");
        if (sysMm==null||sysMm.getColumnList()==null||sysMm.getColumnList().size()==0) throw new IllegalArgumentException("元数据模型必须设置，且列信息不能为空");
        if (sti==null||sti.getTitleInfo()==null||sti.getTitleInfo().size()==0) throw new IllegalArgumentException("Sheet中的表结构区域信息必须设置，且表头信息不能为空");
        if (parse==null||parse.getSheetInfo()==null||parse.getSheetInfo().getStiList()==null) throw new IllegalArgumentException("excel解析单元必须设置");
        else {
            boolean isMate = false;
            for (SheetTableInfo _sti: parse.getSheetInfo().getStiList()) {
                if (_sti.equals(sti)) {
                    isMate=true;
                    break;
                }
            }
            if (!isMate) throw new IllegalArgumentException("参数：paras(excel解析单元)必须与参数：sti(表结构区域信息)相匹配");
        }

        Object[] paramArray = new Object[sysMm.getColumnList().size()];
        String insertSql = "insert into "+tempTableName+"(#columnSql) values(#valueSql)", columnSql="", valueSql="";
        for (MetadataColumn mc: sysMm.getColumnList()) {
            columnSql+=","+mc.getColumnName();
            valueSql+=",?";
        }
        if (columnSql.trim().length()>0) columnSql=columnSql.substring(1);
        if (valueSql.trim().length()>0) valueSql=valueSql.substring(1);
        insertSql = insertSql.replaceAll("#columnSql", columnSql).replaceAll("#valueSql", valueSql);

        Connection conn = null;
        PreparedStatement ps = null;

        Map<String, Object> titleCol = null;
        //日志信息准备
//        int _log_readAllCount/*读取总行数*/, _log_insertOkCount=0/*新增成功行数*/, _log_insertFailCount=0/*新增失败行数*/, _log_ignoreCount=0/*忽略行数*/;
        Map<Integer, String> _log_failMap = new HashMap<Integer, String>();//新增失败的行及其原因
        Map<Integer, String> _log_ignoreMap = new HashMap<Integer, String>();//忽略行及其原因

        boolean autoCommit = false;
        try {
            conn = dataSource.getConnection();
            autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(true);
            ps = conn.prepareStatement(insertSql);
            
            List<Map<String, Object>> rowData = null;
//            _log_readAllCount = sti.getEndY()-sti.getBeginY()+1;
            for (int i=sti.getBeginY(); i<=sti.getEndY(); i++) {
                rowData = parse.readOneRow(i);
                rowData = parse.convert2DataRow(rowData);
                if (parse.isEmptyRow(rowData)) {
  //                  _log_ignoreCount++;
                    _log_ignoreMap.put(i, "第"+i+"行为空行。");
                    continue;
                }
                for (int j=0; j<paramArray.length; j++) paramArray[j]=null;

                int _mmDType, _infoDType;
                Object v;
                for (Map<String, Object> cell: rowData) {
                    titleCol = parse.findMatchTitle(cell, sti);
                    if (titleCol!=null) {
                        for (int k=0; k<sysMm.getColumnList().size(); k++) {
                            MetadataColumn mc = sysMm.getColumnList().get(k);
                            _mmDType = ExcelConstants.convert2DataType(mc.getColumnType());
                            _infoDType = -1;
                            if (mc.getTitleName().equals((String)titleCol.get("title"))) {
                                Map<String, Object> kv = (Map<String, Object>)cell.get("transData");
                                _infoDType = (Integer)kv.get("dType");
                                v = null;
                                if (_infoDType==_mmDType) v = kv.get("value");
                                else {
                                    kv = (Map<String, Object>)cell.get("nativeData");
                                    _infoDType = (Integer)kv.get("dType");
                                    if (_infoDType==_mmDType) v = kv.get("value");
                                    else if (_mmDType==ExcelConstants.DATA_TYPE_DOUBLE) {
                                        if (_infoDType==ExcelConstants.DATA_TYPE_INTEGER||_infoDType==ExcelConstants.DATA_TYPE_NUMERIC) {
                                            v = kv.get("value");
                                        }
                                    }
                                }
                                if (_mmDType==ExcelConstants.DATA_TYPE_STRING&&v==null) {
                                    v=((Map<String, Object>)cell.get("transData")).get("value")+"";
                                }
                                paramArray[k]=v;
                            }
                        }
                    }
                }
                boolean canInsert = false;
                for (int j=0; j<paramArray.length; j++) {
                    if (paramArray[j]!=null) {
                        canInsert = true;
                        break;
                    }
                }
                if (!canInsert) {
//                    _log_ignoreCount++;
                    _log_ignoreMap.put(i, "第"+i+"行数据与元数据不匹配，行数据为<<>>，元数据为<<>>。");
                    continue;
                }
                try{
                    for (int j=0; j<paramArray.length; j++) {
                        ps.setObject(j+1, paramArray[j]);
                    }
                    int insertOk = ps.executeUpdate();
                    if (insertOk>0) {
  //                      _log_insertOkCount += insertOk;
                    } else {
    //                    _log_insertFailCount++;
                        _log_failMap.put(i,  "第"+i+"行数据新增失败，原因未知！");
                    }
                } catch(SQLException sqlE) {
      //              _log_insertFailCount++;
                    _log_failMap.put(i,  "第"+i+"行数据新增失败，原因为："+sqlE.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (ps!=null) {ps.close();ps = null;} } catch (Exception e) {e.printStackTrace();} finally {ps = null;};
            try { if (conn!=null) {conn.setAutoCommit(autoCommit);conn.close();conn = null;} } catch (Exception e) {e.printStackTrace();} finally {conn = null;};
        }
    }

    /*
     * 保存积累表信息
     * @param em 从Excel中分析出来的元数据信息，注意，这里包括sheet信息
     * @param sysMm 元数据信息（已在系统注册过的），这其中包括积累表信息
     */
    private void saveDataToAccumulationTab(SheetTableInfo sti, MetadataModel sysMm, PoiParseUtils parse) {
        if (sysMm==null||sysMm.getColumnList()==null||sysMm.getColumnList().size()==0) throw new IllegalArgumentException("元数据模型必须设置，且列信息不能为空");
        String mainTableName = sysMm.getTableName();
        if (StringUtils.isNullOrEmptyOrSpace(mainTableName)) throw new IllegalArgumentException("元数据模型中必须有积累表名称");
        if (sti==null||sti.getTitleInfo()==null||sti.getTitleInfo().size()==0) throw new IllegalArgumentException("Sheet中的表结构区域信息必须设置，且表头信息不能为空");
        if (parse==null||parse.getSheetInfo()==null||parse.getSheetInfo().getStiList()==null) throw new IllegalArgumentException("excel解析单元必须设置");
        else {
            boolean isMate = false;
            for (SheetTableInfo _sti: parse.getSheetInfo().getStiList()) {
                if (_sti.equals(sti)) {
                    isMate=true;
                    break;
                }
            }
            if (!isMate) throw new IllegalArgumentException("参数：paras(excel解析单元)必须与参数：sti(表结构区域信息)相匹配");
        }

        String insertSql = "insert into "+mainTableName+"(#columnSql) values(#valueSql)", columnSql="", valueSql="";
        String updateSql = "update "+mainTableName+" set #updateSet where #updateKey", updateSet="", updateKey="";
        Object[] paramArray4Insert = new Object[sysMm.getColumnList().size()];
        Object[] paramArray4Update = new Object[sysMm.getColumnList().size()];
        Map<String, Integer> insertColIndexMap = new HashMap<String, Integer>();
        Map<String, Integer> updateColIndexMap = new HashMap<String, Integer>();
        for (int k=0; k<sysMm.getColumnList().size(); k++) {
            MetadataColumn mc = sysMm.getColumnList().get(k);
            columnSql+=","+mc.getColumnName();
            valueSql+=",?";
            insertColIndexMap.put(mc.getColumnName(), k);
            if (!mc.isPk()) {
                updateSet += ","+mc.getColumnName()+"=?";
            } else {
                updateKey += " and "+mc.getColumnName()+"=?";
            }
        }
        if (columnSql.trim().length()>0) columnSql=columnSql.substring(1);
        if (valueSql.trim().length()>0) valueSql=valueSql.substring(1);
        insertSql = insertSql.replaceAll("#columnSql", columnSql).replaceAll("#valueSql", valueSql);
        if (updateKey.trim().length()==0||updateSet.trim().length()==0) updateSql=null;
        else {
            if (updateSet.trim().length()>0) updateSet=updateSet.substring(1);
            if (updateKey.trim().length()>0) updateKey=updateKey.substring(5);
            updateSql = updateSql.replaceAll("#updateSet", updateSet).replaceAll("#updateKey", updateKey);
            String[] s = updateSet.split(",");
            int k=0;
            for (; k<s.length; k++) updateColIndexMap.put(s[k].substring(0, s[k].length()-2), k);
            s = updateKey.split(" and ");
            for (int l=0; l<s.length; l++) updateColIndexMap.put(s[l].substring(0, s[l].length()-2), k+l);
        }

        Connection conn = null;
        PreparedStatement psInsert = null;
        PreparedStatement psUpdate = null;

        Map<String, Object> titleCol = null;
        //日志信息准备
        int _log_readAllCount/*读取总行数*/, _log_insertOkCount=0/*新增成功行数*/,_log_updateOkCount=0/*新增成功行数*/, _log_saveFailCount=0/*新增失败行数*/, _log_ignoreCount=0/*忽略行数*/;
        Map<Integer, String> _log_failMap = new HashMap<Integer, String>();//存储失败的行及其原因
        Map<Integer, String> _log_ignoreMap = new HashMap<Integer, String>();//忽略行及其原因

        boolean autoCommit = false;
        try {
            conn = dataSource.getConnection();
            autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(true);
            psInsert = conn.prepareStatement(insertSql);
            if (updateSql!=null) psUpdate = conn.prepareStatement(updateSql);

            int keyCount=0;
            int _mmDType, _infoDType;
            Object v;
            String tagStr;

            List<Map<String, Object>> rowData = null;
            _log_readAllCount = sti.getEndY()-sti.getBeginY()+1;
            for (int i=sti.getBeginY(); i<=sti.getEndY(); i++) {
                rowData = parse.readOneRow(i);
                rowData = parse.convert2DataRow(rowData);
                if (parse.isEmptyRow(rowData)) {
                    _log_ignoreCount++;
                    _log_ignoreMap.put(i, "第"+i+"行为空行。");
                    continue;
                }
                for (int j=0; j<paramArray4Insert.length; j++) paramArray4Insert[j]=null;
                if (updateSql!=null) for (int j=0; j<paramArray4Update.length; j++) paramArray4Update[j]=null;

                keyCount=0;
                tagStr = "";
                for (Map<String, Object> cell: rowData) {
                    titleCol = parse.findMatchTitle(cell, sti);
                    if (titleCol!=null) {
                        for (int k=0; k<sysMm.getColumnList().size(); k++) {
                            MetadataColumn mc = sysMm.getColumnList().get(k);
                            _mmDType = ExcelConstants.convert2DataType(mc.getColumnType());
                            _infoDType = -1;
                            if (tagStr.indexOf(","+k)!=-1) continue;
                            if (mc.getTitleName().equals((String)titleCol.get("title"))) {
                                Map<String, Object> kv = (Map<String, Object>)cell.get("transData");
                                _infoDType = (Integer)kv.get("dType");
                                v = null;
                                if (_infoDType==_mmDType) v = kv.get("value");
                                else {
                                    kv = (Map<String, Object>)cell.get("nativeData");
                                    _infoDType = (Integer)kv.get("dType");
                                    if (_infoDType==_mmDType) v = kv.get("value");
                                    else if (_mmDType==ExcelConstants.DATA_TYPE_DOUBLE) {
                                        if (_infoDType==ExcelConstants.DATA_TYPE_INTEGER||_infoDType==ExcelConstants.DATA_TYPE_NUMERIC) {
                                            v = kv.get("value");
                                        }
                                    }
                                    if (_mmDType==ExcelConstants.DATA_TYPE_STRING&&v==null) {
                                        v=((Map<String, Object>)cell.get("transData")).get("value")+"";
                                    }
                                }
                                paramArray4Insert[insertColIndexMap.get(mc.getColumnName())] = v;
                                if (updateSql!=null) paramArray4Update[updateColIndexMap.get(mc.getColumnName())] = v;
                                tagStr+=","+k;
                                break;
                            }
                        }
                    }
                }
                boolean canSave = false;
                for (int j=0; j<paramArray4Insert.length; j++) {
                    if (paramArray4Insert[j]!=null) {
                        canSave = true;
                        break;
                    }
                }
                if (!canSave) {
                    _log_ignoreCount++;
                    _log_ignoreMap.put(i, "第"+i+"行数据与元数据不匹配，行数据为<<>>，元数据为<<>>。");
                    continue;
                }
                //先修改，再新增
                boolean canInsert = true;
                int j=0;
                if (updateSql!=null) {
                    psUpdate.clearParameters();
                    try{
                        for (j=0; j<paramArray4Update.length; j++) {
                            psUpdate.setObject(j+1, paramArray4Update[j]);
                        }
                        int updateOk = psUpdate.executeUpdate();
                        if (updateOk>0) {
                            canInsert=false;
                            _log_updateOkCount += updateOk;
                        } else {
                            canInsert=true;
                        }
                        canInsert = !(psUpdate.executeUpdate()==1);
                    } catch(SQLException sqlE) {
                        canInsert=true;
                    }
                }
                if (canInsert) {
                    psInsert.clearParameters();
                    try {
                        for (j=0; j<paramArray4Insert.length; j++) {
                            psInsert.setObject(j+1, paramArray4Insert[j]);
                        }
                        int insertOk = psInsert.executeUpdate();
                        if (insertOk>0) {
                            _log_insertOkCount += insertOk;
                        } else {
                            _log_saveFailCount++;
                            _log_failMap.put(i,  "第"+i+"行数据新增失败，原因未知！");
                        }
                    } catch(SQLException sqlE) {
                        _log_saveFailCount++;
                        _log_failMap.put(i,  "第"+i+"行数据新增失败，原因为："+sqlE.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (psUpdate!=null) {psUpdate.close();psUpdate = null;} } catch (Exception e) {e.printStackTrace();} finally {psUpdate = null;};
            try { if (psInsert!=null) {psInsert.close();psInsert = null;} } catch (Exception e) {e.printStackTrace();} finally {psInsert = null;};
            try { if (conn!=null) {conn.setAutoCommit(autoCommit);conn.close();conn = null;} } catch (Exception e) {e.printStackTrace();} finally {conn = null;};
        }
    }
}
