package com.spiritdata.dataanal.templet.model;

import com.spiritdata.dataanal.exceptionC.Dtal1002CException;
import com.spiritdata.framework.core.model.BaseObject;

/**
 * 模板类，此模板类只包含模板本身的信息，不包括处理模板相关的信息
 * <br/>包括：_HEAD,_DATA,_TEMPET
 * @author wh
 */
public class Templet extends BaseObject {
    private static final long serialVersionUID = 518670183146944686L;

    private Object _HEAD;//头信息，可以是String templetHead 对象
    private Object _DATA;//数据信息，可以是String templetHead 对象
    private Object _TEMPLET;//模板主题信息，可以是String templetHead 对象

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
    public Object get_TEMPLET() {
        return _TEMPLET;
    }
    public void set_TEMPLET(Object _TEMPLET) {
        this._TEMPLET = _TEMPLET;
    }

    public String convert2Json() {
        if (_HEAD==null) throw new Dtal1002CException("templetD不规范：头信息(_HEAD)必须设置！");
        if (_DATA==null) throw new Dtal1002CException("templetD不规范：数据信息(_DATA)必须设置！");
        if (_TEMPLET==null) throw new Dtal1002CException("templetD不规范：模板主题信息(_TEMPLET)必须设置！");
        
        return null;
    }
}