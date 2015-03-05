package com.spiritdata.filemanage.category.IMP.service;

import java.util.List;

import javax.annotation.Resource;

import com.spiritdata.filemanage.category.IMP.model.ImportFile;
import com.spiritdata.filemanage.core.service.FileManageService;

/**
 * 导入文件服务类
 * @author wh
 */
public class ImportFileService {
    @Resource
    private FileManageService fmService;

    public List<ImportFile> getImportFileList(ImportFile param) {
        return null;
    }
}