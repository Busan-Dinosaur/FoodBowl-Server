package com.dinosaur.foodbowl.domain.user.exception.signup;

public class NicknameInvalidFormatException extends RuntimeException {

  private String nickname;

  public NicknameInvalidFormatException(String nickname) {
    super("닉네임은 1~16자 영어, 숫자, '_'만 가능합니다. 입력하신 로그인 아이디: '" + nickname + "'");
  }
}
