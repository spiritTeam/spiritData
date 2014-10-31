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
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;
import com.gmteam.spiritdata.metadata.relation.pojo.TableMapOrg;
import com.gmteam.spiritdata.metadata.relation.pojo._OwnerMetadata;
import com.gmteam.spiritdata.metadata.relation.service.MetadataSessionService;
import com.gmteam.spiritdata.util.SequenceUUID;

/** 
 * @author mht
 * @version  
 * 类说明 
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
    public Object dealUploadFile(Map<String, Object> uploadInfoMap, HttpSession session) throws Exception {
        this.session = session;
        // 1、uploadFile
        saveUploadFileInfo(uploadInfoMap);
        String uploadFileName = (String) uploadInfoMap.get("storeFilename");
        int fileType = getFileType(uploadFileName);
        File excelFile = new File(uploadFileName);
        WorkBookProxy workBookProxy = new WorkBookProxy(excelFile,fileType);
        List<SheetInfo> sheetInfoList = workBookProxy.getSheetList();
        for(SheetInfo sheetInfo:sheetInfoList){
            // 2、分析MetadataColumn
            Map<String,Object> retMap = PoiUtils.getMdModelMap(sheetInfo);
            MetadataModel excelMd = (MetadataModel) retMap.get("md");
            int titleRowIndex = (Integer) retMap.get("titleRowIndex");
            mdService.setSession(this.session);
            TableMapOrg [] tabMapOrgAry = mdService.storeMdModel4Import(excelMd);
            TableMapOrg tempTabMapOrg = tabMapOrgAry[1];
            String ownerId = tempTabMapOrg.getOwnerId();
            String sumTabName = tabMapOrgAry[0].getTableName();
            // 3、储存临时表
            saveDataInTempTab(sheetInfo,excelMd,tempTabMapOrg,titleRowIndex);
            // 4、分析临时表指标
            analTempQuota(tempTabMapOrg.getTableName());
            // 5、分析主键
            String pk = analPK(tempTabMapOrg.getTableName());
            // 6、存储积累表{根据上面的结果设置主键}
            saveDataInSumTab(sumTabName,pk,sheetInfo,excelMd,ownerId);
            // 7、分析积累表指标
            analSumTabQuota(sumTabName);
            // 8、分析元数据语义
            analMDSemantic(sumTabName);
            
        }
        return null;
    }
    private void saveDataInSumTab(String sumTabName, String pk,SheetInfo sheetInfo, MetadataModel excelMd, String ownerId) {
    }
    private void analMDSemantic(String sumTabName2) {
    }
    private void analSumTabQuota(String sumTabName2) {
    }
    private String analPK(String tempTabName2) {
        return null;
    }
    private void analTempQuota(String tempTabName) {
    }
    private void saveDataInTempTab(SheetInfo sheetInfo,MetadataModel excelMd, TableMapOrg tempTableMapOrg, int titleRowIndex) {
        Connection conn = null;
        try {
            conn = ds.getConnection();
            _OwnerMetadata _om = (_OwnerMetadata)this.session.getAttribute(SDConstants.SESSION_OWNER_RMDUNIT);
            MetadataModel newMD = _om.getMetadataById(tempTableMapOrg.getMdMId());
            //logTabOrg
            saveLogTabOrg(newMD,sheetInfo,tempTableMapOrg);
            PoiUtils.saveInDB(conn,sheetInfo,excelMd,newMD,tempTableMapOrg.getTableName(),titleRowIndex);
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
    private void saveUploadFileInfo(Map<String, Object> uploadInfoMap) {
        fulDao.setNamespace("fileUploadLog");
        try {
            FileUploadLog ful = new FileUploadLog();
            ful.setOwnerId(session.getId());
            UgaUser user = (UgaUser)session.getAttribute(FConstants.SESSION_USER);
            if(user==null) ful.setOwnerId(session.getId());
            ful.setsFileName((String)uploadInfoMap.get("storeFilename"));
            ful.setcFileName((String)uploadInfoMap.get("orglFilename"));
            ful.setFileSize((Long)uploadInfoMap.get("size"));
            ful.setId(SequenceUUID.getUUID());
            fulDao.insert(ful);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}