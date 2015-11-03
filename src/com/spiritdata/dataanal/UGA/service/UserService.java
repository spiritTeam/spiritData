package com.spiritdata.dataanal.UGA.service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.spiritdata.framework.UGA.UgaUserService;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.dataanal.UGA.pojo.User;

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

    public User getUserByMailAdress(String mail) {
        try {
            return userDao.getInfoObject("getUserByMailAdress", mail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int insertUser(User user) {
        int i=0;
        try {
            userDao.insert("insertUser", user);
            i=1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    public int updateUser(User user) {
        int i = 0;
        try {
            userDao.update("updateUser", user);
            i=1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }
}