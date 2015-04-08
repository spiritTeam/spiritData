package com.spiritdata.dataanal.expreport.word.model.jsond;

import java.io.Serializable;
/**
 * jsonD model
 * @author mht
 */
public class _JSOND implements Serializable {
    private static final long serialVersionUID = 7880103785898374745L;
    private String _id;
    private String _code;
    private String _file;
    private String _cTime;
    private String desc;
    private _JSOND_HEAD _HEAD;//头信息，可以是String reportHead 对象
    private Object _DATA;//报告主题信息，可以是String reportHead 对象
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
    public String get_file() {
        return _file;
    }
    public void set_file(String _file) {
        this._file = _file;
    }
    public String get_cTime() {
        return _cTime;
    }
    public void set_cTime(String _cTime) {
        this._cTime = _cTime;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public _JSOND_HEAD get_HEAD() {
        return _HEAD;
    }
    public void set_HEAD(_JSOND_HEAD _HEAD) {
        this._HEAD = _HEAD;
    }
    public Object get_DATA() {
        return _DATA;
    }
    public void set_DATA(Object _DATA) {
        this._DATA = _DATA;
    }
}
