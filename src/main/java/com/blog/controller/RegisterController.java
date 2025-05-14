package com.blog.controller;

import com.blog.model.User;
import com.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("email") String email,
            Model model) {
        try {
            if (userService.existsByUsername(username)) {
                model.addAttribute("error", "Tên đăng nhập đã tồn tại");
                return "register";
            }
            if (userService.existsByEmail(email)) {
                model.addAttribute("error", "Email đã tồn tại");
                return "register";
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);

            userService.createUser(user);
            return "redirect:/login";

        } catch (Exception e) {
            model.addAttribute("error", "Đăng ký không thành công: " + e.getMessage());
            return "register";
        }
    }
}