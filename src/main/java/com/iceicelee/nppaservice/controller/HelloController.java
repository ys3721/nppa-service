package com.iceicelee.nppaservice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: Yao Shuai
 * @date: 2021/4/1 20:42
 */
@RestController
public class HelloController {

    @RequestMapping("/hello")
    public String hello() {
        return "woqu!";
    }
}
