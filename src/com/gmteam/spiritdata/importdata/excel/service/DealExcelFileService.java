package com.gmteam.spiritdata.importdata.excel.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.gmteam.framework.FConstants;
import com.gmteam.framework.core.cache.SystemCache;
import com.gmteam.framework.util.FileNameUtils;
import com.gmteam.framework.util.JsonUtils;
import com.gmteam.jsonD.model.AtomData;
import com.gmteam.spiritdata.SDConstants;
import com.gmteam.spiritdata.importdata.excel.ExcelConstants;
import com.gmteam.spiritdata.importdata.excel.pojo.SheetTableInfo;
import com.gmteam.spiritdata.importdata.excel.pojo.SheetInfo;
import com.gmteam.spiritdata.importdata.excel.util.PoiParseUtils;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataColumn;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;
import com.gmteam.spiritdata.metadata.relation.pojo.TableMapOrg;
import com.gmteam.spiritdata.metadata.relation.pojo._OwnerMetadata;
import com.gmteam.spiritdata.metadata.relation.semanteme.func.AnalKey;
import com.gmteam.spiritdata.metadata.relation.service.MdKeyService;
import com.gmteam.spiritdata.metadata.relation.service.MdQuotaService;
import com.gmteam.spiritdata.metadata.relation.service.MetadataSessionService;
import com.gmteam.spiritdata.util.SequenceUUID;

/**
 * 处理excel文件。
 * 导入系统，并进行初步分析，主要是导入
 * @author wh
 */
@Component
public class DealExcelFileService {
    private Logger logger = Logger.getLogger(DealExcelFileService.class);

    @Resource
    private MetadataSessionService mdSessionService;
    @Resource
    private MdQuotaService mdQutotaService;
    @Resource
    private MdKeyService mdKeyService ;
    @Resource
    private AnalKey analKey;
    @Resource
    private DataSource dataSource ;

