package com.spiritdata.dataanal.expreport.word.util;

import java.util.LinkedList;
import java.util.List;

import com.spiritdata.dataanal.expreport.word.model.Report;
import com.spiritdata.jsonD.model.JsonD;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public abstract class WordUtils{
	/**
	 * @param jsonInput
	 * @param type
	 * @return
	 * @throws Exception 
	 */
    @SuppressWarnings({ "rawtypes", "static-access" })
	public static List getObjList(String jsonInput, String type) throws Exception{
    	if(jsonInput==""||jsonInput==null){
    		throw new Exception("json 为空");
    	}else if(type==""||jsonInput==type){
    		throw new Exception("type 为定义");
    	}
    	else if (type.equals("report")) {
    		List<Report> resultList = new LinkedList<Report>();
            JSONArray jsonArray = JSONArray.fromObject(jsonInput);
            Object tmpObj = null;
            JSONObject jsonObject = null;
            for (int i = 0; i< jsonArray.size(); i++) {
                tmpObj = jsonArray.get(i);
                jsonObject = JSONObject.fromObject(tmpObj);
                resultList.add((Report) jsonObject.toBean(jsonObject, Report.class));
            }
            return resultList;
    	} else {
    		List<JsonD> resultList = new LinkedList<JsonD>();
            JSONArray jsonArray = JSONArray.fromObject(jsonInput);
            Object tmpObj = null;
            JSONObject jsonObject = null;
            for (int i = 0; i< jsonArray.size(); i++) {
                tmpObj = jsonArray.get(i);
                jsonObject = JSONObject.fromObject(tmpObj);
                resultList.add((JsonD) jsonObject.toBean(jsonObject, JsonD.class));
            }
            return resultList;
    	}
    }
}
