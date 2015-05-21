package com.spiritdata.dataanal.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.spiritdata.dataanal.SDConstants;
import com.spiritdata.dataanal.common.model.Owner;
import com.spiritdata.framework.UGA.UgaConstants;
import com.spiritdata.framework.core.cache.AbstractCacheLifecycleUnit;
import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;

public class DataanalCLU extends AbstractCacheLifecycleUnit {
    private Logger logger = Logger.getLogger(DataanalCLU.class);

    @Override
    public void init() {
        //装载未访问用户列表
        loadNoVisitList();
    }

    @Override
    public void refresh(String key) {
    }

    /**
     * 装载未访问列表到内存
     */
    private void loadNoVisitList() {
        Map<String, Map<Owner, List<?>>> noVisitMap = new ConcurrentHashMap<String, Map<Owner, List<?>>>();
        
        SystemCache.setCache(new CacheEle<Map<String, Map<Owner, List<?>>>>(SDConstants.CACHE_NOVISIT, "未访问对象列表", noVisitMap));
    }
}