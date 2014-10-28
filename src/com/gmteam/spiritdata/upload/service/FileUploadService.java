package com.gmteam.spiritdata.upload.service;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
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
import com.gmteam.spiritdata.metadata.relation.service.MetadataService;
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
    /**workBook代理类*/
    private WorkBookProxy workBookProxy;
    private HttpSession session;
    /**
     * 获取workBook,和MdList
     * @param session 
     */
    @SuppressWarnings("unchecked")
    public Object dealUploadFile(Map<String, Object> uploadInfoMap, HttpSession session) throws Exception {
        this.session=session;
        /** fileInfo*/
//        System.out.println("001============================"+(new Date()).getTime());
        //001记录文件上传日志
        saveUploadFileInfo(uploadInfoMap);
        String uploadFileName = (String) uploadInfoMap.get("storeFilename");
        int fileType = getFileType(uploadFileName);
        //文件类型，要用于表判断返回来的workbook类型*/
        File excelFile = new File(uploadFileName);
        //得到md和delIndex构成的map*/
        Map<SheetInfo,Object> rstMap = new HashMap<SheetInfo,Object>();
        workBookProxy = new WorkBookProxy(excelFile,fileType);
        //002分析metadata
        rstMap = (Map<SheetInfo, Object>) workBookProxy.getMDMap();
//        System.out.println("002============================"+(new Date()).getTime());
        //分别取出delIndex和metadata
        Iterator<SheetInfo> it = rstMap.keySet().iterator();
        //003存储临时表
        //004分析临时表指标
        //005分析主键
        //006存储积累表{根据上面的结果设置主键}
        //007分析积累表指标
        //008分析元数据语义
        
        //003存数据
        while(it.hasNext()){
            SheetInfo sheetInfo = it.next();
            Map<String,Object> valMap = (Map<String, Object>) rstMap.get(sheetInfo);
            //md
            MetadataModel oldMD = (MetadataModel) valMap.get("metadataModel");
            saveData(sheetInfo,oldMD);
        }
        return null;
    }
    @Resource
    MetadataService mdService;
    @Resource(name="dataSource")
    private  BasicDataSource ds;
    /**
     * 得到比对之后的表名，和新的MD
     * @param sheetInfo
     * @param delIndexMap
     * @param oldMD
     */
    private void saveData(SheetInfo sheetInfo, MetadataModel oldMD) {
        TableMapOrg[] tabMapOrgAry;
        Connection conn = null;
        try {
            mdService.setSession(session);
            conn = ds.getConnection();
            tabMapOrgAry = mdService.storeMdModel4Import(oldMD);
            _OwnerMetadata _om = (_OwnerMetadata)this.session.getAttribute(SDConstants.SESSION_OWNERRMDUNIT);
            MetadataModel newMD = _om.getMetadataById(tabMapOrgAry[0].getMdMId());
            //logTabOrg
            saveLogTabOrg(newMD,sheetInfo,tabMapOrgAry);
            PoiUtils.saveInDB(conn,sheetInfo,oldMD,newMD,tabMapOrgAry);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            CommonUtils.closeConn(conn, null, null);
        }
    }
    @Resource
    private MybatisDAO<UploadLogTableOrg> ultoDao;
    private void saveLogTabOrg(MetadataModel newMD, SheetInfo sheetInfo, TableMapOrg[] tabMapOrgAry) {
        ultoDao.setNamespace("uploadLogTableOrg");
        String ownerId = newMD.getOwnerId();
        int sheetIndex = sheetInfo.getSheetIndex();
        String sheetName = sheetInfo.getSheetName();
        for(TableMapOrg tmo :tabMapOrgAry){
            try {
                UploadLogTableOrg ulto = new UploadLogTableOrg();
                ulto.setId(SequenceUUID.getUUID());
                ulto.setSheetIndex(sheetIndex);
                ulto.setSheetName(sheetName);
                ulto.setUfId(ownerId);
                ulto.setTmoId(tmo.getId());
                ulto.setTmId(tmo.getMdMId());
                ultoDao.insert(ulto);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
