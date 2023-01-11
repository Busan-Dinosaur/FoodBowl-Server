package com.dinosaur.foodbowl.domain.user.exception.signup;

public class LoginIdInvalidFormatException extends RuntimeException {

  private String loginId;

  public LoginIdInvalidFormatException(String loginId) {
    super("로그인 아이디는 4~12자 영어, 숫자, '_'만 가능합니다. 입력하신 로그인 아이디: '" + loginId + "'");
  }
}
