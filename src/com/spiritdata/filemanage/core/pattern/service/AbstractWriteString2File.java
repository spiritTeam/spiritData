package com.spiritdata.filemanage.core.pattern.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.spiritdata.filemanage.exceptionC.Flmg0003CException;
import com.spiritdata.framework.util.FileNameUtils;
import com.spiritdata.jsonD.util.JsonUtils;

/**
 * 把字符串存储到文件中的虚类。<br/>
 * 此类封装了写文件的IO操作，文件存储位置及名称，需要实现getStoreFileName方法，此方法应用到文件名种子，因此需要先设置fileNameSeed。
 * @author wh
 */
public abstract class AbstractWriteString2File {
    protected String fileNameSeed; //文件名种子，根据这个种子生成存储文件全路径名
    protected String fullFileName; //文件名全名

    /**
     * 设置文件名种子
     * @param fileNameSeed
     */
    public void setFileNameSeed(String fileNameSeed) {
        this.fileNameSeed = fileNameSeed;
    }

    /**
     * 设置文件名全名
     * @param fullFileName
     */
    public void setFullFileName(String fullFileName) {
        this.fullFileName = fullFileName;
    }
    /**
     * 写字符串内容content到文件
     * @param content 字符串内容
     * @return 若文件存储成功，返回文件的全路径名称
     */
    public String write2File(String content) {
        if ((this.fileNameSeed==null||this.fileNameSeed.trim().length()==0)&&(this.fullFileName==null||this.fullFileName.trim().length()==0)) {
            throw new Flmg0003CException("'文件名生成种子'或'文件全名'至少设定一个");
        }

        //写文件
        FileOutputStream fileOutputStream = null;
        try {
            String storeFile = getStoreFileName();
            File file = new File(storeFile);
            if (!file.exists()) {
                File dirs = new File(FileNameUtils.getFilePath(storeFile));
                if (!dirs.exists()) {
                    if (dirs.mkdirs()) file.createNewFile();
                    else throw new Flmg0003CException("创建目录["+FileNameUtils.getFilePath(storeFile)+"]失败，无法写入文件");
                }
            }
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(content.getBytes());
            return storeFile;
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
     * 写Json字符串到文件
     * @param jsonStr Json字符串
     * @return 若文件存储成功，返回文件的全路径名称
     */
    public String writeJson2File(String jsonStr) {
        return write2File(JsonUtils.formatJsonStr(jsonStr, null));
    }

    /**
     * 得到存储文件名称，全路径名
     * @param fileNameSeed 存储文件的种子，根据种子，继承此类的服务类生成存储文件名称
     * @return 存储文件名称
     */
    public String getStoreFileName() {
        if (this.fullFileName==null||this.fullFileName.trim().length()==0) {
            return buildFileName();
        } else {
            return this.fullFileName;
        }
    }

    //虚拟方法
    /**
     * 得到存储文件名称(全路径名)
     * @return 存储文件名称
     */
    public abstract String buildFileName();
}