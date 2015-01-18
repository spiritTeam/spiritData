package com.spiritdata.jsonD;

/**
 * 把一个对象转换为json格式的接口
 * @author wh
 */
public interface ConvertJson {
    /**
     * 把对象本身转换为json
     * @return json字符串
     */
    public String toJson();
}