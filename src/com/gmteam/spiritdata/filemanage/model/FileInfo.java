package com.gmteam.spiritdata.filemanage.model;

import java.util.List;

import com.gmteam.framework.core.model.BaseObject;
import com.gmteam.spiritdata.filemanage.persistence.pojo.FileIndexPo;
import com.gmteam.spiritdata.util.SequenceUUID;

/**
 * 模型化文件信息对象，包括文件的索引信息，文件的分类信息，以及文件的关系信息。
 * 它与持久化中的FileIndex区别在于：
 * 1-持久化中的FileIndex与数据库对应，这里的类与实际模型相对应。
 * 2-持久化中类中的字段是基本字段，而这里的字段可以是枚举值。
 * 使用模型类更加规范，但开销大——结构复杂
 * @author wh
 */
public class FileInfo extends BaseObject {
    private static final long serialVersionUID = 12366632000244738L;

    private String id; //文件分类id
    private String ownerId; //文件所有者id
    private int ownerType; //文件所有者类型

    private List<FileCategory> fileCategoryList; //本文件的分类列表，一个文件可以属于多个分类
    private List<FileRelation> positiveRelationFiles; //与本文件相关的正向关联关系
    private List<FileRelation> inverseRelationFiles; //与本文件相关的反向关联关系
    private List<FileRelation> equalRelationFiles; //与本文件相关的反向关联关系

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 把当前对象转换为Po对象，为数据库操作做准备
     * @throws Exception 
     */
    public FileIndexPo convert2Po() throws Exception {
        FileIndexPo ret = new FileIndexPo();
        //id处理
        if (this.id==null||this.id.length()==0) {//没有id，自动生成一个
            SequenceUUID.getPureUUID();
        } else {
            ret.setId(this.id);
        }
        //所有者
        ret.setOwnerId(this.ownerId);
        ret.setOwnerType(this.ownerType);
        //文件访问类型，现在不用枚举，这个属性的使用还是教复杂的
        //ret.setAccessType(this.accessType);
        //文件信息
        //ret.set
        //其他
        //ret.setDesc(this.desc);
        //ret.setCTime(this.CTime);
        //ret.setLmTime(this.lmTime);
        return ret;
    }
}