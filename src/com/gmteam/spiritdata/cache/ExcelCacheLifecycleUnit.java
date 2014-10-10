package com.gmteam.spiritdata.cache;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.gmteam.framework.core.cache.AbstractCacheLifecycleUnit;
import com.gmteam.framework.core.cache.CacheEle;
import com.gmteam.framework.core.cache.SystemCache;
import com.gmteam.spiritdata.importdata.excel.ExcelConstants;
import com.gmteam.spiritdata.importdata.excel.service.ExcelCacheService;

/**
 * 精灵数据分析缓存生命周期
 * @author mht
 */
@Component
public class ExcelCacheLifecycleUnit extends AbstractCacheLifecycleUnit {
    /**
     * 日志
     */
    private Logger logger = Logger.getLogger(ExcelCacheLifecycleUnit.class);
    @Override
    public void init() {
        try {
            //装载MateData信息
            loadMateDataCache();
            //装载MateData信息
        } catch (Exception e) {
            logger.info("启动时加载{DataTools MateData}缓存出错", e);
        }
    }
    @Resource
    ExcelCacheService excelCacheService;
    public void loadMateDataCache() throws Exception {
        try {
            Map<String,Object> moStore = excelCacheService.getResultMap(); 
            SystemCache.setCache(new CacheEle<Map<String, Object>>(ExcelConstants.DATATOOLS_METADATA_CATCH_STORE, "模块", moStore));
        } catch(Exception e) {
            throw new Exception("加载缓存项{DataTools[MateData-储存]}失败：", e);
        }
    }
    /**
     * 刷新缓存中指定的缓存单元(CacheEle)
     * @param key 缓存单元的标识
     */
    @Override
    public void refresh(String key) {
        try {
            CacheEle<?> rce;
            if (key.equals(ExcelConstants.DATATOOLS_METADATA_CATCH_STORE)) {
                rce = (CacheEle<?>)SystemCache.remove(ExcelConstants.DATATOOLS_METADATA_CATCH_STORE);
                key = rce.getName();
                loadMateDataCache();
            }
        } catch (Exception e) {
            logger.info("加载缓存项{UGA["+key+"]}失败：", e);
        }
    }

}
