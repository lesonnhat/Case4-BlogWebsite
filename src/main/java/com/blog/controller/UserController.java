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
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        return "users/create";
    }

    @PostMapping
    public String createUser(@ModelAttribute("user") User user,
                             @RequestParam(value = "avatar", required = false) MultipartFile avatar,
                             Model model) {
        if (user.getUsername() == null || user.getEmail() == null || user.getPassword() == null) {
            model.addAttribute("error", "Vui lòng điền đầy đủ thông tin (username, email, password).");
            return "users/create";
        }

        if (avatar != null && !avatar.isEmpty()) {
            try {
                String contentType = avatar.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    model.addAttribute("error", "Vui lòng chọn file ảnh (jpg, png, v.v.)");
                    return "users/create";
                }

                String uploadDir = resourceLoader.getResource("classpath:static/media/").getFile().getAbsolutePath();
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String fileName = UUID.randomUUID().toString() + "_" + avatar.getOriginalFilename();
                File dest = new File(uploadDir + File.separator + fileName);
                avatar.transferTo(dest);

                user.setAvatar("/media/" + fileName);
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("error", "Lỗi khi upload ảnh: " + e.getMessage());
                return "users/create";
            }
        }

        try {
            userService.createUser(user);
            return "redirect:/users";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tạo người dùng: " + e.getMessage());
            return "users/create";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !currentUser.getId().equals(id)) {
            return "redirect:/posts";
        }

        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "users/edit";
        } else {
            return "error/404";
        }
    }

    @PostMapping("/{id}/edit")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") User user,
                             @RequestParam(value = "avatar", required = false) MultipartFile avatar,
                             HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !currentUser.getId().equals(id)) {
            return "redirect:/posts";
        }

        Optional<User> existingUser = userService.getUserById(id);
        if (!existingUser.isPresent()) {
            return "error/404";
        }

        User userToUpdate = existingUser.get();

        // Debug dữ liệu nhận được
        System.out.println("Received user data: id=" + id + ", username=" + user.getUsername() + ", email=" + user.getEmail() + ", password=" + user.getPassword());

        // Kiểm tra username và email duy nhất, nhưng bỏ qua nếu không thay đổi
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            model.addAttribute("error", "Username không được để trống.");
            return "users/edit";
        }
        if (!user.getUsername().equals(userToUpdate.getUsername()) && userService.existsByUsername(user.getUsername())) {
            model.addAttribute("error", "Username đã tồn tại, vui lòng chọn username khác.");
            return "users/edit";
        }
        userToUpdate.setUsername(user.getUsername());

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            model.addAttribute("error", "Email không được để trống.");
            return "users/edit";
        }
        if (!user.getEmail().equals(userToUpdate.getEmail()) && userService.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Email đã tồn tại, vui lòng chọn email khác.");
            return "users/edit";
        }
        userToUpdate.setEmail(user.getEmail());

        // Cập nhật password chỉ khi có giá trị mới, giữ nguyên nếu để trống
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            userToUpdate.setPassword(user.getPassword());
        } else {
            userToUpdate.setPassword(existingUser.get().getPassword()); // Giữ nguyên password cũ
        }

        if (avatar != null && !avatar.isEmpty()) {
            try {
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

//                userToUpdate.setAvatar("/media/" + fileName);
                userToUpdate.setAvatar(fileName);
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("error", "Lỗi khi upload ảnh: " + e.getMessage());
                return "users/edit";
            }
        }

        try {
            userService.updateUser(userToUpdate);
            if (currentUser.getId().equals(id)) {
                session.setAttribute("user", userToUpdate);
            }
            return "redirect:/posts";
        } catch (Exception e) {
            System.out.println("Update error: " + e.getMessage());
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