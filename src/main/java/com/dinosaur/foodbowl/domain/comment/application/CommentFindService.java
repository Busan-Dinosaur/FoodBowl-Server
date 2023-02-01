package com.dinosaur.foodbowl.domain.comment.application;

import static com.dinosaur.foodbowl.global.error.ErrorCode.COMMENT_NOT_FOUND;

import com.dinosaur.foodbowl.domain.comment.dao.CommentRepository;
import com.dinosaur.foodbowl.domain.comment.entity.Comment;
import com.dinosaur.foodbowl.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentFindService {

  private final CommentRepository commentRepository;

  public Comment findById(final long id) {
    return commentRepository.findById(id)
        .orElseThrow(() -> new BusinessException(id, "commentId", COMMENT_NOT_FOUND));
  }
}
