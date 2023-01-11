package com.dinosaur.foodbowl.domain.user.exception.signup;

public class PasswordInvalidFormatException extends RuntimeException {

  private String password;

  public PasswordInvalidFormatException(String password) {
    super("비밀번호는 8~20자여야 하고 영어, 숫자가 포함되어야 합니다. 입력하신 비밀번호: '" + password + "'");
  }
}
