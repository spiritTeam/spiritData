package com.spiritdata.filemanage.core.pattern.service;

import com.spiritdata.filemanage.core.pattern.model.ToBeStoreFile;
import com.spiritdata.filemanage.exceptionC.Flmg0003CException;
import com.spiritdata.filemanage.util.FileOperUtils;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.jsonD.util.JsonUtils;

/**
 * 把字符串存储到“被存储文件对象”所指定的文件中的虚类。<br/>
 * 此类封装了写字符串或Json串到文件的IO操作。<br/>
 * 此类的实现类需要实现buildFileName方法。
 * @author wh
 */
public abstract class AbstractWriteString2FileByToBeStoreFile {
    /**
     * 得到存储文件名称，全路径名
     * @param tbsf 将要存储的文件对象
     * @return 存储文件名称
     */
    public String getStoreFileName(ToBeStoreFile tbsf) {
        if (StringUtils.isNullOrEmptyOrSpace(tbsf.getFileNameSeed())
          &&StringUtils.isNullOrEmptyOrSpace(tbsf.getFullFileName())) {
            throw new Flmg0003CException("'文件名种子'或'文件全名'至少设定一个");
        }

        if (StringUtils.isNullOrEmptyOrSpace(tbsf.getFullFileName())) return buildFileName(tbsf.getFileNameSeed());
        else return tbsf.getFullFileName();
    }

    /**
     * 写字符串内容content到文件
     * @param content 字符串内容
     * @param tbsf 文件全名，包括路径
     */
    public String write2File(String content, ToBeStoreFile tbsf) {
        String storeFileName = getStoreFileName(tbsf);
        FileOperUtils.write2File(content, storeFileName);
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