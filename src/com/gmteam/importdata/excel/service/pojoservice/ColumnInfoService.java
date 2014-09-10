package com.gmteam.importdata.excel.service.pojoservice;


import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gmteam.framework.core.dao.mybatis.MybatisDAO;
import com.gmteam.importdata.excel.pojo.ColumnInfo;
/** 
 * @author 
 * @version  
 * 类说明 
 */
@Service
public class ColumnInfoService {
    @Resource(name="defaultDAO")
    private MybatisDAO<ColumnInfo> dao;
    @PostConstruct
    public void initParam() {
        dao.setNamespace("columnInfo");
    }
    public List<ColumnInfo> getColumnInfoList(){
        try {
            return dao.queryForList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
