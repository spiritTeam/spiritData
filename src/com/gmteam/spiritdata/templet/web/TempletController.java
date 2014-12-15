package com.gmteam.spiritdata.templet.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.dataanal.templet.service.TempletService;


@Controller
public class TempletController {
    @Resource
    private TempletService templetService;

    @RequestMapping(value="/templet/getTemplet.do")
    public @ResponseBody String getTemplet(HttpServletRequest request, HttpServletResponse response) {
        String templetId = request.getParameter("templetId");
        try {
            return "{jsonType:1, data:"+templetService.getTempletJsonById(templetId)+"}";
        } catch(Exception e) {
            return "{jsonType:0, message:'"+e.getMessage()+"'}";
        }
    }
}