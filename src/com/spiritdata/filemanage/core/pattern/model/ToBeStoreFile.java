package com.spiritdata.filemanage.core.pattern.model;

/**
 * 将要被存储的文件对象的接口
 * 
 * @author wh
 */
public interface ToBeStoreFile {

    /**
     * 获得文件名种子
     * @return 文件名种子
     */
    public String getFileNameSeed();

    /**
     * 获得文件全名
     * @return 文件全名
     */
    public String getFullFileName();
}