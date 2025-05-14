package com.blog.repository;

import com.blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
    boolean existsByUsername(String username); // Đảm bảo phương thức này được định nghĩa
    boolean existsByEmail(String email);      // Đảm bảo phương thức này được định nghĩa
}