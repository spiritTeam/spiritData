package com.spiritdata.dataanal.visitmanage.run.mem;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.spiritdata.dataanal.common.model.Owner;
import com.spiritdata.dataanal.task.core.service.TaskManageService;
import com.spiritdata.dataanal.visitmanage.service.VisitLogService;
import com.spiritdata.filemanage.core.service.FileManageService;
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

    /**
     * 访问日志内存数据
     */
    protected VisitMemory vm = null;
    protected void setVisitMemory() {
        this.vm = VisitMemory.getInstance();
    }

    /**
     * 初始化方法
     */
    public void init() {
        this.vm.init();
        //1-报告访问数据初始化
        ServletContext sc = (ServletContext)SystemCache.getCache(FConstants.SERVLET_CONTEXT).getContent();
        VisitLogService vlService = (VisitLogService)WebApplicationContextUtils.getWebApplicationContext(sc).getBean("visitLogService");
        Map<Owner, List<?>> reportData = vlService.getNoVisitData_REPORT();
        this.vm.ownersNoVisitData.put("report", reportData);
        //其他未访问的信息再继续在下面加，这个可以考虑用一个配置文件加载，再说
    }

}