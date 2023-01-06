package com.dinosaur.foodbowl.domain.comment.dao;

import com.dinosaur.foodbowl.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
