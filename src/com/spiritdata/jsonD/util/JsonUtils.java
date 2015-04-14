package com.spiritdata.jsonD.util;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spiritdata.framework.exceptionC.Plat0001CException;
import com.spiritdata.framework.exceptionC.Plat0002CException;

/**
 * 处理Json的方法类
 * @author wh
 */
public abstract class JsonUtils {
    private static ObjectMapper mapper;

    /**
     * 获取ObjectMapper实例
     * @param createNew 方式：true，新实例；false,存在的mapper实例
     * @return ObjectMapper实例
     */
    private static synchronized ObjectMapper getMapperInstance(boolean createNew) {     
        if (createNew)  return new ObjectMapper();
        else if (mapper==null) mapper=new ObjectMapper();
        //单引号
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        //无引号
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        //特殊字符
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        return mapper;
    }

    /**
     * 将java对象转换成json字符串
     * @param obj 准备转换的对象
     * @return json字符串
     * @throws Plat0001CException
     */
    public static String objToJson(Object obj) {
        try {
            ObjectMapper objectMapper=getMapperInstance(false);
            String json=objectMapper.writeValueAsString(obj);
            return json;
        } catch (Exception e) {
            throw new Plat0001CException(e);
        }
    }

    /**
     * 将java对象转换成json字符串
     * @param obj 准备转换的对象
     * @param createNew ObjectMapper实例方式:true，新实例;false,存在的mapper实例
     * @return json字符串
     * @throws Plat0001CException
     */
    public static String objToJson(Object obj,Boolean createNew) {
        try {
            ObjectMapper objectMapper=getMapperInstance(createNew);
            String json=objectMapper.writeValueAsString(obj);
            return json;
        } catch (Exception e) {
            throw new Plat0001CException(e.getMessage());
        }
    }

    /**
     * 将对象转换为Json字符串。type是输出类型。
     * 输出的json对象为{jsonType:#type, data:#cls2json}
     * @param obj 欲转换的对象，如果是String 则直接返回到data中
     * @param type 类型：1是成功，0是失败，其他整型类型可自己定义
     * @return Json字符串
     * @throws Plat0001CException 异常 
     */
    public static String obj2AjaxJson(Object obj, int type) {
        return JsonUtils.objToJson(JsonUtils.obj2AjaxMap(obj, type));
    }

    /**
     * 将json字符串转换成java对象
     * @param json 准备转换的json字符串
     * @param cls  准备转换的类
     * @return java对象
     * @throws Plat0002CException
     */
    public static Object jsonToObj(String json, Class<?> cls) {
        try {
            ObjectMapper objectMapper=getMapperInstance(false);
            Object vo=objectMapper.readValue(json, cls);
            return vo;
        } catch (Exception e) {
            throw new Plat0002CException(e);
        }
    }

    /**
     * 将json字符串转换成java对象
     * @param json 准备转换的json字符串
     * @param cls  准备转换的类
     * @param createNew ObjectMapper实例方式:true，新实例;false,存在的mapper实例
     * @return java对象
     * @throws Plat0002CException
     */
    public static Object jsonToObj(String json, Class<?> cls,Boolean createNew) {
        try {
            ObjectMapper objectMapper=getMapperInstance(createNew);
            Object vo=objectMapper.readValue(json, cls);
            return vo;
        } catch (Exception e) {
            throw new Plat0002CException(e);
        }
    }

