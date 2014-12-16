package com.spiritdata.dataanal.filemanage.core.service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.dataanal.filemanage.core.model.FileCategory;
import com.spiritdata.dataanal.filemanage.core.model.FileInfo;
import com.spiritdata.dataanal.filemanage.core.model.FileRelation;
import com.spiritdata.dataanal.filemanage.core.persistence.pojo.FileCategoryPo;
import com.spiritdata.dataanal.filemanage.core.persistence.pojo.FileIndexPo;
import com.spiritdata.dataanal.filemanage.core.persistence.pojo.FileRelationPo;

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
     * @throws Exception 
     */
    public void saveFileInfo(FileInfo fi) throws Exception {
        //文件映射表
        FileIndexPo fiPo = fi.convert2Po();
        //TODO 还要判断是否已经存在！！！！ 
        fileIndexDao.insert(fiPo);
        //文件分类表
        if (fi.getFileCategoryList()!=null&&fi.getFileCategoryList().size()>0) {
            for (FileCategory fc: fi.getFileCategoryList()) {
                //TODO 还要判断是否已经存在！！！！
                FileCategoryPo fcPo = fc.convert2Po();
                fcPo.setFId(fiPo.getId());
                fileCategoryDao.insert(fcPo);
            }
        }
        //文件关系表
        //TODO 还要判断是否已经存在！！！！
        if (fi.getPositiveRelationFiles()!=null&&fi.getPositiveRelationSize()>0) {
            for (FileRelation fr: fi.getPositiveRelationFiles()) {
                FileRelationPo frPo= fr.convert2Po();
                if (frPo.getRType2().indexOf("ContraryRef::")==0) frPo.setRType2(frPo.getRType2().substring("ContraryRef::".length()+1));
                if (frPo.getDesc().indexOf("反关系—")==0) frPo.setDesc(frPo.getDesc().substring("反关系—".length()+1));
                fileRelationDao.insert(frPo);
            }
        }
        if (fi.getEqualRelationFiles()!=null&&fi.getEqualRelationSize()>0) {
            for (FileRelation fr: fi.getEqualRelationFiles()) {
                FileRelationPo frPo= fr.convert2Po();
                if (frPo.getRType2().indexOf("ContraryRef::")==0) frPo.setRType2(frPo.getRType2().substring("ContraryRef::".length()+1));
                if (frPo.getDesc().indexOf("反关系—")==0) frPo.setDesc(frPo.getDesc().substring("反关系—".length()+1));
                fileRelationDao.insert(frPo);
            }
        }
    }
}