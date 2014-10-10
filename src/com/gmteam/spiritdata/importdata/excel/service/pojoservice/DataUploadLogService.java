package com.gmteam.spiritdata.importdata.excel.service.pojoservice;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gmteam.framework.core.dao.mybatis.MybatisDAO;
import com.gmteam.framework.core.model.BaseObject;
import com.gmteam.spiritdata.importdata.excel.storepojo.DataUploadLog;
/** 
 * @author 
 * @version  
 * 类说明 
 */
@SuppressWarnings("serial")
@Service
public class DataUploadLogService extends BaseObject{
    @Resource(name="defaultDAO")
    private MybatisDAO<DataUploadLog> dao;// = new MybatisDAO<BriefUploadLog>();
    @PostConstruct
    public void initParam() {
        dao.setNamespace("dataUploadLog");
    }
    public boolean insertUploadLog(DataUploadLog bul){
        boolean rst = false;
        try {
            dao.insert(bul);
            return rst=true;
        } catch (Exception e) {
            e.printStackTrace();
            return rst;
        }
    }
    public boolean createDataTableInfo(Map<String,Object> createSql){
        boolean rst = false;
        try {
            dao.excute(createSql);
            return rst=true;
        } catch (Exception e) {
            e.printStackTrace();
            return rst;
        }
    }
    public List<DataUploadLog> getUploadLogList(){
        try {
            return dao.queryForList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
