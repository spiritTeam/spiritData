package com.spiritdata.filemanage.core.pattern.service;

import com.spiritdata.filemanage.core.pattern.model.BeManageFile;
import com.spiritdata.filemanage.core.pattern.model.ToBeStoreFile;

/**
 * 按JsonD的方式写入文件的接口，所有需要按JsonD方式写入文件的服务类，都应实现本接口
 * @author wh
 */
public interface WriteJsonD {
    /**
     * 以JsonD格式写入文件
     * @param content 预写入的内容，以对象形式传入
     * @param fileSeed 文件的种子，以此为参照，写入文件，如必须规定文件的写入路径
     * @return 写入后，返回文件信息，应该是被管理文件实现类的实例
     */
    public BeManageFile write2FileAsJson(Object content, ToBeStoreFile fileSeed);
}