package com.spiritdata.filemanage.category.IMP.model;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

import com.spiritdata.dataanal.common.model.Owner;
import com.spiritdata.framework.util.DateUtils;
import com.spiritdata.framework.util.FileNameUtils;
import com.spiritdata.framework.util.FileUtils;
import com.spiritdata.filemanage.core.enumeration.FileCategoryType1;
import com.spiritdata.filemanage.core.model.FileCategory;
import com.spiritdata.filemanage.core.model.FileInfo;
import com.spiritdata.filemanage.core.pattern.model.BeManageFile;
import com.spiritdata.filemanage.exceptionC.Flmg0001CException;

/**
 * 导入文件模型，用于记录导入文件，基于File管理模型
 * @author wh
 */
public class ImportFile implements Serializable, BeManageFile {
    private static final long serialVersionUID = -6413748884964474948L;

    private String id; //上传文件的id
    private Owner owner; //所有者
    private String serverFileName; //服务端文件全名(包括目录和文件名)
    private String clientFileName; //客户端文件全名(包括目录和文件名)

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
        File f = new File(this.serverFileName);
        if (f==null||!f.isFile()) throw new Flmg0001CException(new IllegalArgumentException("导入文件对象中serverFileName所指向的文件为空或是一个目录！"));

        FileInfo ret = new FileInfo();
        //主信息
        ret.setFile(f);
        ret.setId(this.id);
        ret.setOwner(this.owner);
        ret.setAccessType(1);
        ret.setDesc("导入数据，文件为:"+FileNameUtils.getFileName(this.serverFileName)+"；导入时间:"+DateUtils.convert2TimeChineseStr(new Date(FileUtils.getFileCreateTime(f))));
        //分类信息
        FileCategory fc = new FileCategory();
        fc.setFType1(FileCategoryType1.IMP);
        if (ret.getExtName().equalsIgnoreCase(".XLS")||ret.getExtName().equalsIgnoreCase(".XLSX")||ret.getExtName().equalsIgnoreCase(".CVS")) {
            fc.setFType2("Rdata");
        } else {
            fc.setFType2("Tdata");
        }
        fc.setFType3(ret.getExtName());
        fc.setExtInfo((String)this.getClientFileName());
        ret.addFileCategoryList(fc);

        return ret;
    }
    @Override
    public void buildFromFileInfo(FileInfo fi) {
        // TODO Auto-generated method stub
        
    }
}