    /**
     * 将对象转换为AjaxMap对象。type是输出类型。
     * @param obj 欲转换的对象，如果是String 则直接返回到data中
     * @param type 类型：1是成功，0是失败，其他整型类型可自己定义
     * @return AjaxMap对象
     * @throws Exception 异常 
     */
    public static Map<String, Object> obj2AjaxMap(Object obj, int type) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("jsonType", type);
        if (type==1) m.put("data", obj);
        else m.put("message", obj);
        return m;
    }

    /**
     * 得到紧凑型Json串
     * @param jsonStr 原Json串
     * @return 紧凑型Json串
     */
    public static String getCompactJsonStr(String jsonStr) {
        if (jsonStr==null||jsonStr.trim().length()==0) return null;

        int dQuotes = 0, sQuotes=0; //单双引号标志
        int rnFlag = 0; //回车换行标记
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<jsonStr.length(); i++) {
            char c = jsonStr.charAt(i);
            if (c=='\'') sQuotes=(sQuotes==0?((sQuotes<<1)+1):(sQuotes>>1));
            if (c=='\"') dQuotes=(dQuotes==0?((dQuotes<<1)+1):(dQuotes>>1));

            if (sQuotes>0||dQuotes>0) {
                if (c=='\r'||c=='\n') {
                    while (sb.charAt(sb.length()-1)==' ') sb.deleteCharAt(sb.length()-1);
                    rnFlag=1;
                    continue;
                }
                if (rnFlag>0) {
                    if (c!='\t'&&c!=' ') {
                        sb.append(c);
                        rnFlag=0;
                    }
                } else sb.append(c);
            } else if (c!=' '&&c!='\t'&&c!='\n'&&c!='\r') sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 把json字符串，格式化为更可读的形式
     * @param jsonStr json字符串，注意必须是已经Json了
     * @param indentStr 缩进字符串，可为空，若为空，缩进字符串为两个ASCII空格
     * @return 格式化后的字符串
     */
    public static String formatJsonStr(String jsonStr, String indentStr) {
        if (jsonStr==null||jsonStr.trim().length()==0) return null;

        int dQuotes = 0, sQuotes=0; //单双引号标志
        int rnFlag = 0; //回车换行标记

        //更换
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<jsonStr.length(); i++) {
            char c = jsonStr.charAt(i);
            if (c=='\'') sQuotes=(sQuotes==0?((sQuotes<<1)+1):(sQuotes>>1));
            if (c=='\"') dQuotes=(dQuotes==0?((dQuotes<<1)+1):(dQuotes>>1));

            if (sQuotes>0||dQuotes>0) {
                if (c=='\r'||c=='\n') {
                    while (sb.charAt(sb.length()-1)==' ') sb.deleteCharAt(sb.length()-1);
                    rnFlag=1;
                    continue;
                }
                if (rnFlag>0) {
                    if (c!='\t'&&c!=' ') {
                        sb.append(c);
                        rnFlag=0;
                    }
                } else sb.append(c);
            } else if (c!=' '&&c!='\t'&&c!='\n'&&c!='\r') sb.append(c);
        }

        //格式化Json串
        dQuotes = 0; sQuotes=0;//重置引号标志位
        StringBuffer _sb = new StringBuffer();
        //占位符处理
        String _indentStr = indentStr;
        if (_indentStr==null||_indentStr.trim().length()==0) _indentStr="  ";
        String _is = "";
        for (int i=0; i<sb.length(); i++) {
            char c = sb.charAt(i);
            char _c = c;
            if (i<sb.length()-1) _c = sb.charAt(i+1);
            if (c=='\''&&dQuotes%2==0) sQuotes++;
            if (c=='\"'&&sQuotes%2==0) dQuotes++;

            if (sQuotes%2==1||dQuotes%2==1) _sb.append(c);
            else {
                if ((c=='['&&_c==']')||(c=='{'&&_c=='}')) {
                    _sb.append(c);
                    _sb.append(_c);
                    i++;
                    continue;
                }
                if (c=='{'||c=='['||(c==','&&_c!='['&&_c!='{')) {
                    _sb.append(c);
                    _sb.append("\n");
                    if (c=='{'||c=='[') {
                        _is +=_indentStr;
                    }
                    _sb.append(_is);
                } else if (c=='}'||c==']') {
                    _is = _is.substring(0, _is.length()-_indentStr.length());
                    _sb.append("\n");
                    _sb.append(_is);
                    _sb.append(c);
                } else if (c==':') {
                    _sb.append(c);
                    _sb.append(' ');
                } else {
                    _sb.append(c);
                }
            }
        }
        return _sb.toString();
    }
}
