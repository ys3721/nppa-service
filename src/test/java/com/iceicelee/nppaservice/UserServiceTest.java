package com.iceicelee.nppaservice;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Jedis;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestClass;

/**
 * @author: Yao Shuai
 * @date: 2021/4/25 20:27
 */
@SpringBootTest
public class UserServiceTest {

    @BeforeTestClass public static void initJedis() {
    }

}
