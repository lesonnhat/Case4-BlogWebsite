package com.blog.service;

import com.blog.model.Comment;
import com.blog.model.Post;
import com.blog.model.User;
import com.blog.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Transactional
    public Comment createComment(Comment comment, Post post, User user) {
        comment.setPost(post);
        comment.setUser(user);
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment updateComment(Comment comment) {
        Optional<Comment> existingComment = commentRepository.findById(comment.getId());
        if (!existingComment.isPresent()) {
            throw new IllegalArgumentException("Comment not found");
        }
        Comment commentToUpdate = existingComment.get();
        commentToUpdate.setContent(comment.getContent());
        return commentRepository.save(commentToUpdate);
    }

    @Transactional
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    public List<Comment> getCommentsByPost(Post post) {
        return commentRepository.findByPost(post);
    }

    public List<Comment> getCommentsByUser(User user) {
        return commentRepository.findByUser(user);
    }
}