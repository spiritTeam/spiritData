package com.spiritdata.dataanal.login.service;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.dbcp.BasicDataSource;

import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.UGA.UgaUser;
import com.spiritdata.framework.component.login.service.LoginService;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.util.FileUtils;
import com.spiritdata.dataanal.SDConstants;
import com.spiritdata.dataanal.UGA.pojo.User;
import com.spiritdata.dataanal.exceptionC.Dtal1105CException;
import com.spiritdata.dataanal.task.core.service.TaskManageService;
import com.spiritdata.dataanal.visitmanage.core.service.VisitLogService;

public class LoginServiceImpl implements LoginService {
    @Resource
    private BasicDataSource dataSource;
    @Resource
    private TaskManageService tmService;
    @Resource
    private VisitLogService vlService;

    @Override
    public Map<String, Object> beforeUserLogin(HttpServletRequest request) {
        return null;
    }

    @Override
    public Map<String, Object> afterUserLoginOk(UgaUser user, HttpServletRequest request) {
        Map<String,Object> retMap = new HashMap<String,Object>();
        try {
            String activeFlag = request.getParameter("activeFlag");
            //激活邮箱
            User u = (User)user;
            String activeUrl = request.getContextPath()+"/index/analIndex.jsp?activeFlag=";
            //==0,未发邮箱激活
            if (u.getUserState()==0) {
                retMap.put("activeFlag",0);
                retMap.put("retInfo", "账号还未激活！");
                retMap.put("user", user);
                //处理激活
                if (activeFlag!=null) retMap.put("redirectUrl", activeUrl+"0");
            } else {
                changeOwnerId(request.getSession(), user.getUserId());
                String toDeletURI = (String)(SystemCache.getCache(FConstants.APPOSPATH)).getContent()+"/checkCodeImges/"+request.getSession().getId();
                FileUtils.deleteFile(new File(toDeletURI));
                retMap.put("activeFlag",1);
                retMap.put("retInfo", "登录成功！");
                retMap.put("success", "success");
                //处理激活
                if (activeFlag!=null) retMap.put("redirectUrl", activeUrl+activeFlag);
            }
        } catch(Exception e) {
            retMap.put("success", "false");
            retMap.put("retInfo", e.getMessage());
        }
        return retMap;
    }

    /**
     * 当登录或注册成功后(邮件验证之前)，通过此方法，把之前通过session处理的对象重定位为用户处理
     * @param session 当前Session
     * @param userId 用户Id 用户的id
     */
    private void changeOwnerId(HttpSession session, String newOwnerId) {
        Connection conn = null;
        Statement st = null;
        boolean autoCommitFlag = true;
        String sessionId = session.getId();
        try {
            //更新持久化部分
            conn = dataSource.getConnection();
            autoCommitFlag = conn.getAutoCommit();
            conn.setAutoCommit(false);
            //修改信息
            st = conn.createStatement();
            st.execute("update plat_dictm set ownerId='"+newOwnerId+"', ownerType=1 where ownerId='"+sessionId+"' and ownerType=2");
            st.execute("update sa_file_index set ownerId='"+newOwnerId+"', ownerType=1 where ownerId='"+sessionId+"' and ownerType=2");
            st.execute("update sa_md_tabmodel set ownerId='"+newOwnerId+"', ownerType=1 where ownerId='"+sessionId+"' and ownerType=2");
            st.execute("update sa_md_tabmap_rel set ownerId='"+newOwnerId+"', ownerType=1 where ownerId='"+sessionId+"' and ownerType=2");
            st.execute("update sa_report_info set ownerId='"+newOwnerId+"', ownerType=1 where ownerId='"+sessionId+"' and ownerType=2");
            conn.commit();
            conn.setAutoCommit(autoCommitFlag);
            //修改任务所有者，这里之所以不按照上面的方式去处理是因为：以上的内容都缓存在Session中，而task缓存在TaskMemery中
            tmService.changeOwnerId(session.getId(), newOwnerId);
            vlService.changeOwnerId(session.getId(), newOwnerId);
        } catch (Exception e) {
            if (conn!=null) {
                try {
                    conn.rollback();
                    conn.setAutoCommit(autoCommitFlag);
                } catch (SQLException sqlE) {
                    throw new Dtal1105CException(sqlE.getMessage(), e);
                }
            }
            throw new Dtal1105CException(e);
        } finally {
            try { if (st!=null) {st.close();st = null;} } catch (Exception e) {e.printStackTrace();} finally {st = null;};
            try { if (conn!=null) {conn.close();conn = null;} } catch (Exception e) {e.printStackTrace();} finally {conn = null;};
        }
    }

    @Override
    public Map<String, Object> onLogout(HttpServletRequest req) {
        Map<String,Object> retMap = new HashMap<String,Object>();
        try {
            req.getSession().removeAttribute(SDConstants.SESSION_OWNER_RMDUNIT);
            req.getSession().removeAttribute(SDConstants.SESSION_OWNER_DICT);
            req.getSession().removeAttribute(SDConstants.SESSION_HAD_UPLOAD);
            retMap.put("retInfo", "注销成功!");
            retMap.put("success", "success");
        } catch(Exception e) {
            retMap.put("success", "false");
            retMap.put("retInfo", e.getMessage());
        }
        return retMap;
    }
}