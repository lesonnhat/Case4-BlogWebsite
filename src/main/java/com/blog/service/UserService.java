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

    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        // Loại bỏ mã hóa password
        // user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getAvatar() == null || user.getAvatar().isEmpty()) {
            user.setAvatar("/media/default_avatar.png");
        }
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId()).orElse(null);
        if (existingUser == null) {
            throw new IllegalArgumentException("User not found");
        }

        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        // Loại bỏ mã hóa password
        // if (user.getPassword() != null && !user.getPassword().isEmpty()) {
        //     existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        // }
        existingUser.setPassword(user.getPassword()); // Cập nhật password trực tiếp
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
}