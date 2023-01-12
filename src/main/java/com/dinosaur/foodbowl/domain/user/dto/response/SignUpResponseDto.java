package com.dinosaur.foodbowl.domain.user.dto.response;

import com.dinosaur.foodbowl.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SignUpResponseDto {

  private Long userId;
  private String loginId;
  private String nickname;
  private String introduce;
  private String thumbnailURL;
  private String accessToken;

  public static SignUpResponseDto of(User user, String accessToken) {
    return SignUpResponseDto.builder()
        .userId(user.getId())
        .loginId(user.getLoginId())
        .nickname(user.getNickname())
        .introduce(user.getIntroduce())
        .thumbnailURL(user.getThumbnailURL().orElse(null))
        .accessToken(accessToken)
        .build();
  }

  @Builder
  public SignUpResponseDto(Long userId, String loginId, String nickname, String introduce,
      String thumbnailURL, String accessToken) {
    this.userId = userId;
    this.loginId = loginId;
    this.nickname = nickname;
    this.introduce = introduce;
    this.thumbnailURL = thumbnailURL;
    this.accessToken = accessToken;
  }
}
