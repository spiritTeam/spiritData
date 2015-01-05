package com.spiritdata.filemanage.ANAL.service;

/**
 * 分析文件服务类
 * @author wh
 */
import javax.annotation.Resource;

import com.spiritdata.filemanage.ANAL.model.AnalResultFile;
import com.spiritdata.filemanage.core.model.FileInfo;
import com.spiritdata.filemanage.core.service.FileManageService;
import com.spiritdata.filemanage.exceptionC.Flmg0101CException;

public class AanlResultFileService {
    @Resource
    private FileManageService fmService;

    /**
     * 保存分析结果文件
     * @param arFile 分析结果文件对象
     * @return 对应该分析结果文件对象的保存后的文件信息
     */
    public FileInfo saveFile(AnalResultFile arFile) {
        try {
            FileInfo fi = arFile.convertToFileInfo();
            fmService.saveFileInfo(fi);
            return fi;
        } catch(Exception e) {
            throw new Flmg0101CException(e);
        }
    }
}