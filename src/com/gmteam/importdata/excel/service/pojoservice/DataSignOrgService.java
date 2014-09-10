package com.gmteam.importdata.excel.service.pojoservice;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.gmteam.framework.core.dao.mybatis.MybatisDAO;
import com.gmteam.importdata.excel.pojo.DataSignOrg;

/** 
 * @author 
 * @version  
 * 类说明 
 */
@Component
public class DataSignOrgService {
    @Resource(name="defaultDAO")
    private MybatisDAO<DataSignOrg> dao;
    @PostConstruct
    public void initParam() {
        dao.setNamespace("dataSignOrg");
    }
    public List<DataSignOrg> getDataSignOrgList(){
        try {
            return dao.queryForList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean insertDataSignOrg(DataSignOrg dataSignOrg){
        boolean rst = false;
        try {
            dao.insert(dataSignOrg);
            return rst=true;
        } catch (Exception e) {
            e.printStackTrace();
            return rst;
        }
    }
}
