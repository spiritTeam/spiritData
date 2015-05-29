package com.spiritdata.dataanal.visitmanage.core.service;

import java.util.List;
import java.util.Map;

import com.spiritdata.dataanal.common.model.Owner;
import com.spiritdata.dataanal.report.persistence.pojo.ReportPo;
import com.spiritdata.dataanal.visitmanage.run.mem.VisitMemoryService;

public abstract class AbstractCategoryService implements VL_CategoryService{

    /**
     * 得到某一用户的未访问报告列表
     * @param o 所属用户
     * @return 未访问报告列表
     */
    public List<ReportPo> getNoVisitList(Owner o) {
        VisitMemoryService vms = VisitMemoryService.getInstance();
        return (List<ReportPo>)vms.getNoVisitList(o, this.getCategory().getName());
    }

    /**
     * 得到的所有未访问报告的数据
     * @return 所有未访问报告的数据
     */
    public Map<Owner, List<?>> getNoVisitMap() {
        VisitMemoryService vms = VisitMemoryService.getInstance();
        return vms.getNoVisitData(this.getCategory().getName());
    }
}