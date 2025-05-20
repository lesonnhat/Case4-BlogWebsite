package com.blog.service;

import com.blog.model.User;
import com.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }
        if (user.getAvatar() == null || user.getAvatar().isEmpty()) {
            user.setAvatar("default_avatar.jpg");
        }
        if (user.getRole() == null) {
            user.setRole("USER"); // Đặt vai trò mặc định là USER
        }
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId()).orElse(null);
        if (existingUser == null) {
            throw new IllegalArgumentException("Người dùng không tồn tại");
        }

        // Đảm bảo không set password thành null
        if (user.getPassword() == null) {
            user.setPassword(existingUser.getPassword());
        }

        // Giữ nguyên vai trò của người dùng hiện tại
        user.setRole(existingUser.getRole());
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());
        existingUser.setAvatar(user.getAvatar());
        return userRepository.save(existingUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean isAdmin(User user) {
        return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
    }
}