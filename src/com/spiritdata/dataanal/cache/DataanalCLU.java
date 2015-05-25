package com.spiritdata.dataanal.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.spiritdata.dataanal.SDConstants;
import com.spiritdata.dataanal.common.model.Owner;
import com.spiritdata.dataanal.visitmanage.service.VisitLogService;
import com.spiritdata.framework.core.cache.AbstractCacheLifecycleUnit;
import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;

public class DataanalCLU extends AbstractCacheLifecycleUnit {
    private Logger logger = Logger.getLogger(DataanalCLU.class);

    @Resource
    private VisitLogService vlService;

    @Override
    public void init() {
        //装载未访问用户列表
        loadNoVisitList();
        logger.info("装载用户未访问对象列表成功！");
    }

    @Override
    public void refresh(String key) {
    }

    /**
     * 装载未访问列表到内存
     */
    private void loadNoVisitList() {
        Map<String, Map<Owner, List<?>>> ownersNoVisitData = new ConcurrentHashMap<String, Map<Owner, List<?>>>();
        //未访问的数据
        //1-报告
        Map<Owner, List<?>> reportData = vlService.getNoVisitData_REPORT();
        ownersNoVisitData.put("report", reportData);
        //其他未访问的信息再继续在下面加，这个可以考虑用一个配置文件加载，再说

//        SystemCache.setCache(new CacheEle<Map<String, Map<Owner, List<?>>>>(SDConstants.CACHE_NOVISIT, "用户访问", ownersNoVisitData));
    }
}
