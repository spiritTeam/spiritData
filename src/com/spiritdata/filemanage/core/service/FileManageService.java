package com.spiritdata.filemanage.core.service;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.filemanage.core.model.FileCategory;
import com.spiritdata.filemanage.core.model.FileInfo;
import com.spiritdata.filemanage.core.model.FileRelation;
import com.spiritdata.filemanage.core.persistence.pojo.FileCategoryPo;
import com.spiritdata.filemanage.core.persistence.pojo.FileIndexPo;
import com.spiritdata.filemanage.core.persistence.pojo.FileRelationPo;
import com.spiritdata.filemanage.exceptionC.Flmg0101CException;

/**
 * 文件管理服务类
 * @author wh
 */
@Component
public class FileManageService {
    @Resource(name="defaultDAO")
    private MybatisDAO<FileIndexPo> fileIndexDao;
    @Resource(name="defaultDAO")
    private MybatisDAO<FileCategoryPo> fileCategoryDao;
    @Resource(name="defaultDAO")
    private MybatisDAO<FileRelationPo> fileRelationDao;

    @PostConstruct
    public void initParam() {
        fileIndexDao.setNamespace("fileIndex");
        fileCategoryDao.setNamespace("fileCategory");
        fileRelationDao.setNamespace("fileRelation");
    }

    /**
     * 存储文件信息
     * @param fi 文件信息
     */
    public void saveFileInfo(FileInfo fi) {
        try {
            //文件映射表
            FileIndexPo fiPo = fi.convert2Po();
            fi.setId(fiPo.getId());
            //TODO 还要判断是否已经存在！！！！ 
            fileIndexDao.insert(fiPo);
            //文件分类表
            if (fi.getFileCategoryList()!=null&&fi.getFileCategoryList().size()>0) {
                for (FileCategory fc: fi.getFileCategoryList()) {
                    //TODO 还要判断是否已经存在！！！！
                    FileCategoryPo fcPo = fc.convert2Po();
                    fc.setId(fcPo.getId());
                    fcPo.setFId(fiPo.getId());
                    fileCategoryDao.insert(fcPo);
                }
            }
            //文件关系表
            //TODO 还要判断是否已经存在！！！！
            if (fi.getPositiveRelationFiles()!=null&&fi.getPositiveRelationSize()>0) {
                for (FileRelation fr: fi.getPositiveRelationFiles()) {
                    FileRelationPo frPo= fr.convert2Po();
                    fr.setId(frPo.getId());
                    if (frPo.getRType2().indexOf("ContraryRef::")==0) frPo.setRType2(frPo.getRType2().substring("ContraryRef::".length()+1));
                    if (frPo.getDesc().indexOf("反关系—")==0) frPo.setDesc(frPo.getDesc().substring("反关系—".length()+1));
                    fileRelationDao.insert(frPo);
                }
            }
            if (fi.getEqualRelationFiles()!=null&&fi.getEqualRelationSize()>0) {
                for (FileRelation fr: fi.getEqualRelationFiles()) {
                    FileRelationPo frPo= fr.convert2Po();
                    fr.setId(frPo.getId());
                    if (frPo.getRType2().indexOf("ContraryRef::")==0) frPo.setRType2(frPo.getRType2().substring("ContraryRef::".length()+1));
                    if (frPo.getDesc().indexOf("反关系—")==0) frPo.setDesc(frPo.getDesc().substring("反关系—".length()+1));
                    fileRelationDao.insert(frPo);
                }
            }
        } catch(Exception e) {
            throw new Flmg0101CException("存储文件信息", e);
        }
    }

    /**
     * 存储文件关系
     * @param fr 文件关系
     */
    public void saveFileRelation(FileRelation fr) {
        try {
            //TODO 还要判断是否已经存在！！！！
            fileRelationDao.insert(fr.convert2Po());
        } catch(Exception e) {
            throw new Flmg0101CException("存储文件关系", e);
        }
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
}