    /**
     * 处理Excel文件
     * @param fileName 文件名称
     * @param session 用户Session
     */
    public void process(String fileName, HttpSession session) {
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

        File excelFile = new File(fileName);
        Workbook book = null;
        int excelType = 0;
        FileInputStream fis = null;

        try {
            //获得处理excel的workbook
            try {
                fis = new FileInputStream(excelFile);
                book = new HSSFWorkbook(fis);
                excelType = ExcelConstants.EXECL2003_FLAG;
            } catch (Exception e) {
            }
            if (book==null) {
                try {
                    fis = new FileInputStream(excelFile);
                    book = new XSSFWorkbook(fis);
                    excelType = ExcelConstants.EXECL2007_FLAG;
                } catch (Exception e) {
                }
            }
            if (excelType==0) {
                logger.info("以excel格式读取文件["+fileName+"]失败");
                return;
            }

            //根据sheet进行处理
            int i=0;
            PoiParseUtils parseExcel;
            SheetInfo si;

            //1-分析文件，得到元数据信息，并把分析结果存入si
            List<PoiParseUtils> excelParseList = new ArrayList<PoiParseUtils>();

            Map<SheetInfo, Object> sheetLogMap = new HashMap<SheetInfo, Object>();
            for (; i<book.getNumberOfSheets(); i++) {
                try {//处理每个Sheet，并保证某个Sheet处理失败后，继续处理后续Sheet
                    Sheet sheet = book.getSheetAt(i);
                    si = initSheetInfo(sheet, excelType, i);
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
                fileOutputStream.write((JsonUtils.formatJsonStr(JsonUtils.beanToJson(jsonMap), null)).getBytes()); 
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
            if (excelParseList.size()>0) {
                for (i=0; i<excelParseList.size(); i++) {
                    parseExcel = excelParseList.get(i);
                    si = parseExcel.getSheetInfo();
                    if (si.getStiList()==null||si.getStiList().size()==0) continue;
                    for (SheetTableInfo sti: si.getStiList()) {
                        try {//--处理sheet中的每个元数据
                            //--保存分析后的元数据信息，包括数据表的注册与创建
                            //-- 若元数据信息在系统中已经存在，则只生成临时表
                            //-- 否则，创建新的元数据，并生成积累表和临时表
                            mdSessionService.setSession(session);
                            TableMapOrg[] tabMapOrgAry = mdSessionService.storeMdModel4Import(sti.getMm());

                            // TODO 为了有更好的处理响应时间，以下逻辑可以采用多线程处理

                            //--获得系统保存的与当前Excel元数据信息匹配的元数据信息
                            _OwnerMetadata _om = (_OwnerMetadata)session.getAttribute(SDConstants.SESSION_OWNER_RMDUNIT);
                            MetadataModel sysMd = _om.getMetadataById(tabMapOrgAry[0].getMdMId());
                            //2-储存临时表
                            saveDataToTempTab(sti, sysMd, tabMapOrgAry[1].getTableName(), parseExcel);
                            //3-临时表分析
                            mdQutotaService.caculateQuota(tabMapOrgAry[1]); //分析临时表指标
                            analKey.scanOneTable(tabMapOrgAry[1].getTableName(), sysMd, null);
                            mdKeyService.adjustMdKey(sysMd); //分析主键，此时，若分析出主键，则已经修改了模式对应的积累表的主键信息
                            //4-存储积累表
                            if (sysMd.getTableName().equalsIgnoreCase(tabMapOrgAry[0].getTableName())) {
                                saveDataToAccumulationTab(sti, sysMd, parseExcel);
                                //5-积累表分析
                                mdQutotaService.caculateQuota(tabMapOrgAry[0]); //分析积累表指标
                                //6-元数据语义分析
                                // TODO 分析元数据语义，目前想到——字典项/身份证/经纬度/URL分析/mail地址分析/姓名分析；另外（列之间关系，如数值的比例等）
                            } else {
                                throw new Exception("元数据信息中的积累表名称与对应的TableMap中的表名称不相符!");
                            }
                        } catch(Exception e) {
                            // TODO 记录日志 
                            e.printStackTrace();
                        }
                    }
                }
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
        if (tempTableName==null||tempTableName.equals("")) throw new IllegalArgumentException("临时表名称必须确定");
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
        if (columnSql.length()>0) columnSql=columnSql.substring(1);
        if (valueSql.length()>0) valueSql=valueSql.substring(1);
        insertSql = insertSql.replaceAll("#columnSql", columnSql).replaceAll("#valueSql", valueSql);

        Connection conn = null;
        PreparedStatement ps = null;

        Map<String, Object> titleCol = null;
        //日志信息准备
        int _log_readAllCount/*读取总行数*/, _log_insertOkCount=0/*新增成功行数*/, _log_insertFailCount=0/*新增失败行数*/, _log_ignoreCount=0/*忽略行数*/;
        Map<Integer, String> _log_failMap = new HashMap<Integer, String>();//新增失败的行及其原因
        Map<Integer, String> _log_ignoreMap = new HashMap<Integer, String>();//忽略行及其原因

        boolean autoCommit = false;
        try {
            conn = dataSource.getConnection();
            autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(true);
            ps = conn.prepareStatement(insertSql);
            
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
                if (rowData.size()>sysMm.getColumnList().size()) {
                    _log_ignoreCount++;
                    _log_ignoreMap.put(i, "第"+i+"行数据，列个数与元数据列个数不匹配，行数据为<<>>，元数据为<<>>。");
                    continue;
                }
                for (int j=0; j<paramArray.length; j++) paramArray[j]=null;
                for (Map<String, Object> cell: rowData) {
                    titleCol = parse.findMatchTitle(cell, sti);
                    if (titleCol!=null) {
                        for (int k=0; k<sysMm.getColumnList().size(); k++) {
                            MetadataColumn mc = sysMm.getColumnList().get(k);
                            if (mc.getTitleName().equals((String)titleCol.get("title"))) {
                                Map<String, Object> kv = (Map<String, Object>)cell.get("transData");
                                if ((Integer)kv.get("dType")==ExcelConstants.convert2DataType(mc.getColumnType())) {
                                    paramArray[k] = kv.get("value");
                                } else {
                                    kv = (Map<String, Object>)cell.get("nativeData");
                                    if ((Integer)kv.get("dType")==ExcelConstants.convert2DataType(mc.getColumnType())) {
                                        paramArray[k] = kv.get("value");
                                    }
                                }
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
                    _log_ignoreCount++;
                    _log_ignoreMap.put(i, "第"+i+"行数据与元数据不匹配，行数据为<<>>，元数据为<<>>。");
                    continue;
                }
                try{
                    for (int j=0; j<paramArray.length; j++) {
                        ps.setObject(j+1, paramArray[j]);
                    }
                    int insertOk = ps.executeUpdate();
                    if (insertOk>0) {
                        _log_insertOkCount += insertOk;
                    } else {
                        _log_insertFailCount++;
                        _log_failMap.put(i,  "第"+i+"行数据新增失败，原因未知！");
                    }
                } catch(SQLException sqlE) {
                    _log_insertFailCount++;
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
        if (mainTableName==null||mainTableName.equals("")) throw new IllegalArgumentException("元数据模型中必须有积累表名称");
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
        List<Object> updateSetParam = new ArrayList<Object>(), updateKeyParam = new ArrayList<Object>();

        String insertSql = "insert into "+mainTableName+"(#columnSql) values(#valueSql)", columnSql="", valueSql="";
        String updateSql = "update "+mainTableName+" set #updateSet where #updateKey", updateSet="", updateKey="";
        for (MetadataColumn mc: sysMm.getColumnList()) {
            columnSql+=","+mc.getColumnName();
            valueSql+=",?";
            if (!mc.isPk()) {
                updateSet += ","+mc.getColumnName()+"=?";
            } else {
                updateKey += "and "+mc.getColumnName()+"=?";
            }
        }
        if (columnSql.length()>0) columnSql=columnSql.substring(1);
        if (valueSql.length()>0) valueSql=valueSql.substring(1);
        if (updateSet.length()>0) updateSet=updateSet.substring(1);
        if (updateKey.length()>0) updateKey=updateKey.substring(4);
        insertSql = insertSql.replaceAll("#columnSql", columnSql).replaceAll("#valueSql", valueSql);
        updateSql = updateSql.replaceAll("#updateSet", updateSet).replaceAll("#updateKey", updateKey);

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
            psUpdate = conn.prepareStatement(updateSql);
            
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
                if (rowData.size()>sysMm.getColumnList().size()) {
                    _log_ignoreCount++;
                    _log_ignoreMap.put(i, "第"+i+"行数据，列个数与元数据列个数不匹配，行数据为<<>>，元数据为<<>>。");
                    continue;
                }
                for (int j=0; j<paramArray.length; j++) paramArray[j]=null;
                updateSetParam.clear();
                updateKeyParam.clear();

                for (Map<String, Object> cell: rowData) {
                    titleCol = parse.findMatchTitle(cell, sti);
                    if (titleCol!=null) {
                        for (int k=0; k<sysMm.getColumnList().size(); k++) {
                            MetadataColumn mc = sysMm.getColumnList().get(k);
                            if (mc.getTitleName().equals((String)titleCol.get("title"))) {
                                Map<String, Object> kv = (Map<String, Object>)cell.get("transData");
                                if ((Integer)kv.get("dType")==ExcelConstants.convert2DataType(mc.getColumnType())) {
                                    paramArray[k] = kv.get("value");
                                    if (mc.isPk()) updateKeyParam.add(kv.get("value"));
                                    else updateSetParam.add(kv.get("value"));
                                } else {
                                    kv = (Map<String, Object>)cell.get("nativeData");
                                    if ((Integer)kv.get("dType")==ExcelConstants.convert2DataType(mc.getColumnType())) {
                                        paramArray[k] = kv.get("value");
                                        if (mc.isPk()) updateKeyParam.add(kv.get("value"));
                                        else updateSetParam.add(kv.get("value"));
                                    }
                                }
                            }
                        }
                    }
                }
                boolean canSave = false;
                for (int j=0; j<paramArray.length; j++) {
                    if (paramArray[j]!=null) {
                        canSave = true;
                        break;
                    }
                }
                if (!canSave) {
                    _log_ignoreCount++;
                    _log_ignoreMap.put(i, "第"+i+"行数据与元数据不匹配，行数据为<<>>，元数据为<<>>。");
                    continue;
                }
                boolean canInsert = true;
                int j=0;
                try{
                    for (Object v: updateSetParam) {
                        psUpdate.setObject(++j, v);
                    }
                    for (Object v: updateKeyParam) {
                        psUpdate.setObject(++j, v);
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
                if (canInsert) {
                    try {
                        for (j=0; j<paramArray.length; j++) {
                            psInsert.setObject(j+1, paramArray[j]);
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
