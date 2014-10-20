package com.gmteam.spiritdata.upload.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;

import com.gmteam.framework.core.web.AbstractFileUploadController;
import com.gmteam.spiritdata.upload.service.FileUploadService;

/** 
 * @author mht
 * @version  
 * 类说明 
 */
@Controller
public class FileUpLoadController extends AbstractFileUploadController {
    @Resource
    private FileUploadService fileUploadService;
    @Resource
    HttpServletRequest request;
    @Override
    public void afterUploadAllFiles(List<Map<String, Object>> uploadInfoMapList,Map<String, Object> arg1, Map<String, Object> arg2) {Map<String,Object> uploadInfoMap= uploadInfoMapList.get(0);
        HttpSession session = request.getSession();
        String uploadFileName = (String) uploadInfoMap.get("storeFilename");
        int fileType = fileUploadService.getFileType(uploadFileName);
        try {
            fileUploadService.getFileMetaDate(uploadFileName,fileType,session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public Map<String, Object> afterUploadOneFileOnSuccess(
            Map<String, Object> arg0, Map<String, Object> arg1,
            Map<String, Object> arg2) {
        return null;
    }
    
}
