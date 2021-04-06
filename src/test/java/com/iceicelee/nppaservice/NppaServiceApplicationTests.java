package com.iceicelee.nppaservice;

import com.iceicelee.nppaservice.dao.UserDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.SQLException;

@SpringBootTest
class NppaServiceApplicationTests {

    @Autowired
    DataSource dataSource;

    @Autowired
    UserDao userDao;

    @Test
    void contextLoads() throws SQLException {
        System.out.println(dataSource.getConnection());
        userDao.findAll();
    }


}
