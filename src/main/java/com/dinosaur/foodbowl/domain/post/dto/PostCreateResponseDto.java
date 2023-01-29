package com.dinosaur.foodbowl.domain.post.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostCreateResponseDto {

  private final Long postId;

  public static PostCreateResponseDto of(Long postId) {
    return PostCreateResponseDto.builder()
        .postId(postId)
        .build();
  }

  @Builder
  private PostCreateResponseDto(Long postId) {
    this.postId = postId;
  }
}
