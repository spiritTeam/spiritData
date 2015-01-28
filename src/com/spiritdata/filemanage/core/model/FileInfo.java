package com.spiritdata.filemanage.core.model;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.spiritdata.framework.util.FileNameUtils;
import com.spiritdata.framework.util.FileUtils;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.filemanage.core.enumeration.RelType1;
import com.spiritdata.filemanage.core.persistence.pojo.FileIndexPo;
import com.spiritdata.filemanage.exceptionC.Flmg0002CException;

/**
 * 模型化文件信息，包括文件的索引信息，文件的分类信息，以及文件的关系信息。
 * 它与持久化中的FileIndex区别在于：
 * 1-持久化中的FileIndex与数据库对应，这里的类与实际模型相对应。
 * 2-持久化中类中的字段是基本字段，而这里的字段可以是枚举值。
 * 使用模型类更加规范，但开销大——结构复杂
 * @author wh
 */
public class FileInfo extends FileIndexPo {
    private static final long serialVersionUID = 12366632000244738L;

    private File file; //文件信息所对应的文件：当accessType==1(操作系统文件)时；若是其他accessType，则这个是null

    private List<FileCategory> fileCategoryList; //本文件的分类列表，一个文件可以属于多个分类

    private List<FileRelation> positiveRelationFiles; //与本文件相关的正向关联关系
    private List<FileRelation> inverseRelationFiles; //与本文件相关的反向关联关系
    private List<FileRelation> equalRelationFiles; //与本文件相关的平等关联关系

    public File getFile() {
        return file;
    }

    /**
     * 设置文件，文件必须是合法的文件
     * @param file 文件
     */
    public void setFile(File file) {
        if (file==null||!file.isFile()) throw new IllegalArgumentException("文件为空或是一个目录！");

        this.file = file;
        this.setPath(FileNameUtils.getFilePath(file.getAbsolutePath()));
        this.setFileName(FileNameUtils.getFileName(file.getAbsolutePath()));
        this.setFileSize(file.length());
        this.setFcTime(new Timestamp(FileUtils.getFileCreateTime4Win(this.file)));
        this.setFlmTime(new Timestamp(this.file.lastModified()));
    }

    //文件分类列表处理
    public void addFileCategoryList(FileCategory fc) {
        if (fc.getCTime()==null&&this.CTime!=null) fc.setCTime(this.CTime);
        fc.setCategoryFile(this);
        if (this.fileCategoryList==null) this.fileCategoryList = new ArrayList<FileCategory>();
        this.fileCategoryList.add(fc);
    }
    public List<FileCategory> getFileCategoryList() {
        return fileCategoryList;
    }
    public void setFileCategoryList(List<FileCategory> fileCategoryList) {
        if (fileCategoryList!=null&&fileCategoryList.size()>0) {
            for (FileCategory fc: fileCategoryList) {
                this.addFileCategoryList(fc);
            }
        }
    }

    //文件关系关系
    /*
     * 与本文件建立关联关系
     * @param obj 关联关系中另一个对象，只能是{@linkplain com.spiritdata.dataanal.filemanage.model.FileInfo FileInfo}
     *         或{@linkplain com.spiritdata.dataanal.filemanage.model.FileCategory FileCategory}
     * @param type 关联关系类型，是枚举值{@linkplain com.spiritdata.dataanal.filemanage.enumeration.RelType1 RelType1} 
     * @param rType2 第二分类 
     * @param desc 关系描述
     * @return 所建立的关系
     * @throws Exception 如果obj不是符合的类型
     */
    protected FileRelation _buildRel(Object obj, RelType1 type, String rType2, String desc) throws Exception {
        if (!(obj instanceof FileInfo)&&!(obj instanceof FileCategory)) {
            throw new Flmg0002CException(new IllegalArgumentException("另一关联对象只能是FileInfo或FileCategory类型，无法转换！"));
        }
        FileRelation fr = new FileRelation();
        fr.setElement1(this);
        fr.setElement2(obj);
        fr.setRType1(type);
        fr.setDesc(desc);
        fr.setCTime(new Timestamp(new Date().getTime()));
        //关系处理
        boolean canAdd = true;
        switch(type) {
        case EQUAL:
            if (equalRelationFiles!=null&&equalRelationFiles.size()>0) {
                for (FileRelation _fr: equalRelationFiles) {
                    if (fr.equals(_fr)) {
                        canAdd = false;
                        break;
                    }
                }
            }
            if (canAdd) {
                if (equalRelationFiles==null) equalRelationFiles = new ArrayList<FileRelation>();
                equalRelationFiles.add(fr);
            }
            break;
        case POSITIVE:
            if (positiveRelationFiles!=null&&positiveRelationFiles.size()>0) {
                for (FileRelation _fr: positiveRelationFiles) {
                    if (fr.equals(_fr)) {
                        canAdd = false;
                        break;
                    }
                }
            }
            if (canAdd) {
                if (positiveRelationFiles==null) positiveRelationFiles = new ArrayList<FileRelation>();
                positiveRelationFiles.add(fr);
            }
            break;
        case INVERSE:
            if (inverseRelationFiles!=null&&inverseRelationFiles.size()>0) {
                for (FileRelation _fr: inverseRelationFiles) {
                    if (fr.equals(_fr)) {
                        canAdd = false;
                        break;
                    }
                }
            }
            if (canAdd) {
                if (inverseRelationFiles==null) inverseRelationFiles = new ArrayList<FileRelation>();
                inverseRelationFiles.add(fr);
            }
            break;
        default:
            break;
        }
        return fr;
    }

