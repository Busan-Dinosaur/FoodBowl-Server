package com.dinosaur.foodbowl.domain.post.application;

import static com.dinosaur.foodbowl.global.error.ErrorCode.POST_NOT_FOUND;

import com.dinosaur.foodbowl.domain.post.dao.PostRepository;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostFindService {

  private final PostRepository postRepository;

  public Post findById(final long id) {
    return postRepository.findById(id)
        .orElseThrow(() -> new BusinessException(id, "postId", POST_NOT_FOUND));
  }
}
