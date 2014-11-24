package com.gmteam.spiritdata.upload.service;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import com.gmteam.framework.FConstants;
import com.gmteam.framework.UGA.UgaUser;
import com.gmteam.framework.util.FileNameUtils;
import com.gmteam.spiritdata.importdata.excel.service.DealExcelFileService;
import com.gmteam.spiritdata.util.SequenceUUID;

/**
 * 处理上传文件
 * @author wh, mht
 */
@Component
public class DealUploadFileService {
//    @Resource
//    private ImportFileLogService importFileLogService;
    @Resource
    private DealExcelFileService dealExcelService;

    /**
     * 处理上传文件
     * @param uploadInfoMap 文件上传信息
     * @param session 处理的Session
     * @throws Exception 
     */
    public void dealUploadFile(Map<String, Object> uploadInfoMap, HttpSession session) throws Exception  {
        //记录日志
//        ImportFileLog ifl = getIfsFromUploadInfoMap(uploadInfoMap, session);
//        importFileLogService.addImportFileLog(ifl);
        // TODO 写文件日志
        //得到文件扩展名
        String extName = FileNameUtils.getExt("ifl.getsFileName()");
        if (extName.toUpperCase().indexOf(".XLS")==0||extName.toUpperCase().indexOf(".XLSX")==0) {
            //对excel进行处理
            dealExcelService.process("ifl.getsFileName()", session);
        } else { //处理其他文件类型的文件
            
        }
    }

//    private ImportFileLog getIfsFromUploadInfoMap(Map<String, Object> uploadInfoMap, HttpSession session) {
//        ImportFileLog ret = new ImportFileLog();
//        ret.setId(SequenceUUID.getUUIDSubSegment(4));
//        UgaUser user = (UgaUser)session.getAttribute(FConstants.SESSION_USER);
//        if(user==null){
//            ret.setOwnerId(session.getId());
//            ret.setOwnerType(2);
//        }else{
//            ret.setOwnerId(user.getUserId());
//            ret.setOwnerType(1);
//        }
//        ret.setsFileName((String)uploadInfoMap.get("storeFilename"));
//        ret.setcFileName((String)uploadInfoMap.get("orglFilename"));
//        ret.setFileSize((Long)uploadInfoMap.get("size"));
//        return ret;
//    }
}
