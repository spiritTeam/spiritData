package com.gmteam.spiritdata.login.web;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gmteam.spiritdata.UGA.pojo.User;
import com.gmteam.spiritdata.UGA.service.UserService;
import com.gmteam.spiritdata.login.util.RandomValidateCode;
import com.gmteam.spiritdata.util.SequenceUUID;

@Controller
public class CheckRegisterInfoController {
    /**
     * 得到验证码
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping("getCheckCode.do")
    public void getCheckCode(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        response.setContentType("image/jpeg");//设置相应类型,告诉浏览器输出的内容为图片
        response.setHeader("Pragma", "No-cache");//设置响应头信息，告诉浏览器不要缓存此内容
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expire", 0);
        RandomValidateCode randomValidateCode = new RandomValidateCode();
        try {
            randomValidateCode.getRandcode(request, response);//输出图片方法
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Resource
    private UserService userService;
    /**
     * 验证登录名
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping("verificationLoginName.do")
    public @ResponseBody boolean verificationLoginName(HttpServletRequest request, HttpServletResponse response) {
        String loginName = request.getParameter("loginName");
        User user  = userService.getUserByLoginName(loginName);
        if(user!=null){
            return false;
        }else{
            return true;
        }
    }
    /**
     * 验证邮箱
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping("verificationMail.do")
    public @ResponseBody boolean verificationMail(HttpServletRequest request, HttpServletResponse response) {
        String mail = request.getParameter("mail");
        User user  = userService.getUserByMailAdress(mail);
        if(user!=null){
            return false;
        }else{
            return true;
        }
    }
    /**
     * 验证验证码
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping("verificationCheckCode.do")
    public @ResponseBody boolean verificationCheckCode(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String sessionCC = (String) session.getAttribute("RANDOMVALIDATECODEKEY");
        String registerCC = request.getParameter("checkCode");
        if(sessionCC.equals(registerCC.toUpperCase())){
            return true;
        }else{
            return false;  
        }
    }
    /**
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("saveRegisterInfo.do")
    public @ResponseBody boolean saveRegisterInfo(HttpServletRequest request, HttpServletResponse response) {
        String loginName = request.getParameter("loginName");
        String password = request.getParameter("password");
        String userName = request.getParameter("userName");
        String mailAdress = request.getParameter("mailAdress");
        User user  = new User();
        user.setLoginName(loginName);
        user.setPassword(password);
        user.setMailAdress(mailAdress);
        user.setUserName(userName);
        user.setUserId(SequenceUUID.getUUID());
        user.setUserState(0);
        int rst = userService.insertUser(user);
        if(rst==1){
            return true;
        }else{
            return false;  
        }
    }
}
