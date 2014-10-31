package com.gmteam.spiritdata.dictdata.service;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

/**
 * 字典处理服务，关于所有者字典的操作都封装在这里。
 * 所有者信息存储在Session中，本服务针对Session中的字典进行处理。
 * 包括Session和持久化存储同步的相关操作。
 * 所有者字典被以Map<dictMasterId, dictModel>的方式存储在Session中。
 * @author wh
 */
@Component
public class DictSessionService {
    private HttpSession session;
    public void setSession(HttpSession session) {
        this.session = session;
    }
}