package com.dinosaur.foodbowl.domain.user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserException extends RuntimeException {

  private final Object invalidValue;
  private final String fieldName;
  private final HttpStatus httpStatus;
  private final String message;

  public UserException(Object invalidValue, String fieldName, UserErrorCode userErrorCode) {
    super(userErrorCode.getMessage());
    this.invalidValue = invalidValue;
    this.fieldName = fieldName;
    this.httpStatus = userErrorCode.getHttpStatus();
    this.message = userErrorCode.getMessage();
  }
}
