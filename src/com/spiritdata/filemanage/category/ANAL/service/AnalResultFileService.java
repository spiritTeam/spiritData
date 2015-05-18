package com.spiritdata.filemanage.category.ANAL.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.filemanage.category.ANAL.model.AnalResultFile;
import com.spiritdata.filemanage.core.model.FileInfo;
import com.spiritdata.filemanage.core.pattern.model.BeManageFile;
import com.spiritdata.filemanage.core.pattern.model.ToBeStoreFile;
import com.spiritdata.filemanage.core.pattern.service.AbstractWriteString2FileByToBeStoreFile;
import com.spiritdata.filemanage.core.pattern.service.WriteJsonD;
import com.spiritdata.filemanage.core.persistence.pojo.FileIndexPo;
import com.spiritdata.filemanage.core.service.FileManageService;
import com.spiritdata.filemanage.exceptionC.Flmg0101CException;
import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.util.FileNameUtils;
import com.spiritdata.jsonD.model.JsonD;
import com.spiritdata.jsonD.model.JsonDHead;

/**
 * 分析文件服务类
 * @author wh
 */
public class AnalResultFileService extends AbstractWriteString2FileByToBeStoreFile implements WriteJsonD {
    @Resource
    private FileManageService fmService;

    @Resource(name="defaultDAO")
    private MybatisDAO<FileIndexPo> fileIndexDao;

    @PostConstruct
    public void initParam() {
        fileIndexDao.setNamespace("fileIndex");
    }

    //=以下数据库处理==================
    /**
     * 按条件获得分析文件列表
     * @param m 条件参数
     * @return
     */
    public List<FileIndexPo> getAnalFiles(Map<String, Object> m) {
        try {
            return fileIndexDao.queryForList("getAnalList", m);
        } catch(Exception e) {
            new Flmg0101CException("获得分析结果文件列表", e); 
        }
        return null;
    }

    /**
     * 存储分析结果文件信息到数据库
     * @param arf 分析结果文件模型
     * @return 分析结果文件对应的模型化文件信息对象
     */
    public FileInfo saveFile(AnalResultFile arf) {
        return fmService.saveFile(arf);
    }

    //=以下文件处理==================
    @Override
    public String buildFileName(String fileNameSeed) {
        String root = (String)(SystemCache.getCache(FConstants.APPOSPATH)).getContent();
        String storeFile = FileNameUtils.concatPath(root, "DataCenter/analysis"+File.separator+fileNameSeed+".json");
        return storeFile.replace("\\", "/");
    }

    /**
     * 把JsonD写入文件，并返回分析结果对象文件。<br/>
     * @param content jsonD数据
     * @param analResultSeed 分析结果文件的种子，需要设置分类和说，返回值将根据这个种子进行设置
     * @return 分析结果文件，根据种子生成，并补充jsonDCode和名称信息
     */
    @Override
    public BeManageFile write2FileAsJson(Object jsonD, ToBeStoreFile fileSeed) {
        JsonD _jsonD = (JsonD)jsonD;
        AnalResultFile analResultSeed = (AnalResultFile)fileSeed;

        //文件存储
        String storeFileName = this.getStoreFileName(fileSeed);
        Object _HEAD = _jsonD.get_HEAD();
        if (_HEAD instanceof JsonDHead) {
            ((JsonDHead)_HEAD).setFileName(storeFileName);
        }
        this.writeJson2File(_jsonD.toJson(), fileSeed); //存储为文件

        //返回值处理
        if (_HEAD instanceof JsonDHead) {
            analResultSeed.setJsonDCode(((JsonDHead)_HEAD).getCode());
        }
        analResultSeed.setFileName(storeFileName);

        return analResultSeed;
    }
}