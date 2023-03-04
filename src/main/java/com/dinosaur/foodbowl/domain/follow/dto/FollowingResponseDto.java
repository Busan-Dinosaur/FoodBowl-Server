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
  private final Long followerCount;
  private final LocalDateTime createdAt;

  public static FollowingResponseDto from(Follow follow, long followerCount) {
    return FollowingResponseDto.builder()
        .userId(follow.getFollowing().getId())
        .thumbnailURL(follow.getFollowing().getThumbnailURL().orElseGet(() -> null))
        .nickName(follow.getFollowing().getNickname().getNickname())
        .followerCount(followerCount)
        .createdAt(follow.getCreatedAt())
        .build();
  }
}
