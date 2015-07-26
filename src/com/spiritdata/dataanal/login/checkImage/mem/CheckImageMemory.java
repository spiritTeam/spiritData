package com.spiritdata.dataanal.login.checkImage.mem;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <p>验证码内存结构
 * <p>本对象只存储数据，不进行任何相关的操作，相关操作在CheckImageMemoryService中
 * @author wh
 */
public class CheckImageMemory {
    //java的占位单例模式===begin
    private static class InstanceHolder {
        public static CheckImageMemory instance = new CheckImageMemory();
    }
    public static CheckImageMemory getInstance() {
        return InstanceHolder.instance;
    }
    public CheckImageMemory() {
        super();
        checkImagePool = new ConcurrentHashMap<String, OneCheckImage>();
        checkCodeList = new CopyOnWriteArrayList<String>();
    }
    //java的占位单例模式===end

    protected static int maxImageSize = 1000; //最大存储1000个验证码信息
    protected Map<String, OneCheckImage> checkImagePool;
    protected List<String> checkCodeList;

    protected int getSize() {
        return checkCodeList.size();
    }
}