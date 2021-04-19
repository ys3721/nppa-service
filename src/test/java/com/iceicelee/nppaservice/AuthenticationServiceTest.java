package com.iceicelee.nppaservice;

import com.iceicelee.nppaservice.dao.IUserEntityDao;
import com.iceicelee.nppaservice.dao.UserDao;
import com.iceicelee.nppaservice.pojo.LoginOutRequestModel;
import com.iceicelee.nppaservice.pojo.LoginOutResponse;
import com.iceicelee.nppaservice.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Yao Shuai
 * @date: 2021/4/19 18:36
 */
@SpringBootTest
public class AuthenticationServiceTest {

    @Autowired
    private AuthenticationService service;

    @Autowired
    private IUserEntityDao userDao;


    @Test
    public void testReportLoginOut() {
        List<LoginOutRequestModel> reports = new ArrayList<>();
        LoginOutRequestModel model = new LoginOutRequestModel();
        model.setRoleId(4593671619917907954L+"");
        model.setTimeSeconds(System.currentTimeMillis()/1000);
        model.setIdfa("");
        model.setLogInOut(1);
        model.setAuthType(0);
        model.setPi("1hgdcie10snjw6shvx144f3ai0z3d6b2u6fcm6");
        reports.add(model);
        service.goNppaReportLoginOut(reports);
    }

}
