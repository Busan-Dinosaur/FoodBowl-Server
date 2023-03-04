package com.dinosaur.foodbowl.domain.follow.dto;

import com.dinosaur.foodbowl.domain.follow.entity.Follow;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowingResponseDto {

  private final Long userId;
  private final String thumbnailURL;
  private final String nickName;
  private final LocalDateTime createdAt;

  public static FollowingResponseDto of(Follow follow) {
    return FollowingResponseDto.builder()
        .userId(follow.getFollowing().getId())
        .thumbnailURL(follow.getFollowing().getThumbnailURL().orElseGet(() -> null))
        .nickName(follow.getFollowing().getNickname().getNickname())
        .createdAt(follow.getCreatedAt())
        .build();
  }
}
