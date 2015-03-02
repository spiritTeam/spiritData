package com.spiritdata.framework.component.login.web;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.UGA.UgaUser;
import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.component.login.pojo.UserLogin;
import com.spiritdata.framework.util.JsonUtils;

public class LoginFilter implements Filter {
    private static Logger logger = Logger.getLogger(LoginFilter.class);
    private String ingores;
    private String noLogin;
    private String hasNewLogin;
    private String errorPage;

    public void init(FilterConfig config) throws ServletException {
        this.ingores = config.getInitParameter("ingores");
        this.noLogin = config.getInitParameter("noLogin");
        this.hasNewLogin = config.getInitParameter("hasNewLogin");
        this.errorPage = config.getInitParameter("errorPage");
    }

    public void doFilter(ServletRequest req, ServletResponse res,FilterChain  chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;
        HttpSession session = request.getSession();
        String path=request.getServletPath();
        try {
            String ingoresArray[] = ingores.split(",");
            if (isIngore(path, ingoresArray)) chain.doFilter(req, res);
            else if (path.endsWith(".css")||path.endsWith(".js")||path.endsWith(".json")) chain.doFilter(req, res);
            else if (session.getAttribute(FConstants.SESSION_USER)!=null) {
                //判断是否用其他Sesson登录了
                CacheEle<Map<String, UserLogin>> userSessionMap = (CacheEle<Map<String, UserLogin>>)SystemCache.getCache(FConstants.USERSESSIONMAP);
                UgaUser user = (UgaUser)session.getAttribute(FConstants.SESSION_USER);
                UserLogin uli = userSessionMap.getContent().get(user.getUserId());
                if (uli!=null&&!uli.getSessionId().equals(session.getId())) {
                    String loginInfo = "";
                    loginInfo += "&clientIp="+uli.getClientIp();
                    loginInfo += "&clientMacAddr="+uli.getClientMacAddr();
                    loginInfo += "&browser="+uli.getBrowser();
                    loginInfo += "&"+request.getQueryString();
                    response.sendRedirect(request.getContextPath()+hasNewLogin+"?"+loginInfo.substring(1));
                } else chain.doFilter(req, res);
            } else {
                String newUrl = request.getContextPath()+noLogin;
                newUrl = (newUrl.indexOf("?")==-1?newUrl+"?"+request.getQueryString():newUrl+"&"+request.getQueryString());
                response.sendRedirect(newUrl);
            }
        } catch (Exception e) {
            logger.error("登录验证过滤器产生异常：",e);
            Map<String, String> errorInfo= new HashMap<String, String>();
            errorInfo.put("type", "error");
            errorInfo.put("title", "登录验证过滤器产生异常");
            //StringPrintWriter strintPrintWriter = new StringPrintWriter();
            //e.printStackTrace(strintPrintWriter);
            //errorInfo.put("message", strintPrintWriter.toString().replaceAll("<%", "<％").replaceAll("%>", "％>").replaceAll("\r\n", "").replaceAll("\r", "").replaceAll("\n", ""));
            errorInfo.put("message", e.getMessage());
            errorInfo.put("nextPage", request.getContextPath()+noLogin);
            request.setAttribute("errorJson", JsonUtils.objToJson(errorInfo));
            request.getRequestDispatcher(errorPage).forward(request,response);
        }
    }

    private boolean isIngore(String path,String ingores[]) {
        String _path=(path.indexOf("?")>0)?path.substring(0, path.indexOf("?")):path;
        _path=(_path.indexOf(".do")>0)?(_path.indexOf("!")>0?(_path.substring(0,_path.indexOf("!"))+".do"):_path):_path;
        for (int i=0; i<ingores.length; i++) {
            String ingore=ingores[i];
            if(_path.indexOf(ingore)>=0 ) return true;
        }
        return false;
    }

    public void destroy() {}
}