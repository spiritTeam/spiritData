package com.spiritdata.filemanage.core.pattern.model;

import com.spiritdata.filemanage.core.model.FileInfo;

/**
 * 被管理的文件都要实现这个接口。<br/>
 * 被管理的文件要能够转换为基础文件模型，而且都要至少有一个文件分类和他相互对应。
 * @author wh
 */
public interface BeManageFile {
    /**
     * 把被管里的对象转换为“模型化文件信息”
     * @return 模型化文件信息
     */
    public FileInfo convert2FileInfo();
}