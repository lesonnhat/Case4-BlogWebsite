package com.blog.controller;

import com.blog.model.Post;
import com.blog.model.User;
import com.blog.service.LikeService;
import com.blog.service.PostService;
import com.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @PostMapping("/like")
    public String likePost(@RequestParam("postId") Long postId, @RequestParam("userId") Long userId) {
        Optional<Post> post = postService.getPostById(postId);
        Optional<User> user = userService.getUserById(userId);

        if (post.isPresent() && user.isPresent()) {
            likeService.likePost(post.get(), user.get());
        }
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/unlike")
    public String unlikePost(@RequestParam("postId") Long postId, @RequestParam("userId") Long userId) {
        Optional<Post> post = postService.getPostById(postId);
        Optional<User> user = userService.getUserById(userId);

        if (post.isPresent() && user.isPresent()) {
            likeService.unlikePost(post.get(), user.get());
        }
        return "redirect:/posts/" + postId;
    }
}