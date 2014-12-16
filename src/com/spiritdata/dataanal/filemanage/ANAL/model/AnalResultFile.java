package com.spiritdata.dataanal.filemanage.ANAL.model;

import com.spiritdata.framework.core.model.BaseObject;

/**
 * 分析结果文件，虽然叫分析结果，但也可以是其他分析的数据输入。注意：分析结果是按照jsonD存储的<br/>
 * 分析结果文件存储在analData中，按照二级分类进行存储，目前分类有：<br/>
 * -METADATA:元数据结构分析，又可分为key/dict/sement——主键/字典/语义等<br/>
 * 若为-METEDATA，则第三分类为元数据的Id及MetadataModelId<br/>
 * <br/>
 * 所分析对象，在这里用objId,objType,objAdress标识，若是excel文件，则objId为元数据id，objType="file"，objAdress="文件地址"
 * 注意：这里的分析结构，都按照
 * @author wh
 */
public class AnalResultFile extends BaseObject {
    private static final long serialVersionUID = 7715689049076212381L;

    private String analType; //分析类型-METADATA:元数据结构分析，又可分为key/dict/sement，今后根据情况再扩充，用于确定保存的目录
    private String describe; //分析描述，包括分类等信息，是一个json复合数据

    private String objId; //所分析的对象Id，MetadataModelId
    private String objType; //分析对象类型，
    private String objAdress; //分析对象的访问地址

    
}