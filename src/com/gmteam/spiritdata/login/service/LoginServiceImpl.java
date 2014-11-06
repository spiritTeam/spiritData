package com.gmteam.spiritdata.login.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.gmteam.framework.UGA.UgaUser;
import com.gmteam.framework.component.login.service.LoginService;

public class LoginServiceImpl implements LoginService {
    @Override
    public Map<String, Object> beforeUserLogin(HttpServletRequest req) {
        return null;
    }

    @Override
    public Map<String, Object> afterUserLoginOk(UgaUser user, HttpServletRequest req) {
        return null;
    }
}