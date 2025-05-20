package com.blog.controller;

import com.blog.model.User;
import com.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Sai tên đăng nhập hoặc mật khẩu");
        }
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               HttpServletRequest request,
                               Model model) {
        User user = userService.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            if (userService.isAdmin(user)) {
                return "redirect:/users"; // Chuyển hướng admin đến danh sách người dùng
            } else {
                return "redirect:/posts"; // Người dùng thường đến bảng tin
            }
        } else {
            model.addAttribute("error", "Sai tên đăng nhập hoặc mật khẩu");
            return "login";
        }
    }
}