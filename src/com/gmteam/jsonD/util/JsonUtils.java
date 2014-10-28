package com.gmteam.jsonD.util;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
    private static ObjectMapper mapper;

    /**
     * 获取ObjectMapper实例
     * @param createNew 方式：true，新实例；false,存在的mapper实例
     * @return ObjectMapper实例
     */
    private static synchronized ObjectMapper getMapperInstance(boolean createNew) {     
        if (createNew)  return new ObjectMapper();
        else if (mapper==null) mapper=new ObjectMapper();
        return mapper;
    }

    /**
     * 将java对象转换成json字符串
     * @param obj 准备转换的对象
     * @return json字符串
     * @throws JsonProcessingException 
     * @throws Exception
     */
    public static String objToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper=getMapperInstance(false);
        String json=objectMapper.writeValueAsString(obj);
        return json;
    }

    /**
     * 将java对象转换成json字符串
     * @param obj 准备转换的对象
     * @param createNew ObjectMapper实例方式:true，新实例;false,存在的mapper实例
     * @return json字符串
     * @throws Exception
     */
    public static String objToJson(Object obj,Boolean createNew) throws Exception {
        try {
            ObjectMapper objectMapper=getMapperInstance(createNew);
            String json=objectMapper.writeValueAsString(obj);
            return json;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 将json字符串转换成java对象
     * @param json 准备转换的json字符串
     * @param cls  准备转换的类
     * @return java对象
     * @throws Exception
     */
    public static Object jsonToObj(String json, Class<?> cls) throws Exception {
        try {
            ObjectMapper objectMapper=getMapperInstance(false);
            Object vo=objectMapper.readValue(json, cls);
            return vo;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 将json字符串转换成java对象
     * @param json 准备转换的json字符串
     * @param cls  准备转换的类
     * @param createNew ObjectMapper实例方式:true，新实例;false,存在的mapper实例
     * @return java对象
     * @throws Exception
     */
    public static Object jsonToObj(String json, Class<?> cls,Boolean createNew) throws Exception {
        try {
            ObjectMapper objectMapper=getMapperInstance(createNew);
            Object vo=objectMapper.readValue(json, cls);
            return vo;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 将对象转换为Json字符串。type是输出类型。
     * 输出的json对象为{jsonType:#type, data:#cls2json}
     * @param obj 欲转换的对象，如果是String 则直接返回到data中
     * @param type 类型：1是成功，0是失败，其他整型类型可自己定义
     * @return Json字符串
     * @throws JsonProcessingException 异常 
     */
    public static String Obj2AjaxJson(Object obj, int type) throws JsonProcessingException {
        return JsonUtils.objToJson(JsonUtils.Obj2AjaxMap(obj, type));
    }

    /**
     * 将对象转换为AjaxMap对象。type是输出类型。
     * @param obj 欲转换的对象，如果是String 则直接返回到data中
     * @param type 类型：1是成功，0是失败，其他整型类型可自己定义
     * @return AjaxMap对象
     * @throws Exception 异常 
     */
    public static Map<String, Object> Obj2AjaxMap(Object obj, int type) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("jsonType", type);
        m.put("data", obj);
        return m;
    }
}