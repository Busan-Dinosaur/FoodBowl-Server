package com.dinosaur.foodbowl.domain.clip.dao;

import com.dinosaur.foodbowl.domain.clip.entity.Clip;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.repository.Repository;

public interface ClipRepository extends Repository<Clip, Long> {

  Clip save(Clip save);

  void delete(Clip clip);

  boolean existsClipByUserAndPost(User user, Post post);

  Optional<Clip> findClipByUserAndPost(User user, Post post);

  @EntityGraph(attributePaths = {"post", "post.thumbnail"}, type = EntityGraphType.LOAD)
  List<Clip> findClipByUser(User user, Pageable pageable);
}
