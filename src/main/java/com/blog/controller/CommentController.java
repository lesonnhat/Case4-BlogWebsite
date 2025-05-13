package com.blog.controller;

import com.blog.model.Comment;
import com.blog.model.Post;
import com.blog.model.User;
import com.blog.service.CommentService;
import com.blog.service.PostService;
import com.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public String createComment(@ModelAttribute("comment") Comment comment,
                                @RequestParam("postId") Long postId,
                                HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login"; // Chưa đăng nhập, chuyển hướng đến login
        }

        Optional<Post> post = postService.getPostById(postId);
        if (post.isPresent()) {
            commentService.createComment(comment, post.get(), user);
            return "redirect:/posts/" + postId;
        } else {
            return "error/404";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<Comment> comment = commentService.getCommentById(id);
        if (!comment.isPresent()) {
            return "error/404";
        }

        Comment commentToEdit = comment.get();
        // Kiểm tra quyền: Chỉ người tạo bình luận mới được chỉnh sửa
        if (!commentToEdit.getUser().getId().equals(currentUser.getId())) {
            model.addAttribute("error", "Bạn không có quyền chỉnh sửa bình luận này");
            return "redirect:/posts/" + commentToEdit.getPost().getId();
        }

        model.addAttribute("comment", commentToEdit);
        model.addAttribute("postId", commentToEdit.getPost().getId());
        return "comments/edit"; // Template để chỉnh sửa bình luận
    }

    @PostMapping("/{id}/edit")
    public String updateComment(@PathVariable Long id, @ModelAttribute("comment") Comment comment,
                                @RequestParam("postId") Long postId, HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<Comment> existingComment = commentService.getCommentById(id);
        if (!existingComment.isPresent()) {
            return "error/404";
        }

        Comment commentToUpdate = existingComment.get();
        // Kiểm tra quyền
        if (!commentToUpdate.getUser().getId().equals(currentUser.getId())) {
            model.addAttribute("error", "Bạn không có quyền chỉnh sửa bình luận này");
            return "redirect:/posts/" + postId;
        }

        comment.setId(id);
        commentService.updateComment(comment);
        return "redirect:/posts/" + postId;
    }

    @GetMapping("/delete/{id}")
    public String deleteComment(@PathVariable Long id, @RequestParam("postId") Long postId, HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<Comment> comment = commentService.getCommentById(id);
        if (!comment.isPresent()) {
            return "error/404";
        }

        // Kiểm tra quyền: Chỉ người tạo bình luận mới được xóa
        if (!comment.get().getUser().getId().equals(currentUser.getId())) {
            model.addAttribute("error", "Bạn không có quyền xóa bình luận này");
            return "redirect:/posts/" + postId;
        }

        commentService.deleteComment(id);
        return "redirect:/posts/" + postId;
    }
}