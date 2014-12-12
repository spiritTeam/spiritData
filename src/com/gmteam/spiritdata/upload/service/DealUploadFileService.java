package com.gmteam.spiritdata.upload.service;

import java.io.File;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import com.gmteam.framework.FConstants;
import com.gmteam.framework.UGA.UgaUser;
import com.gmteam.framework.util.FileNameUtils;
import com.gmteam.spiritdata.filemanage.IMP.model.ImportFile;
import com.gmteam.spiritdata.filemanage.IMP.service.ImportFileService;
import com.gmteam.spiritdata.importdata.excel.service.DealExcelFileService;

/**
 * 处理上传文件
 * @author wh, mht
 */
@Component
public class DealUploadFileService {
    @Resource
    private ImportFileService ifService;
    @Resource
    private DealExcelFileService dealExcelService;

    /**
     * 处理上传文件
     * @param uploadInfoMap 文件上传信息
     * @param session 处理的Session
     * @throws Exception
     */
    public void dealUploadFile(Map<String, Object> uploadInfoMap, HttpSession session) throws Exception  {
        //记录文件
        ImportFile fi = getFileInfo(uploadInfoMap, session);
        ifService.saveImportFile(fi);

        //得到文件扩展名
        String extName = FileNameUtils.getExt(fi.getServerFileName());
        if (extName.toUpperCase().indexOf(".XLS")==0||extName.toUpperCase().indexOf(".XLSX")==0) {
            //对excel进行处理
            dealExcelService.process(fi.getServerFileName(), session);
        } else { //处理其他文件类型的文件
            
        }
    }

    private ImportFile getFileInfo(Map<String, Object> uploadInfoMap, HttpSession session) throws Exception {
        //文件处理
        String allFileName = (String)uploadInfoMap.get("storeFilename");
        File f = new File(allFileName);
        if (!f.exists()||!f.isFile()) throw new Exception("文件不存在");

        //文件信息
        ImportFile ret = new ImportFile();
        UgaUser user = (UgaUser)session.getAttribute(FConstants.SESSION_USER);
        if(user==null){
            ret.setOwnerType(2);
            ret.setOwnerId(session.getId());
        }else{
            ret.setOwnerType(1);
            ret.setOwnerId(user.getUserId());
        }
        ret.setServerFileName(allFileName);
        ret.setClientFileName((String)uploadInfoMap.get("orglFilename"));
        ret.setFileSize((Long)uploadInfoMap.get("size"));
        return ret;
    }
}