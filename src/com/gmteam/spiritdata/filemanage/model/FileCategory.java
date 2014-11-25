package com.gmteam.spiritdata.filemanage.model;

import java.sql.Timestamp;

import com.gmteam.framework.core.model.BaseObject;
import com.gmteam.spiritdata.filemanage.enumeration.FileClassType1;
import com.gmteam.spiritdata.filemanage.persistence.pojo.FileCategoryPo;
import com.gmteam.spiritdata.util.SequenceUUID;

/**
 * 模型化文件分类对象。它与持久化中的FileClassPo区别在于：
 * 1-持久化中的FileClass与数据库对应，这里的类与实际模型相对应。
 * 2-这里是对象化的模型。
 * 使用模型类更加规范，但开销大——结构复杂
 * @author wh
 */
public class FileCategory extends BaseObject {
    private static final long serialVersionUID = 3467199927097139932L;

    private String id; //文件分类id
    private FileInfo categoryFile; //被分类的文件
    private FileClassType1 FType1; //文件大分类，是
    private String FType2; //文件分类—中类
    private String FType3; //文件分类—小类
    private String extInfo; //扩展信息
    private Timestamp CTime; //创建时间

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public FileInfo getCategoryFile() {
        return categoryFile;
    }
    public void setClassFile(FileInfo categoryFile) {
        this.categoryFile = categoryFile;
    }
    public FileClassType1 getFType1() {
        return FType1;
    }
    public void setFType1(FileClassType1 fType1) {
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

    /**
     * 把当前对象转换为Po对象，为数据库操作做准备
     * @throws Exception 
     */
    public FileCategoryPo convert2Po() throws Exception {
        FileCategoryPo ret = new FileCategoryPo();
        //id处理
        if (this.id==null||this.id.length()==0) {//没有id，自动生成一个
            SequenceUUID.getPureUUID();
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