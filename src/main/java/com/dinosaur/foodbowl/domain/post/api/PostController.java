package com.dinosaur.foodbowl.domain.post.api;

import com.dinosaur.foodbowl.domain.post.application.PostService;
import com.dinosaur.foodbowl.domain.post.dto.request.PostCreateRequestDto;
import com.dinosaur.foodbowl.domain.post.dto.request.PostUpdateRequestDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.util.resolver.LoginUser;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

  private final PostService postService;

  @PostMapping
  public ResponseEntity<Void> createPost(
      @LoginUser User me,
      @RequestPart(required = true) List<MultipartFile> images,
      @Valid @RequestPart("request") PostCreateRequestDto request) {

    Long postId = postService.createPost(me, request, images);

    return ResponseEntity.created(URI.create("/posts/" + postId))
        .build();
  }

  @PostMapping("/{id}")
  public ResponseEntity<Void> updatePost(
      @LoginUser User me,
      @PathVariable("id") Long postId,
      @RequestPart(required = true) List<MultipartFile> images,
      @Valid @RequestPart PostUpdateRequestDto request) {

    return ResponseEntity.created(URI.create("/posts/" +
            postService.updatePost(me, postId, request, images)))
        .build();
  }
}
