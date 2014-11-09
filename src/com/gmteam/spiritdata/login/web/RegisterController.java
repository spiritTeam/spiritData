package com.gmteam.spiritdata.login.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
import com.gmteam.spiritdata.login.util.SendValidataUrlToMail;
import com.gmteam.spiritdata.util.SequenceUUID;
@Controller
public class RegisterController {
    @Resource
    private UserService userService;
    @RequestMapping("activeUser.do")
    public void activeMail(HttpServletRequest request, HttpServletResponse response){
        String authCode = request.getParameter("authCode");
        String userId = authCode.substring(0,authCode.lastIndexOf("~"));
        String code = authCode.substring(authCode.lastIndexOf("~"));
        User user  = userService.getUserById(userId);
        if(user.getValidataSequence().equals(code)){
            user.setUserState(1);
            userService.updateUser(user);
            System.out.println("激活成功");
        }
    }
    /**
     * 得到验证码
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping("getValidateCode.do")
    public void getValidateCode(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
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
    /**
     * 验证登录名
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping("validateLoginName.do")
    public @ResponseBody boolean validateLoginName(HttpServletRequest request, HttpServletResponse response) {
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
    @RequestMapping("validateMail.do")
    public @ResponseBody boolean validateMail(HttpServletRequest request, HttpServletResponse response) {
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
    @RequestMapping("validateValidateCode.do")
    public @ResponseBody boolean validateValidateCode(HttpServletRequest request, HttpServletResponse response) {
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
    @RequestMapping("Register.do")
    public @ResponseBody Map<String,Object> saveRegisterInfo(HttpServletRequest request, HttpServletResponse response) {
        Map<String,Object> retMap = new HashMap<String,Object>();
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
            String validatsaSequence = SequenceUUID.getPureUUID();
            user.setValidataSequence(validatsaSequence);
            //1代表以发验证到邮箱验证，用户为验证
            user.setUserState(1);
            String url = "请点击以下链接激活绑定邮箱，如果不成功，把链接复制到浏览器地址栏访问/n"
                    + " http://localhost:8080/sa/activeUser.do?authCode="+user.getUserId()+"~"+validatsaSequence;
            SendValidataUrlToMail svu = new SendValidataUrlToMail();
            svu.send("jiao80496263@163.com", "北京灵派诺达股份有限公司", url);
            userService.updateUser(user);
            retMap.put("success", true);
            retMap.put("retInfo", "已经向您的邮箱发送一封邮件，请激活账号");
            return retMap;
        }else{
            retMap.put("success", false);
            retMap.put("retInfo", "注册不成功，请重试");
            return retMap;  
        }
    }
}
