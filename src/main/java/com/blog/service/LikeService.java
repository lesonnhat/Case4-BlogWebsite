package com.blog.service;

import com.blog.model.Like;
import com.blog.model.Post;
import com.blog.model.User;
import com.blog.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Transactional
    public void likePost(Post post, User user) {
        if (likeRepository.findByPostAndUser(post, user) == null) {
            Like like = new Like();
            like.setPost(post);
            like.setUser(user);
            likeRepository.save(like);
        }
    }

    @Transactional
    public void unlikePost(Post post, User user) {
        Like like = likeRepository.findByPostAndUser(post, user);
        if (like != null) {
            likeRepository.delete(like);
        }
    }

    public List<Like> getLikesByPost(Post post) {
        return likeRepository.findByPost(post);
    }

    public List<Like> getLikesByUser(User user) {
        return likeRepository.findByUser(user);
    }

    public long countLikesByPost(Post post) {
        return likeRepository.countByPost(post);
    }
}