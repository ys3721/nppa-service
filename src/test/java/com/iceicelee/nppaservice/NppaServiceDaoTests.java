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
    }

    @Test
    void testQueryUser() throws SQLException {

    }
}
