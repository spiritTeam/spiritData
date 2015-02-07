package com.spiritdata.filemanage.core.pattern.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.spiritdata.filemanage.core.pattern.model.ToBeStoreFile;
import com.spiritdata.filemanage.exceptionC.Flmg0003CException;
import com.spiritdata.framework.util.FileNameUtils;
import com.spiritdata.jsonD.util.JsonUtils;

/**
 * 把字符串存储到“被存储文件对象”所指定的文件中的虚类。<br/>
 * 此类封装了写字符串或Json串到文件的IO操作。<br/>
 * 此类的实现类需要实现buildFileName方法。
 * @author wh
 */
public abstract class AbstractWriteString2FileByToBeStoreFile {
    /*
     * 写字符串内容content到文件
     * @param content 字符串内容
     * @param fullFileName 文件全名，包括路径
     */
    private void write2File(String content, String fullFileName) {
        if (fullFileName==null||fullFileName.trim().length()==0) throw new Flmg0003CException(new IllegalArgumentException("文件名参数[fullFileName]为空或空串，无法写入文件"));
        //写文件
        FileOutputStream fileOutputStream = null;
        try {
            File file = new File(fullFileName);
            if (!file.exists()) {
                File dirs = new File(FileNameUtils.getFilePath(fullFileName));
                if (!dirs.exists()) {
                    if (dirs.mkdirs()) file.createNewFile();
                    else throw new Flmg0003CException("创建目录["+FileNameUtils.getFilePath(fullFileName)+"]失败，无法写入文件");
                }
            }
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(content.getBytes());
        } catch (FileNotFoundException e) {
            throw new Flmg0003CException(e);
        } catch (IOException e) {
            throw new Flmg0003CException(e);
        } finally {
            if (fileOutputStream!=null) {
                try {fileOutputStream.close();}catch(IOException e) {e.printStackTrace();}
            }
        }
    }

    /**
     * 得到存储文件名称，全路径名
     * @param tbsf 将要存储的文件对象
     * @return 存储文件名称
     */
    public String getStoreFileName(ToBeStoreFile tbsf) {
        if ((tbsf.getFileNameSeed()==null||tbsf.getFileNameSeed().trim().length()==0)
          &&(tbsf.getFullFileName()==null||tbsf.getFullFileName().trim().length()==0)) {
            throw new Flmg0003CException("'文件名种子'或'文件全名'至少设定一个");
        }
        
        if (tbsf.getFullFileName()==null||tbsf.getFullFileName().trim().length()==0) {
            return buildFileName(tbsf.getFileNameSeed());
        } else {
            return tbsf.getFullFileName();
        }
    }

    /**
     * 写字符串内容content到文件
     * @param content 字符串内容
     * @param tbsf 文件全名，包括路径
     */
    public String write2File(String content, ToBeStoreFile tbsf) {
        String storeFileName = getStoreFileName(tbsf);
        this.write2File(content, storeFileName);
        return storeFileName;
    }

    /**
     * 写Json字符串到文件
     * @param jsonStr Json字符串
     * @param fullFileName 文件全名，包括路径
     */
    public String writeJson2File(String jsonStr, ToBeStoreFile tbsf) {
        return write2File(JsonUtils.formatJsonStr(jsonStr, null), tbsf);
    }

    //虚拟方法
    /**
     * 根据文件名种子，构建存储文件名称(全路径名)
     * @param fileNameSeed 文件名种子
     * @return 存储文件(全路径)名称
     */
    public abstract String buildFileName(String fileNameSeed);
}