package com.iceicelee.nppaservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: Yao Shuai
 * @date: 2021/4/17 16:20
 */
@RestController
@RequestMapping("/sdk")
public class LoginOutReportController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    @RequestMapping(value = "/loginout.go")
    public String loginOutReport(HttpServletRequest req, int userId, String idfa) {
        String ip = req.getRemoteAddr();
        return "";
    }

}
