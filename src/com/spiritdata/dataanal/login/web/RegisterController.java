package com.spiritdata.dataanal.login.web;

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

import com.spiritdata.framework.FConstants;
import com.spiritdata.dataanal.UGA.pojo.User;
import com.spiritdata.dataanal.UGA.service.UserService;
import com.spiritdata.dataanal.login.LoginConstants;
import com.spiritdata.dataanal.login.util.RandomValidateCode;
import com.spiritdata.dataanal.login.util.SendValidataUrlToMail;
import com.spiritdata.dataanal.util.SequenceUUID;
@Controller
public class RegisterController {
    @Resource
    private UserService userService;
    /**
     * 发送重设密码的验证邮件
     * @param request
     * @return
     */
    @RequestMapping(value="/login/sendBackPwdMail.do")
    public @ResponseBody Map<String,Object> sendBackPwdMail(HttpServletRequest request){
        Map<String,Object> retMap = new HashMap<String,Object>();
        String loginName = request.getParameter("loginName");
        User user = userService.getUserByLoginName(loginName);
        String validatsaSequence = SequenceUUID.getPureUUID();
        user.setValidataSequence(validatsaSequence);
        //发布名
        String deployName = request.getContextPath();
        //serverPort
        int  serverPort = request.getServerPort();
        //serverName
    	String serverName = request.getServerName();
    	//验证url=serverName+deployName+servletPath
        String url = "请前往以下地址修改密码\n"+serverName+":"+serverPort+deployName+LoginConstants.ACTIVE_MODIFY_PASSWORD_REQUEST+"?authCode="+user.getUserId()+"~"+validatsaSequence;
        SendValidataUrlToMail svu = new SendValidataUrlToMail();
        svu.send(user.getMailAdress(), "北京灵派诺达股份有限公司", url);
        userService.updateUser(user);
        retMap.put("success", true);
        retMap.put("retInfo", "已经向您的邮箱发送一封邮件，请注意查看!");
        return retMap;
    }
    /**
     * 接收验证邮件,如果找到用户，并且验证信息正确，
     * 转发到修改页面。
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("login/activeModifyPasswordRequest.do")
    public @ResponseBody Map<String,Object> activePwdMail(HttpServletRequest request, HttpServletResponse response){
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
            retMap.put("retInfo", "验证码缺失!");
        }else{
            if(user.getValidataSequence().equals(code)){
                user.setUserState(1);
                user.setUserType(1);
                HttpSession session = request.getSession();
                session.removeAttribute(FConstants.SESSION_USER);
                session.setAttribute(FConstants.SESSION_USER, user);
                userService.updateUser(user);
                response.setContentType("text/html; charset=gb2312");
                try {
                	String deployName = request.getContextPath();
                	String redirectUrl = deployName+"/login/modPwd.jsp?modType=2&userName="+user.getUserName();
                    response.sendRedirect(redirectUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                retMap.put("success", false);
                retMap.put("retInfo", "激活码不完整!请从新点击激活链接或从登录页面再次发送激活邮件!");
            }
        }
        return retMap;
    }
    /**
     * 修改密码
     * @param req
     * @return
     */
    @RequestMapping(value="/login/modifyPwd.do")
    public @ResponseBody boolean modifyPwd(HttpServletRequest req){
        HttpSession session =req.getSession();
        User user = (User) session.getAttribute(FConstants.SESSION_USER);
        String pwd = req.getParameter("password");
        user.setPassword(pwd);
        int i = userService.updateUser(user); 
        if(i==1){
            return true;
        }else{
            return false;
        }
    }
    /**
     * 从新发送激活邮件
     */
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
            	//发布名
                String deployName = request.getContextPath();
                //serverPort
                int  serverPort = request.getServerPort();
                //serverName
            	String serverName = request.getServerName();
            	//验证url=serverName+deployName+servletPath
                String validatsaSequence = SequenceUUID.getPureUUID();
                user.setValidataSequence(validatsaSequence);
                String url = "请前往以下地址激活账号\n"+serverName+":"+serverPort+deployName+"/login/activeUser.do?authCode="+user.getUserId()+"~"+validatsaSequence;
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
     * 注册，
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
        String validatsaSequence = SequenceUUID.getPureUUID();
        User user  = new User();
        user.setLoginName(loginName);
        user.setPassword(password);
        user.setMailAdress(mailAdress);
        user.setUserName(userName);
        user.setUserId(SequenceUUID.getPureUUID());
        user.setUserState(0);
        user.setUserType(1);
        user.setValidataSequence(validatsaSequence);
        int rst = userService.insertUser(user);
        if(rst==1){
            String deployName = request.getContextPath();
            int  serverPort = request.getServerPort();
        	String serverName = request.getServerName();
            String url = "请点击以下链接激活绑定邮箱，如果不成功，把链接复制到浏览器地址栏访问\n"
                    + serverName+":"+serverPort+deployName+ "/login/activeUser.do?authCode="+user.getUserId()+"~"+validatsaSequence;
            try{
            	 SendValidataUrlToMail svu = new SendValidataUrlToMail();
                 svu.send(user.getMailAdress(), "北京灵派诺达股份有限公司", url);
                 retMap.put("success", true);
                 retMap.put("retInfo", "已经向您的邮箱发送一封邮件，请激活账号");
                 return retMap; 
            }catch(Exception e){
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
