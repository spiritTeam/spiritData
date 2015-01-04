package com.spiritdata.filemanage.IMP.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spiritdata.filemanage.IMP.model.ImportFile;
import com.spiritdata.filemanage.core.model.FileInfo;
import com.spiritdata.filemanage.core.service.FileManageService;
import com.spiritdata.filemanage.exceptionC.Flmg0101CException;

/**
 * 导入文件服务类
 * @author wh
 */

@Component
public class ImportFileService {
    @Resource
    private FileManageService fmService;

    public List<ImportFile> getImportFileList(ImportFile param) {
        return null;
    }

    /**
     * 保存导入文件
     * @param impFile 导入文件对象
     * @return 对应该导入文件对象的保存后的文件信息
     */
    public FileInfo saveImportFile(ImportFile impFile) {
        try {
            FileInfo fi = impFile.convertToFileInfo();
            fmService.saveFileInfo(fi);
            return fi;
        } catch(Exception e) {
            throw new Flmg0101CException(e);
        }
    }
}