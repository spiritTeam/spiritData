package com.spiritdata.jsonD.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.jsonD.web.service.JsondService;

/**
 * Jsond功能的web控制类
 * @author wh
 */
@Controller
public class JsondController {
    @Resource
    private JsondService jsondService;

    @RequestMapping(value="/jsonD/getJsonD.do")
    @ResponseBody
    public String getJsond(HttpServletRequest request, HttpServletResponse response) {
        String ret = null;
        String jsondId = request.getParameter("jsondId");
        if (jsondId!=null) {
            try {
                ret="{jsonType:1, data:"+jsondService.getJsondById(jsondId)+"}";
            } catch(Exception e) {
                ret="{jsonType:0, message:'"+e.getMessage()+"'}";
            }
        }
        String uri = request.getParameter("uri");
        if (uri!=null) {
            try {
                ret="{jsonType:1, data:"+jsondService.getJsondByUri(uri)+"}";
            } catch(Exception e) {
                ret="{jsonType:0, message:'"+e.getMessage()+"'}";
            }
        }
        return ret;
    }
}