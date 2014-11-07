package com.gmteam.spiritdata.login.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.gmteam.framework.UGA.UgaUser;
import com.gmteam.framework.component.login.service.LoginService;

public class LoginServiceImpl implements LoginService {
    @Override
    public Map<String, Object> beforeUserLogin(HttpServletRequest request) {
        Map<String,Object> retMap = new HashMap<String,Object>();
        String requestCC = request.getParameter("checkCode");
        HttpSession session = request.getSession();
        String checkCode = (String) session.getAttribute("RANDOMVALIDATECODEKEY");
        System.out.println(requestCC+checkCode);
        if(requestCC.equals(checkCode)){
            retMap.put("success", "success");
            return retMap;
        }
        return null;
    }

    @Override
    public Map<String, Object> afterUserLoginOk(UgaUser user, HttpServletRequest req) {
        return null;
    }
}