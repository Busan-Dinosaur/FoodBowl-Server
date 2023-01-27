package com.dinosaur.foodbowl.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

  private final Object invalidValue;
  private final String fieldName;
  private final HttpStatus httpStatus;
  private final String message;

  public BusinessException(Object invalidValue, String fieldName, ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.invalidValue = invalidValue;
    this.fieldName = fieldName;
    this.httpStatus = errorCode.getHttpStatus();
    this.message = errorCode.getMessage();
  }

  public BusinessException(Object invalidValue, String fieldName, HttpStatus httpStatus,
      String message) {
    super(message);
    this.invalidValue = invalidValue;
    this.fieldName = fieldName;
    this.httpStatus = httpStatus;
    this.message = message;
  }
}
