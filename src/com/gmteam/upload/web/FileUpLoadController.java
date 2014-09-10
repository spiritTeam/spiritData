package com.gmteam.upload.web;

import java.util.List;
import java.util.Map;

import com.gmteam.framework.core.web.AbstractFileUploadController;

/** 
 * @author 
 * @version  
 * 类说明 
 */
public class FileUpLoadController extends AbstractFileUploadController {
    @Override
    public void afterUploadAllFiles(List<Map<String, Object>> uploadInfoMapList,
            Map<String, Object> arg1, Map<String, Object> arg2) {
        Map<String,Object> uploadInfoMap= uploadInfoMapList.get(0);
        String uploadFileName = (String) uploadInfoMap.get("storeFilename");
        String fileType = getFileType(uploadFileName);
    }

    private String getFileType(String uploadFileName) {
        String fileType = uploadFileName.substring(uploadFileName.lastIndexOf("."),uploadFileName.length());
        if(fileType.equals(".xls")||fileType.equals(".xlsx")){
            
        }
        return fileType;
    }

    @Override
    public Map<String, Object> afterUploadOneFileOnSuccess(
            Map<String, Object> arg0, Map<String, Object> arg1,
            Map<String, Object> arg2) {
        return null;
    }

}
