package com.spiritdata.filemanage.category.ANAL.model;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.spiritdata.dataanal.common.model.Owner;
import com.spiritdata.framework.util.DateUtils;
import com.spiritdata.framework.util.FileNameUtils;
import com.spiritdata.framework.util.FileUtils;
import com.spiritdata.jsonD.util.JsonUtils;
import com.spiritdata.filemanage.core.enumeration.FileCategoryType1;
import com.spiritdata.filemanage.core.model.FileCategory;
import com.spiritdata.filemanage.core.model.FileInfo;
import com.spiritdata.filemanage.core.pattern.model.AbstractToBeStoreFile;
import com.spiritdata.filemanage.core.pattern.model.BeManageFile;
import com.spiritdata.filemanage.exceptionC.Flmg0001CException;

/**
 * 分析结果文件，虽然叫分析结果，但也可以是其他分析的数据输入。注意：分析结果是按照jsonD存储的<br/>
 * 分析结果文件存储在analData中，按照二级分类进行存储，目前分类有：<br/>
 * -METADATA:元数据结构分析，又可分为key/dict/sement——主键/字典/语义等<br/>
 * 若为-METEDATA，则第三分类为元数据的Id及MetadataModelId<br/>
 * <br/>
 * 所分析对象，在这里用objId,objType,objAdress标识，若是excel文件，则objId为元数据id，objType="file"，objAdress="文件地址"
 * 注意：<br/>
 * 1-这里的分析结构，都按照JsonD处理<br/>
 * 2-这些有实际意义的文件都要在文件分类表中有对应
 * @author wh
 */
//今后，若有可能把分析结果和jsonD进行结合
public class AnalResultFile extends AbstractToBeStoreFile implements Serializable, BeManageFile {
    private static final long serialVersionUID = 7715689049076212381L;

    private String id; //分析文件id
    private String fileName; //分析文件的地址
    private String jsonDCode; //jsonD的Code

    private String analType; //对应Type2,分析类型-METADATA:元数据结构分析，又可分为key/dict/sement，今后根据情况再扩充，用于确定保存的目录
    private String subType; //对应Type3,今后相当于任务Id

    private String objId; //所分析的对象Id，MetadataModelId
    private String objType; //分析对象类型

    private Map<String, Object> extInfo; //分析的扩展信息，将转换为json存储在ExtInfo中

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getAnalType() {
        return analType;
    }
    public void setAnalType(String analType) {
        this.analType = analType;
    }
    public String getSubType() {
        return subType;
    }
    public void setSubType(String subType) {
        this.subType = subType;
    }
    public String getJsonDCode() {
        return jsonDCode;
    }
    public void setJsonDCode(String jsonDCode) {
        this.jsonDCode = jsonDCode;
    }
    public String getObjId() {
        return objId;
    }
    public void setObjId(String objId) {
        this.objId = objId;
    }
    public String getObjType() {
        return objType;
    }
    public void setObjType(String objType) {
        this.objType = objType;
    }
    public Map<String, Object> getExtInfo() {
        return extInfo;
    }
    public void setExtInfo(Map<String, Object> extInfo) {
        this.extInfo = extInfo;
    }

    public AnalResultFile() {
    }
    public AnalResultFile(FileInfo fi) {
        if (fi==null) return ;
        this.buildFromFileInfo(fi);
    }

    /**
     * 转换为模型化文件信息，注意，这里要验证服务器端文件是否存在。<br/>
     * @return 模型化文件信息
     */
    public FileInfo convert2FileInfo() {
        File f = new File(this.fileName);
        if (f==null||!f.isFile()) throw new Flmg0001CException(new IllegalArgumentException("分析结果文件对象中fileName所指向的文件为空或是一个目录！"));

        FileInfo ret = new FileInfo();
        //主信息
        ret.setFile(f);
        ret.setId(this.id);
        Owner owner = new Owner();
        owner.setOwnerId("sys");
        owner.setOwnerType(3);
        ret.setOwner(owner);
        ret.setAccessType(1);
        /**
        if (this.CTime==null) this.CTime=new Timestamp(new Date().getTime());
        ret.setCTime(this.CTime);
        */
        ret.setDesc("分析结果文件，文件为:"+FileNameUtils.getFileName(this.fileName)+"；得到分析结果时间:"+DateUtils.convert2TimeChineseStr(new Date(FileUtils.getFileCreateTime(f)))
                +"；分析类型："+this.analType+"::"+this.subType+"，jsonD代码："+this.jsonDCode);
        //分类信息
        FileCategory fc = new FileCategory();
        fc.setFType1(FileCategoryType1.ANAL);
        fc.setFType2(this.analType);
        fc.setFType3(this.subType);
        //分类扩展信息是一个json，包括jsonD的编码
        Map<String, Object> _extInfo = new HashMap<String, Object>();
        if (this.extInfo!=null&&this.extInfo.size()>0) _extInfo.putAll(this.extInfo);
        _extInfo.put("JSOND", this.jsonDCode);
        if (this.objType!=null&&this.objId!=null) _extInfo.put(this.objType, this.objId);
        fc.setExtInfo(JsonUtils.objToJson(_extInfo));
        ret.addFileCategoryList(fc);
        return ret;
    }

    /**
     * 此方法无法获得相关的文件分类的信息
     */
    @Override
    public void buildFromFileInfo(FileInfo fi) {
        if (fi!=null) {
            this.id=fi.getId();
            this.fileName = fi.getAllFileName();
        }
    }
}