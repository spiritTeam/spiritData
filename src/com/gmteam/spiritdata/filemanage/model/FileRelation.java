package com.gmteam.spiritdata.filemanage.model;

import java.sql.Timestamp;

import com.gmteam.framework.core.model.BaseObject;
import com.gmteam.spiritdata.filemanage.enumeration.RelType1;
import com.gmteam.spiritdata.filemanage.persistence.pojo.FileRelationPo;
import com.gmteam.spiritdata.util.SequenceUUID;

/**
 * 模型化文件关联对象。它与持久化中的FileRelationPo区别在于：
 * 1-持久化中的FileCategory与数据库对应，这里的类与实际模型相对应。
 * 2-这里是对象化的模型。
 * 使用模型类更加规范，但开销大——结构复杂
 * @author wh
 */
public class FileRelation extends BaseObject {
    private static final long serialVersionUID = 2171855816787994983L;

    private String id; //文件id
    private Object element1; //可能是文件，也可能是文件分类
    private Object element2; //可能是文件，也可能是文件分类
    private RelType1 RType1; //关联类型1
    private String RType2; //关联类型2
    private String desc; //说明
    private Timestamp CTime; //关系创建时间

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
        if (!(element1 instanceof FileInfo)&&!(element1 instanceof FileCategory)) {
            throw new IllegalArgumentException("第一关联对象只能是FileInfo或FileCategory类型");
        }
        this.element1 = element1;
    }
    public Object getElement2() {
        return element2;
    }
    public void setElement2(Object element2) {
        if (!(element2 instanceof FileInfo)&&!(element2 instanceof FileCategory)) {
            throw new IllegalArgumentException("第二关联对象只能是FileInfo或FileCategory类型");
        }
        this.element2 = element2;
    }
    public RelType1 getRType1() {
        return RType1;
    }
    public void setRType1(RelType1 RType1) {
        this.RType1 = RType1;
    }
    public String getRType2() {
        return RType2;
    }
    public void setRType2(String RType2) {
        this.RType2 = RType2;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public Timestamp getCTime() {
        return CTime;
    }
    public void setCTime(Timestamp CTime) {
        this.CTime = CTime;
    }

    /**
     * 得到相反的关系
     * @return 相反的关系
     */
    public FileRelation getContraryRelation() throws Exception {
        if (this.RType1==RelType1.EQUAL) throw new Exception("平等关系不取反，取反无意义");
        FileRelation ret = new FileRelation();
        ret.setId(null);
        //交换关联实体
        ret.setElement1(this.element2);
        ret.setElement2(this.element1);
        //设置关联类型1反关系
        ret.setRType1(this.RType1.getContrary());
        //设置关联类型2
        if (this.RType2.indexOf("ContraryRef::")==0) ret.setRType2(this.RType2.substring("ContraryRef::".length()+1));
        else ret.setRType2("ContraryRef::"+this.RType2);
        //设置说明
        if (this.desc.indexOf("反关系—")==0) ret.setDesc(this.desc.substring("反关系—".length()+1));
        else ret.setDesc("反关系—"+ret.desc);
        //设置时间
        ret.setCTime(this.CTime);
        return ret;
    }

    /**
     * 把当前对象转换为Po对象，为数据库操作做准备
     * @throws Exception 
     */
    public FileRelationPo convert2Po() throws Exception {
        if (!(element1 instanceof FileInfo)&&!(element1 instanceof FileCategory)) {
            throw new Exception("第一关联对象只能是FileInfo或FileCategory类型，无法转换！");
        }
        if (!(element2 instanceof FileInfo)&&!(element2 instanceof FileCategory)) {
            throw new Exception("第二关联对象只能是FileInfo或FileCategory类型，无法转换！");
        }

        FileRelationPo ret = new FileRelationPo();
        //id处理
        if (this.id==null||this.id.length()==0) {//没有id，自动生成一个
            SequenceUUID.getPureUUID();
        } else {
            ret.setId(this.id);
        }
        //第一元素处理
        if (this.element1 instanceof FileInfo) {
            ret.setAId(((FileInfo)this.element1).getId());
            ret.setAType(1);
        } else if (this.element1 instanceof FileCategory) {
            ret.setAId(((FileCategory)this.element1).getId());
            ret.setAType(2);
        }
        //第二元素处理
        if (this.element2 instanceof FileInfo) {
            ret.setBId(((FileInfo)this.element2).getId());
            ret.setBType(1);
        } else if (this.element2 instanceof FileCategory) {
            ret.setBId(((FileCategory)this.element2).getId());
            ret.setBType(2);
        }
        //分类处理
        ret.setRType1(this.RType1.getValue());
        ret.setRType2(this.RType2);
        //其他
        ret.setDesc(this.desc);
        ret.setCTime(this.CTime);
        return null;
    }

    /**
     * 判断相等，所有有意义的字段都相等—obj1,obj2,rtype1,rtype2,desc，不包括id和CTime
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FileRelation)) return false;
        boolean ret = super.equals(obj)&&(this.element1.equals(((FileRelation)obj).element1))
                &&(this.element2.equals(((FileRelation)obj).element2))
                &&(this.element1.equals(((FileRelation)obj).element1))
                &&(this.RType1==((FileRelation)obj).RType1);

        String s1, s2;
        if (ret) {
            s1=this.RType2.indexOf("ContraryRef::")==0?(this.RType2.substring("ContraryRef::".length()+1)):this.RType2;
            s2=((FileRelation)obj).getRType2();
            s2=s2.indexOf("ContraryRef::")==0?(s2.substring("ContraryRef::".length()+1)):s2;
            ret=s1.equals(s2);
        }
        if (ret) {
            s1=this.desc.indexOf("反关系—")==0?(this.desc.substring("反关系—".length()+1)):this.desc;
            s2=((FileRelation)obj).getDesc();
            s2=s2.indexOf("反关系—")==0?(s2.substring("反关系—".length()+1)):s2;
            ret=s1.equals(s2);
        }
        return ret;
    }
}