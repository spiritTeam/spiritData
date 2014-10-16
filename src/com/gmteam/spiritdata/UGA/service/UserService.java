package com.gmteam.spiritdata.UGA.service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gmteam.framework.UGA.UgaUserService;
import com.gmteam.framework.core.dao.mybatis.MybatisDAO;
import com.gmteam.spiritdata.UGA.pojo.User;

@Service
public class UserService implements UgaUserService {
    @Resource(name="defaultDAO")
    private MybatisDAO<User> userDao;

    @PostConstruct
    public void initParam() {
        userDao.setNamespace("SA_UGA");
    }

    @Override
    @SuppressWarnings("unchecked")
    public User getUserByLoginName(String loginName) {
        try {
            return userDao.getInfoObject("getUserByLoginName", loginName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public User getUserById(String userId) {
        try {
            return userDao.getInfoObject("getUserById", userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}