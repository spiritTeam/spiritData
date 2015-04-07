package com.spiritdata.jsonD.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.spiritdata.framework.CodeException;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.jsonD.Convert2Json;
import com.spiritdata.jsonD.exceptionC.JsonD0001CException;
import com.spiritdata.jsonD.util.JsonUtils;


/**
 * 原子数据类型，或最小元数据。
 * 包括数据的类型、数据的值和数据的名称
 * @author wh
 */
public class JsonDAtomData implements Serializable, Convert2Json {
    private static final long serialVersionUID = 5914919027685891934L;

    private String dataName;//信息名称
    private String dataType;//数据类型，可以考虑用Enum来处理，以后再说！！！
    private Object dataValue;//数据值

    /**
     * 获取信息名称
     * @return 信息名称
     */
    public String getDataName() {
        return dataName;
    }

    /**
     * 获得数据类型
     * @return 数据类型
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * 获取数据的值
     * @return 数据的值
     */
    public Object getDataValue() {
        return dataValue;
    }

    /**
     * 空构造函数
     */
    public JsonDAtomData() {
        super();
    }

    /**
     * 构造函数，并给各必要属性赋值
     * @param dataName 信息名称
     * @param dataType 数据类型
     * @param dataValue 数据值
     */
    public JsonDAtomData(String dataName, String dataType, Object dataValue) {
        super();
        setAtomData(dataName, dataType, dataValue);
    }

    /**
     * 设置各属性
     * @param dataName 信息名称
     * @param dataType 数据类型
     * @param dataValue 数据值
     */
    public void setAtomData(String dataName, String dataType, Object dataValue) {
        this.dataName = dataName;
        this.dataType = dataType;
        this.dataValue = dataValue;
    }

    /**
     * 转换为jsonMap，为转换为json串做准备
     * @return jsonMap jsonMap
     * @throws CodeException 当
     */
    public Map<String, Object> toJsonMap() throws CodeException {
        checkAttributes();//检查合法性

        Object directedValue = null;
        boolean dTypeIsNull = StringUtils.isNullOrEmptyOrSpace(this.dataType);

        Map<String, Object> dataMap = new HashMap<String, Object>();
        if (this.dataValue!=null) {
            if (!dTypeIsNull) dataMap.put("value", this.dataValue);
            else directedValue=this.dataValue;
        } else {
            if (!dTypeIsNull) dataMap.put("value", null);
        }
        if (!StringUtils.isNullOrEmptyOrSpace(this.dataType)) {
            dataMap.put("dtype", this.dataType);
        }
        Map<String, Object> retMap = new HashMap<String, Object>();
        if (dataMap.size()==0) dataMap=null;
        if (directedValue==null) retMap.put(this.dataName, dataMap);
        else retMap.put(this.dataName, directedValue);
        return retMap;
    }

    /**
     * 转换为json串
     * @return jsonMap
     * @throws CodeException 
     */
    public String toJson() throws CodeException {
        return JsonUtils.objToJson(this.toJsonMap());
    }

    /*
     * 检查属性是否合法：名称不能为空
     * @throws CodeException
     */
    private void checkAttributes() throws CodeException {
        if (StringUtils.isNullOrEmptyOrSpace(this.dataName)) throw new JsonD0001CException("名称dataName必须设置！");
    }
}