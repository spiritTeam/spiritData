package com.gmteam.upload.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.gmteam.framework.core.web.AbstractFileUploadController;
import com.gmteam.upload.service.FileUploadService;
import com.gmteam.upload.util.ExcelPoiUtils;

/** 
 * @author 
 * @version  
 * 类说明 
 */
public class FileUpLoadController extends AbstractFileUploadController {
    @Resource
    private FileUploadService fileUploadService;
    @Override
    public void afterUploadAllFiles(List<Map<String, Object>> uploadInfoMapList,Map<String, Object> arg1, Map<String, Object> arg2) {Map<String,Object> uploadInfoMap= uploadInfoMapList.get(0);
        String uploadFileName = (String) uploadInfoMap.get("storeFilename");
        ExcelPoiUtils excelPoiUtils;
        int fileType = fileUploadService.getFileType(uploadFileName);
        switch (fileType) {
        case 1:
            try {
                excelPoiUtils = new ExcelPoiUtils(new File(uploadFileName));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            break;
        default:
            break;
        }
        //String fileType = getFileType(uploadFileName);
    }
    @Override
    public Map<String, Object> afterUploadOneFileOnSuccess(
            Map<String, Object> arg0, Map<String, Object> arg1,
            Map<String, Object> arg2) {
        return null;
    }
    
}
