package com.iceicelee.nppaservice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: Yao Shuai
 * @date: 2021/4/17 16:33
 */
@RestController
@RequestMapping("/game")
public class LoginOutReportController {

    @RequestMapping(value = "/login.go")
    public String login(HttpServletRequest req, int userId, String idfa) {
        String ip = req.getRemoteAddr();
        return null;
    }


    @RequestMapping(value = "/logout.go")
    public String logout(HttpServletRequest req, int usreId, String idfa) {
        String ip = req.getRemoteAddr();
        return "";
    }
}
