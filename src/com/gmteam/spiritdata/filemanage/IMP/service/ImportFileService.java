package com.gmteam.spiritdata.filemanage.IMP.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.gmteam.framework.core.dao.mybatis.MybatisDAO;
import com.gmteam.spiritdata.filemanage.IMP.model.ImportFile;
import com.gmteam.spiritdata.filemanage.core.service.FileManageService;

/**
 * 导入文件服务类
 * @author wh
 */

@Component
public class ImportFileService {
    @Resource(name="defaultDAO")
    private MybatisDAO<ImportFile> inportFileDao;
    @Resource
    private FileManageService fmService;

    public List<ImportFile> getImportFileList(ImportFile param) {
        return null;
    }

    public void saveImportFile(ImportFile impFile) throws Exception {
        fmService.saveFileInfo(impFile.convertToFileInfo());
    }
}