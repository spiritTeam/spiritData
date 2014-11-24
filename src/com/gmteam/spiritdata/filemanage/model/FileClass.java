package com.gmteam.spiritdata.filemanage.model;

import java.sql.Timestamp;

import com.gmteam.framework.core.model.BaseObject;
import com.gmteam.spiritdata.filemanage.enumeration.FileClassType1;

/**
 * 模型化文件分类对象。它与持久化中的FileClassPo区别在于：
 * 1-持久化中的FileClass与数据库对应，这里的类与实际模型相对应。
 * 2-这里是对象化的模型。
 * 使用模型类更加规范，但开销大——结构复杂
 * @author wh
 */
public class FileClass extends BaseObject {
    private static final long serialVersionUID = 3467199927097139932L;

    private String id; //文件分类id
    private FileInfo classFile; //被分类的文件
    private FileClassType1 FType1; //文件大分类，是
    private String FType2; //文件分类—中类
    private String FType3; //文件分类—小类
    private String extInfo; //扩展信息
    private Timestamp CTime; //创建时间
}