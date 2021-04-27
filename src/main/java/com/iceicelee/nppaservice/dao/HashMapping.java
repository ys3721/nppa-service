package com.iceicelee.nppaservice.dao;

import com.iceicelee.nppaservice.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author: Yao Shuai
 * @date: 2021/4/27 17:55
 */
public class HashMapping {

    public static String USER_KEY = "userMap";


    Jackson2HashMapper mapper = new Jackson2HashMapper(true);

    public void writeHash(String key, User user) {
        Map<String, Object> mappedHash = mapper.toHash(user);
    }

}
