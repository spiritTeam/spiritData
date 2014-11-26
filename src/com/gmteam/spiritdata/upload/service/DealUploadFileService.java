package com.gmteam.spiritdata.upload.service;

import java.io.File;
import java.sql.Timestamp;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import com.gmteam.framework.FConstants;
import com.gmteam.framework.UGA.UgaUser;
import com.gmteam.framework.util.FileNameUtils;
import com.gmteam.spiritdata.filemanage.enumeration.FileCategoryType1;
import com.gmteam.spiritdata.filemanage.model.FileCategory;
import com.gmteam.spiritdata.filemanage.model.FileInfo;
import com.gmteam.spiritdata.filemanage.service.FileManageService;
import com.gmteam.spiritdata.importdata.excel.service.DealExcelFileService;

/**
 * 处理上传文件
 * @author wh, mht
 */
@Component
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
        FileInfo fi = getFileInfo(uploadInfoMap, session);
        fmService.saveFileInfo(fi);

        //得到文件扩展名
        String extName = FileNameUtils.getExt("ifl.getsFileName()");
        if (extName.toUpperCase().indexOf(".XLS")==0||extName.toUpperCase().indexOf(".XLSX")==0) {
            //对excel进行处理
            dealExcelService.process("ifl.getsFileName()", session);
        } else { //处理其他文件类型的文件
            
        }
    }

    private FileInfo getFileInfo(Map<String, Object> uploadInfoMap, HttpSession session) throws Exception {
        //文件处理
        String allFileName = (String)uploadInfoMap.get("storeFilename");
        File f = new File(allFileName);
        if (!f.exists()||!f.isFile()) throw new Exception("文件不存在");

        //文件信息
        FileInfo ret = new FileInfo();
        UgaUser user = (UgaUser)session.getAttribute(FConstants.SESSION_USER);
        if(user==null){
            ret.setOwnerType(2);
            ret.setOwnerId(session.getId());
        }else{
            ret.setOwnerType(1);
            ret.setOwnerId(user.getUserId());
        }
        ret.setAllFileName(allFileName);
        ret.setFileSize((Long)uploadInfoMap.get("size"));
        ret.setAccessType(1);
        ret.setCTime(new Timestamp(1234));
        ret.setLmTime(new Timestamp(f.lastModified()));

        //创建分类，上传文件的分类中
        FileCategory fc = new FileCategory();
        fc.setFType1(FileCategoryType1.IMP);
        fc.setFType2("关系型数据");
        fc.setFType2(ret.getExtName());
        fc.setExtInfo((String)uploadInfoMap.get("orglFilename"));
        ret.addFileCategoryList(fc);

        return ret;
    }
}