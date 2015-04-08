package com.spiritdata.dataanal.expreport.word.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import com.spiritdata.dataanal.expreport.word.model.jsond._JSOND;
import com.spiritdata.dataanal.expreport.word.model.report._REPORT;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * word utils
 * @author mht
 */
public abstract class WordUtils{
    /**
     * @param jsonInput
     * @param type
     * @return
     * @throws Exception 
     */
    @SuppressWarnings({ "rawtypes", "static-access" })
    public static List json2ObjList(String jsonInput, String type) throws Exception{
        if(jsonInput==""||jsonInput==null){
            throw new Exception("json 为空");
        }else if(type==""||jsonInput==type){
            throw new Exception("type 为定义");
        }
        else if (type.equals("report")) {
            List<_REPORT> resultList = new LinkedList<_REPORT>();
            JSONArray jsonArray = JSONArray.fromObject(jsonInput);
            Object tmpObj = null;
            JSONObject jsonObject = null;
            for (int i = 0; i< jsonArray.size(); i++) {
                tmpObj = jsonArray.get(i);
                jsonObject = JSONObject.fromObject(tmpObj);
                resultList.add((_REPORT) jsonObject.toBean(jsonObject, _REPORT.class));
            }
            return resultList;
        } else {
            List<_JSOND> resultList = new LinkedList<_JSOND>();
            JSONArray jsonArray = JSONArray.fromObject(jsonInput);
            Object tmpObj = null;
            JSONObject jsonObject = null;
            for (int i = 0; i< jsonArray.size(); i++) {
                tmpObj = jsonArray.get(i);
                jsonObject = JSONObject.fromObject(tmpObj);
                resultList.add((_JSOND) jsonObject.toBean(jsonObject, _JSOND.class));
            }
            return resultList;
        }
    }

    /**
     * 关闭输出流
     * @param os
     */
    public static void close(OutputStream os) {
        if (os != null) {
           try {
               os.close();
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
    }
}
