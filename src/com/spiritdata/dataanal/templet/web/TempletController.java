package com.spiritdata.dataanal.templet.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.dataanal.templet.service.TempletService;

/**
 * 模板功能的web控制类
 * @author wh
 */
@Controller
public class TempletController {
    @Resource
    private TempletService templetService;
    @RequestMapping(value="/templet/getTemplet.do")
    @ResponseBody
    public String getTemplet(HttpServletRequest request, HttpServletResponse response) {
        String ret = null;
        String templetId = request.getParameter("templetId");
        if (templetId!=null) {
            try {
                ret="{jsonType:1, data:"+templetService.getTempletJsonById(templetId)+"}";
            } catch(Exception e) {
                ret="{jsonType:0, message:'"+e.getMessage()+"'}";
            }
        }
        String uri = request.getParameter("uri");
        if (uri!=null) {
            try {
                ret="{jsonType:1, data:"+templetService.getTempletJsonByUri(uri)+"}";
            } catch(Exception e) {
                ret="{jsonType:0, message:'"+e.getMessage()+"'}";
            }
        }
        return ret;
    }
}