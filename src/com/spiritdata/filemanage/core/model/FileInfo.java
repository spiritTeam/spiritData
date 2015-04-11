package com.spiritdata.filemanage.core.model;

import java.io.File;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.spiritdata.dataanal.common.model.Owner;
import com.spiritdata.framework.core.model.ModelSwapPo;
import com.spiritdata.framework.exceptionC.Plat0006CException;
import com.spiritdata.framework.util.FileNameUtils;
import com.spiritdata.framework.util.FileUtils;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.filemanage.core.enumeration.RelType1;
import com.spiritdata.filemanage.core.persistence.pojo.FileIndexPo;
import com.spiritdata.filemanage.exceptionC.Flmg0001CException;
import com.spiritdata.filemanage.exceptionC.Flmg0002CException;

/**
 * 模型化文件信息，包括文件的索引信息，文件的分类信息，以及文件的关系信息。
 * 它与持久化中的FileIndex区别在于：
 * 1-持久化中的FileIndex与数据库对应，这里的类与实际模型相对应。
 * 2-持久化中类中的字段是基本字段，而这里的字段可以是枚举值。
 * 使用模型类更加规范，但开销大——结构复杂
 * @author wh
 */
public class FileInfo implements Serializable, ModelSwapPo {
    private static final long serialVersionUID = 12366632000244738L;

    protected String id; //文件id
    private Owner owner; //所有者
    protected int accessType; //文件访问类型，可通过这个类型转换为file:///；ftp:///等，可能需要再定义访问的一些属性没，如ftp的用户名/密码/端口等
    protected String path; //文件路径，不包括文件名
    protected String fileName; //文件名称，包括扩展名
    protected String extName; //文件扩展名
    protected Long fileSize; //文件大小
    protected String desc; //文件说明
    protected Timestamp fcTime; //文件创建时间
    protected Timestamp flmTime; //文件最后修改时间
    protected Timestamp CTime; //记录创建时间

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Owner getOwner() {
        return owner;
    }
    public void setOwner(Owner owner) {
        this.owner = owner;
    }
    public int getAccessType() {
        return accessType;
    }
    public void setAccessType(int accessType) {
        this.accessType = accessType;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getFileName() {
        return fileName;
    }
    /**
     * 设置文件名，同时根据文件名设置文件扩展名
     * @param fileName 文件名，包括文件扩展名
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
        this.extName = FileNameUtils.getExt(fileName);
    }
    public String getExtName() {
        return extName;
    }
    public Long getFileSize() {
        return fileSize;
    }
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public Timestamp getFcTime() {
        return fcTime;
    }
    public void setFcTime(Timestamp fcTime) {
        this.fcTime = fcTime;
    }
    public Timestamp getFlmTime() {
        return flmTime;
    }
    public void setFlmTime(Timestamp flmTime) {
        this.flmTime = flmTime;
    }
    public Timestamp getCTime() {
        return CTime;
    }
    public void setCTime(Timestamp CTime) {
        this.CTime = CTime;
    }

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
        if (file==null||!file.isFile()) throw new Flmg0001CException(new IllegalArgumentException("文件为空或是一个目录！"));

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

    /**
     * 获得所有关联文件列表<br/>
     * 包括正向关系、反向关系和相等关系。
     * @return 所有关联文件列表
     */
    public List<FileRelation> getAllRelationFiles() {
        List<FileRelation> ret = new ArrayList<FileRelation>();
        if (positiveRelationFiles!=null) ret.addAll(positiveRelationFiles);
        if (inverseRelationFiles!=null) ret.addAll(inverseRelationFiles);
        if (equalRelationFiles!=null) ret.addAll(equalRelationFiles);
        return ret;
    }

    /**
     * 获得所有关系的个数
     * @return 关系个数
     */
    public int getAllRelationSize() {
        return (positiveRelationFiles==null?0:positiveRelationFiles.size())
               +(inverseRelationFiles==null?0:inverseRelationFiles.size())
               +(equalRelationFiles==null?0:equalRelationFiles.size());
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


    /**
     * 把当前对象转换为Po对象，为数据库操作做准备
     * @return 文件信息Po对象
     */
    public FileIndexPo convert2Po() {
        FileIndexPo ret = new FileIndexPo();
        //id处理，没有id，自动生成一个
        if (StringUtils.isNullOrEmptyOrSpace(this.id)) ret.setId(SequenceUUID.getPureUUID());
        else ret.setId(this.id);

        //所有者
        ret.setOwnerId(this.owner.getOwnerId());
        ret.setOwnerType(this.owner.getOwnerType());
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
     * <p>从po得到模型对象，对于文件信息对象来说：
     * <p>fileCategoryList属性（文件分类列表），没有做处理，通过数据库检索可以得到这个属性，之所以没有处理，是要把这个功能留到Service中再处理。
     * 这样做考虑如下：读取数据库，慢！而在Service中，可能上下文已经得到了文件的信息，这样可能更快，而且不用从数据库获得两次(本方法中一次，Service中一次)。
     * <p>同样理由，本模型对象中的三类文件关系列表也不在这里处理。(通过读取数据库相关信息，这三个列表也是能够得到的)
     * <p>因此要注意：通过本方法构建的对象信息是不完整的。
     */
    @Override
    public void buildFromPo(Object po) {
        if (po==null) throw new Plat0006CException("Po对象为空，无法从空对象得到概念/逻辑对象！");
        if (!(po instanceof FileIndexPo)) throw new Plat0006CException("Po对象不是FileCategoryPo的实例，无法从此对象构建文件分类对象！");
        FileIndexPo _po = (FileIndexPo)po;
        this.id = _po.getId();
        this.owner = new Owner(_po.getOwnerType(), _po.getOwnerId());
        this.accessType = _po.getAccessType();
        this.path = _po.getPath();
        this.setFileName(_po.getFileName());
        this.fileSize = _po.getFileSize();
        this.CTime = _po.getCTime();
        this.fcTime = _po.getFcTime();
        this.flmTime = _po.getFlmTime();
        //这里不判断文件是否存在
        File f = new File(FileNameUtils.concatPath(this.path, this.fileName));
        this.file = f;
    }

    /**
     * 通过文件关系po对象获得文件信息的模型/概念对象的所有信息，请参看:
     * {@linkplain com.spiritdata.filemanage.core.model.FileInfo FileInfo}类中的buildFromPo方法
     * @param po 文件关系持久化对象
     */
    public void buildFromPo_AllField(FileIndexPo po) {
        this.buildFromPo(po);
        //TODO 读取数据库，获得相关的信息
    }
}