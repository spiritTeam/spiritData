package com.spiritdata.dataanal.visitmanage.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.dataanal.common.model.Owner;
import com.spiritdata.dataanal.report.persistence.pojo.ReportPo;
import com.spiritdata.dataanal.visitmanage.core.enumeration.ObjType;
import com.spiritdata.dataanal.visitmanage.run.mem.VisitMemoryService;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;

/**
 * 报告访问服务类
 * @author wh
 */
public class ReportVisitService {
    @Resource(name="defaultDAO")
    private MybatisDAO<ReportPo> reportDao;

    @PostConstruct
    public void initParam() {
        reportDao.setNamespace("report");
    }

    /**
     * 装载未访问数据
     * @return 未访问数据
     */
    public Map<Owner, List<?>> loadNoVisitData() {
        Map<Owner, List<?>> ret = new HashMap<Owner, List<?>>();
        //得到用户报告对象
        List<ReportPo> noVisitL = reportDao.queryForList("noVisitList");
        if (noVisitL!=null&&noVisitL.size()>0) {
            String ownerId = "";
            Owner o=null, _o=null;
            List<?> ownerReportNoVisitList = null;
            int ownerType = -1;
            for (ReportPo rPo: noVisitL) {
                ownerId=rPo.getOwnerId();
                ownerType=rPo.getOwnerType();
                _o = new Owner(ownerId, ownerType);
                if (!_o.equals(o)) {
                    if (o!=null) ret.put(o, ownerReportNoVisitList);
                    ownerReportNoVisitList = new ArrayList<ReportPo>();
                    o=_o;
                }
                ((List<ReportPo>)ownerReportNoVisitList).add(rPo);
            }
            ret.put(o, ownerReportNoVisitList);
        }
        return ret;
    }

    /**
     * 得到某一用户的未访问报告列表
     * @param o 所属用户
     * @return 未访问报告列表
     */
    public List<?> getNoVisitList(Owner o) {
        VisitMemoryService vms = VisitMemoryService.getInstance();
        return vms.getNoVisitList(o, ObjType.REPORT.getName());
    }

    /**
     * 得到的所有未访问报告的数据
     * @return 所有未访问报告的数据
     */
    public Map<Owner, List<?>> getNoVisitMap() {
        VisitMemoryService vms = VisitMemoryService.getInstance();
        return vms.getNoVisitData(ObjType.REPORT.getName());
    }
}