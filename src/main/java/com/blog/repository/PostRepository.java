package com.blog.repository;

import com.blog.model.Post;
import com.blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthor(User author);
    List<Post> findByTitleContainingIgnoreCase(String title); // Tìm kiếm bài viết theo tiêu đề (không phân biệt hoa thường)
    List<Post> findAllByOrderByCreatedAtDesc(); // Lấy tất cả bài viết, sắp xếp theo thời gian tạo giảm dần
}