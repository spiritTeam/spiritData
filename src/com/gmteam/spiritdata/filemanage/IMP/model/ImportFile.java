package com.gmteam.spiritdata.filemanage.IMP.model;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;

import com.gmteam.framework.core.model.BaseObject;
import com.gmteam.framework.util.DateUtils;
import com.gmteam.framework.util.FileNameUtils;
import com.gmteam.spiritdata.filemanage.core.enumeration.FileCategoryType1;
import com.gmteam.spiritdata.filemanage.core.model.FileCategory;
import com.gmteam.spiritdata.filemanage.core.model.FileInfo;

/**
 * 导入文件模型，用于记录导入文件，基于File管理模型
 * @author wh
 */
public class ImportFile extends BaseObject{
    private static final long serialVersionUID = -6413748884964474948L;

    private String id; //文件上传id
    private int ownerType; //模式所对应的所有者类型（1=注册用户;2=非注册用户(session)）
    private String ownerId; //所有者标识（可能是用户id，也可能是SessionID）
    private String serverFileName; //服务端文件全名(包括目录和文件名)
    private String clientFileName; //客户端文件全名(包括目录和文件名)
    private Long fileSize; //文件大小
    private Timestamp CTime; //记录创建时间

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public int getOwnerType() {
        return ownerType;
    }
    public void setOwnerType(int ownerType) {
        this.ownerType = ownerType;
    }
    public String getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
    public String getServerFileName() {
        return serverFileName;
    }
    public void setServerFileName(String serverFileName) {
        this.serverFileName = serverFileName;
    }
    public String getClientFileName() {
        return clientFileName;
    }
    public void setClientFileName(String clientFileName) {
        this.clientFileName = clientFileName;
    }
    public Long getFileSize() {
        return fileSize;
    }
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    public Timestamp getCTime() {
        return CTime;
    }
    public void setCTime(Timestamp CTime) {
        this.CTime = CTime;
    }

    /**
     * 转换为模型化文件信息，注意，这里要验证服务器端文件是否存在
     * @param impFile 导入的文件
     * @return 模型化文件信息
     * @throws Exception 若服务器端文件不存在
     */
    public FileInfo convertToFileInfo() throws Exception {
        File f = new File(this.serverFileName);
        if (f==null||!f.isFile()) throw new IllegalArgumentException("导入文件对象中serverFileName所指向的文件为空或是一个目录！");

        FileInfo ret = new FileInfo();
        //主信息
        ret.setFile(f);
        ret.setId(this.id);
        ret.setOwnerId(this.ownerId);
        ret.setOwnerType(this.ownerType);
        ret.setAccessType(1);
        ret.setFile(f);
        ret.setDesc("导入数据，文件为:"+FileNameUtils.getFileName(this.serverFileName)+"导入时间:"+DateUtils.convert2TimeChineseStr(new Date()));
        ret.setCTime(this.CTime);
        //分类信息
        FileCategory fc = new FileCategory();
        fc.setFType1(FileCategoryType1.IMP);
        fc.setFType2("关系型数据");
        fc.setFType3(ret.getExtName());
        fc.setExtInfo((String)this.getClientFileName());
        ret.addFileCategoryList(fc);

        return ret;
    }
}