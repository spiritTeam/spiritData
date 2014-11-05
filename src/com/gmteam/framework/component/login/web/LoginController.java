package com.gmteam.framework.component.login.web;

import com.gmteam.framework.UGA.UgaAuthorityService;
import com.gmteam.framework.UGA.UgaUser;
import com.gmteam.framework.UGA.UgaUserService;
import com.gmteam.framework.component.login.pojo.UserLogin;
import com.gmteam.framework.component.login.service.LoginServiceImpl;
import com.gmteam.framework.core.cache.SystemCache;
import com.gmteam.framework.core.model.tree.TreeNode;
import com.gmteam.framework.core.web.SessionLoader;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController
{

  @Resource
  private UgaUserService ugaUserService;

  @Resource
  private SessionLoader sessionLoader1;

  @Resource
  private UgaAuthorityService ugaAuthorityService;

  @Resource
  private LoginServiceImpl loginServiceImpl;

  @SuppressWarnings({ "unused", "unchecked" })
@RequestMapping({"login.do"})
  @ResponseBody
  public Map<String, Object> Login(UserLogin userLogin, HttpServletRequest req)
  {
    Map<String,Object> retObj = new HashMap<String,Object>();
    try {
      HttpSession session = req.getSession();
      Map<String,Object> beforeM = null;
      try {
        beforeM = this.loginServiceImpl.beforeUserLogin(req);
      } catch (Exception e) {
        if (beforeM == null) {
          beforeM = new HashMap<String,Object>();
          beforeM.put("success", "success");
        }
      }
      if (beforeM.get("success") != null)
      {
        UgaUser user = this.ugaUserService.getUserByLoginName(userLogin.getLoginName());
        if (user == null) {
          retObj.put("type", "2");
          retObj.put("data", "没有登录名为[" + userLogin.getLoginName() + "]的用户！");
        } else if (!userLogin.getPassword().equals(user.getPassword())) {
          retObj.put("type", "2");
          retObj.put("data", "密码不匹配！");
        } else {
          Map<String,Object> afterM = null;
          try {
              afterM = this.loginServiceImpl.afterUserLoginOk(user, req);
          } catch (Exception e) {
            if (afterM == null) {
              afterM = new HashMap<String,Object>();
              afterM.put("success", "success");
            }
          }
          if (afterM.get("success") != null)
          {
            UserLogin oldUserLogin = (UserLogin)((Map)SystemCache.getCache("userSessionMap").getContent()).remove(user.getUserId());
            userLogin.setSessionId(session.getId());
            ((Map)SystemCache.getCache("userSessionMap").getContent()).put(user.getUserId(), userLogin);

            session.setAttribute("userInfo", user);

            TreeNode um = this.ugaAuthorityService.getUserModuleAuthByUserId(user.getUserId());
            session.setAttribute("userAuthority", um);
            retObj.put("type", "1");
            retObj.put("data", "登录成功");
          } else {
            retObj.put("type", "-1");
            retObj.put("data", afterM);
          }
        }

        this.sessionLoader1.setSession(session);
        this.sessionLoader1.loader();
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

  @RequestMapping({"logout.do"})
  @ResponseBody
  public Map<String, Object> logout(HttpServletRequest req)
  {
    Map retObj = new HashMap();
    try
    {
      Map userSessionMap = (Map)SystemCache.getCache("userSessionMap").getContent();
      HttpSession session = req.getSession();
      UgaUser user = (UgaUser)session.getAttribute("userInfo");
      UserLogin userLogin = (UserLogin)userSessionMap.get(user.getUserId());
      if ((userLogin != null) && (userLogin.getSessionId().equals(session.getId()))) userSessionMap.remove(user.getUserId());

      session.removeAttribute("userInfo");

      retObj.put("type", "1");
      retObj.put("data", null);
    } catch (Exception e) {
      retObj.put("type", "-1");
      retObj.put("data", e.getMessage());
    }
    return retObj;
  }
}