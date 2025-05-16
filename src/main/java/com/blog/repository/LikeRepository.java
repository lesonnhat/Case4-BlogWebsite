package com.blog.repository;

import com.blog.model.Like;
import com.blog.model.Post;
import com.blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findByPost(Post post);
    List<Like> findByUser(User user); // Lấy tất cả like của một updateUser
    Like findByPostAndUser(Post post, User user); // Kiểm tra xem updateUser đã like bài viết này chưa
    long countByPost(Post post); // Đếm số lượng like của một bài viết
}