package com.dinosaur.foodbowl.domain.clip.dao;

import com.dinosaur.foodbowl.domain.clip.entity.Clip;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClipRepository extends JpaRepository<Clip, Long> {

  boolean existsClipByUserAndPost(User user, Post post);

  Optional<Clip> findClipByUserAndPost(User user, Post post);
}
