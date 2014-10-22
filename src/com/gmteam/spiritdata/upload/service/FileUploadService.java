package com.gmteam.spiritdata.upload.service;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.stereotype.Component;

import com.gmteam.spiritdata.SDConstants;
import com.gmteam.spiritdata.importdata.excel.ExcelConstants;
import com.gmteam.spiritdata.importdata.excel.pojo.SheetInfo;
import com.gmteam.spiritdata.importdata.excel.proxy.WorkBookProxy;
import com.gmteam.spiritdata.importdata.excel.util.PoiUtils;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;
import com.gmteam.spiritdata.metadata.relation.pojo.TableMapOrg;
import com.gmteam.spiritdata.metadata.relation.pojo._OwnerMetadata;
import com.gmteam.spiritdata.metadata.relation.service.MdBasisService;
import com.gmteam.spiritdata.metadata.relation.service.MetadataService;

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
    private  WorkBookProxy workBookProxy;
    private HttpSession session;
    /**
     * 获取workBook,和MdList
     * @param session 
     */
    public Object getDealMetaDate(String uploadFileName, HttpSession session) throws Exception {
        int fileType = getFileType(uploadFileName);
        this.session = session;
        /**文件类型，要用于表判断返回来的workbook类型*/
        File excelFile = new File(uploadFileName);
        Object workBook = null;
        Map<SheetInfo,MetadataModel> mdMap = new HashMap<SheetInfo,MetadataModel>();
        workBookProxy = new WorkBookProxy(excelFile,fileType);
        workBook = (HSSFWorkbook) workBookProxy.getWorkBook();
        mdMap = (Map<SheetInfo, MetadataModel>) workBookProxy.getMDMap();
        Map<SheetInfo,Map<Integer,Integer>> delColIndexMap = PoiUtils.delColIndexMap;
        Map<SheetInfo,TableMapOrg[]> sheetTabOrgMap = getTabName(mdMap);
        saveDate(sheetTabOrgMap,delColIndexMap,mdMap);
        return workBook;
    }
    private void saveDate(Map<SheetInfo, TableMapOrg[]> sheetTabOrgMap, Map<SheetInfo, Map<Integer,Integer>> delColIndexMap, Map<SheetInfo, MetadataModel> mdMap) {
        Iterator<SheetInfo> it = sheetTabOrgMap.keySet().iterator();
        while(it.hasNext()){
            SheetInfo sheetInfo = it.next();
            TableMapOrg[] tabMapOrg = sheetTabOrgMap.get(sheetInfo);
            Map<Integer,Integer> delColIndexList = delColIndexMap.get(sheetInfo);
            MetadataModel md = mdMap.get(sheetInfo);
            saveInDB(tabMapOrg,sheetInfo,delColIndexList,md);
        }
    }
    private void saveInDB(TableMapOrg[] tabMapOrg,SheetInfo sheetInfo, Map<Integer,Integer> delColIndexMap, MetadataModel md) {
        try {
            if(sheetInfo.getSheetType()==ExcelConstants.EXCEL_FILE_TYPE_XSSF){
                workBookProxy= new WorkBookProxy(sheetInfo,delColIndexMap,tabMapOrg);
            }else if(sheetInfo.getSheetType()==ExcelConstants.EXCEL_FILE_TYPE_HSSF){
                workBookProxy= new WorkBookProxy(sheetInfo,delColIndexMap,tabMapOrg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Resource
    MetadataService mdService;
    private Map<SheetInfo,TableMapOrg[]> getTabName(Map<SheetInfo, MetadataModel> mdMap) throws Exception {
        Map<SheetInfo,TableMapOrg[]> sheetTabOrgMap = new HashMap<SheetInfo, TableMapOrg[]>();
        mdService.setSession(session);
        Iterator<SheetInfo> it = mdMap.keySet().iterator();
        while(it.hasNext()){
            SheetInfo sheetInfo = it.next();
            MetadataModel md = mdMap.get(sheetInfo);
            TableMapOrg[] art =mdService.storeMdModel4Import(md);
            _OwnerMetadata _om = (_OwnerMetadata)this.session.getAttribute(SDConstants.SESSION_OWNERRMDUNIT);
            md = _om.getMetadataById(art[0].getMdMId());
            sheetTabOrgMap.put(sheetInfo, art);
        }
        return sheetTabOrgMap;
    }
}
