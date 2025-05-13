package com.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {

    @GetMapping("/register")
    public String register() {
        return "register"; // Trả về view "register.html"
    }

    @PostMapping("/register")
    public String processRegister(@RequestParam("username") String username,
                                  @RequestParam("password") String password,
                                  @RequestParam("confirmPassword") String confirmPassword,
                                  Model model) {
        // TODO: Xác thực dữ liệu, kiểm tra password và confirmPassword, lưu vào database
        if (/* Đăng ký thành công */ true) {
            // Chuyển hướng đến trang đăng nhập hoặc trang chủ
            return "redirect:/login";
        } else {
            // Thêm thông báo lỗi và trả về trang đăng ký
            model.addAttribute("error", "Đăng ký không thành công");
            return "register";
        }
    }
}