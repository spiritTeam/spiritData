package com.gmteam.spiritdata.importdata.service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.gmteam.framework.core.dao.mybatis.MybatisDAO;
import com.gmteam.spiritdata.importdata.pojo.ImportFileLog;

/**
 * 文件导入日志服务类
 * @author wh
 */
@Component
public class ImportFileService {
    @Resource
    private MybatisDAO<ImportFileLog> iflDao;

    @PostConstruct
    public void initParam() {
        iflDao.setNamespace("importFileLog");
    }

    /**
     * 新增文件导入日志
     * @param ifl 文件导入日志对象
     */
    public void addImportFile(ImportFileLog ifl) throws Exception {
        iflDao.insert(ifl);
    }
}