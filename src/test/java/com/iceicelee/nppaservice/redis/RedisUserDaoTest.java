package com.iceicelee.nppaservice.redis;

import com.iceicelee.nppaservice.constants.AuthenticationConstants.AuthenticationStatus;
import com.iceicelee.nppaservice.dao.RedisUserDao;
import com.iceicelee.nppaservice.pojo.User;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Yao Shuai
 * @date: 2021/4/27 15:31
 */
@SpringBootTest
public class RedisUserDaoTest {

    @Autowired
    public RedisUserDao redisUserDao;

    @Test
    public void testAddLink() throws MalformedURLException {

    }

    @Test
    public void testDuang() {
        Map<Long, User> userMap = new HashMap();
        for (int i = 0; i < 100000; i++) {
            User user = new User(i);
            if (Math.random() > 0.5) {
                user.setId(i);
            } else {
                user.setId(i + 1);
            }
            user.setAuthTime(new Timestamp(System.currentTimeMillis() + 1000 + i));
            user.setCreateTime(new Timestamp(System.currentTimeMillis()));
            user.setGameId(5632);
            user.setIdNumber(Math.random() * i + (i << 10)+"");
            user.setPi(RandomStringUtils.random(6));
            user.setRealName(i+"房贷卡");
            user.setAuthStatus(AuthenticationStatus.UNDER_WAY);
            user.setPassportName("fd"+ ((int)(Math.random() * i) << 5));
            redisUserDao.addOrUpdateUser(user);
            userMap.put(user.getId(), user);
        }

        for (User user : userMap.values()) {
            User userByCache = redisUserDao.queryCacheUser(user.getId());
            Assertions.assertTrue(userByCache.toJsonStr().equals(user.toJsonStr()), userByCache.toJsonStr()+"\r\n"+user.toJsonStr());
        }
    }

    @Test
    public void testSpeed() {
        for (int i = 0; i < 100; i++) {
            long ct = System.currentTimeMillis();
        }
    }

    public RedisUserDao getRedisUserDao() {
        return redisUserDao;
    }

    public void setRedisUserDao(RedisUserDao redisUserDao) {
        this.redisUserDao = redisUserDao;
    }
}
