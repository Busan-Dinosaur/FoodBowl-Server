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
  private final Long followerCount;
  private final LocalDateTime createdAt;

  public static FollowerResponseDto from(Follow follow) {
    return FollowerResponseDto.builder()
        .userId(follow.getFollower().getId())
        .thumbnailURL(follow.getFollower().getThumbnailURL().orElseGet(() -> null))
        .nickName(follow.getFollower().getNickname().getNickname())
        .followerCount(follow.getFollower().getFollowerSize())
        .createdAt(follow.getCreatedAt())
        .build();
  }
}
