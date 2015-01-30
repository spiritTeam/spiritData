package com.spiritdata.framework.component.login.web;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.UGA.UgaAuthorityService;
import com.spiritdata.framework.UGA.UgaModule;
import com.spiritdata.framework.UGA.UgaUser;
import com.spiritdata.framework.UGA.UgaUserService;
import com.spiritdata.framework.component.login.pojo.UserLogin;
import com.spiritdata.framework.component.login.service.LoginService;
import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.core.model.tree.TreeNode;
import com.spiritdata.framework.core.web.SessionLoaderShell;

@Controller
public class LoginController {
    @Resource
    private UgaUserService ugaUserService;
    @Resource
    private SessionLoaderShell sessionLoaderShell;
    @Resource
    private UgaAuthorityService ugaAuthorityService;
    @Resource
    private LoginService loginService;
    
    /**
     * 用户登录
     * @param userLogin 用户登录信息
     * @param req request对象
     * @return 返回登录信息对象
     */
    @RequestMapping("login.do")
    public @ResponseBody Map<String,Object> Login(UserLogin userLogin, HttpServletRequest req) {
        Map<String,Object> retObj = new HashMap<String,Object>();
        try {
            HttpSession session = req.getSession();
            Map<String, Object> beforeM = loginService.beforeUserLogin(req);
            if (beforeM==null||beforeM.get("success")!=null) {
                //用户处理
                UgaUser user = ugaUserService.getUserByLoginName(userLogin.getLoginName());
                if (user==null) {
                    retObj.put("type", "2");
                    retObj.put("data", "没有登录名为["+userLogin.getLoginName()+"]的用户！");
                } else if(!userLogin.getPassword().equals(user.getPassword())) {
                    retObj.put("type", "2");
                    retObj.put("data", "密码不匹配！");
                } else {
                    Map<String, Object> afterM = loginService.afterUserLoginOk(user, req);
                    if (afterM==null||afterM.get("success")!=null) {
                        //设置用户Session缓存
                        UserLogin oldUserLogin = ((CacheEle<Map<String, UserLogin>>)SystemCache.getCache(FConstants.USERSESSIONMAP)).getContent().remove(user.getUserId());
                        userLogin.setSessionId(session.getId());
                        ((CacheEle<Map<String, UserLogin>>) SystemCache.getCache(FConstants.USERSESSIONMAP)).getContent().put(user.getUserId(), userLogin);
                        //写用户信息
                        session.setAttribute(FConstants.SESSION_USER, user);
                        //写用户权限信息
                        TreeNode<UgaModule> um = ugaAuthorityService.getUserModuleAuthByUserId(user.getUserId());
                        session.setAttribute(FConstants.SESSION_USERAUTHORITY, um);
                        retObj.put("type", "1");
                    } else {
                        retObj.put("type", "-1");
                    }
                    retObj.put("data", afterM);
                }
                //SessionLoader处理
                sessionLoaderShell.loader(session);
            } else {
                retObj.put("type", "-1");
                retObj.put("data", beforeM);
            }
        } catch (Exception e) {
            e.printStackTrace();
            retObj.put("type", "-1");
            retObj.put("data", e.getMessage());
        }
        return retObj;
    }
    /**
     * 用户注销
     * @param req
     * @return
     */
    @RequestMapping("logout.do")
    public @ResponseBody Map<String,Object> logout(HttpServletRequest req){
        Map<String, Object> retObj = new HashMap<String, Object>();
        try {
            //清除全局变量中的Session
            Map<String, UserLogin> userSessionMap = ((CacheEle<Map<String, UserLogin>>)SystemCache.getCache(FConstants.USERSESSIONMAP)).getContent();
            HttpSession session = req.getSession();
            UgaUser user = (UgaUser)session.getAttribute(FConstants.SESSION_USER);
            UserLogin userLogin = userSessionMap.get(user.getUserId());
            if (userLogin!=null&&userLogin.getSessionId().equals(session.getId())) userSessionMap.remove(user.getUserId());
            //清除Session
            session.removeAttribute(FConstants.SESSION_USER);
            //清除权限
            session.removeAttribute(FConstants.SESSION_USERAUTHORITY);
            retObj.put("type", "1");
            retObj.put("data", null);
        } catch(Exception e) {
            retObj.put("type", "-1");
            retObj.put("data", e.getMessage());
        }
        return retObj;
    }
}