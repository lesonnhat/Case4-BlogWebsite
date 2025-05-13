package com.blog.repository;

import com.blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email); // Thêm tìm kiếm theo email
    boolean existsByUsername(String username); // Kiểm tra username đã tồn tại chưa
    boolean existsByEmail(String email); // Kiểm tra email đã tồn tại chưa
}