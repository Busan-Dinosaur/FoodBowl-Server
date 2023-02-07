package com.dinosaur.foodbowl.domain.post.dto.response;

import com.dinosaur.foodbowl.domain.post.entity.Post;
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
public class PostThumbnailResponseDto {

  private Long postId;
  private String thumbnailPath;
  @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime createdAt;

  public static PostThumbnailResponseDto from(Post post) {
    return PostThumbnailResponseDto.builder()
        .postId(post.getId())
        .thumbnailPath(post.getThumbnail().getPath())
        .createdAt(post.getCreatedAt())
        .build();
  }
}
