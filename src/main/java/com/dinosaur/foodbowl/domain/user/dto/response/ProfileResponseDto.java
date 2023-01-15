package com.dinosaur.foodbowl.domain.user.dto.response;

import com.dinosaur.foodbowl.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProfileResponseDto {

  private long userId;
  private String nickname;
  private String introduce;
  private long followerCount;
  private long followingCount;
  private String thumbnailURL;

  public static ProfileResponseDto of(User user, long followerCount, long followingCount) {
    return ProfileResponseDto.builder()
        .userId(user.getId())
        .nickname(user.getNickname())
        .introduce(user.getIntroduce())
        .followerCount(followerCount)
        .followerCount(followingCount)
        .thumbnailURL(user.getThumbnailURL().orElse(null))
        .build();
  }

  @Builder
  public ProfileResponseDto(Long userId, String nickname, String introduce,
      long followerCount, long followingCount, String thumbnailURL) {
    this.userId = userId;
    this.followerCount = followerCount;
    this.followingCount = followingCount;
    this.nickname = nickname;
    this.introduce = introduce;
    this.thumbnailURL = thumbnailURL;
  }
}
