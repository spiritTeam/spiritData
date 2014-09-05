package com.upload.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;

import com.gmteam.framework.core.web.AbstractFileUploadController;

/** 
 * @author 
 * @version  
 * 类说明 
 */
@Controller
public class FileUpLoadController extends AbstractFileUploadController {

    @Override
    public void afterUploadAllFiles(List<Map<String, Object>> uploadInfoMapList,
            Map<String, Object> arg1, Map<String, Object> arg2) {
        
    }

    @Override
    public Map<String, Object> afterUploadOneFileOnSuccess(
            Map<String, Object> arg0, Map<String, Object> arg1,
            Map<String, Object> arg2) {
        return null;
    }

}
