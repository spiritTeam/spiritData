package com.gmteam.spiritdata.login.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.gmteam.framework.UGA.UgaUser;
import com.gmteam.framework.component.login.service.LoginService;
import com.gmteam.spiritdata.UGA.pojo.User;
import com.gmteam.spiritdata.login.util.SendValidataUrlToMail;

public class LoginServiceImpl implements LoginService {
    @Override
    public Map<String, Object> beforeUserLogin(HttpServletRequest request) {
        Map<String,Object> retMap = new HashMap<String,Object>();
        String requestCC = request.getParameter("checkCode");
        HttpSession session = request.getSession();
        String checkCode = (String) session.getAttribute("RANDOMVALIDATECODEKEY");
        if(requestCC.equals(checkCode)){
            retMap.put("success", "success");
            return retMap;
        }
        return null;
    }
    @Override
    public Map<String, Object> afterUserLoginOk(UgaUser user, HttpServletRequest req) {
        User u = (User)user;
        if(u.getUserState()==0){
            String url = "请点击以下链接激活:http://localhost:8080/sa/activeMail.do";
            SendValidataUrlToMail svu = new SendValidataUrlToMail();
            svu.send("jiao80496263@163.com", "北京灵派诺达", "测试BBB");
        }
        return null;
    }
}