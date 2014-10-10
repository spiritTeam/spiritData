package com.gmteam.spiritdata.importdata.excel.service.pojoservice;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gmteam.framework.core.dao.mybatis.MybatisDAO;
import com.gmteam.spiritdata.importdata.excel.pojo.store.TableInfo;
/** 
 * @author 
 * @version  
 * 类说明 
 */
@Service
public class TableInfoService {
    @Resource(name="defaultDAO")
    private MybatisDAO<TableInfo> dao;
    @PostConstruct
    public void initParam() {
        dao.setNamespace("tableInfo");
    }
    public boolean insertTableInfo(TableInfo ti){
        boolean rst = false;
        try {
            dao.insert(ti);
            return rst=true;
        } catch (Exception e) {
            e.printStackTrace();
            return rst;
        }
    }
    public List<TableInfo> getTableInfoList(){
        try {
            return dao.queryForList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
