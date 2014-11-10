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
        requestCC = requestCC.toUpperCase();
        HttpSession session = request.getSession();
        String checkCode = (String) session.getAttribute("RANDOMVALIDATECODEKEY");
        if(requestCC.equals(checkCode)){
            retMap.put("success", "success");
            return retMap;
        }
        return null;
    }
    @Resource
    private UserService userService;
    @Override
    public Map<String, Object> afterUserLoginOk(UgaUser user, HttpServletRequest request) {
        Map<String,Object> retMap = new HashMap<String,Object>();
        //changeOwnerId(request.getSession(), user.getUserId());
        //激活邮箱
        User u = (User)user;
        //==0,未发邮箱激活
        if(u.getUserState()==0){
            retMap.put("retInfo", "您未通过邮箱激活账号,请转至邮箱激活,如果邮箱丢失，请点击验证邮箱，我们将会从新发送一封验证邮件");
        }else{
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
