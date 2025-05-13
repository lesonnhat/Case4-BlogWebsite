package com.blog.service;

import com.blog.model.Post;
import com.blog.model.User;
import com.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public void createPost(Post post) {
        postRepository.save(post);
    }

    @Transactional
    public Post updatePost(Post post) {
        Optional<Post> existingPost = postRepository.findById(post.getId());
        if (!existingPost.isPresent()) {
            throw new IllegalArgumentException("Post not found");
        }
        Post postToUpdate = existingPost.get();
        postToUpdate.setTitle(post.getTitle());
        postToUpdate.setContent(post.getContent());
        // Cập nhật imageUrl nếu có
        if (post.getImageUrl() != null) {
            postToUpdate.setImageUrl(post.getImageUrl());
        }
        return postRepository.save(postToUpdate);
    }

    @Transactional
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    public List<Post> getPostsByAuthor(User author) {
        return postRepository.findByAuthor(author);
    }

    public List<Post> searchPosts(String keyword) {
        return postRepository.findByTitleContainingIgnoreCase(keyword);
    }
}