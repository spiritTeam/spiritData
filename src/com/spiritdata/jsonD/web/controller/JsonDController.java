package com.spiritdata.jsonD.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.jsonD.web.service.JsonDService;

/**
 * jsonD功能的web控制类
 * @author wh
 */
@Controller
public class JsonDController {
    @Resource
    private JsonDService jsonDService;

    @RequestMapping(value="/jsonD/getJsonD.do")
    @ResponseBody
    public Map<String, Object> getJsonD(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> ret = new HashMap<String, Object>();
        String jsonDId = request.getParameter("jsonDId");
        if (jsonDId!=null) {
            try {
                ret = jsonDService.getJsonDById(jsonDId);
                if (ret!=null)  return ret;
            } catch(Exception e) {
                ret.put("jsonType", 0);
                ret.put("message", e.getMessage());
            }
        }
        String uri = request.getParameter("uri");
        if (uri!=null) {
            try {
                ret = jsonDService.getJsonDByUri(uri);
                if (ret!=null)  return ret;
            } catch(Exception e) {
                ret.put("jsonType", 0);
                ret.put("message", e.getMessage());
            }
        }
        if (ret==null) {
            ret = new HashMap<String, Object>();
            ret.put("jsonType", 0);
            ret.put("message", "空");
        }
        return ret;
    }
}