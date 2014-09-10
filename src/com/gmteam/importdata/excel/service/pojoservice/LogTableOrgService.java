package com.gmteam.importdata.excel.service.pojoservice;


import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gmteam.framework.core.dao.mybatis.MybatisDAO;
import com.gmteam.importdata.excel.pojo.LogTableOrg;
/** 
 * @author 
 * @version  
 * 类说明 
 */
@Service
public class LogTableOrgService {
    @Resource(name="defaultDAO")
    private MybatisDAO<LogTableOrg> dao;
    @PostConstruct
    public void initParam() {
        dao.setNamespace("logTableOrg");
    }
    public boolean insertLogTableOrg(LogTableOrg lto){
        boolean rst = false;
        try {
            dao.insert(lto);
            return rst=true;
        } catch (Exception e) {
            e.printStackTrace();
            return rst;
        }
    }
    public List<LogTableOrg> getLogTableOrgList(){
        try {
            return dao.queryForList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
