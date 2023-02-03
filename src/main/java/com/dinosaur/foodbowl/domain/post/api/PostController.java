package com.dinosaur.foodbowl.domain.post.api;

import com.dinosaur.foodbowl.domain.post.application.PostService;
import com.dinosaur.foodbowl.domain.post.dto.response.PostThumbnailResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

  private final PostService postService;

  @GetMapping("/users/{id}/thumbnails")
  public ResponseEntity<List<PostThumbnailResponseDto>> getThumbnails(
      @PathVariable("id") Long userId,
      @PageableDefault(size = 18, sort = "createdAt") Pageable pageable) {
    List<PostThumbnailResponseDto> response = postService.getThumbnails(userId, pageable);

    return ResponseEntity.ok(response);
  }
}
