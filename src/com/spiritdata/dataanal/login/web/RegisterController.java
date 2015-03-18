package com.spiritdata.dataanal.login.web;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.util.FileUtils;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.dataanal.UGA.pojo.User;
import com.spiritdata.dataanal.UGA.service.UserService;
import com.spiritdata.dataanal.exceptionC.Dtal1101CException;
import com.spiritdata.dataanal.exceptionC.Dtal1102CException;
import com.spiritdata.dataanal.exceptionC.Dtal1103CException;
import com.spiritdata.dataanal.exceptionC.Dtal1104CException;
import com.spiritdata.dataanal.login.util.RandomValidateCode;
import com.spiritdata.dataanal.login.util.SendValidataUrlToMail;

/**
 * 用于登陆和注册
 * @author mht
 */
@Controller
public class RegisterController {
    /**
     * 发送重设密码的验证邮件
     */
    @Resource
    private UserService userService;
    @RequestMapping(value="/login/sendBackPasswordMail.do")
    public @ResponseBody Map<String,Object> sendBackPasswordMail(HttpServletRequest request){
        Map<String,Object> retMap = new HashMap<String,Object>();
        String loginName = request.getParameter("loginName");
        User user = userService.getUserByLoginName(loginName);
        String retInfo = "";
        Exception ee = null;
        if (user==null||user.equals("")) {
            retInfo = "没有账号为"+loginName+"的用户";
            ee = new Dtal1104CException(retInfo);
            retMap.put("success", false);
            retMap.put("retInfo", ee.getMessage());
            return retMap;
        }
        if (user.getUserState() == 0) {
            retInfo = "您的账号还未激活，请先激活！";
            ee = new Dtal1104CException(retInfo);
            retMap.put("success", false);
            retMap.put("retInfo", ee.getMessage());
            return retMap;
        }
        String validatsaSequence = SequenceUUID.getPureUUID();
        user.setValidataSequence(validatsaSequence);
        user.setUserState(3);
        User suser = (User) request.getSession().getAttribute("FConstants.SESSION_USER");
        if (suser!=null&&!suser.equals("")) {
            suser.setUserState(3);
            suser.setValidataSequence(validatsaSequence);
        }
        try {
            userService.updateUser(user);
            //发布名
            String deployName = request.getContextPath();
            //serverPort
            int serverPort = request.getServerPort();
            //serverName
            String serverName = request.getServerName();
            //验证url=serverName+deployName+servletPath
            String url = "请前往以下地址修改密码\n"+serverName+":"+serverPort+deployName+"/login/activeModifyPassword.do?authCode="+user.getUserId()+"~"+validatsaSequence;
            SendValidataUrlToMail svu = new SendValidataUrlToMail();
            svu.send(user.getMailAdress(), "北京灵派诺达股份有限公司", url);
            retMap.put("success", true);
            retInfo = "已经向您的邮箱发送一封邮件，请注意查看!";
            retMap.put("retInfo", retInfo);
            return retMap;
        } catch (MessagingException mex) {
            retInfo = dwMEXException(mex);
            ee = new Dtal1103CException(retInfo);
            retMap.put("success", false);
            retMap.put("retInfo", ee.getMessage());
            return retMap;
        }
    }
    /**
     * 更新用户信息
     */
    @RequestMapping("/login/update.do")
    public @ResponseBody Map<String,Object> update(HttpServletRequest request){
        Map<String,Object> retMap = new HashMap<String,Object>();
        String loginName = request.getParameter("loginName");
        String password = request.getParameter("password");
        String userName = request.getParameter("userName");
        String mailAdress = request.getParameter("mailAdress");
        User user = userService.getUserByLoginName(loginName);
        String retInfo = "";
        Exception ee = null;
        if (user==null||user.equals("")) {
            retMap.put("success", false);
            retInfo = "修改异常,请重试,未找到账号为"+loginName+"的用户，请重新填写账号！";
            ee = new Dtal1104CException("为找到该账号对应的用户！");
            retMap.put("retInfo", ee.getMessage());
            return retMap;
        } else {
            user.setPassword(password);
            user.setMailAdress(mailAdress);
            user.setUserName(userName);
            int rst = userService.updateUser(user);
            if (rst==1) {
                String toDeletURI = (String)(SystemCache.getCache(FConstants.APPOSPATH)).getContent()+"/checkCodeImges/"+request.getSession().getId();
                FileUtils.deleteFile(new File(toDeletURI));
                HttpSession session = request.getSession();
                User userInfo = ((User)session.getAttribute(FConstants.SESSION_USER));
                if (userInfo!=null&&!userInfo.equals("")) {
                    userInfo.setPassword(password);
                    userInfo.setMailAdress(mailAdress);
                    userInfo.setUserName(userName);
                }
                retMap.put("success", true);
                retMap.put("retInfo", "修改成功");
                return retMap;
            } else {
                retInfo = "保存用户信息失败!";
                ee = new Dtal1102CException(retInfo);
                retMap.put("success", false);
                retMap.put("retInfo", ee.getMessage());
                return retMap;
            }
        }
    }
    /**
     * 处理发送邮件不成功的异常,暂时只支持单一邮件发送
     * @param mex MessagingException异常
     * @return 返回错误信息
     */
    private String dwMEXException(MessagingException mex) {
        Exception ex = mex;
        String retInfo = "";
        ex.printStackTrace();
        if ((ex!=null)&& (ex instanceof SendFailedException)) {
            SendFailedException sfex = (SendFailedException)ex;
            //无效的地址
            Address addres;
            Address[] invalid = sfex.getInvalidAddresses();
            if (invalid != null&&invalid.length>0) {
                System.out.println("    ** Invalid Addresses");
                addres = invalid[0];
                System.out.println("         " + addres);
                retInfo = "无效的邮箱地址\""+addres+"\"！";
            }
            //有效的地址，但是消息没发送成功。
            Address[] validUnsent = sfex.getValidUnsentAddresses();
            if (validUnsent != null&&validUnsent.length>0&&validUnsent[0]!=null) {
                addres = validUnsent[0];
                System.out.println("    ** ValidUnsent Addresses");
                System.out.println("         " + addres);
                retInfo = "邮箱地址\""+addres+"\"是有效的，但可能是由于网络或其他原因造成发送失败！";
            }
            //返回消息发送成功的地址，这个if可能不会走到，因为只有现仅针对一个单邮件
            Address[] validSent = sfex.getValidSentAddresses();
            if (validSent != null&&validSent.length>0&&validSent[0]!=null) {
                addres = validSent[0];
                System.out.println("    ** ValidSent Addresses");
                System.out.println("         " + addres);
                retInfo = "已成功向邮箱\""+addres+"\"发送成功！";
            }
        }
        return retInfo;
    }
    /**
     * 接收验证邮件,如果找到用户，并且验证信息正确，
     * 转发到修改页面。
     * @return 暂时未return，而是重定向到login页面
     */
    @RequestMapping("login/activeModifyPassword.do")
    public @ResponseBody Map<String,Object> activeModifyPasswordMail(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> retMap = new HashMap<String,Object>();
        String authCode = request.getParameter("authCode");
        String retInfo = "";
        Exception ee = null;
        if (authCode==null) {
            retMap.put("success", false);
            retInfo = "激活码不完整!请重新新点击激活链接或从登录页面再次发送激活邮件!";
            ee = new Dtal1101CException(retInfo);
            retMap.put("retInfo", ee.getMessage());
            return retMap;
        }
        String userId = authCode.substring(0,authCode.lastIndexOf("~"));
        String code = authCode.substring(authCode.lastIndexOf("~")+1);
        User user = userService.getUserById(userId);
        if (user==null) {
            retMap.put("success", false);
            retInfo = "验证码缺失!";
            ee = new Dtal1101CException(retInfo);
            retMap.put("retInfo", ee.getMessage());
            return retMap;
        } else {
            if (user.getUserState()==1) {
                retMap.put("success", false);
                retInfo = "链接已失效！";
                ee = new Dtal1101CException(retInfo);
                retMap.put("retInfo", ee.getMessage());
                return retMap;
            } else {
                if (user.getValidataSequence().equals(code)) {
                    user.setUserState(1);
                    user.setValidataSequence("");
                    HttpSession session = request.getSession();
                    User suser = (User) session.getAttribute(FConstants.SESSION_USER);
                    if ( suser!=null&&!suser.equals("")) {
                        suser.setUserState(1);
                        suser.setValidataSequence("");
                    }
                    userService.updateUser(user);
                    try {
                        //在重定向的基础上修改为转发
                        String actionUrl = "/login/modifyPassword.jsp?modifyType=1&loginName="+user.loginName;
                        request.setAttribute("action", "1");
                        request.setAttribute("actionUrl", actionUrl);
                        request.getRequestDispatcher("../asIndex.jsp").forward(request, response);
                        return null;
                    } catch (Exception e) {
                        e.printStackTrace();
                        retMap.put("success", false);
                        retInfo = "未知错误！";
                        ee = new Dtal1101CException(retInfo);
                        retMap.put("retInfo",ee.getMessage());
                        return retMap;
                    }//转发到apage.jsp
                } else {
                    retMap.put("success", false);
                    retInfo = "激活码不完整!请重新点击激活链接或从登录页面再次发送激活邮件!";
                    ee = new Dtal1101CException(retInfo);
                    retMap.put("retInfo",ee.getMessage());
                    return retMap;
                }
            }
        }
    }
    /**
     * 修改密码
     */
    @RequestMapping(value="/login/modifyPassword.do")
    public @ResponseBody Map<String,Object> modifyPassword(HttpServletRequest request){
        Map<String,Object> retMap = new HashMap<String,Object>();
        String retInfo = "";
        HttpSession session =request.getSession();
        String loginName = request.getParameter("loginName");
        User user = (User) session.getAttribute(FConstants.SESSION_USER);
        String password = request.getParameter("password");
        Exception ee = null;
        if (user!=null&&!user.equals("")) user.setPassword(password);
        else user = userService.getUserByLoginName(loginName);
        user.setPassword(password);
        int i = userService.updateUser(user);
        if (i==1) {
            retInfo = "修改密码成功!";
            retMap.put("success", true);
            retMap.put("retInfo", retInfo);
        } else {
            retInfo = "修改密码失败!";
            ee = new Dtal1102CException(retInfo);
            retMap.put("success", false);
            retMap.put("retInfo", ee.getMessage());
        }
        return retMap;
    }
    /**
     * 重新发送激活邮件，并保存修改后的邮箱
     */
    @RequestMapping("login/activeUserAgain.do")
    public @ResponseBody Map<String,Object> activeUserAgain(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> retMap = new HashMap<String,Object>();
        String loginName = request.getParameter("loginName");
        String newMail = request.getParameter("mailAdress");
        User user = userService.getUserByMailAdress(newMail);
        String retInfo = "";
        Exception ee = null;
        if (user==null||user.getLoginName().equals(loginName)) {
        	if (user==null) user = userService.getUserByLoginName(loginName);
            if (user==null) {
                retInfo = "登录名错误！";
                ee = new Dtal1104CException(retInfo);
                retMap.put("success", false);
                retMap.put("retInfo", ee.getMessage());
                return retMap;
            } else {
                if (user.getUserState()==1) {
                    retInfo = "该账号已经激活！";
                    ee = new Dtal1101CException(retInfo);
                    retMap.put("success", true);
                    retMap.put("retInfo", ee.getMessage());
                    return retMap;
                } else {
                    //发布名
                    String deployName = request.getContextPath();
                    //serverPort
                    int serverPort = request.getServerPort();
                    //serverName
                    String serverName = request.getServerName();
                    //验证url=serverName+deployName+servletPath
                    String validatsaSequence = SequenceUUID.getPureUUID();
                    user.setValidataSequence(validatsaSequence);
                    String url = "请前往以下地址激活账号\n"+serverName+":"+serverPort+deployName+"/login/activeUser.do?authCode="+user.getUserId()+"~"+validatsaSequence;
                    SendValidataUrlToMail svu = new SendValidataUrlToMail();
                    try {
                        //更新邮箱并储存
                        if (!user.getMailAdress().equals(newMail)) user.setMailAdress(newMail);
                        userService.updateUser(user);
                        svu.send(newMail, "北京灵派诺达股份有限公司", url);
                        userService.updateUser(user);
                        retMap.put("success", true);
                        retInfo = "已经向您的邮箱发送一封邮件，请激活账号";
                        retMap.put("retInfo", "已经向您的邮箱发送一封邮件，请激活账号");
                        return retMap;
                    } catch (MessagingException mex) {
                        retInfo = dwMEXException(mex);
                        ee = new Dtal1103CException(retInfo);
                        retMap.put("success", true);
                        retMap.put("retInfo", ee.getMessage());
                        return retMap;
                    }
                }
            }
        }else {
        	retInfo = "该邮箱已经被使用！";
            ee = new Dtal1103CException(retInfo);
            retMap.put("success", false);
            retMap.put("retInfo", ee.getMessage());
            return retMap;
        }
    }
    /**
     * 激活新注册用户
     */
    @RequestMapping("login/activeUser.do")
    public @ResponseBody Map<String,Object> activeMail(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> retMap = new HashMap<String,Object>();
        String authCode = request.getParameter("authCode");
        Exception ee = null;
        String retInfo = "";
        if (authCode==null) {
            retInfo = "激活码不完整！请重新新点击激活链接或从登录页面再次发送激活邮件！";
            ee = new Dtal1101CException(retInfo);
            retMap.put("success", false);
            retMap.put("retInfo", ee.getMessage());
        }
        String userId = authCode.substring(0,authCode.lastIndexOf("~"));
        String code = authCode.substring(authCode.lastIndexOf("~")+1);
        User user = userService.getUserById(userId);
        if (user==null) {
            retInfo = "该用户不存在！";
            ee = new Dtal1104CException(retInfo);
            retMap.put("success", false);
            retMap.put("retInfo",ee.getMessage());
        } else {
            if (user.getUserState()==0) {
                if (user.getValidataSequence().equals(code)) {
                    user.setUserState(1);
                    user.setUserType(1);
                    userService.updateUser(user);
                    retInfo = "激活成功！";
                    retMap.put("success", true);
                    retMap.put("retInfo",retInfo);
                    try {
                        String deployName = request.getContextPath();
                        //uT=1,表示激活成功过来的账号，uT=2表示修改密码成功后跳转的，ut=3表示正常的跳转的
                        String redirectUrl = deployName+"/asIndex.jsp?activeSuccess=true";
                        response.sendRedirect(redirectUrl);
                        //在重定向的基础上修改为转发
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    retInfo = "激活码不完整！请重新新点击激活链接或从登录页面再次发送激活邮件！";
                    ee = new Dtal1101CException(retInfo);
                    retMap.put("success", false);
                    retMap.put("retInfo", ee.getMessage());
                }
            } else {
                retInfo = "您的账号已经激活！";
                ee = new Dtal1101CException(retInfo);
                retMap.put("success", false);
                retMap.put("retInfo", ee.getMessage());
            }
        }
        return retMap;
    }
    /**
     * 得到验证码
     */
    @RequestMapping("login/refreshValidateCode.do")
    public @ResponseBody Map<String,Object> refreshValidateCode(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> retMap = new HashMap<String,Object>();
        try {
            RandomValidateCode randomValidateCode = new RandomValidateCode();
            retMap.put("success", true);
            retMap.putAll(randomValidateCode.saveImg2File(request));
            return retMap;
        } catch (Exception e) {
            retMap.put("success", false);
            retMap.put("retInfo", e.getMessage());
            e.printStackTrace();
            return retMap;
        }
    }
    /**
     * 注册:注册后成功后，向用户邮箱发送验证邮件
     */
    @RequestMapping("login/register.do")
    public @ResponseBody Map<String,Object> saveRegisterInfo(HttpServletRequest request) {
        String loginName = request.getParameter("loginName");
        String password = request.getParameter("password");
        String userName = request.getParameter("userName");
        String mailAdress = request.getParameter("mailAdress");
        Map<String,Object> retMap = new HashMap<String,Object>();
        Exception ee = null;
        String retInfo = "";
        try {
            //1-检查
            User user = null;
            try {
                user = userService.getUserByLoginName(loginName);
                if (user!=null) retInfo+= "<br/>["+loginName+"]账号已被使用";
                user = userService.getUserByMailAdress(mailAdress);
                if (user!=null) retInfo+= "<br/>["+mailAdress+"]邮箱已被注册";
                if (retInfo.length()>0) {
                    retMap.put("success", false);
                    ee = new Dtal1104CException(retInfo.substring(5));
                    retMap.put("retInfo", ee.getMessage());
                    return retMap;
                }
            } catch(Exception e) {
                ee = new Dtal1104CException(retInfo,e);
                return retMap;
            }
            //2-保存
            int rst = 0;
            String validatsaSequence = SequenceUUID.getPureUUID();
            try {
                user = new User();
                user.setLoginName(loginName);
                user.setPassword(password);
                user.setMailAdress(mailAdress);
                user.setUserName(userName);
                user.setUserId(SequenceUUID.getPureUUID());
                user.setUserState(0);
                user.setUserType(1);
                user.setValidataSequence(validatsaSequence);
                rst = userService.insertUser(user);
            } catch(Exception e) {
                retMap.put("success", false);
                retInfo = e.getMessage();
                ee = new Dtal1102CException(retInfo);
                retMap.put("retInfo", ee.getMessage());
                return retMap;
            }
            if (rst==1) {
                //删除储存验证码的文件夹
                String toDeletURI = (String)(SystemCache.getCache(FConstants.APPOSPATH)).getContent()+"/checkCodeImges/"+request.getSession().getId();
                FileUtils.deleteFile(new File(toDeletURI));
                String deployName = request.getContextPath();
                int  serverPort = request.getServerPort();
                String serverName = request.getServerName();
                String url = "请点击以下链接激活绑定邮箱，如果不成功，把链接复制到浏览器地址栏访问\n"
                        + serverName+":"+serverPort+deployName+ "/login/activeUser.do?authCode="+user.getUserId()+"~"+validatsaSequence;
                try {
                    SendValidataUrlToMail svu = new SendValidataUrlToMail();
                    svu.send(user.getMailAdress(), "北京灵派诺达股份有限公司", url);
                    retMap.put("success", true);
                    retInfo = "注册成功，已经向您的邮箱发送一封邮件，请登陆邮箱激活账号";
                    retMap.put("retInfo", retInfo);
                    //删除验证码
                    return retMap;
                } catch (MessagingException mex) {
                    retInfo = "注册成功,验证邮箱发送失败，"+dwMEXException(mex);
                    ee = new Dtal1103CException(retInfo);
                    retMap.put("success", false);
                    retMap.put("retInfo", ee.getMessage());
                    return retMap;
                }
            } else {
                retMap.put("success", false);
                retInfo = "注册不成功，请稍后重试！";
                ee = new Dtal1102CException(retInfo);
                retMap.put("retInfo", ee.getMessage());
                return retMap;
            }
        } catch(Exception e) {
            retMap.put("success", false);
            retInfo = "注册失败，请稍后重试！";
            ee = new Dtal1102CException(retInfo);
            retMap.put("retInfo", ee.getMessage());
            return retMap;
        }
    }
}
