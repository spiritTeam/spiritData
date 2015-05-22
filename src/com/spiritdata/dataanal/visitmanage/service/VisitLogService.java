package com.spiritdata.dataanal.visitmanage.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.dataanal.SDConstants;
import com.spiritdata.dataanal.common.model.Owner;
import com.spiritdata.dataanal.visitmanage.persistence.pojo.VisitLogPo;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;

/**
 * 访问日志管理类，这是一个壳程序
 * @author wh
 */
public class VisitLogService {
    @Resource(name="defaultDAO")
    private MybatisDAO<VisitLogPo> visitLogDao; //任务组

    @Resource
    private ReportVisitService rvService;

    @PostConstruct
    public void initParam() {
        visitLogDao.setNamespace("visitLog");
    }

    /**
     * 调整所有者Id。登录成功后，切换所有者时所调用的方法
     * @param oldOwnerId 旧所有者Id，目前是SessionId
     * @param newOwnerId 新所有者Id，目前是用户的Id
     * @return 调整成功，返回true，否则，返回false
     */
    public boolean changeOwnerId(String oldOwnerId, String newOwnerId) {
        //调整缓存中的数据
        //调整数据库中的数据
        Map<String, String> param = new HashMap<String, String>();
        param.put("oldOwnerId", oldOwnerId);
        param.put("newOwnerId", newOwnerId);
        visitLogDao.execute("changeOwner", param);
        //修改内存中的用户所属
        Owner o = new Owner(2, oldOwnerId);
        Map<String, Map<Owner, List<?>>> m = (Map<String, Map<Owner, List<?>>>)SystemCache.getCache(SDConstants.CACHE_NOVISIT);
        if (m==null) {
            
        }
        //若有其他类型的未访问对象，则需要在这里加入新的内容
        //1-未访问报告方法（若有其他未访问，可照此办理）
        Map<Owner, List<?>> cacheData = m.get("reportData");
        if (cacheData==null) {
            cacheData = rvService.getNoVisitData();
            if (cacheData!=null) {
                m.put("reportData", cacheData);
            }
        }
        if (cacheData!=null) {
            List<?> noVisitReportL = cacheData.remove(o);
            if (noVisitReportL!=null) {
                Owner no = new Owner(1, newOwnerId);
                cacheData.put(no, noVisitReportL);
            }
        }
        return true;
    }

    public Map<Owner, List<?>> getNoVisitData_REPORT() {
        return rvService.getNoVisitData();
    }
}