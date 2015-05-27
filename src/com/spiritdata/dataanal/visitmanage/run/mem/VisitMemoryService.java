package com.spiritdata.dataanal.visitmanage.run.mem;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.spiritdata.dataanal.common.model.Owner;
import com.spiritdata.dataanal.visitmanage.core.service.VisitLogService;
import com.spiritdata.dataanal.visitmanage.enumeration.ObjType;
import com.spiritdata.dataanal.visitmanage.persistence.pojo.VisitLogPo;
import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.SystemCache;

public class VisitMemoryService {
    protected Logger log = Logger.getLogger(this.getClass());

    //java的占位单例模式===begin
    private static class InstanceHolder {
        public static VisitMemoryService instance = new VisitMemoryService();
    }
    public static VisitMemoryService getInstance() {
        VisitMemoryService tms = InstanceHolder.instance;
        tms.setVisitMemory();
        return tms;
    }
    //java的占位单例模式===end

    /*
     * 访问日志内存数据
     */
    protected VisitMemory vm = null;
    protected void setVisitMemory() {
        this.vm = VisitMemory.getInstance();
    }

    protected VisitLogService vls = null;
    private void setVls() {
        ServletContext sc = (ServletContext)SystemCache.getCache(FConstants.SERVLET_CONTEXT).getContent();
        this.vls = (VisitLogService)WebApplicationContextUtils.getWebApplicationContext(sc).getBean("visitLogService");
    }
    private VisitLogService getVls() {
        if (this.vls==null) this.setVls();
        return this.vls;
    }

    /**
     * 初始化方法
     */
    public void init() {
        this.vm.init();
        //1-报告访问数据初始化
        this.setVls();
        Map<Owner, List<?>> reportData = this.vls.loadNoVisitData_REPORT();
        this.vm.ownersNoVisitData.put(ObjType.REPORT.getName(), reportData);
        //其他未访问的信息再继续在下面加，这个可以考虑用一个配置文件加载，再说
    }

    /**
     * 把队列中的内容存入数据库
     * @throws InterruptedException 
     */
    public void Save2DB() throws InterruptedException {
        VisitLogPo vlp = this.vm.visitQueue.take();
        VisitLogService _vls=this.getVls();
        _vls.SaveVLP2DB(vlp);
    }

    /**
     * 存储访问日志信息到队列
     * @param vlp 访问日志信息
     * @throws InterruptedException 
     */
    public void put2Queue(VisitLogPo vlp) throws InterruptedException {
        vlp.setVisitTime(new Timestamp(System.currentTimeMillis()));
        this.vm.visitQueue.put(vlp);
        //清理未访问数据
        if (this.vm.ownersNoVisitData!=null) {
            Owner _o = null;
            Map<Owner, List<?>> oneCategoryMap = this.vm.ownersNoVisitData.get(ObjType.getObjType(vlp.getObjType()).getName());
            if (oneCategoryMap!=null&&oneCategoryMap.size()>0) {
                for (Owner o: oneCategoryMap.keySet()) {
                    _o= new Owner(vlp.getOwnerType(), vlp.getOwnerId());
                    if (o.equals(_o)) {//移除已访问的信息
                        List<?> l = oneCategoryMap.get(o);
                        if ()
                    }
                }
            }
        }
    }

    /**
     * 根据用户信息，得到某类未访问对象的数据列表
     * @param o 用户信息
     * @param nvCatagory 对象分类字符串，目前只有report
     * @return
     */
    public List<?> getNoVisitList(Owner o, String nvCategory) {
        Map<Owner, List<?>> categoryMap = this.vm.ownersNoVisitData.get(nvCategory);
        if (categoryMap!=null&&categoryMap.size()>0) {
            List<?> ret = categoryMap.get(o);
            if (ret!=null&&ret.size()>0) return ret;
        }
        return null;
    }

    /**
     * 得到某类未访问对象的所有数据
     * @param nvCatagory 对象分类字符串，目前只有report
     * @return 某类未访问对象的所有数据
     */
    public Map<Owner, List<?>> getNoVisitData(String nvCategory) {
        Map<Owner, List<?>> categoryMap = this.vm.ownersNoVisitData.get(nvCategory);
        if (categoryMap!=null&&categoryMap.size()>0) return categoryMap;
        return null;
    }

    /**
     * 调整内存中所有者。登录成功后，切换所有者时所调用的方法
     * @param oldOwnerId 旧用户Id，必然是Session类型
     * @param newOwnerId 新用户Id，必然是注册用户类型
     * @return 调整成功，返回true，否则，返回false
     */
    public boolean changeOwnerId(String oldOwnerId, String newOwnerId) {
        return false;
    }
}