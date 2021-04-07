package com.iceicelee.nppaservice;

import com.iceicelee.nppaservice.dao.UserDao;
import com.iceicelee.nppaservice.http.IHttpClient;
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

    @Autowired
    IHttpClient httpClient;

    @Test
    void contextLoads() throws SQLException {
        System.out.println(dataSource.getConnection());
        userDao.findAll();
    }

    @Test
    void testInjectBean() {
        System.out.println(httpClient.getClass());
    }

}