    /**
     * 与本文件建立关联关系
     * @param obj 关联关系中另一个对象，只能是{@linkplain com.spiritdata.dataanal.filemanage.model.FileInfo FileInfo}
     *         或{@linkplain com.spiritdata.dataanal.filemanage.model.FileCategory FileCategory}
     * @param type 关联关系类型，是枚举值{@linkplain com.spiritdata.dataanal.filemanage.enumeration.RelType1 RelType1} 
     * @param rType2 第二分类 
     * @param desc 关系描述
     * @return 所建立的关系
     * @throws Exception 如果obj不是符合的类型
     */
    public FileRelation buildRel(Object obj, RelType1 type, String rType2, String desc) throws Exception {
        FileRelation ret = this._buildRel(obj, type, rType2, desc);
        FileRelation _contraryRet = ret.getContraryRelation();
        if (obj instanceof FileInfo) ((FileInfo)obj).buildFileRel(this, _contraryRet.getRType1(), _contraryRet.getRType2(), _contraryRet.getDesc());
        if (obj instanceof FileCategory) ((FileCategory)obj).buildFileRel(this, _contraryRet.getRType1(), _contraryRet.getRType2(), _contraryRet.getDesc());
        return ret;
    }

    /**
     * 与一个文件建立关联关系
     * @param fc 文件分类对象，关联关系中另一个对象，
     * @param type 关联关系类型，是枚举值{@linkplain com.spiritdata.dataanal.filemanage.enumeration.RelType1 RelType1} 
     * @param rType2 第二分类 
     * @param desc 关系描述
     * @return 所建立的关系
     * @throws Exception 如果obj不是符合的类型
     */
    public FileRelation buildFileRel(FileInfo fc, RelType1 type, String rType2, String desc) throws Exception {
        return this.buildRel(fc, type, rType2, desc);
    }

    /**
     * 与一个文件分类建立关联关系
     * @param fc 文件分类对象，关联关系中另一个对象，
     * @param type 关联关系类型，是枚举值{@linkplain com.spiritdata.dataanal.filemanage.enumeration.RelType1 RelType1} 
     * @param rType2 第二分类 
     * @param desc 关系描述
     * @return 所建立的关系
     * @throws Exception 如果obj不是符合的类型
     */
    public FileRelation buildCategoryRel(FileCategory fc, RelType1 type, String rType2, String desc) throws Exception {
        return this.buildRel(fc, type, rType2, desc);
    }

    public List<FileRelation> getPositiveRelationFiles() {
        return positiveRelationFiles;
    }
    public int getPositiveRelationSize() {
        return positiveRelationFiles==null?0:positiveRelationFiles.size();
    }
    public List<FileRelation> getInverseRelationFiles() {
        return inverseRelationFiles;
    }
    public int getInverseRelationSize() {
        return inverseRelationFiles==null?0:inverseRelationFiles.size();
    }
    public List<FileRelation> getEqualRelationFiles() {
        return equalRelationFiles;
    }
    public int getEqualRelationSize() {
        return inverseRelationFiles==null?0:inverseRelationFiles.size();
    }
    public List<FileRelation> getAllRelationFiles() {
        List<FileRelation> ret = new ArrayList<FileRelation>();
        ret.addAll(positiveRelationFiles);
        ret.addAll(inverseRelationFiles);
        ret.addAll(equalRelationFiles);
        return ret;
    }
    public int getAllRelationSize() {
        return (positiveRelationFiles==null?0:positiveRelationFiles.size())
               +(inverseRelationFiles==null?0:inverseRelationFiles.size())
               +(equalRelationFiles==null?0:equalRelationFiles.size());
    }
    
    /**
     * 把当前对象转换为Po对象，为数据库操作做准备
     * @return 文件信息Po对象
     */
    public FileIndexPo convert2Po() {
        FileIndexPo ret = new FileIndexPo();
        //id处理
        if (this.id==null||this.id.length()==0) {//没有id，自动生成一个
            ret.setId(SequenceUUID.getPureUUID());
        } else {
            ret.setId(this.id);
        }
        //所有者
        ret.setOwnerId(this.ownerId);
        ret.setOwnerType(this.ownerType);
        //文件访问类型，现在不用枚举，这个属性的使用还是教复杂的
        ret.setAccessType(this.accessType);
        //文件信息
        ret.setPath(this.path);
        ret.setFileName(this.fileName);
        ret.setFileSize(this.fileSize);
        ret.setFcTime(this.fcTime);
        ret.setFlmTime(this.flmTime);
        if (this.file!=null&&this.file.isFile()) {
            String allFileName = this.file.getAbsolutePath();
            ret.setPath(FileNameUtils.getFilePath(allFileName));
            ret.setFileName(FileNameUtils.getFileName(allFileName));
            ret.setFcTime(new Timestamp(FileUtils.getFileCreateTime4Win(this.file)));
            ret.setFlmTime(new Timestamp(this.file.lastModified()));
        } else {
            ret.setPath(this.path);
            ret.setFileName(this.fileName);
            ret.setFileSize(this.fileSize);
            ret.setFcTime(this.fcTime);
            ret.setFlmTime(this.flmTime);
        }
        //其他
        ret.setDesc(this.desc);
        ret.setCTime(this.CTime);
        return ret;
    }

    /**
     * 用全部文件名(目录+文件名+扩展名)
     * @param allFileName 全部文件名
     */
    public void setAllFileName(String allFileName) {
        this.setPath(FileNameUtils.getFilePath(allFileName));
        this.setFileName(FileNameUtils.getFileName(allFileName));
    }

    /**
     * 返回全部文件名
     * @return 全部文件名
     */
    public String getAllFileName() {
        return FileNameUtils.concatPath(this.getPath(), this.getFileName());
    }
}