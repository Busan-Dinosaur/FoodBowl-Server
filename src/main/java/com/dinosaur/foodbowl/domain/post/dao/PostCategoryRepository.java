package com.dinosaur.foodbowl.domain.post.dao;

import com.dinosaur.foodbowl.domain.post.entity.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCategoryRepository extends JpaRepository<PostCategory, Long> {

}
