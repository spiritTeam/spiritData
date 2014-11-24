package com.gmteam.spiritdata.filemanage.model;

import java.sql.Timestamp;

import com.gmteam.framework.core.model.BaseObject;
import com.gmteam.spiritdata.filemanage.enumeration.RelType1;
import com.gmteam.spiritdata.filemanage.persistence.pojo.FileRelationPo;
import com.gmteam.spiritdata.util.SequenceUUID;

/**
 * 模型化文件关联对象。它与持久化中的FileRelationPo区别在于：
 * 1-持久化中的FileClass与数据库对应，这里的类与实际模型相对应。
 * 2-这里是对象化的模型。
 * 使用模型类更加规范，但开销大——结构复杂
 * @author wh
 */
public class FileRelation extends BaseObject {
    private static final long serialVersionUID = 2171855816787994983L;

    private String id; //文件id
    private Object element1; //可能是文件，也可能是文件关系
    private Object element2; //可能是文件，也可能是文件关系
    private RelType1 rType1; //关联类型1
    private String rType2; //关联类型2
    private String desc; //说明
    private Timestamp cTime; //关系创建时间

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Object getElement1() {
        return element1;
    }
    public void setElement1(Object element1) {
        if (!(element1 instanceof FileInfo)&&!(element1 instanceof FileClass)) {
            throw new IllegalArgumentException("只能是FileInfo或FileClass类型");
        }
        this.element1 = element1;
    }
    public Object getElement2() {
        return element2;
    }
    public void setElement2(Object element2) {
        if (!(element2 instanceof FileInfo)&&!(element2 instanceof FileClass)) {
            throw new IllegalArgumentException("只能是FileInfo或FileClass类型");
        }
        this.element2 = element2;
    }
    public RelType1 getrType1() {
        return rType1;
    }
    public void setrType1(RelType1 rType1) {
        this.rType1 = rType1;
    }
    public String getrType2() {
        return rType2;
    }
    public void setrType2(String rType2) {
        this.rType2 = rType2;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public Timestamp getcTime() {
        return cTime;
    }
    public void setcTime(Timestamp cTime) {
        this.cTime = cTime;
    }

    /**
     * 把当前对象转换为Po对象，为数据库操作做准备
     */
    public FileRelationPo convert2Po() {
        FileRelationPo ret = new FileRelationPo();
        //id处理
        if (this.id==null||this.id.length()==0) {//没有id，自动生成一个
            SequenceUUID.getPureUUID();
        } else {
            ret.setId(this.id);
        }
        //第一元素处理
        //第二元素处理
        //分类处理
        //其他
        
        return null;
    }
}