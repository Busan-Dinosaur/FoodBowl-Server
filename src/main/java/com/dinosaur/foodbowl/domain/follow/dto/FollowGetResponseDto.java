package com.dinosaur.foodbowl.domain.follow.dto;

import com.dinosaur.foodbowl.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowGetResponseDto {

  private Long userId;
  private String thumbnailURL;
  private String nickName;

  public static FollowGetResponseDto toDto(User user) {
    return FollowGetResponseDto.builder()
        .userId(user.getId())
        .nickName(user.getNickname().getNickname())
        .thumbnailURL(user.getThumbnailURL().orElseGet(() -> null))
        .build();
  }
}
