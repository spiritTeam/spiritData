package com.spiritdata.dataanal.visitmanage.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.dataanal.common.model.Owner;
import com.spiritdata.dataanal.report.persistence.pojo.ReportPo;
import com.spiritdata.dataanal.visitmanage.persistence.pojo.VisitLogPo;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;

/**
 * 报告访问服务类
 * @author wh
 */
public class ReportVisitService {
    @Resource(name="defaultDAO")
    private MybatisDAO<ReportPo> reportDao;
    @Resource(name="defaultDAO")
    private MybatisDAO<VisitLogPo> visitLogDao;

    @PostConstruct
    public void initParam() {
        reportDao.setNamespace("report");
        visitLogDao.setNamespace("visitLog");
    }

    public Map<Owner, List<?>> getNoVisitData() {
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
}