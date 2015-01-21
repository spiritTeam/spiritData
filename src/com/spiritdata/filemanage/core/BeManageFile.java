package com.spiritdata.filemanage.core;

import com.spiritdata.filemanage.core.model.FileInfo;

/**
 * 被管理的文件都要实现这个接口
 * @author wh
 */
public interface BeManageFile {
    /**
     * 把被管里的对象转换为“模型化文件信息”
     * @return 模型化文件信息
     */
    public FileInfo convert2FileInfo();
}