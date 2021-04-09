package com.iceicelee.nppaservice.controller.error;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: Yao Shuai
 * @date: 2021/4/6 20:45
 */
@RestController
public class NppaServiceErrorController implements ErrorController {

    private static final String PATH = "/error";

    @RequestMapping(value = PATH)
    public String error() {
        return "fail:999";
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
