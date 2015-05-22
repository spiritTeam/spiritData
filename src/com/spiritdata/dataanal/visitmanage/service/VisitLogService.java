package com.spiritdata.dataanal.visitmanage.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.spiritdata.dataanal.SDConstants;
import com.spiritdata.dataanal.common.model.Owner;
import com.spiritdata.dataanal.report.persistence.pojo.ReportPo;
import com.spiritdata.dataanal.visitmanage.persistence.pojo.VisitPo;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;

/**
 * 访问日志管理类
 * @author wh
 */
public class VisitLogService {
    @Resource(name="defaultDAO")
    private MybatisDAO<VisitPo> visitDao; //任务组

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
        visitDao.execute("changeOwner", param);
        //修改内存中的用户所属
        Owner o = new Owner(2, oldOwnerId);
        Map<String, Map<Owner, List<?>>> m = (Map<String, Map<Owner, List<?>>>)SystemCache.getCache(SDConstants.CACHE_NOVISIT);
        Map<Owner, List<?>> cacheData = m.get("reportData");
        List<?> noVisitReportL = cacheData.get(o);
        if (noVisitReportL!=null) {
            
        }
        return true;
    }

    
}