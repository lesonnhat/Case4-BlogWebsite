package com.blog.controller;

import com.blog.model.User;
import com.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users/list"; // Tên view: users/list.html
    }

    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "users/view"; // Tên view: users/view.html
        } else {
            return "error/404"; // Trang lỗi 404
        }
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        return "users/create"; // Tên view: users/create.html
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute("user") User user) {
        userService.createUser(user);
        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "users/edit"; // Tên view: users/edit.html
        } else {
            return "error/404"; // Trang lỗi 404
        }
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") User user) {
        user.setId(id); // Đảm bảo ID được set
        userService.updateUser(user);
        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }
}