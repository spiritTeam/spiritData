package com.spiritdata.filemanage.ANAL.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.filemanage.ANAL.model.AnalResultFile;
import com.spiritdata.filemanage.core.persistence.pojo.FileIndexPo;
import com.spiritdata.filemanage.core.service.AbstractWriteString2File;
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
public class AanlResultFileService extends AbstractWriteString2File{
    @Resource(name="defaultDAO")
    private MybatisDAO<FileIndexPo> fileIndexDao;

    @PostConstruct
    public void initParam() {
        fileIndexDao.setNamespace("fileIndex");
    }

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

    @Override
    public String getStoreFileName() {
        //文件名
        String root = (String)(SystemCache.getCache(FConstants.APPOSPATH)).getContent();
        String storeFile = FileNameUtils.concatPath(root, "analData"+File.separator+this.fileNameSeed+".json");
        return storeFile.replace("\\", "/");
    }

    /**
     * 把JsonD写入文件，并返回分析结果对象文件
     * @param jsond jsond数据
     * @param analResultSeed 分析结果文件的种子，返回值将根据这个种子设置，并返回，实际主要是写入jsondCode和文件信息，而种子主要是设置分类和说明
     * @return
     */
    public AnalResultFile write2FileAsJsonD(JsonD jsond, AnalResultFile analResultSeed) {
        AnalResultFile ret = new AnalResultFile();
        String storeFileName = this.getStoreFileName();
        Object _HEAD = jsond.get_HEAD();
        if (_HEAD instanceof JsondHead) {
            ((JsondHead)_HEAD).setFileName(storeFileName);
        }
        this.writeJson2File(jsond.toJson());
        ret.setFileName(storeFileName);
        if (_HEAD instanceof JsondHead) {
            ret.setJsonDCode(((JsondHead)_HEAD).getCode());
        }
        ret.setAnalType(analResultSeed.getAnalType());
        ret.setSubType(analResultSeed.getSubType());
        ret.setObjType(analResultSeed.getObjType());
        ret.setObjId(analResultSeed.getObjId());
        return ret;
    }
}