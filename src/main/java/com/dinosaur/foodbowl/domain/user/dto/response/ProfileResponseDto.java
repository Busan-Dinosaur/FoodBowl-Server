package com.dinosaur.foodbowl.domain.user.dto.response;

import com.dinosaur.foodbowl.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class ProfileResponseDto {

  private final long userId;
  private final String nickname;
  private final String introduce;
  private final long followerCount;
  private final long followingCount;
  private final long postCount;
  private final String thumbnailURL;

  public static ProfileResponseDto of(User user, long followerCount, long followingCount,
      long postCount) {
    return ProfileResponseDto.builder()
        .userId(user.getId())
        .nickname(user.getNickname())
        .introduce(user.getIntroduce())
        .followerCount(followerCount)
        .followingCount(followingCount)
        .postCount(postCount)
        .thumbnailURL(user.getThumbnailURL().orElse(null))
        .build();
  }

  @Builder
  private ProfileResponseDto(long userId, @NonNull String nickname, @NonNull String introduce,
      long followerCount, long followingCount, long postCount, String thumbnailURL) {
    this.userId = userId;
    this.followerCount = followerCount;
    this.followingCount = followingCount;
    this.nickname = nickname;
    this.introduce = introduce;
    this.postCount = postCount;
    this.thumbnailURL = thumbnailURL;
  }
}
