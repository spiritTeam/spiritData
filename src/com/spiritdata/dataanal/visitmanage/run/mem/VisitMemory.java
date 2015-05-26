package com.spiritdata.dataanal.visitmanage.run.mem;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.spiritdata.dataanal.common.model.Owner;


/**
 * 访问内存对象，这里只包括
 * @author wh
 */
public class VisitMemory {
    //java的占位单例模式===begin
    private static class InstanceHolder {
        public static VisitMemory instance = new VisitMemory();
    }
    public static VisitMemory getInstance() {
        return InstanceHolder.instance;
    }

    protected Map<String, Map<Owner, List<?>>> ownersNoVisitData = null;

    /**
     * 参数初始化，必须首先执行这个方法，访问日志内存类才能使用
     */
    public void init() {
        ownersNoVisitData = new ConcurrentHashMap<String, Map<Owner, List<?>>>();
    }
}