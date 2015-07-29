package com.spiritdata.dataanal.login.web;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.dataanal.UGA.pojo.User;
import com.spiritdata.dataanal.UGA.service.UserService;
import com.spiritdata.dataanal.exceptionC.Dtal1101CException;
import com.spiritdata.dataanal.exceptionC.Dtal1102CException;
import com.spiritdata.dataanal.exceptionC.Dtal1103CException;
import com.spiritdata.dataanal.exceptionC.Dtal1104CException;
import com.spiritdata.dataanal.login.checkImage.mem.CheckImageMemoryService;
import com.spiritdata.dataanal.login.checkImage.mem.OneCheckImage;
import com.spiritdata.dataanal.login.util.SendValidataUrlToMail;

/**
 * 用于登录和注册
 * @author mht
 */
@Controller
public class RegisterController {
	private String mainPage = "/index/analIndex.jsp";
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
        if (user==null) {
            retInfo = "不存在账号为["+loginName+"]的用户";
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
        if (suser!=null) {
            suser.setUserState(3);
            suser.setValidataSequence(validatsaSequence);
        }
        try {
            userService.updateUser(user);
            //发布名
            String serverName = "http://www.0pidata.com";
            String mailMessage = "请访问以下地址以修改密码：\n"+serverName+"/login/activeModifyPassword.do?authCode="+user.getUserId()+"~"+validatsaSequence;
            SendMail sendMail = new SendMail(user.getMailAdress(), mailMessage);
            sendMail.start();
            retMap.put("success", true);
            retInfo = "已向账号["+user.getLoginName()+"]所注册的邮箱发送一封用于找回密码的邮件!";
            retMap.put("retInfo", retInfo);
            return retMap;
        } catch (Exception e) {
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
        if (user==null) {
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
                HttpSession session = request.getSession();
                User userInfo = ((User)session.getAttribute(FConstants.SESSION_USER));
                if (userInfo!=null) {
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
    protected String dwMEXException(MessagingException mex) {
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
     * 用户找回密码：接收验证邮件,如果找到用户，并且验证信息正确，转发到修改页面。
     * @return 暂时未return，而是重定向到login页面
     */
    @RequestMapping("login/activeModifyPassword.do")
    public @ResponseBody Map<String,Object> activeModifyPasswordMail(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> retMap = new HashMap<String,Object>();
        String authCode = request.getParameter("authCode");
        String retInfo = "";
        Exception ee = null;
        if (authCode==null) retInfo = "连接非法，无法找回密码!";
        else {
            String userId = authCode.substring(0,authCode.lastIndexOf("~"));
            String code = authCode.substring(authCode.lastIndexOf("~")+1);
            User user = userService.getUserById(userId);
            if (user==null) retInfo = "没有对应的账号，无法找回密码!";
            else {
                if (!user.getValidataSequence().equals(code)) retInfo = "验证码不匹配，无法找回密码!";
                else {
                    //为安全起见，把用户改为失效状态
                    user.setUserState(2);
                    userService.updateUser(user);
                    //在重定向的基础上修改为转发
                    String actionUrl = "/login/modifyPassword.jsp?modifyType=1&loginName="+user.loginName;
                    request.setAttribute("actionUrl", actionUrl);
                }
            }
        }
        try {
            request.setAttribute("fromModifyUser", "1");
            if (!retInfo.equals("")) {
                retMap.put("success", false);
                ee = new Dtal1101CException(retInfo);
                retMap.put("retInfo", ee.getMessage());
                request.setAttribute("retMap", retMap);
            }
            request.getRequestDispatcher(mainPage).forward(request, response);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            retMap.put("success", false);
            retInfo = "未知错误！";
            ee = new Dtal1101CException(retInfo);
            retMap.put("retInfo",ee.getMessage());
            return retMap;
        }//转发到apage.jsp
    }
    /**
     * 修改密码
     */
    @RequestMapping(value="/login/modifyPassword.do")
    public @ResponseBody Map<String,Object> modifyPassword(HttpServletRequest request){
        Map<String,Object> retMap = new HashMap<String,Object>();
        String retInfo = "";
        String loginName = request.getParameter("loginName");
        User user = userService.getUserByLoginName(loginName);
        String password = request.getParameter("password");
        Exception ee = null;
        if (user==null) {
            retInfo = "找不到对应的用户，无法修改密码!";
            ee = new Dtal1102CException(retInfo);
            retMap.put("success", false);
            retMap.put("retInfo", ee.getMessage());
        } else {
            if (user.getUserState()==2) {
                user.setPassword(password);
                user.setUserState(1);
                user.setValidataSequence(SequenceUUID.getPureUUID());
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
            } else {
                retInfo = "用户状态不合法，无法修改密码!";
                ee = new Dtal1102CException(retInfo);
                retMap.put("success", false);
                retMap.put("retInfo", ee.getMessage());
            }
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
                retInfo = "不存在登录名为["+loginName+"]的用户！";
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
                    String deployName = request.getContextPath();
                    int  serverPort = request.getServerPort();
                    String serverName = request.getServerName();
                    String mailMessage = "请点击以下链接激活账号：\n"
                            + serverName+":"+serverPort+deployName+ "/login/activeUser.do?authCode="+user.getUserId()+"~"+user.getValidataSequence()
                            + "\n或把以上链接复制到浏览器地址栏，以激活帐号。";
                    //调用发送邮件线程减少前台相应时间
                    SendMail sendMail = new SendMail(user.getMailAdress(),mailMessage);
                    sendMail.start();
                    //更新邮箱并储存
                    if (!user.getMailAdress().equals(newMail)) user.setMailAdress(newMail);
                    userService.updateUser(user);
                    retMap.put("success", true);
                    retInfo = "‘激活连接’已发至您所注册的邮箱！";
                    retMap.put("retInfo", retInfo);
                    return retMap;
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
    public @ResponseBody Map<String,Object> activeUser(HttpServletRequest request, HttpServletResponse response){
        String authCode = request.getParameter("authCode");
        String aciveFlag = "0"; //状态：
        String deployName = request.getContextPath();
        String retInfo = "";

        Map<String,Object> retMap = new HashMap<String,Object>();

        Exception ee = null;
        if (authCode==null) {
            retInfo = "激活码不完整！请重新访问激活链接或从在登录页面用未激活帐号登录后再次发送激活邮件！";
            ee = new Dtal1101CException(retInfo);
            retMap.put("success", false);
            retMap.put("retInfo", ee.getMessage());
        }
        String userId = authCode.substring(0,authCode.lastIndexOf("~"));
        String code = authCode.substring(authCode.lastIndexOf("~")+1);
        User user = userService.getUserById(userId);
        if (user==null) {
            retInfo = "不存在用户Id为["+userId+"]的用户！";
            ee = new Dtal1104CException(retInfo);
            retMap.put("success", false);
            retMap.put("retInfo", ee.getMessage());
        } else if (user.getUserState()==0) {
            if (user.getValidataSequence().equals(code)) {
                user.setUserState(1);
                user.setUserType(1);
                userService.updateUser(user);
                retInfo = "激活成功！";
                retMap.put("success", true);
                retMap.put("retInfo",retInfo);
                //uT=1,表示激活成功过来的账号，uT=2表示修改密码成功后跳转的，ut=3表示正常的跳转的
                aciveFlag="1";//成功激活
            } else {
                aciveFlag="2";//激活码不正常
                retInfo = "激活码不正常！";
                ee = new Dtal1101CException(retInfo);
                retMap.put("success", false);
                retMap.put("retInfo", ee.getMessage());
            }
        } else {
            aciveFlag="3";//已激活，无需再次激活
            retInfo = "已激活，无需再次激活！";
            ee = new Dtal1101CException(retInfo);
            retMap.put("success", false);
            retMap.put("retInfo", ee.getMessage());
        }

        try {
            String _url = deployName+"/login.do?activeFlag="+aciveFlag;
            if (aciveFlag.equals("1")||aciveFlag.equals("3")) {
                _url += "&loginName="+user.getLoginName()+"&password="+user.getPassword();
                response.sendRedirect(_url);
            } else {
                response.sendRedirect(deployName+"/index/analIndex.jsp?activeFlag="+aciveFlag);
            }
        } catch(Exception e) {
            retInfo = "激活后页面跳转异常！";
            retMap.put("success", false);
            retMap.put("retInfo", e.getMessage());
        }
        return retMap;
    }

    /**
     * 从内存获得一个新的验证码
     */
    @RequestMapping("login/getNewCheckImage.do")
    public @ResponseBody Map<String,Object> getNewCheckImage(HttpServletRequest request, HttpServletResponse response) {
        Map<String,Object> retMap = new HashMap<String,Object>();
        try {
            CheckImageMemoryService cims = CheckImageMemoryService.getInstance();
            OneCheckImage oci = cims.getOneCheckImage();
            request.getSession().setAttribute("BufferImage", oci.getCheckImage());
            retMap.put("success", true);
            retMap.put("checkCode", oci.getCheckCode());
        } catch (Exception e) {
            retMap.put("success", false);
            retMap.put("retInfo", e.getMessage());
            e.printStackTrace();
        }
        return retMap;
    }
    @RequestMapping("login/drawCheckImage.do")
    public void drawCheckImage(HttpServletRequest request, HttpServletResponse response) {
        //禁止图像缓存
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");

        ServletOutputStream sos=null;
        try {
            BufferedImage bi = (BufferedImage)request.getSession().getAttribute("BufferImage");
            if (bi==null) {
                CheckImageMemoryService cims = CheckImageMemoryService.getInstance();
                bi = (cims.getCheckImage(request.getParameter("checkCode"))).getCheckImage();
            }
            sos = response.getOutputStream();
            if (bi!=null) ImageIO.write(bi, "jpeg", sos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sos!=null) {
                try {
                    sos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
                if (user!=null) retInfo+= "<br/>["+loginName+"]账号已被注册。";
                user = userService.getUserByMailAdress(mailAdress);
                if (user!=null) retInfo+= "<br/>["+mailAdress+"]邮箱已被注册。";
                if (retInfo.length()>0) {
                    retMap.put("success", false);
                    ee = new Dtal1104CException(retInfo);
                    retMap.put("retInfo", ee.getMessage());
                    return retMap;
                }
            } catch(Exception e) {
                ee = new Dtal1104CException(retInfo,e);
                return retMap;
            }
            //2-保存
            String validatsaSequence = SequenceUUID.getPureUUID();
            try {
                user = new User();
                user.setLoginName(loginName);
                user.setPassword(password);
                user.setMailAdress(mailAdress);
                user.setUserName(userName);
                user.setUserId(SequenceUUID.getPureUUID());
//              user.setUserState(0);
                user.setUserState(1); //默认激活
                user.setUserType(1);
                user.setValidataSequence(validatsaSequence);
                int rst = userService.insertUser(user);
                if(rst==1){
                    //去掉邮件确认的功能
//                    String deployName = request.getContextPath();
//                    int  serverPort = request.getServerPort();
//                    String serverName = request.getServerName();
//                    String mailMessage = "请点击以下链接激活账号：\n"
//                            + serverName+":"+serverPort+deployName+ "/login/activeUser.do?authCode="+user.getUserId()+"~"+user.getValidataSequence()
//                            + "\n或把以上链接复制到浏览器地址栏，以激活帐号。";
//                    //调用发送邮件线程减少前台相应时间
//                    SendMail sendMail = new SendMail(user.getMailAdress(),mailMessage);
//                    sendMail.start();
                    retMap.put("success", true);
//                    retInfo = "注册成功！<br/>‘激活连接’已发至您所注册的邮箱！";
                    retInfo = "注册成功！";
                    retMap.put("retInfo", retInfo);
                    return retMap;
                }else{
                    retMap.put("success", false);
                    retInfo = "注册失败，请稍后重试！";
                    retMap.put("retInfo", retInfo);
                    return retMap;
                }
            } catch(Exception e) {
                retMap.put("success", false);
                retInfo = e.getMessage();
                ee = new Dtal1102CException(retInfo);
                retMap.put("retInfo", retInfo);
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

/**
 * 用于发送邮件，减少等待时间
 * @author mht
 */
class SendMail extends Thread{
    //邮件地址
    String mailAdress;
    //邮件内容
    String mailMessage;
    /**
     * @param mailAdress 发送地址
     * @param mailMessage 发送信息
     */
    public SendMail(String mailAdress,String mailMessage){
        this.mailMessage = mailMessage;
        this.mailAdress = mailAdress;
    }
    public void run(){
        SendValidataUrlToMail svu = new SendValidataUrlToMail();
        try {
            svu.send(mailAdress, "北京灵派诺达股份有限公司", mailMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}