package com.gmteam.spiritdata.importdata.service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.gmteam.framework.core.dao.mybatis.MybatisDAO;
import com.gmteam.spiritdata.importdata.pojo.ImportFileLog;

public class ImportFileLogService {
    @Resource(name="defaultDAO")
    private MybatisDAO<ImportFileLog> iflDao;

    @PostConstruct
    public void initParam() {
        iflDao.setNamespace("importFileLog");
    }

    public void addImportFileLog(ImportFileLog ifl) throws Exception {
        iflDao.insert(ifl);
    }
}