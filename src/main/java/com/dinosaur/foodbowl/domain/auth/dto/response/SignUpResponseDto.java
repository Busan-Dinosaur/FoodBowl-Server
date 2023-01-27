package com.dinosaur.foodbowl.domain.auth.dto.response;

import com.dinosaur.foodbowl.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SignUpResponseDto {

  private final Long userId;
  private final String loginId;
  private final String nickname;
  private final String introduce;
  private final String thumbnailURL;

  public static SignUpResponseDto of(User user) {
    return SignUpResponseDto.builder()
        .userId(user.getId())
        .loginId(user.getLoginId())
        .nickname(user.getNickname())
        .introduce(user.getIntroduce())
        .thumbnailURL(user.getThumbnailURL().orElse(null))
        .build();
  }

  @Builder
  private SignUpResponseDto(Long userId, String loginId, String nickname, String introduce,
      String thumbnailURL) {
    this.userId = userId;
    this.loginId = loginId;
    this.nickname = nickname;
    this.introduce = introduce;
    this.thumbnailURL = thumbnailURL;
  }
}
