package com.spiritdata.dataanal.upload.service;

import java.io.File;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.spiritdata.framework.util.FileNameUtils;
import com.spiritdata.dataanal.common.util.SessionUtils;
import com.spiritdata.dataanal.exceptionC.Dtal0101CException;
import com.spiritdata.dataanal.importdata.excel.service.DealExcelFileService;
import com.spiritdata.filemanage.category.IMP.model.ImportFile;
import com.spiritdata.filemanage.core.model.FileInfo;
import com.spiritdata.filemanage.core.service.FileManageService;
import com.spiritdata.filemanage.exceptionC.Flmg0002CException;

/**
 * 处理上传文件
 * @author wh, mht
 */
public class DealUploadFileService {
    @Resource
    private FileManageService fmService;
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
        ImportFile ifl = getFileInfo(uploadInfoMap, session);
        FileInfo fi = fmService.saveFile(ifl);
        try {
            //得到文件扩展名
            String extName = FileNameUtils.getExt(ifl.getServerFileName());
            if (extName.toUpperCase().indexOf(".XLS")==0||extName.toUpperCase().indexOf(".XLSX")==0) {
                //对excel进行处理
                dealExcelService.process(fi, session);
            } else { //处理其他文件类型的文件
                throw new Flmg0002CException("目前只能处理excl97之后的电子表格数据。");
            }
        } catch(Exception e) {
            if (e instanceof Dtal0101CException) throw e;
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
        ret.setOwner(SessionUtils.getOwner(session));
        ret.setServerFileName(allFileName);
        ret.setClientFileName((String)uploadInfoMap.get("orglFilename"));
        return ret;
    }
}