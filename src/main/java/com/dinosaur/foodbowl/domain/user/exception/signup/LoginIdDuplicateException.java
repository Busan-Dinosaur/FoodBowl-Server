package com.dinosaur.foodbowl.domain.user.exception.signup;

public class LoginIdDuplicateException extends RuntimeException {

  private String loginId;

  public LoginIdDuplicateException(String loginId) {
    super("로그인 아이디: '" + loginId + "'가 중복됩니다.");
  }
}
