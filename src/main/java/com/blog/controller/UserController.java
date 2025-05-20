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
    public String listUsers(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !userService.isAdmin(currentUser)) {
            return "redirect:/posts"; // Chỉ admin truy cập danh sách người dùng
        }
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("currentUser", currentUser);
        return "users/list";
    }

    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login"; // Người dùng phải đăng nhập để xem
        }
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            model.addAttribute("currentUser", currentUser);
            return "users/view";
        } else {
            return "error/404";
        }
    }

    @GetMapping("/new")
    public String showCreateForm(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !userService.isAdmin(currentUser)) {
            return "redirect:/posts";
        }
        model.addAttribute("user", new User());
        return "users/create";
    }

    @PostMapping
    public String createUser(@ModelAttribute("user") User user, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !userService.isAdmin(currentUser)) {
            return "redirect:/posts";
        }

        try {
            user.setRole("USER"); // Mặc định người dùng mới là USER
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
            if (!userService.isAdmin(currentUser) && !user.get().getId().equals(currentUser.getId())) {
                return "redirect:/posts"; // Chỉ admin hoặc chính người dùng đó có thể sửa
            }
            model.addAttribute("user", user.get());
            return "users/edit";
        } else {
            return "error/404";
        }
    }

    @PostMapping("/{id}/edit")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") User user,
                             @RequestParam(value = "avatar", required = false) MultipartFile avatar, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<User> existingUser = userService.getUserById(id);
        if (!existingUser.isPresent() || (!userService.isAdmin(currentUser) && !existingUser.get().getId().equals(currentUser.getId()))) {
            return "redirect:/posts";
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
            return userService.isAdmin(currentUser) ? "redirect:/users" : "redirect:/posts";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "users/edit";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi cập nhật người dùng: " + e.getMessage());
            return "users/edit";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !userService.isAdmin(currentUser)) {
            return "redirect:/posts";
        }
        userService.deleteUser(id);
        return "redirect:/users";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}