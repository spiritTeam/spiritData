package com.spiritdata.filemanage.IMP.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.dataanal.exceptionC.Dtal0101CException;
import com.spiritdata.filemanage.IMP.model.ImportFile;
import com.spiritdata.filemanage.core.service.FileManageService;

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

    public void saveImportFile(ImportFile impFile) {
        try {
            fmService.saveFileInfo(impFile.convertToFileInfo());
        } catch(Exception e) {
            if (e instanceof Dtal0101CException) throw (Dtal0101CException)e;
            else throw new Dtal0101CException(e);
        }
    }
}