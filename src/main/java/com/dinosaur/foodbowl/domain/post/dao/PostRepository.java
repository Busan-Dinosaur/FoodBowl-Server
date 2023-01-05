package com.dinosaur.foodbowl.domain.post.dao;

import com.dinosaur.foodbowl.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}
