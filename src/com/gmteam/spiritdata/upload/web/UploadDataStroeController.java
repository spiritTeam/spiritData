package com.gmteam.spiritdata.upload.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gmteam.framework.core.model.BaseObject;
import com.gmteam.spiritdata.importdata.excel.ExcelConstants;
import com.gmteam.spiritdata.importdata.excel.ExcelContentAttributes;
import com.gmteam.spiritdata.importdata.excel.service.UploadDataStroeService;
import com.gmteam.spiritdata.importdata.excel.storepojo.PkNameCacheIdSign;
import com.gmteam.spiritdata.importdata.excel.storepojo.SaveResultInfo;
import com.gmteam.spiritdata.importdata.excel.util.SheetInfo;

/** 
 * 类说明 
 * @author
 * @version
 */
@SuppressWarnings("serial")
@Controller
public class UploadDataStroeController extends BaseObject {
    @Resource
    private UploadDataStroeService uploadDataStroeService;
    @SuppressWarnings("unchecked")
    @RequestMapping("saveData.do")
    public @ResponseBody  List<SaveResultInfo> saveUplodData(HttpServletRequest request){
        List<SaveResultInfo> saveSaveResultInfoList = new ArrayList<SaveResultInfo>();
        String pkCacheIdSignStr = request.getParameter("pkCacheIdSignStr");
        List<PkNameCacheIdSign> pkNameCacheIdSPignList = uploadDataStroeService.getPkCacheIdSignList(pkCacheIdSignStr);
        for(int i=0;i<pkNameCacheIdSPignList.size();i++){
            PkNameCacheIdSign pcs = pkNameCacheIdSPignList.get(i);
            String cacheId = pcs.getCacheId();
            String pkName = pcs.getPkName();
            String sheetName = pcs.getSheetName();
            String sign = pcs.getSign();
            Map<String,Object> uploadDataMap = uploadDataStroeService.getUploadDataCacheMap(cacheId);
            Map<SheetInfo, Object[][]> dataMap = (Map<SheetInfo, Object[][]>) uploadDataMap.get(ExcelConstants.DATATOOLS_UPLOADDATA_DATAMAP_NAME);
            ExcelContentAttributes contentAttributes=(ExcelContentAttributes) uploadDataMap.get(ExcelConstants.DATATOOLS_UPLOADDATA_SAVEATTRIBUTE_NAME);
            contentAttributes.setPkName(pkName);
            contentAttributes.setSheetName(sheetName);
            contentAttributes.setSign(sign);
            SaveResultInfo  saveResultInfo = uploadDataStroeService.saveInDB(contentAttributes,uploadDataMap,dataMap);
            saveSaveResultInfoList.add(saveResultInfo);
        }
        return saveSaveResultInfoList;
    }
}
