package com.iceicelee.nppaservice.controller;

import com.iceicelee.nppaservice.constants.AuthenticationConstants.AuthenticationStatus;
import com.iceicelee.nppaservice.http.IHttpClient;
import com.iceicelee.nppaservice.pojo.LoginOutRequestModel;
import com.iceicelee.nppaservice.pojo.LoginOutResponse;
import com.iceicelee.nppaservice.pojo.User;
import com.iceicelee.nppaservice.service.AuthenticationService;
import com.iceicelee.nppaservice.service.IUserService;
import com.iceicelee.nppaservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Yao Shuai
 * @date: 2021/4/17 16:20
 */
@RestController
@RequestMapping("/sdk")
public class LoginOutReportController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    private IUserService userService;

    private AuthenticationService authService;

    @Autowired
    public LoginOutReportController(UserService userService, AuthenticationService authService) {
        this.userService = userService;
        this.authService = authService;
    }


    @RequestMapping(value = "/loginout.go")
    public String loginOutReport(HttpServletRequest req, long userId, long roleId, String idfa, int inOrOut) {
        LoginOutRequestModel model = new LoginOutRequestModel();
        User user = userService.findUserByPassportId(userId);
        int autType = user != null && user.getAuthStatus() == AuthenticationStatus.SUCCESS ? 0 : 2;
        String pi = user != null ? user.getPi() : "";
        model.setRoleId(roleId + "");
        model.setLogInOut(inOrOut);
        model.setTimeSeconds(System.currentTimeMillis()/1000);
        model.setAuthType(autType);
        model.setIdfa(idfa == null ? "" : idfa);
        model.setPi(pi == null ? "" : pi);
        List<LoginOutRequestModel> modelList = new ArrayList<>();
        modelList.add(model);
        LoginOutResponse response = authService.goNppaReportLoginOut(modelList);
        if (response.getErrcode() != 0) {
            log.error("NPPA汇报报错了：" + response.getErrmsg());
            response.getData().toString();
        }
        return "ok:1";
    }
}
