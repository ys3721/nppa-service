package com.iceicelee.nppaservice.controller;

import com.iceicelee.nppaservice.pojo.User;
import com.iceicelee.nppaservice.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author yaoshuai
 */
@Controller
@RequestMapping("/")
public class UserListController {

    private UserRepository userRepo;

    @Autowired
    public UserListController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list(Map<String, Object> model) {
        List<User> userList = userRepo.findAll();
        model.put("contacts", userList);
        return "home";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submit(User user) {
        user.setCreateTime(new Timestamp(new Date().getTime()));
        user.setAuthStatus((byte) 4);
        userRepo.save(user);
        return "redirect:/";
    }
}
