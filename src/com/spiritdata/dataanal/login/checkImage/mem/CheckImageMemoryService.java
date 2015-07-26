package com.spiritdata.dataanal.login.checkImage.mem;

import java.util.Date;
import java.util.Random;

import org.apache.log4j.Logger;

import com.spiritdata.dataanal.common.util.SpiritRandom;
import com.spiritdata.dataanal.login.checkImage.ImageGenerate;

/**
 * <p>验证码内存的操作服务类。
 * <pre>
 * 包括:
 * 1-增加、删除、更新；
 * 2-内存的加载；
 * </pre>
 * @author wh
 */
public class CheckImageMemoryService {
    private String randSeed = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";//随机产生的字符串
    private int strLength = 4; //随机数长度
    private long maxInterval = 12*60*60*1000; //陈旧时间，若内存中的对象与现在时间间隔大于此值，则意味着需要更换了，如果为-1，则内存永久不刷新

    protected Logger log = Logger.getLogger(this.getClass());

    //java的占位单例模式===begin
    private static class InstanceHolder {
        public static CheckImageMemoryService instance = new CheckImageMemoryService();
    }
    public static CheckImageMemoryService getInstance() {
        return InstanceHolder.instance;
    }
    //java的占位单例模式===end

    /**
     * 初始化内存对象
     */
    public void initMemory() {
        CheckImageMemory cim = CheckImageMemory.getInstance();
        while (cim.getSize()<CheckImageMemory.maxImageSize) addNew();
    }

    /**
     * 随机从内存中获得一个验证码对象
     * @return 验证码对象
     */
    public OneCheckImage getOneCheckImage() {
        CheckImageMemory cim = CheckImageMemory.getInstance();

        //从内存中，随机获得一个验证码对象
        OneCheckImage oci = null;
        if (cim.checkImagePool!=null&&cim.checkImagePool.size()>0&&cim!=null) {
            int index = SpiritRandom.getRandom(new Random(), 0, cim.getSize()-1);
            if (index<cim.checkCodeList.size()) oci=cim.checkImagePool.get(cim.checkCodeList.get(index));

            if (oci==null) oci = this.generateNew();//若得不到，就生成一个新的，并返回
            else { //若时间太久，则替换新的
                if (this.maxInterval!=-1) {
                    if (((new Date()).getTime()-oci.createTime)>this.maxInterval) {
                        synchronized(cim) {
                            oci=this.generateNew();
                            cim.checkImagePool.remove(cim.checkCodeList.get(index));
                            cim.checkCodeList.add(index, oci.checkCode);
                            cim.checkImagePool.put(oci.checkCode, oci);
                        }
                    }
                }
            }
        }
        return oci;
    }

    /**
     * 从内存中，根据验证码获得验证码对象
     * @param _index 这是一个随机数，是memeory中内存对象的序号
     * @return 验证码对象
     */
    public OneCheckImage getCheckImage(String checkCode) {
        CheckImageMemory cim = CheckImageMemory.getInstance();
        if (cim.checkImagePool==null||cim.checkImagePool.size()==0||cim==null) return null;
        return cim.checkImagePool.get(checkCode);
    }

    /*
     * 向内存加入一个新的验证码
     */
    private void addNew() {
        this.addOneCheckImage(this.generateNew());
    }
    /*
     * 加入一个验证码对象
     * @param oci 验证码对象
     */
    private synchronized void addOneCheckImage(OneCheckImage oci) {
        CheckImageMemory cim = CheckImageMemory.getInstance();
        if (cim.getSize()>=CheckImageMemory.maxImageSize) return;

        cim.checkImagePool.put(oci.checkCode, oci);
        cim.checkCodeList.add(oci.checkCode);
    }
    /*
     * 生成一个新的验证码对象
     * @return 新验证码对象
     */
    private OneCheckImage generateNew() {
        OneCheckImage oci = new OneCheckImage();
        oci.createTime = (new Date()).getTime();
        oci.checkCode = this.getRandomStr();
        oci.checkImage = ImageGenerate.generate(oci.checkCode);
        return oci;
    }
    /*
     * 生成一个随机数
     * 返回一个长度为4的随机字符串
     */
    private String getRandomStr() {
        Random random = new Random();
        String ret = "";
        for (int i=0; i<this.strLength; i++) ret+=this.randSeed.charAt(random.nextInt(this.randSeed.length()));
        return ret;
    }
}