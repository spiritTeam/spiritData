package com.gmteam.spiritdata.upload.service;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.stereotype.Component;

import com.gmteam.framework.FConstants;
import com.gmteam.framework.UGA.UgaUser;
import com.gmteam.framework.core.dao.mybatis.MybatisDAO;
import com.gmteam.spiritdata.SDConstants;
import com.gmteam.spiritdata.importdata.excel.ExcelConstants;
import com.gmteam.spiritdata.importdata.excel.pojo.FileUploadLog;
import com.gmteam.spiritdata.importdata.excel.pojo.SheetInfo;
import com.gmteam.spiritdata.importdata.excel.pojo.UploadLogTableOrg;
import com.gmteam.spiritdata.importdata.excel.proxy.WorkBookProxy;
import com.gmteam.spiritdata.importdata.excel.util.CommonUtils;
import com.gmteam.spiritdata.importdata.excel.util.PoiUtils;
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
 * 文件处理
 * @author mht
 */
@Component
public class FileUploadService {
    /**
     * 
     * @param uploadFileName
     * @return
     * 根据文件名，返回int值，代表文件类型（暂时只支持excel，其他类型有待扩展）
     * 1--xls，2---。xlsx
     */
    public int getFileType(String uploadFileName) {
        int fileType = 0;
        String fileTypeStr = uploadFileName.substring(uploadFileName.lastIndexOf("."),uploadFileName.length());
        if(fileTypeStr.equals(".xls")){
            fileType = ExcelConstants.EXCEL_FILE_TYPE_HSSF;
        }else if(fileTypeStr.equals(".xlsx")){
            fileType = ExcelConstants.EXCEL_FILE_TYPE_XSSF;
        }
        return fileType;
    }
    private HttpSession session;
    @Resource
    MetadataSessionService mdService;
    @Resource(name="dataSource")
    private  BasicDataSource ds;
    public Object dealUploadFile(Map<String, Object> uploadInfoMap, HttpSession session)  {
        this.session = session;
        // 1、uploadFile
        saveUploadFileInfo(uploadInfoMap);
        String uploadFileName = (String) uploadInfoMap.get("storeFilename");
        int fileType = getFileType(uploadFileName);
        File excelFile = new File(uploadFileName);
        WorkBookProxy workBookProxy;
        try {
            workBookProxy = new WorkBookProxy(excelFile,fileType);
            List<SheetInfo> sheetInfoList = workBookProxy.getSheetList();
            for(SheetInfo sheetInfo:sheetInfoList){
                // 2、分析MetadataColumn
                Map<String,Object> retMap = PoiUtils.getMdModelMap(sheetInfo);
                MetadataModel excelMd = (MetadataModel) retMap.get("md");
                if(excelMd==null) continue;
                int titleRowIndex = (Integer) retMap.get("titleRowIndex");
                mdService.setSession(this.session);
                TableMapOrg[] tabMapOrgAry;
                tabMapOrgAry = mdService.storeMdModel4Import(excelMd);
                TableMapOrg tempTabMapOrg = tabMapOrgAry[1];
                String ownerId = tempTabMapOrg.getOwnerId();
                String sumTabName = tabMapOrgAry[0].getTableName();
                // 3、储存临时表
                _OwnerMetadata _om = (_OwnerMetadata)this.session.getAttribute(SDConstants.SESSION_OWNER_RMDUNIT);
                MetadataModel andlMd = _om.getMetadataById(tempTabMapOrg.getMdMId());
                saveDataInTempTab(sheetInfo,excelMd,tempTabMapOrg,titleRowIndex,andlMd);
                // 5、分析主键
                analTempTab(tempTabMapOrg.getTableName(),andlMd,tempTabMapOrg);
                // 6、存储积累表{根据上面的结果设置主键}
                saveDataInSumTab(sumTabName,sheetInfo,excelMd,ownerId,andlMd,titleRowIndex);
                // 8、分析元数据语义
                analMDSemantic(sumTabName);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        
        return null;
    }
    @Resource
    MdQuotaService mdQutotaService ;
    @Resource
    private MdKeyService mdKeyService;
    @Resource
    private AnalKey analKey;
    /**
     * 通过分析临时表，计算临时表指标计信息
     * 分析主键
     * 调整元数据主键
     * @param tableName
     * @param andlMd
     * @param tempTabMapOrg
     * @return
     */
    private void analTempTab(String tableName,MetadataModel andlMd, TableMapOrg tempTabMapOrg) {
        try {
            mdQutotaService.caculateQuota(tempTabMapOrg);
            analKey.scanOneTable(tableName, andlMd, null);
            mdKeyService.adjustMdKey(andlMd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 保存数据到积累表，并且分析积累表
     * @param sumTabName 积累表名称
     * @param sheetInfo sheetInfo
     * @param excelMd excel分析出的md
     * @param ownerId
     * @param andlMd
     * @param titleRowIndex title的起始行
     */
    private void saveDataInSumTab(String sumTabName,SheetInfo sheetInfo, MetadataModel excelMd, String ownerId, MetadataModel andlMd, int titleRowIndex) {
        Connection conn = null;
        try {
            conn = ds.getConnection();
            PoiUtils.saveSubTabInDB(conn,sheetInfo,excelMd,andlMd,sumTabName,titleRowIndex);
            mdQutotaService.caculateQuota(ownerId,sumTabName);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            CommonUtils.closeConn(conn, null, null);
        }
    }
    private void analMDSemantic(String sumTabName) {
    }
    private void saveDataInTempTab(SheetInfo sheetInfo,MetadataModel excelMd, TableMapOrg tempTableMapOrg, int titleRowIndex, MetadataModel newMd) {
        Connection conn = null;
        try {
            //logTabOrg
            saveLogTabOrg(newMd,sheetInfo,tempTableMapOrg);
            //保存数据到临时表
            PoiUtils.saveTempTabInDB(conn,sheetInfo,excelMd,newMd,tempTableMapOrg.getTableName(),titleRowIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            CommonUtils.closeConn(conn, null, null);
        }
    }
    @Resource
    private MybatisDAO<UploadLogTableOrg> ultoDao;
    private void saveLogTabOrg(MetadataModel newMD, SheetInfo sheetInfo, TableMapOrg tabMapOrg) {
        ultoDao.setNamespace("uploadLogTableOrg");
        String ownerId = newMD.getOwnerId();
        int sheetIndex = sheetInfo.getSheetIndex();
        String sheetName = sheetInfo.getSheetName();
        try {
            UploadLogTableOrg ulto = new UploadLogTableOrg();
            ulto.setId(SequenceUUID.getUUID());
            ulto.setSheetIndex(sheetIndex);
            ulto.setSheetName(sheetName);
            ulto.setUfId(ownerId);
            ulto.setTmoId(tabMapOrg.getId());
            ulto.setTmId(tabMapOrg.getMdMId());
            ultoDao.insert(ulto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Resource
    private MybatisDAO<FileUploadLog> fulDao;
    private StringBuffer saveUploadFileInfo(Map<String, Object> uploadInfoMap) {
        fulDao.setNamespace("fileUploadLog");
        StringBuffer fileInfo = new StringBuffer();
        try {
            FileUploadLog ful = new FileUploadLog();
            ful.setOwnerId(session.getId());
            UgaUser user = (UgaUser)session.getAttribute(FConstants.SESSION_USER);
            if(user==null){
                ful.setOwnerId(session.getId());
            }else{
                ful.setOwnerId(user.getUserId());
            }
            ful.setsFileName((String)uploadInfoMap.get("storeFilename"));
            ful.setcFileName((String)uploadInfoMap.get("orglFilename"));
            ful.setFileSize((Long)uploadInfoMap.get("size"));
            ful.setId(SequenceUUID.getUUID());
            fulDao.insert(ful);
            return fileInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
