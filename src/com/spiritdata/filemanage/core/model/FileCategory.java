package com.spiritdata.filemanage.core.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.spiritdata.framework.core.model.Model2Po;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.filemanage.core.enumeration.FileCategoryType1;
import com.spiritdata.filemanage.core.enumeration.RelType1;
import com.spiritdata.filemanage.core.persistence.pojo.FileCategoryPo;
import com.spiritdata.filemanage.exceptionC.Flmg0002CException;

/**
 * 模型化文件分类对象。它与持久化中的FileClassPo区别在于：
 * 1-持久化中的FileClass与数据库对应，这里的类与实际模型相对应。
 * 2-这里是对象化的模型。
 * 使用模型类更加规范，但开销大——结构复杂
 * @author wh
 */
public class FileCategory implements Serializable, Model2Po {
    private static final long serialVersionUID = 3467199927097139932L;

    private String id; //文件分类id
    private FileInfo categoryFile; //被分类的文件
    private FileCategoryType1 FType1; //文件大分类，是枚举类型，目前包括IMP,LOG,ANAL
    private String FType2; //文件分类—中类
    private String FType3; //文件分类—小类
    private String extInfo; //扩展信息
    private Timestamp CTime; //创建时间

    private List<FileRelation> positiveRelationFiles; //与本文件相关的正向关联关系
    private List<FileRelation> inverseRelationFiles; //与本文件相关的反向关联关系
    private List<FileRelation> equalRelationFiles; //与本文件相关的反向关联关系

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public FileInfo getCategoryFile() {
        return categoryFile;
    }
    public void setCategoryFile(FileInfo categoryFile) {
        this.categoryFile = categoryFile;
    }
    public FileCategoryType1 getFType1() {
        return FType1;
    }
    public void setFType1(FileCategoryType1 fType1) {
        FType1 = fType1;
    }
    public String getFType2() {
        return FType2;
    }
    public void setFType2(String fType2) {
        FType2 = fType2;
    }
    public String getFType3() {
        return FType3;
    }
    public void setFType3(String fType3) {
        FType3 = fType3;
    }
    public String getExtInfo() {
        return extInfo;
    }
    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
    }
    public Timestamp getCTime() {
        return CTime;
    }
    public void setCTime(Timestamp cTime) {
        CTime = cTime;
    }

    //文件分类关联关系
    /*
     * 与本文件分类建立关联关系
     * @param obj 关联关系中另一个对象，只能是{@linkplain com.spiritdata.dataanal.filemanage.model.FileInfo FileInfo}
     *         或{@linkplain com.spiritdata.dataanal.filemanage.model.FileCategory FileCategory}
     * @param type 关联关系类型，是枚举值{@linkplain com.spiritdata.dataanal.filemanage.enumeration.RelType1 RelType1} 
     * @param rType2 第二分类 
     * @param desc 关系描述
     * @return 所建立的关系
     * @throws Exception 如果obj不是符合的类型
     */
    protected FileRelation _buildRel(Object obj, RelType1 type, String rType2, String desc) {
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
     * 与本文件分类建立关联关系
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
        if (obj instanceof FileInfo) ((FileInfo)obj).buildCategoryRel(this, _contraryRet.getRType1(), _contraryRet.getRType2(), _contraryRet.getDesc());
        if (obj instanceof FileCategory) ((FileCategory)obj).buildCategoryRel(this, _contraryRet.getRType1(), _contraryRet.getRType2(), _contraryRet.getDesc());
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
     * @return 文件分类的Po对象
     */
    public FileCategoryPo convert2Po() {
        FileCategoryPo ret = new FileCategoryPo();
        //id处理
        if (this.id==null||this.id.length()==0) {//没有id，自动生成一个
            ret.setId(SequenceUUID.getPureUUID());
        } else {
            ret.setId(this.id);
        }
        //文件id
        ret.setFId(this.categoryFile.getId());
        //文件分类
        ret.setFType1(this.FType1.getValue());
        ret.setFType2(this.FType2);
        ret.setFType3(this.FType3);
        //其他
        ret.setExtInfo(this.extInfo);
        ret.setCTime(this.CTime);
        return ret;
    }
}