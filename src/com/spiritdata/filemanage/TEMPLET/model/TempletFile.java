package com.spiritdata.filemanage.TEMPLET.model;

import java.io.Serializable;

import com.spiritdata.filemanage.core.BeManageFile;
import com.spiritdata.filemanage.core.model.FileInfo;

public class TempletFile implements Serializable, BeManageFile {
    private static final long serialVersionUID = -1625546654030117440L;

    private String id; //模板文件的id
    private int ownerType; //模板文件所对应的所有者类型（1=注册用户;2=非注册用户(session)）
    private String ownerId; //模板文件所有者标识（可能是用户id，也可能是SessionID）
    private String templetFileName; //模板文件文件全名(包括目录和文件名)

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
    public String getTempletFileName() {
        return templetFileName;
    }
    public void setTempletFileName(String templetFileName) {
        this.templetFileName = templetFileName;
    }

    /**
     * 
     */
    /**
     * 转换为模型化文件信息，注意，这里要验证服务器端文件是否存在。<br/>
     * 根据文件类型，控制分类的第二分类，目前有：<br/>
     * -Rdata:关系型数据<br/>
     * -Tdata:文本形数据<br/>
     * -Vdata:视频数据<br/>
     * -Adata:音频数据<br/>
     * -Idata:图片数据
     * @return 模型化文件信息
     */
    public FileInfo convert2FileInfo() {
        return null;
    }
}