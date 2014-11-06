package com.gmteam.spiritdata.login.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

//import com.gmteam.spiritdata.login.util.RandomValidateCode;

/** 
 * @author 
 * @version  
 * 类说明 
 */
@Controller
public class CheckCodeController {
    @RequestMapping("check.do")
    public void getCheckCode(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("image/jpeg");//设置相应类型,告诉浏览器输出的内容为图片
        response.setHeader("Pragma", "No-cache");//设置响应头信息，告诉浏览器不要缓存此内容
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expire", 0);
//        RandomValidateCode randomValidateCode = new RandomValidateCode();
        try {
//            randomValidateCode.getRandcode(request, response);//输出图片方法
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
