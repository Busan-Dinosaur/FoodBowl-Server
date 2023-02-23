package com.dinosaur.foodbowl.domain.post.api;

import com.dinosaur.foodbowl.domain.post.application.PostService;
import com.dinosaur.foodbowl.domain.post.dto.request.PostCreateRequestDto;
import com.dinosaur.foodbowl.domain.post.dto.request.PostUpdateRequestDto;
import jakarta.validation.Valid;
import java.net.URI;
import com.dinosaur.foodbowl.domain.post.dto.response.PostFeedResponseDto;
import com.dinosaur.foodbowl.domain.post.dto.response.PostThumbnailResponseDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.util.resolver.LoginUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.dinosaur.foodbowl.domain.post.dto.response.PostThumbnailResponseDto;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

  private final PostService postService;

  @PostMapping
  public ResponseEntity<Void> createPost(
      @LoginUser User me,
      @RequestPart(name = "images", required = true) List<MultipartFile> images,
      @Valid @RequestPart("request") PostCreateRequestDto request) {

    Long postId = postService.createPost(me, request, images);

    return ResponseEntity.created(URI.create("/posts/" + postId))
        .build();
  }

  @PostMapping("/{id}")
  public ResponseEntity<Long> updatePost(
      @LoginUser User me,
      @PathVariable("id") Long postId,
      @RequestPart(name = "images", required = true) List<MultipartFile> images,
      @Valid @RequestPart("request") PostUpdateRequestDto request) {

    Long updatedPostId = postService.updatePost(me, postId, request, images);

    return ResponseEntity.ok(updatedPostId);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePost(@LoginUser User user, @PathVariable("id") Long postId) {

    postService.deletePost(user, postId);

    return ResponseEntity.noContent().build();
  }

  @GetMapping("/users/{id}/thumbnails")
  public ResponseEntity<List<PostThumbnailResponseDto>> getThumbnails(
      @PathVariable("id") Long userId,
      @PageableDefault(size = 18, sort = "createdAt", direction = Direction.DESC) Pageable pageable
  ) {
    List<PostThumbnailResponseDto> response = postService.getThumbnails(userId, pageable);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/feed")
  public ResponseEntity<List<PostFeedResponseDto>> getFeed(
      @LoginUser User user,
      @PageableDefault(size = 4, sort = "createdAt", direction = Direction.DESC) Pageable pageable
  ) {
    List<PostFeedResponseDto> response = postService.getFeed(user, pageable);

    return ResponseEntity.ok(response);
  }
}
