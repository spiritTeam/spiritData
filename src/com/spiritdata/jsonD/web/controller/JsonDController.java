package com.spiritdata.jsonD.web.controller;

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
    public String getJsonD(HttpServletRequest request, HttpServletResponse response) {
        String ret = null;
        String jsonDId = request.getParameter("jsonDId");
        if (jsonDId!=null) {
            try {
                ret="{jsonType:1, data:"+jsonDService.getJsonDById(jsonDId)+"}";
            } catch(Exception e) {
                ret="{jsonType:0, message:'"+e.getMessage()+"'}";
            }
        }
        String uri = request.getParameter("uri");
        if (uri!=null) {
            try {
                ret="{jsonType:1, data:"+jsonDService.getJsonDByUri(uri)+"}";
            } catch(Exception e) {
                ret="{jsonType:0, message:'"+e.getMessage()+"'}";
            }
        }
        return ret;
    }
}