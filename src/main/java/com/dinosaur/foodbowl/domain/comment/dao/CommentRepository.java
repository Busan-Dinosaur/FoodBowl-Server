package com.dinosaur.foodbowl.domain.comment.dao;

import com.dinosaur.foodbowl.domain.comment.entity.Comment;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  @Query("select c from Comment c"
      + " left join fetch c.blames"
      + " join fetch c.user"
      + " left join fetch c.user.thumbnail"
      + " where c.post = :post and size(c.blames) < 5 order by c.createdAt")
  List<Comment> findUnrestrictedComments(@Param("post") Post post);
}
