package com.spiritdata.dataanal.cache;

import org.apache.log4j.Logger;

import com.spiritdata.framework.core.cache.AbstractCacheLifecycleUnit;

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
        // TODO Auto-generated method stub
        
    }
}