package com.gmteam.spiritdata.upload.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;

import com.gmteam.framework.core.web.AbstractFileUploadController;
import com.gmteam.spiritdata.upload.service.DealUploadFileService;
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
    private DealUploadFileService dealUploadFileService;
    @Resource
    HttpServletRequest request;
    @Override
    public Map<String, Object> afterUploadOneFileOnSuccess(Map<String, Object> uploadInfoMap, Map<String, Object> arg1,Map<String, Object> arg2) {
        HttpSession session = request.getSession();
        try {
            dealUploadFileService.dealUploadFile(uploadInfoMap, session);
            fileUploadService.dealUploadFile(uploadInfoMap,session);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public void afterUploadAllFiles(List<Map<String, Object>> uploadInfoMapList,Map<String, Object> arg1, Map<String, Object> arg2) {
    }
    
}
