package com.gmteam.spiritdata.upload.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;

import com.gmteam.framework.core.web.AbstractFileUploadController;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;
import com.gmteam.spiritdata.metadata.relation.semanteme.func.AnalKey;
import com.gmteam.spiritdata.metadata.relation.service.MdBasisService;
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
    @Resource
    AnalKey analKey;
    @Resource
    MdBasisService mdBasisService;
    @Override
    public Map<String, Object> afterUploadOneFileOnSuccess(Map<String, Object> uploadInfoMap, Map<String, Object> arg1,Map<String, Object> arg2) {
        HttpSession session = request.getSession();
        try {
            MetadataModel mm = mdBasisService.getMetadataMode("f75e75ad1890");
            analKey.scanOneTable("tab_f75e75ad1890", mm);
            return null;
            //fileUploadService.dealUploadFile(uploadInfoMap,session);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public void afterUploadAllFiles(List<Map<String, Object>> uploadInfoMapList,Map<String, Object> arg1, Map<String, Object> arg2) {
    }
    
}
