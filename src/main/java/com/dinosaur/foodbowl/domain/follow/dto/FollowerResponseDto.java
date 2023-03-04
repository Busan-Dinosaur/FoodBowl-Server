package com.dinosaur.foodbowl.domain.follow.dto;

import com.dinosaur.foodbowl.domain.follow.entity.Follow;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowerResponseDto {

  private final Long userId;
  private final String thumbnailURL;
  private final String nickName;
  private final LocalDateTime createdAt;

  public static FollowerResponseDto of(Follow follow) {
    return FollowerResponseDto.builder()
        .userId(follow.getFollower().getId())
        .thumbnailURL(follow.getFollower().getThumbnailURL().orElseGet(() -> null))
        .nickName(follow.getFollower().getNickname().getNickname())
        .createdAt(follow.getCreatedAt())
        .build();
  }
}
