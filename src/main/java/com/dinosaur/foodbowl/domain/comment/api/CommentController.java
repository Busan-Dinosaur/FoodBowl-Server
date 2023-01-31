package com.dinosaur.foodbowl.domain.comment.api;

import com.dinosaur.foodbowl.domain.comment.application.CommentService;
import com.dinosaur.foodbowl.domain.comment.dto.request.CommentWriteRequestDto;
import com.dinosaur.foodbowl.domain.comment.entity.Comment;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.util.resolver.LoginUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
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

  @PatchMapping("/{id}")
  public ResponseEntity<Void> updateComment(@LoginUser User user,
      @PathVariable("id") Long commentId,
      @RequestParam @NotBlank @Size(max = Comment.MAX_MESSAGE_LENGTH) String message) {
    long postId = commentService.updateComment(user, commentId, message);

    return ResponseEntity.status(HttpStatus.SEE_OTHER)
        .location(URI.create("/comments/posts/" + postId))
        .build();
  }
}
