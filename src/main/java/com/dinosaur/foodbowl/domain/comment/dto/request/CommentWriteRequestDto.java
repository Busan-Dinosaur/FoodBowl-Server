package com.dinosaur.foodbowl.domain.comment.dto.request;

import com.dinosaur.foodbowl.domain.comment.entity.Comment;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentWriteRequestDto {

  @NotNull
  private Long postId;

  @NotBlank
  @Size(max = Comment.MAX_MESSAGE_LENGTH)
  private String message;

  public Comment toEntity(User user, Post post) {
    return Comment.builder()
        .post(post)
        .user(user)
        .message(message)
        .build();
  }
}
