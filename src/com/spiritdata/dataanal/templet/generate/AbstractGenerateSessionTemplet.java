package com.spiritdata.dataanal.templet.generate;

import java.util.Map;

import javax.servlet.http.HttpSession;


/**
 * 以Session为基础的
 * @author wh
 */
public abstract class AbstractGenerateSessionTemplet extends AbstractGenerateTemplet {
    private HttpSession session;//session用来缓存与该会话相关的信息
    public HttpSession getSession() {
        return session;
    }
    public void setSession(HttpSession session) {
        this.session = session;
    }
}
