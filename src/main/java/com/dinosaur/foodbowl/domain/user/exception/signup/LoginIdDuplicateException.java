package com.dinosaur.foodbowl.domain.user.exception.signup;

import com.dinosaur.foodbowl.domain.user.exception.UserErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class LoginIdDuplicateException extends RuntimeException {

  private final String loginId;
  private final HttpStatus httpStatus;
  private final String message;

  public LoginIdDuplicateException(String loginId, UserErrorCode userErrorCode) {
    super(userErrorCode.getMessage());
    this.loginId = loginId;
    this.httpStatus = userErrorCode.getHttpStatus();
    this.message = userErrorCode.getMessage();
  }
}
