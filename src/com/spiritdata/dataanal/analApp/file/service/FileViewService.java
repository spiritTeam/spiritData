package com.spiritdata.dataanal.analApp.file.service;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.dataanal.analApp.file.pojo.FileViewPo;
import com.spiritdata.filemanage.core.persistence.pojo.FileIndexPo;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;

public class FileViewService {

    @Resource(name="defaultDAO")
    private MybatisDAO<FileViewPo> fileViewDao;
    
    @PostConstruct
    public void initParam() {
    	fileViewDao.setNamespace("fileView");
    }
    
    /**
     * 条件查询文件列表
     * @param paramMap
     * @return
     */
    public List<FileViewPo> searchFileList(Map paramMap){
    	return fileViewDao.queryForList("getFileList",paramMap);
    }

}
