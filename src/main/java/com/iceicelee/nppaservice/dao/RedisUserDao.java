package com.iceicelee.nppaservice.dao;

import com.iceicelee.nppaservice.pojo.User;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author: Yao Shuai
 * @date: 2021/4/27 15:25
 */
@Component
public class RedisUserDao {

    public static String USER_KEY = "users";

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void addOrUpdateUser(User user) {
        redisTemplate.opsForHash().put(USER_KEY, user.getId()+"", user.toJsonStr());
    }

    public User queryCacheUser(long userId) {
        String userJsonStr = (String) redisTemplate.opsForHash().get(USER_KEY, userId+"");
        if (Strings.isEmpty(userJsonStr)) {
            return null;
        }
        User user = new User();
        user.fromJsonStr(userJsonStr);
        return user;
    }

}
