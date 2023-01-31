package com.dinosaur.foodbowl.domain.comment.api;

import com.dinosaur.foodbowl.domain.comment.application.CommentService;
import com.dinosaur.foodbowl.domain.comment.dto.request.CommentWriteRequestDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.util.resolver.LoginUser;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

  private final CommentService commentService;

  @PostMapping
  public ResponseEntity<Void> writeComment(@LoginUser User user,
      @Valid @RequestBody CommentWriteRequestDto commentWriteRequestDto) {
    commentService.writeComment(user, commentWriteRequestDto);

    return ResponseEntity.status(HttpStatus.SEE_OTHER)
        .location(URI.create("/comments/posts/" + commentWriteRequestDto.getPostId()))
        .build();
  }
}
