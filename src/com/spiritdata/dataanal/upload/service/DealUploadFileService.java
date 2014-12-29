package com.spiritdata.dataanal.upload.service;

import java.io.File;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.UGA.UgaUser;
import com.spiritdata.framework.util.FileNameUtils;

import com.spiritdata.dataanal.exceptionC.Dtal0101CException;
import com.spiritdata.dataanal.importdata.excel.service.DealExcelFileService;

import com.spiritdata.filemanage.IMP.model.ImportFile;
import com.spiritdata.filemanage.IMP.service.ImportFileService;

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
        try {
            ImportFile fi = getFileInfo(uploadInfoMap, session);
            ifService.saveImportFile(fi);
            //得到文件扩展名
            String extName = FileNameUtils.getExt(fi.getServerFileName());
            if (extName.toUpperCase().indexOf(".XLS")==0||extName.toUpperCase().indexOf(".XLSX")==0) {
                //对excel进行处理
                dealExcelService.process(fi, session);
            } else { //处理其他文件类型的文件
                
            }
        } catch(Exception e) {
            if (e instanceof Dtal0101CException) throw (Dtal0101CException)e;
            else throw new Dtal0101CException(e);
        }
    }

    private ImportFile getFileInfo(Map<String, Object> uploadInfoMap, HttpSession session) {
        //文件处理
        String allFileName = (String)uploadInfoMap.get("storeFilename");
        File f = new File(allFileName);
        if (!f.exists()||!f.isFile()) throw new Dtal0101CException("文件["+allFileName+"]不存在！");

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