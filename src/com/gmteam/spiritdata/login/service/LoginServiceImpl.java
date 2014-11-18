package com.gmteam.spiritdata.login.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.dbcp.BasicDataSource;

import com.gmteam.framework.UGA.UgaUser;
import com.gmteam.framework.component.login.service.LoginService;
import com.gmteam.spiritdata.UGA.pojo.User;
import com.gmteam.spiritdata.UGA.service.UserService;

public class LoginServiceImpl implements LoginService {
    @Resource
    private BasicDataSource dataSource;

    @Override
    public Map<String, Object> beforeUserLogin(HttpServletRequest request) {
        Map<String,Object> retMap = new HashMap<String,Object>();
        String requestCC = request.getParameter("checkCode");
        HttpSession session = request.getSession();
        String checkCode = (String) session.getAttribute("RANDOMVALIDATECODEKEY");
        if(checkCode!=null&&checkCode!=""){
            requestCC = requestCC.toUpperCase();
            if(requestCC.equals(checkCode)){
                retMap.put("success", "success");
            }else{
                retMap.put("retInfo", "验证码填写错误，请重新填写");
            } 
        }else{
            retMap.put("success", "success");
        }
        return retMap;
    }
    @Resource
    private UserService userService;
    @Override
    public Map<String, Object> afterUserLoginOk(UgaUser user, HttpServletRequest request) {
        Map<String,Object> retMap = new HashMap<String,Object>();
        //激活邮箱
        User u = (User)user;
        //==0,未发邮箱激活
        if(u.getUserState()==0){
            retMap.put("success", "success");
            retMap.put("activeType",1);
            retMap.put("retInfo", "您未通过邮箱激活账号,请转至邮箱激活,如果验证信息误删，请点击验证邮箱，我们将会重新发送一封验证邮件");
        }else{
            changeOwnerId(request.getSession(), user.getUserId());
            retMap.put("activeType",2);
            retMap.put("retInfo", "登录成功!");
            retMap.put("success", "success");
        }
        return retMap;
    }

    /**
     * 当登录或注册成功后(邮件验证之前)，通过此方法，把之前通过session处理的对象重定位为用户处理
     * @param session 当前Session
     * @param userId 用户Id 用户的id
     */
    public void changeOwnerId(HttpSession session, String userId) {
        Connection conn = null;
        Statement st = null;
        boolean autoCommitFlag = true;
        String sessionId = session.getId();

        try {
            conn = dataSource.getConnection();
            autoCommitFlag = conn.getAutoCommit();
            conn.setAutoCommit(false);
            //修改信息
            st = conn.createStatement();
            st.execute("update sa_imp_log set ownerId='"+userId+"', ownerType=1 where ownerId='"+sessionId+"' and ownerType=2");
            st.execute("update sa_md_tabmodel set ownerId='"+userId+"', ownerType=1 where ownerId='"+sessionId+"' and ownerType=2");
            st.execute("update sa_md_tabmap_org set ownerId='"+userId+"', ownerType=1 where ownerId='"+sessionId+"' and ownerType=2");
            st.execute("update plat_dictm set ownerId='"+userId+"', ownerType=1 where ownerId='"+sessionId+"' and ownerType=2");
            //修改文件夹信息
            conn.commit();
            conn.setAutoCommit(autoCommitFlag);
        } catch (Exception e) {
            if (conn!=null) {
                try {
                    conn.rollback();
                    conn.setAutoCommit(autoCommitFlag);
                } catch (SQLException sqlE) {
                    sqlE.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            try { if (st!=null) {st.close();st = null;} } catch (Exception e) {e.printStackTrace();} finally {st = null;};
            try { if (conn!=null) {conn.close();conn = null;} } catch (Exception e) {e.printStackTrace();} finally {conn = null;};
        }
    }
}
