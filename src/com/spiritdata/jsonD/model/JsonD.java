package com.spiritdata.jsonD.model;

import java.io.Serializable;

import com.spiritdata.jsonD.ConvertJson;
import com.spiritdata.jsonD.exceptionC.JsonD0004CException;
import com.spiritdata.jsonD.util.JsonUtils;

public class JsonD implements Serializable, ConvertJson {
    private static final long serialVersionUID = 7880103785898374745L;

    private Object _HEAD;//头信息，可以是String reportHead 对象
    private Object _DATA;//报告主题信息，可以是String reportHead 对象

    public Object get_HEAD() {
        return _HEAD;
    }
    public void set_HEAD(Object _HEAD) {
        this._HEAD = _HEAD;
    }
    public Object get_DATA() {
        return _DATA;
    }
    public void set_DATA(Object _DATA) {
        this._DATA = _DATA;
    }
    
    @Override
    public String toJson() {
        if (_HEAD==null) throw new JsonD0004CException("jsonD不规范：头信息(_HEAD)必须设置！");

        String jsonS = "{";
        //转换头
        if (_HEAD instanceof JsonDHead) {
            jsonS += ((JsonDHead)_HEAD).toJson();
        } else {
            jsonS += "\"_HEAD\":"+JsonUtils.objToJson(_HEAD);
        }
        jsonS += ",";
        //转换体report
        if (_DATA==null) jsonS += "\"_DATA\":\"\"";
        else {
            if (_DATA instanceof String) {
                jsonS += "\"_DATA\":"+_DATA;
            } else {
                jsonS += "\"_DATA\":"+JsonUtils.objToJson(_DATA);
            }
        }
        return jsonS+"}";
    }

}
