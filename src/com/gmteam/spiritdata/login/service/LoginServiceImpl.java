package com.gmteam.spiritdata.login.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.dbcp.BasicDataSource;

import com.gmteam.framework.UGA.UgaUser;
import com.gmteam.framework.component.login.service.LoginService;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataColumn;
import com.gmteam.spiritdata.metadata.relation.pojo.QuotaColumn;
import com.gmteam.spiritdata.metadata.relation.pojo.QuotaTable;
import com.gmteam.spiritdata.util.SequenceUUID;

public class LoginServiceImpl implements LoginService {
    @Resource
    private BasicDataSource dataSource;

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
        Map<String,Object> retMap = new HashMap<String,Object>();

        changeOwnerId(req.getSession(), user.getUserId());

        retMap.put("success", "success");
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