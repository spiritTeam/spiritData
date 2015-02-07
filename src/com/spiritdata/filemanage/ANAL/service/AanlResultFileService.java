package com.spiritdata.filemanage.ANAL.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.filemanage.ANAL.model.AnalResultFile;
import com.spiritdata.filemanage.core.model.FileInfo;
import com.spiritdata.filemanage.core.pattern.model.BeManageFile;
import com.spiritdata.filemanage.core.pattern.model.ToBeStoreFile;
import com.spiritdata.filemanage.core.pattern.service.AbstractWriteString2FileByToBeStoreFile;
import com.spiritdata.filemanage.core.pattern.service.WriteJsonD;
import com.spiritdata.filemanage.core.persistence.pojo.FileIndexPo;
import com.spiritdata.filemanage.core.service.FileManageService;
import com.spiritdata.filemanage.exceptionC.Flmg0003CException;
import com.spiritdata.filemanage.exceptionC.Flmg0101CException;
import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.util.FileNameUtils;
import com.spiritdata.jsonD.model.JsonD;
import com.spiritdata.jsonD.model.JsondHead;

/**
 * 分析文件服务类
 * @author wh
 */
public class AanlResultFileService extends AbstractWriteString2FileByToBeStoreFile implements WriteJsonD {
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
        String storeFile = FileNameUtils.concatPath(root, "analData"+File.separator+fileNameSeed+".json");
        return storeFile.replace("\\", "/");
    }

    /**
     * 把JsonD写入文件，并返回分析结果对象文件。<br/>
     * @param content jsond数据
     * @param analResultSeed 分析结果文件的种子，需要设置分类和说，返回值将根据这个种子进行设置
     * @return 分析结果文件，根据种子生成，并补充jsondCode和名称信息
     */
    @Override
    public BeManageFile write2FileAsJsonD(Object jsonD, ToBeStoreFile fileSeed) {
        JsonD jsond = (JsonD)jsonD;
        AnalResultFile analResultSeed = (AnalResultFile)fileSeed;
        if ((analResultSeed.getFileNameSeed()==null||analResultSeed.getFileNameSeed().trim().length()==0)
          &&(analResultSeed.getFullFileName()==null||analResultSeed.getFullFileName().trim().length()==0)) {
            throw new Flmg0003CException(new IllegalArgumentException("文件种子对象中'文件名生成种子(fileNameSeed)'或'文件全名(fullFileName)'至少设定一个"));
        }

        //文件存储
        String storeFileName = this.getStoreFileName(fileSeed);
        Object _HEAD = jsond.get_HEAD();
        if (_HEAD instanceof JsondHead) {
            ((JsondHead)_HEAD).setFileName(storeFileName);
        }
        this.writeJson2File(jsond.toJson(), fileSeed); //存储为文件

        //返回值处理
        if (_HEAD instanceof JsondHead) {
            analResultSeed.setJsonDCode(((JsondHead)_HEAD).getCode());
        }
        analResultSeed.setFileName(storeFileName);

        return analResultSeed;
    }
}