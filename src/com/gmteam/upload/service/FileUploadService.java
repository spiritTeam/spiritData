package com.gmteam.upload.service;

import org.springframework.stereotype.Component;

/** 
 * @author mht
 * @version  
 * 类说明 
 */
@Component
public class FileUploadService {
    /**
     * 
     * @param uploadFileName
     * @return
     * 根据文件名，返回int值，代表文件类型（暂时只支持excel，其他类型有待扩展）
     * 1--xls，2---。xlsx
     */
    public int getFileType(String uploadFileName) {
        int fileType = 0;
        String fileTypeStr = uploadFileName.substring(uploadFileName.lastIndexOf("."),uploadFileName.length());
        if(fileTypeStr.equals(".xls")){
            fileType = 1;
        }else if(fileTypeStr.equals(".xlsx")){
            fileType = 2;
        }
        return fileType;
    }
}
