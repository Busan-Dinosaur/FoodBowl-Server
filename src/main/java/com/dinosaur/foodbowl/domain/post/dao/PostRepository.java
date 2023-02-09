package com.dinosaur.foodbowl.domain.post.dao;

import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.user.entity.User;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

  @Query("select p from Post p left join fetch p.thumbnail where p.user = :user")
  List<Post> findThumbnailsByUser(@Param("user") User user, Pageable pageable);
}
