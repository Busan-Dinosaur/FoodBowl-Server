package com.dinosaur.foodbowl.domain.comment.dto.response;

import com.dinosaur.foodbowl.domain.comment.entity.Comment;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentResponseDto {

  private String nickname;
  private String userThumbnailPath;
  private String message;
  @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime createdAt;

  public static CommentResponseDto from(Comment comment) {
    return CommentResponseDto.builder()
        .nickname(comment.getUser().getNickname().getNickname())
        .userThumbnailPath(comment.getUser().getThumbnailURL().orElseGet(() -> null))
        .message(comment.getMessage())
        .createdAt(comment.getCreatedAt())
        .build();
  }
}
