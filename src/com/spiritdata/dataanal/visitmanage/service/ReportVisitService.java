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
import com.spiritdata.dataanal.visitmanage.core.persistence.pojo.VisitLogPo;
import com.spiritdata.dataanal.visitmanage.core.service.VL_CategoryService;
import com.spiritdata.filemanage.core.persistence.pojo.FileIndexPo;
import com.spiritdata.filemanage.core.service.FileManageService;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.util.FileNameUtils;
import com.spiritdata.dataanal.visitmanage.core.service.AbstractCategoryService;

/**
 * 报告访问服务类
 * @author wh
 */
public class ReportVisitService extends AbstractCategoryService implements VL_CategoryService {
    @Resource(name="defaultDAO")
    private MybatisDAO<ReportPo> reportDao;

    @PostConstruct
    public void initParam() {
        reportDao.setNamespace("report");
    }

    //文件操作
    @Resource
    FileManageService fmService;

    /**
     * 装载未访问数据
     * @return 未访问数据
     */
    public Map<Owner, List<?>> load_getNoVisitData() {
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

    @Override
    public ObjType getCategory() {
        return ObjType.REPORT;
    }

    @Override
    public boolean compare(Object cateObj, VisitLogPo vlp) {
        if (vlp==null) return false;
        if (cateObj==null) return false;
        try {
            ReportPo rp = (ReportPo)cateObj;
            if (vlp.getObjId()!=null&&vlp.getObjId().equals(rp.getId())) return true;

            FileIndexPo fip = fmService.getFileIndexPoById(rp.getFId());
            if (fip!=null) {
                String pureFileName = FileNameUtils.getPureFileName(fip.getFileName());
                if (vlp.getObjUrl()!=null&&vlp.getObjUrl().indexOf(pureFileName)!=-1) return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}