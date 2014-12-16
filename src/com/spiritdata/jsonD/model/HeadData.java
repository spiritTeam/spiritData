package com.spiritdata.jsonD.model;

import java.io.Serializable;

/**
 * jsonD格式的数据头信息。
 * 参看相关文档
 * @author wh
 */
public class HeadData implements Serializable {
    private static final long serialVersionUID = 8454602778917800099L;

    private String _id; //UUID
    private String _code; //数据的编码，此编码分为组织编码：大写的以.隔开的名称，<减号>后是小类型编号，采用5位，最大为9999个类型(可扩充)
    private String _parseFun; //这个属性现在不用
    private String _node; //这个属性现在不用
    private String _file; //存储JsonD的文件地址，可能需要和_node共同使用
    private String _desc; //描述
    public String get_id() {
        return _id;
    }
    public void set_id(String _id) {
        this._id = _id;
    }
    public String get_code() {
        return _code;
    }
    public void set_code(String _code) {
        this._code = _code;
    }
    public String get_parseFun() {
        return _parseFun;
    }
    public void set_parseFun(String _parseFun) {
        this._parseFun = _parseFun;
    }
    public String get_node() {
        return _node;
    }
    public void set_node(String _node) {
        this._node = _node;
    }
    public String get_file() {
        return _file;
    }
    public void set_file(String _file) {
        this._file = _file;
    }
    public String get_desc() {
        return _desc;
    }
    public void set_desc(String _desc) {
        this._desc = _desc;
    }

    /**
     * 把头信息转换为Json串
     * @return json串
     */
    public String toJson() {
        
        return null;
    }
}