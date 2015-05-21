package com.spiritdata.filemanage.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.filemanage.core.enumeration.RelType1;
import com.spiritdata.filemanage.core.model.FileCategory;
import com.spiritdata.filemanage.core.model.FileInfo;
import com.spiritdata.filemanage.core.model.FileRelation;
import com.spiritdata.filemanage.core.pattern.model.BeManageFile;
import com.spiritdata.filemanage.core.persistence.pojo.FileCategoryPo;
import com.spiritdata.filemanage.core.persistence.pojo.FileIndexPo;
import com.spiritdata.filemanage.core.persistence.pojo.FileRelationPo;
import com.spiritdata.filemanage.exceptionC.Flmg0101CException;

/**
 * 文件管理服务类
 * @author wh
 */
public class FileManageService {
    @Resource(name="defaultDAO")
    private MybatisDAO<FileIndexPo> fileIndexDao;
    @Resource(name="defaultDAO")
    private MybatisDAO<FileCategoryPo> fileCategoryDao;
    @Resource(name="defaultDAO")
    private MybatisDAO<FileRelationPo> fileRelationDao;

    @PostConstruct
    public void initParam() {
        fileIndexDao.setNamespace("fileIndex");
        fileCategoryDao.setNamespace("fileCategory");
        fileRelationDao.setNamespace("fileRelation");
    }

    /**
     * 存储文件信息
     * @param fi 文件信息
     */
    public void saveFileInfo(FileInfo fi) {
        try {
            FileInfo existFi = null;
            boolean canInsert = false;
            //文件映射表
            FileIndexPo fiPo = fi.convert2Po();
            fi.setId(fiPo.getId());
            //TODO 还要判断是否已经存在！！！！
            try {
                fileIndexDao.insert(fiPo);
            } catch(Exception e) {
                existFi = this.getFileInfoById(fi.getId());
                fileIndexDao.update(fiPo);
            }
            //文件分类表
            if (fi.getFileCategoryList()!=null&&fi.getFileCategoryList().size()>0) {
                for (FileCategory fc: fi.getFileCategoryList()) {
                    FileCategoryPo fcPo = fc.convert2Po();
                    fc.setId(fcPo.getId());
                    fcPo.setFId(fiPo.getId());
                    //判断是否重复
                    canInsert = true;
                    if (existFi!=null) {
                        if (existFi.getFileCategoryList()!=null&&existFi.getFileCategoryList().size()>0) {
                            for (FileCategory _fc: existFi.getFileCategoryList()) {
                                canInsert = !equalFc(_fc, fc);
                                if (!canInsert) break;
                            }
                        }
                    }
                    if (canInsert) {
                        fileCategoryDao.insert(fcPo);
                    } else {
                        fileCategoryDao.update(fcPo);
                    }
                }
            }
            //文件关系表
            if (fi.getPositiveRelationFiles()!=null&&fi.getPositiveRelationSize()>0) {
                for (FileRelation fr: fi.getPositiveRelationFiles()) {
                    FileRelationPo frPo= fr.convert2Po();
                    fr.setId(frPo.getId());
                    if (frPo.getRType2().indexOf("ContraryRef::")==0) frPo.setRType2(frPo.getRType2().substring("ContraryRef::".length()+1));
                    if (frPo.getDesc().indexOf("反关系—")==0) frPo.setDesc(frPo.getDesc().substring("反关系—".length()+1));
                    try {
                        fileRelationDao.insert(frPo);
                    } catch(Exception e) {
                        fileRelationDao.update(frPo);
                    }
                }
            }
            if (fi.getEqualRelationFiles()!=null&&fi.getEqualRelationSize()>0) {
                for (FileRelation fr: fi.getEqualRelationFiles()) {
                    FileRelationPo frPo= fr.convert2Po();
                    fr.setId(frPo.getId());
                    if (frPo.getRType2().indexOf("ContraryRef::")==0) frPo.setRType2(frPo.getRType2().substring("ContraryRef::".length()+1));
                    if (frPo.getDesc().indexOf("反关系—")==0) frPo.setDesc(frPo.getDesc().substring("反关系—".length()+1));
                    try {
                        fileRelationDao.insert(frPo);
                    } catch(Exception e) {
                        fileRelationDao.update(frPo);
                    }
                }
            }
        } catch(Exception e) {
            throw new Flmg0101CException("存储文件信息", e);
        }
    }

