package com.spiritdata.filemanage.core.pattern.model;

/**
 * 将要被存储的文件对象。<br/>
 * 此对象主要用来设置文件的存储路径
 * 
 * @author wh
 */
public abstract class AbstractToBeStoreFile implements ToBeStoreFile {
    /**
     * 文件名称种子
     */
    private String fileNameSeed;
    public String getFileNameSeed() {
        return fileNameSeed;
    }
    public void setFileNameSeed(String fileNameSeed) {
        this.fileNameSeed = fileNameSeed;
    }

    /**
     * 文件名全名
     */
    private String fullFileName;
    public String getFullFileName() {
        return fullFileName;
    }
    public void setFullFileName(String fullFileName) {
        this.fullFileName = fullFileName;
    }
}
