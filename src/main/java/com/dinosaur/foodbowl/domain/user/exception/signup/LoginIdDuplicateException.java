package com.dinosaur.foodbowl.domain.user.exception.signup;

import lombok.Getter;

@Getter
public class LoginIdDuplicateException extends RuntimeException {

  private String loginId;

  public LoginIdDuplicateException(String loginId) {
    super(getMessage(loginId));
    this.loginId = loginId;
  }

  public static String getMessage(String loginId) {
    return String.format("로그인 아이디: '%s'가 중복됩니다.", loginId);
  }
}
