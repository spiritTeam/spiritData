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

import com.gmteam.framework.FConstants;
import com.gmteam.spiritdata.UGA.pojo.User;
import com.gmteam.spiritdata.UGA.service.UserService;
import com.gmteam.spiritdata.login.util.RandomValidateCode;
import com.gmteam.spiritdata.login.util.SendValidataUrlToMail;
import com.gmteam.spiritdata.util.SequenceUUID;
@Controller
public class RegisterController {
    @Resource
    private UserService userService;
    @RequestMapping("login/activeAgain.do")
    public @ResponseBody Map<String,Object> sendAgain(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> retMap = new HashMap<String,Object>();
        String loginName = request.getParameter("loginName");
        User user = userService.getUserByLoginName(loginName);
        if(user==null){ 
            retMap.put("success", false);
            retMap.put("retInfo", "登录名错误");
            return retMap;
        }else{
            if(user.getUserState()==1){
                retMap.put("success", true);
                retMap.put("retInfo", "该账号已经激活啦");
            }else{
                String validatsaSequence = SequenceUUID.getPureUUID();
                user.setValidataSequence(validatsaSequence);
                String url = "请前往以下地址激活账号\n"
                        + " http://localhost:8080/sa/login/activeUser.do?authCode="+user.getUserId()+"~"+validatsaSequence;
                SendValidataUrlToMail svu = new SendValidataUrlToMail();
                svu.send(user.getMailAdress(), "北京灵派诺达股份有限公司", url);
                userService.updateUser(user);
                retMap.put("success", true);
                retMap.put("retInfo", "已经向您的邮箱发送一封邮件，请激活账号");
            }
            return retMap;
        }
    }
    @RequestMapping("login/activeUser.do")
    public @ResponseBody Map<String,Object> activeMail(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> retMap = new HashMap<String,Object>();
        String authCode = request.getParameter("authCode");
        if(authCode==null){
            retMap.put("success", false);
            retMap.put("retInfo", "激活码不完整!请重新新点击激活链接或从登录页面再次发送激活邮件!");
        }
        String userId = authCode.substring(0,authCode.lastIndexOf("~"));
        String code = authCode.substring(authCode.lastIndexOf("~")+1);
        User user  = userService.getUserById(userId);
        if(user==null){
            retMap.put("success", false);
            retMap.put("retInfo", "该用户不存在!");
        }else{
            if(user.getUserState()==0){
                if(user.getValidataSequence().equals(code)){
                    user.setUserState(1);
                    user.setUserType(1);
                    HttpSession session = request.getSession();
                    session.removeAttribute(FConstants.SESSION_USER);
                    session.setAttribute(FConstants.SESSION_USER, user);
                    userService.updateUser(user);
                    retMap.put("success", true);
                    retMap.put("retInfo", "激活成功!");
                }else{
                    retMap.put("success", false);
                    retMap.put("retInfo", "激活码不完整!请从新点击激活链接或从登录页面再次发送激活邮件!");
                }
            }else{
                retMap.put("success", false);
                retMap.put("retInfo", "您的账号已经激活。");
            }
        }
        return retMap;
    }
    /**
     * 得到验证码
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping("login/getValidateCode.do")
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
    @RequestMapping("login/validateLoginName.do")
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
    @RequestMapping("login/validateMail.do")
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
    @RequestMapping("login/validateValidateCode.do")
    public @ResponseBody boolean validateValidateCode(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String sessionCC = (String) session.getAttribute("RANDOMVALIDATECODEKEY");
        String registerCC = request.getParameter("checkCode");
        if(sessionCC==null||sessionCC==""||registerCC==null||registerCC=="")return false;
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
    @RequestMapping("login/register.do")
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
        user.setUserId(SequenceUUID.getPureUUID());
        user.setUserState(0);
        int rst = userService.insertUser(user);
        if(rst==1){
            String validatsaSequence = SequenceUUID.getPureUUID();
            user.setValidataSequence(validatsaSequence);
            //1代表以发验证到邮箱验证，用户为验证
            user.setUserState(0);
            String url = "请点击以下链接激活绑定邮箱，如果不成功，把链接复制到浏览器地址栏访问\n"
                    + " http://localhost:8080/sa/activeUser.do?authCode="+user.getUserId()+"~"+validatsaSequence;
            SendValidataUrlToMail svu = new SendValidataUrlToMail();
            svu.send(user.getMailAdress(), "北京灵派诺达股份有限公司", url);
            int r = userService.updateUser(user);
            if(r==1){
                retMap.put("success", true);
                retMap.put("retInfo", "已经向您的邮箱发送一封邮件，请激活账号");
                return retMap; 
            }else{
                retMap.put("success", false);
                retMap.put("retInfo", "发送不成功，请重试");
                return retMap; 
            }
        }else{
            retMap.put("success", false);
            retMap.put("retInfo", "注册不成功，请重试");
            return retMap;  
        }
    }
}
