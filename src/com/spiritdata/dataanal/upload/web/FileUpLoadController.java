package com.spiritdata.dataanal.upload.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;

import com.spiritdata.framework.core.web.AbstractFileUploadController;
import com.spiritdata.dataanal.common.model.Owner;
import com.spiritdata.dataanal.common.util.SessionUtils;
import com.spiritdata.dataanal.upload.service.DealUploadFileService;

/** 
 * 上传文件处理，是数据分析的入口
 * @author mht, wh
 */
@Controller
public class FileUpLoadController extends AbstractFileUploadController {
    @Resource
    private DealUploadFileService dealUploadFileService;
    @Resource
    HttpServletRequest request;

    @Override
    public Map<String, Object> afterUploadOneFileOnSuccess(Map<String, Object> uploadInfoMap, Map<String, Object> arg1,Map<String, Object> arg2) {
        Map<String, Object> ret = new HashMap<String, Object>();

        HttpSession session = request.getSession();
        try {
            dealUploadFileService.dealUploadFile(uploadInfoMap, session);
            ret.put("success", "TRUE");
        } catch (Exception e) {
            ret.put("exception", e.getMessage());
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public void afterUploadAllFiles(List<Map<String, Object>> uploadInfoMapList,Map<String, Object> arg1, Map<String, Object> arg2) {
    }

    @Override
    public void setMySavePath() {
        HttpSession session = request.getSession();
        Owner o = SessionUtils.getOwner(session);
        this.setSavePath(o.getOwnerId());
    }
}