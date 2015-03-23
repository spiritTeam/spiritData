package com.spiritdata.dataanal.common.cache;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.spiritdata.dataanal.SDConstants;
import com.spiritdata.dataanal.task.service.TaskManageService;
import com.spiritdata.framework.core.cache.AbstractCacheLifecycleUnit;
import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;

/**
 * 数据分析缓存生命周期
 * @author mht,wh
 */
@Component
public class DataanalCacheLifecycleUnit extends AbstractCacheLifecycleUnit {
    /**
     * 日志
     */
    private Logger logger = Logger.getLogger(DataanalCacheLifecycleUnit.class);

    @Resource
    TaskManageService tmService;

    @Override
    public void init() {
        try {
            //装载任务信息
            loadTaskCache();
        } catch (Exception e) {
            logger.info("启动时加载{数据分析}缓存出错", e);
        }
    }

    public void loadTaskCache() throws Exception {
        try {
            Map<String, Map<String, Object>> tasksStore = tmService.makeCacheTasks(); 
            SystemCache.setCache(new CacheEle<Map<String, Map<String, Object>>>(SDConstants.CACHE_TASKS, "任务信息", tasksStore));
        } catch(Exception e) {
            throw new Exception("加载缓存项[任务信息]失败：", e);
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
            if (key.equals(SDConstants.CACHE_TASKS)) {
                rce = (CacheEle<?>)SystemCache.remove(SDConstants.CACHE_TASKS);
                key = rce.getName();
                loadTaskCache();
            }
        } catch (Exception e) {
            logger.info("刷新缓存项{数据分析["+key+"]}失败：", e);
        }
    }
}