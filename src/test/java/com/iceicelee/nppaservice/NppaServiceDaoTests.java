package com.iceicelee.nppaservice;

import com.iceicelee.nppaservice.dao.UserDao;
import com.iceicelee.nppaservice.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Random;

/**
 * @author yaoshuai
 * @date 2021-四月-07
 */
@SpringBootTest
public class NppaServiceDaoTests {

    @Autowired
    UserDao userDao;

    @Test
    void testInsertUser() throws SQLException {
        for (int i = 0; i < 10000; i++) {
            User user;
            user = new User();
            user.setId(i);
            user.setPassportName("");
            user.setRealName("张爱"+i);
            user.setIdNumber("110120130140150"+i*100);
            user.setAuthStatus((byte) 4);
            user.setCreateTime(new Timestamp(System.currentTimeMillis()));
            userDao.save(user);
        }
    }

    @Test
    void testQueryUser() throws SQLException {
        for (int i = 0; i < 10000; i++) {
            User user = userDao.findById(i);
            Assert.isTrue( user != null, i+" 不为空");
        }
        for (int i = 10000; i < 20000; i++) {
            User user = userDao.findById(i);
            Assert.isTrue( user == null, i+" 不为空");
        }
    }
}
