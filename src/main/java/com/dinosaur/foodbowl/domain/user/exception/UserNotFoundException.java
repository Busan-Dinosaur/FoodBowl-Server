package com.dinosaur.foodbowl.domain.user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserNotFoundException extends RuntimeException {

  private final long userId;
  private final HttpStatus httpStatus;
  private final String message;

  public UserNotFoundException(long userId, UserErrorCode userErrorCode) {
    super(userErrorCode.getMessage());
    this.userId = userId;
    this.httpStatus = userErrorCode.getHttpStatus();
    this.message = userErrorCode.getMessage();
  }
}