    /**
     * 存储文件关系
     * @param fr 文件关系
     */
    public void saveFileRelation(FileRelation fr) {
        try {
            //TODO 还要判断是否已经存在！！！！
            fileRelationDao.insert(fr.convert2Po());
        } catch(Exception e) {
            throw new Flmg0101CException("存储文件关系", e);
        }
    }

    /**
     * 存储被管里的文件
     * @param bmf 被管里的文件
     * @return 被管里的文件对应的模型化文件信息对象
     */
    public FileInfo saveFile(BeManageFile bmf) {
        try {
            FileInfo fi = bmf.convert2FileInfo();
            this.saveFileInfo(fi);
            return fi;
        } catch(Exception e) {
            throw new Flmg0101CException(e);
        }
    }

    /**
     * 根据文件Id，得到文件信息
     * @param id 文件Id
     * @return 文件信息
     */
    public FileInfo getFileInfoById(String id) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("id", id);
        FileIndexPo fiPo = fileIndexDao.getInfoObject(param);
        if (fiPo==null) return null;

        FileInfo ret = new FileInfo();
        ret.buildFromPo(fiPo);
        //处理文件的分类，可能是多个，注意：此处得到的分类列表不递归处理，即没有关系信息
        param.clear();
        param.put("FId", id);
        List<FileCategoryPo> fcpL = fileCategoryDao.queryForList(param);
        if (fcpL!=null&&fcpL.size()>0) {
            for (FileCategoryPo fcp: fcpL) {
                FileCategory fc = new FileCategory();
                fc.buildFromPo(fcp);
                ret.addFileCategoryList(fc);
            }
        }
        //处理文件的关系，注意，关系中的对象Object1/Object2不是递归处理的，即Object中的关系和分类表都是空
        //正向关系
        param.clear();
        param.put("AId", id);
        param.put("AType", 1);
        List<FileRelationPo> _12RelList = fileRelationDao.queryForList(param);
        if (_12RelList!=null&&_12RelList.size()>0) {
            for (FileRelationPo frp: _12RelList) {
                Object o=null;
                if (frp.getBType()==1) {
                    FileInfo fi = new FileInfo();
                    fi.setId(frp.getBId());
                    o=fi;
                }
                if (frp.getBType()==2) {
                    FileCategory fc = new FileCategory();
                    fc.setId(frp.getBId());
                    o=fc;
                }
                ret.buildRel(o, RelType1.getRelType1(frp.getRType1()), frp.getRType2(), frp.getDesc());
            }
        }
        //文件关系
        param.clear();
        param.put("BId", id);
        param.put("BType", 1);
        List<FileRelationPo> _21RelList = fileRelationDao.queryForList(param);
        if (_21RelList!=null&&_21RelList.size()>0) {
            for (FileRelationPo frp: _21RelList) {
                Object o=null;
                if (frp.getAType()==1) {
                    FileInfo fi = new FileInfo();
                    fi.setId(frp.getBId());
                    o=fi;
                }
                if (frp.getAType()==2) {
                    FileCategory fc = new FileCategory();
                    fc.setId(frp.getBId());
                    o=fc;
                }
                ret.buildRel(o, RelType1.getRelType1(frp.getRType1()).getContrary(), frp.getRType2(), frp.getDesc());
            }
        }
        return ret;
    }

    public FileCategoryPo getFileCategoryPo(Map<String, Object> m) {
        return fileCategoryDao.getInfoObject(m);
    }

    /*
     * 比较两个文件分类是否相同
     * @param _fc 第一个分类
     * @param fc 第二个分类
     * @return 相同返回true，否则返回false
     */
    private boolean equalFc(FileCategory _fc, FileCategory fc) {
        String temp1, temp2;
        temp1=_fc.getFType3();
        temp2=fc.getFType3();
        if (temp1!=null||temp2!=null) {
            if (temp1!=null) {
                if (!temp1.equals(temp2)) return false;
            } else return false;
        }
        temp1=_fc.getFType2();
        temp2=fc.getFType2();
        if (temp1!=null||temp2!=null) {
            if (temp1!=null) {
                if (!temp1.equals(temp2)) return false;
            } else return false;
        }
        if (_fc.getFType1()!=null||fc.getFType1()!=null) {
            if (_fc.getFType1()!=null) {
                if (_fc.getFType1()!=fc.getFType1()) return false;
            } else return false;
        }
        if (_fc.getCategoryFile()!=null||fc.getCategoryFile()!=null) {
            if (_fc.getCategoryFile()!=null) {
                if (!_fc.getCategoryFile().getId().equals(fc.getCategoryFile().getId())) return false;
            } else return false;
        }
        return true;
    }
}