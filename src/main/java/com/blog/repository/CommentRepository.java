package com.blog.repository;

import com.blog.model.Comment;
import com.blog.model.Post;
import com.blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
    List<Comment> findByUser(User user); // Lấy tất cả comment của một updateUser
}