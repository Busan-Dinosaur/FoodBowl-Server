package com.dinosaur.foodbowl.domain.comment.application;

import com.dinosaur.foodbowl.domain.comment.dao.CommentRepository;
import com.dinosaur.foodbowl.domain.comment.dto.request.CommentWriteRequestDto;
import com.dinosaur.foodbowl.domain.comment.entity.Comment;
import com.dinosaur.foodbowl.domain.post.dao.PostFindDao;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

  private final CommentRepository commentRepository;
  private final PostFindDao postFindDao;

  public void writeComment(User loginUser, CommentWriteRequestDto request) {
    Post post = postFindDao.findById(request.getPostId());
    Comment comment = request.toEntity(loginUser, post);

    commentRepository.save(comment);
  }
}
