package com.gmteam.jsonD.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 原子数据类型，或最小元数据。
 * 包括数据的类型、数据的值和数据的名称
 * @author wh
 */
public class AtomData implements Serializable {
    private static final long serialVersionUID = 5914919027685891934L;

    private String dataType;//数据类型
    private Object dataValue;//数据值
    public String getDataType() {
        return dataType;
    }
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    public Object getDataValue() {
        return dataValue;
    }
    public void setDataValue(Object dataValue) {
        this.dataValue = dataValue;
    }

    public AtomData(String dataType, Object dataValue) {
        super();
        this.dataType = dataType;
        this.dataValue = dataValue;
    }

    public AtomData() {
        super();
    }

    public void setAtomData(String dataType, Object dataValue) {
        this.dataType = dataType;
        this.dataValue = dataValue;
    }

    public void clean() {
        this.dataType = null;
        this.dataValue = null;
    }

    /**
     * 转换为jsonMap
     * @return jsonMap
     */
    public Map<String, Object> toJsonMap() {
        Map<String, Object> ret = new HashMap<String, Object>();
        if (this.dataValue==null) {
            if (this.dataType==null||this.dataType.equals("")) {
                ret = null;
            } else {
                ret.put("dtype", this.dataType);
            }
        } else {
            ret.put("value", this.dataValue);
            if (this.dataType!=null&&!this.dataType.equals("")) {
                ret.put("dtype", this.dataType);
            }
        }
        return ret;
    }
}