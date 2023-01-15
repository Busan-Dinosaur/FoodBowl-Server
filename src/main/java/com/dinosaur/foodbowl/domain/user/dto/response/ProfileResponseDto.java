package com.dinosaur.foodbowl.domain.user.dto.response;

import com.dinosaur.foodbowl.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProfileResponseDto {

  private final long userId;
  private final String nickname;
  private final String introduce;
  private final long followerCount;
  private final long followingCount;
  private final String thumbnailURL;

  public static ProfileResponseDto of(User user, long followerCount, long followingCount) {
    return ProfileResponseDto.builder()
        .userId(user.getId())
        .nickname(user.getNickname())
        .introduce(user.getIntroduce())
        .followerCount(followerCount)
        .followingCount(followingCount)
        .thumbnailURL(user.getThumbnailURL().orElse(null))
        .build();
  }

  @Builder
  private ProfileResponseDto(Long userId, String nickname, String introduce,
      long followerCount, long followingCount, String thumbnailURL) {
    this.userId = userId;
    this.followerCount = followerCount;
    this.followingCount = followingCount;
    this.nickname = nickname;
    this.introduce = introduce;
    this.thumbnailURL = thumbnailURL;
  }
}
