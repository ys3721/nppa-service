package com.iceicelee.nppaservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author: Yao Shuai
 * @date: 2021/4/25 20:27
 */
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private JedisConnectionFactory factory;

    @Test
    public void getJedisConnect() {
        RedisConnection connection = factory.getConnection();
        connection.set("name".getBytes(StandardCharsets.UTF_8), "Yao shuai".getBytes(StandardCharsets.UTF_8));
        String name = new String(Objects.requireNonNull(connection.get("name".getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8);
        assertEquals(name, "Yao shuai");
    }
}
