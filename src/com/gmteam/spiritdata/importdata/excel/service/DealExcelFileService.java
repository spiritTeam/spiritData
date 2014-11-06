package com.gmteam.spiritdata.importdata.excel.service;

import java.io.File;
import java.io.FileInputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.stereotype.Component;

import com.gmteam.spiritdata.SDConstants;
import com.gmteam.spiritdata.importdata.excel.ExcelConstants;
import com.gmteam.spiritdata.importdata.excel.pojo.ExcelTableInfo;
import com.gmteam.spiritdata.importdata.excel.pojo.SheetInfo;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;
import com.gmteam.spiritdata.metadata.relation.pojo.TableMapOrg;
import com.gmteam.spiritdata.metadata.relation.pojo._OwnerMetadata;
import com.gmteam.spiritdata.metadata.relation.service.MdKeyService;
import com.gmteam.spiritdata.metadata.relation.service.MdQuotaService;
import com.gmteam.spiritdata.metadata.relation.service.MetadataSessionService;

/**
 * 处理excel文件。
 * 导入系统，并进行初步分析，主要是导入
 * @author wh
 */
@Component
public class DealExcelFileService {
    @Resource
    MetadataSessionService mdSessionService;
    @Resource
    MdQuotaService mdQutotaService;
    @Resource
    MdKeyService mdKeyService ;

    /**
     * 处理Excel文件
     * @param fileName 文件名称
     * @param session 用户Session
     */
    public void process(String fileName, HttpSession session) {
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
                //TODO 记录日志
            }
            if (book==null) {
                try {
                    fis = new FileInputStream(excelFile);
                    book = new XSSFWorkbook(fis);
                    excelType = ExcelConstants.EXECL2007_FLAG;
                } catch (Exception e) {
                    //TODO 记录日志
                }
            }
            if (excelType==0) {
                // TODO 记录日志 
                return;
            }
            //根据sheet进行处理
            for (int i=0; i<book.getNumberOfSheets(); i++) {
                try {//处理每个Sheet，并保证某个Sheet处理失败后，继续处理后续Sheet
                    Object sheet = book.getSheetAt(i);
                    SheetInfo si = getSheetInfor(sheet, excelType);
                    si.setSheetIndex(i);
                    PoiParseExcelService parseExcel = new PoiParseExcelService(excelType, si);

                    //1-分析文件，得到元数据信息，并把分析结果存入si
                    analSheetMetadata(si);
                    if (si.getEtiList()==null||si.getEtiList().size()==0) continue;
                    for (ExcelTableInfo eti: si.getEtiList()) {
                        try {//--处理sheet中的每个元数据
                            //--保存分析后的元数据信息，包括数据表的注册与创建
                            //-- 若元数据信息在系统中已经存在，则只生成临时表
                            //-- 否则，创建新的元数据，并生成积累表和临时表
                            mdSessionService.setSession(session);
                            TableMapOrg[] tabMapOrgAry = mdSessionService.storeMdModel4Import(eti.getMm());

                            // TODO 为了有更好的处理响应时间，以下逻辑可以采用多线程处理

                            //--获得系统保存的与当前Excel元数据信息匹配的元数据信息
                            _OwnerMetadata _om = (_OwnerMetadata)session.getAttribute(SDConstants.SESSION_OWNER_RMDUNIT);
                            MetadataModel sysMd = _om.getMetadataById(tabMapOrgAry[0].getMdMId());
                            //2-储存临时表
                            saveDataToTempTab(eti, sysMd, tabMapOrgAry[1].getTableName());
                            //3-临时表分析
                            mdQutotaService.caculateQuota(tabMapOrgAry[1]); //分析临时表指标
                            mdKeyService.adjustMdKey(sysMd); //分析主键，此时，若分析出主键，则已经修改了模式对应的积累表的主键信息
                            //4-存储积累表
                            if (sysMd.getTableName().equalsIgnoreCase(tabMapOrgAry[0].getTableName())) {
                                saveDataToAccumulationTab(eti, sysMd);
                                //5-积累表分析
                                mdQutotaService.caculateQuota(tabMapOrgAry[0]); //分析临时表指标
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
                } catch(Exception e) {
                    // TODO 记录日志 
                    e.printStackTrace();
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
    private SheetInfo getSheetInfor(Object sheet, int excelType) {
        SheetInfo ret = new SheetInfo();
        ret.setExcelType(excelType);
        ret.setSheet(sheet);
        if (excelType==ExcelConstants.EXECL2003_FLAG) {
            ret.setSheetName(((HSSFSheet)sheet).getSheetName());
        } else if (excelType==ExcelConstants.EXECL2007_FLAG) {
            ret.setSheetName(((XSSFSheet)sheet).getSheetName());
        }
        return ret;
    }

    /*
     * 分析sheet，得到元数据信息，并把分析结果存入si
     * @param si sheetInfo
     */
    private void analSheetMetadata(SheetInfo si) {
        //首先分析表头
        Object sheet = si.getSheet();
        int rows = 0, firstRowNum = 0;
        if (si.getExcelType()==ExcelConstants.EXECL2003_FLAG) {
            rows = ((HSSFSheet)sheet).getLastRowNum();
            firstRowNum = ((HSSFSheet)sheet).getFirstRowNum();
        } else if (si.getExcelType()==ExcelConstants.EXECL2007_FLAG){
            rows = ((XSSFSheet)sheet).getLastRowNum();
            firstRowNum = ((XSSFSheet)sheet).getFirstRowNum();
        }
        if (rows==firstRowNum&&rows==0) return; //说明是空sheet
        int dataRowBegin = firstRowNum;
        
        //之后分析元数据模型
    }

    /*
     * 保存临时表信息
     * @param em 从Excel中分析出来的元数据信息，注意，这里包括sheet信息
     * @param sysMm 元数据信息（已在系统注册过的）
     * @param tempTableName 临时表名称
     */
    private void saveDataToTempTab(ExcelTableInfo em, MetadataModel sysMm, String tempTableName) {
        
    }

    /*
     * 保存积累表信息
     * @param em 从Excel中分析出来的元数据信息，注意，这里包括sheet信息
     * @param sysMm 元数据信息（已在系统注册过的），这其中包括积累表信息
     */
    private void saveDataToAccumulationTab(ExcelTableInfo em, MetadataModel sysMm) {
        
    }
}