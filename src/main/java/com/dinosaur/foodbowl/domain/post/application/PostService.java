package com.dinosaur.foodbowl.domain.post.application;

import com.dinosaur.foodbowl.domain.post.dao.PostRepository;
import com.dinosaur.foodbowl.domain.post.dto.response.PostThumbnailResponseDto;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.user.application.UserFindService;
import com.dinosaur.foodbowl.domain.user.entity.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

  private final PostRepository postRepository;
  private final UserFindService userFindService;

  public List<PostThumbnailResponseDto> getThumbnails(Long userId, Pageable pageable) {
    User user = userFindService.findById(userId);

    List<Post> posts = postRepository.findThumbnailsByUser(user, pageable);

    return posts.stream()
        .map(PostThumbnailResponseDto::from)
        .collect(Collectors.toList());
  }
}
