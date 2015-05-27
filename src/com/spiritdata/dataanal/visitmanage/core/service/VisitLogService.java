package com.spiritdata.dataanal.visitmanage.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.dataanal.common.model.Owner;
import com.spiritdata.dataanal.visitmanage.core.persistence.pojo.VisitLogPo;
import com.spiritdata.dataanal.visitmanage.run.mem.VisitMemoryService;
import com.spiritdata.dataanal.visitmanage.service.ReportVisitService;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.util.SequenceUUID;

/**
 * 访问日志管理类，这是一个壳程序
 * @author wh
 */
public class VisitLogService {
    @Resource(name="defaultDAO")
    private MybatisDAO<VisitLogPo> visitLogDao; //任务组

    @Resource
    private ReportVisitService rvService;

    @PostConstruct
    public void initParam() {
        visitLogDao.setNamespace("visitLog");
    }

    /**
     * 调整所有者Id。登录成功后，切换所有者时所调用的方法
     * @param oldOwnerId 旧用户Id，必然是Session类型
     * @param newOwnerId 新用户Id，必然是注册用户类型
     * @return 调整成功，返回true，否则，返回false
     */
    public boolean changeOwnerId(String oldOwnerId, String newOwnerId) {
        //1-调整数据库中的数据
        Map<String, String> param = new HashMap<String, String>();
        param.put("oldOwnerId", oldOwnerId);
        param.put("newOwnerId", newOwnerId);
        visitLogDao.execute("changeOwner", param);
        //2-修改内存中的用户所属
        VisitMemoryService vms = VisitMemoryService.getInstance();
        return vms.changeOwnerId(oldOwnerId, newOwnerId);
    }

    /**
     * 装载未访问的报告信息
     * @return 未访问的报告信息
     */
    public Map<Owner, List<?>> loadNoVisitData_REPORT() {
        return rvService.loadNoVisitData();
    }

    /**
     * 保存访问日志信息到数据库
     * @param vlp 访问日志信息
     */
    public void SaveVLP2DB(VisitLogPo vlp) {
        vlp.setId(SequenceUUID.getPureUUID());
        visitLogDao.insert(vlp);
    }

    
}