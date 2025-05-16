package com.blog.controller;

import com.blog.model.Post;
import com.blog.model.User;
import com.blog.service.PostService;
import com.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ResourceLoader;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private ResourceLoader resourceLoader;

    @GetMapping
    public String listPosts(Model model, HttpSession session) {
        List<Post> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);
        User currentUser = (User) session.getAttribute("user");
        if (currentUser != null) {
            model.addAttribute("currentUser", currentUser);
        } else {
            return "redirect:/login";
        }
        return "posts/list";
    }

    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model, HttpSession session) {
        Optional<Post> post = postService.getPostById(id);
        if (post.isPresent()) {
            User sessionUser = (User) session.getAttribute("user");
            System.out.println("Viewing post ID " + id + ", session updateUser: " + (sessionUser != null ? sessionUser.getUsername() : "null"));
            model.addAttribute("post", post.get());
            return "posts/view";
        } else {
            return "error/404";
        }
    }

    @GetMapping("/new")
    public String showCreateForm(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        Post post = new Post();
        model.addAttribute("post", post);
        return "posts/create";
    }

    @PostMapping
    public String createPost(@ModelAttribute("post") Post post,
                             @RequestParam("image") MultipartFile image, Model model,
                             HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        if (!image.isEmpty()) {
            try {
                String contentType = image.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    model.addAttribute("error", "Vui lòng chọn file ảnh (jpg, png, v.v.)");
                    return "posts/create";
                }

                String uploadDir = resourceLoader.getResource("classpath:static/media/").getFile().getAbsolutePath();
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                File dest = new File(uploadDir + File.separator + fileName);
                image.transferTo(dest);

                post.setImageUrl("/media/" + fileName);
                post.setAuthor(user);
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("error", "Lỗi khi upload ảnh: " + e.getMessage());
                return "posts/create";
            }
        } else {
            model.addAttribute("error", "Vui lòng chọn ảnh");
            return "posts/create";
        }

        postService.createPost(post);
        return "redirect:/posts";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<Post> post = postService.getPostById(id);
        if (post.isPresent()) {
            Post existingPost = post.get();
            if (!existingPost.getAuthor().getId().equals(currentUser.getId())) {
                return "redirect:/posts";
            }
            model.addAttribute("post", existingPost);
            return "posts/edit";
        } else {
            return "error/404";
        }
    }

    @PostMapping("/{id}/edit")
    public String updatePost(@PathVariable Long id, @ModelAttribute("post") Post post,
                             @RequestParam(value = "image", required = false) MultipartFile image, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<Post> existingPost = postService.getPostById(id);
        if (!existingPost.isPresent() || !existingPost.get().getAuthor().getId().equals(currentUser.getId())) {
            return "redirect:/posts";
        }

        post.setId(id);
        if (image != null && !image.isEmpty()) {
            try {
                String contentType = image.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    model.addAttribute("error", "Vui lòng chọn file ảnh (jpg, png, v.v.)");
                    return "posts/edit";
                }

                String uploadDir = resourceLoader.getResource("classpath:static/media/").getFile().getAbsolutePath();
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                File dest = new File(uploadDir + File.separator + fileName);
                image.transferTo(dest);

                post.setImageUrl("/media/" + fileName);
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("error", "Lỗi khi upload ảnh: " + e.getMessage());
                return "posts/edit";
            }
        }

        postService.updatePost(post);
        return "redirect:/posts";
    }

    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<Post> post = postService.getPostById(id);
        if (post.isPresent() && post.get().getAuthor().getId().equals(currentUser.getId())) {
            postService.deletePost(id);
        }
        return "redirect:/posts";
    }
}