package com.blog.controller;

import com.blog.model.User;
import com.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ResourceLoader resourceLoader;

    @GetMapping("/posts")
    public String showPosts(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser != null) {
            model.addAttribute("currentUser", currentUser);
        }
        return "posts";
    }

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users/list";
    }

    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "users/view";
        } else {
            return "error/404";
        }
    }

    @GetMapping("/new")
    public String showCreateForm(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", new User());
        return "users/create";
    }

    @PostMapping
    public String createUser(@ModelAttribute("user") User user, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            userService.createUser(user);
            return "redirect:/users";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "users/create";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi không xác định: " + e.getMessage());
            return "users/create";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            if (!user.get().getId().equals(currentUser.getId())) {
                return "redirect:/users";
            }
            model.addAttribute("user", user.get());
            return "users/edit";
        } else {
            return "error/404";
        }
    }

    @PostMapping("/{id}/edit")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") User user,
                             @RequestParam(value = "avatar", required = false) MultipartFile avatar,
                             Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<User> existingUser = userService.getUserById(id);
        if (!existingUser.isPresent() || !existingUser.get().getId().equals(currentUser.getId())) {
            return "redirect:/users";
        }

        user.setId(id);
        try {
            if (avatar != null && !avatar.isEmpty()) {
                String contentType = avatar.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    model.addAttribute("error", "Vui lòng chọn file ảnh (jpg, png, v.v.)");
                    return "users/edit";
                }

                String uploadDir = resourceLoader.getResource("classpath:static/media/").getFile().getAbsolutePath();
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String fileName = UUID.randomUUID().toString() + "_" + avatar.getOriginalFilename();
                File dest = new File(uploadDir + File.separator + fileName);
                avatar.transferTo(dest);

                user.setAvatar(fileName);
            }

            userService.updateUser(user);
            return "redirect:/users";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "users/edit";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi cập nhật người dùng: " + e.getMessage());
            return "users/edit";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}