package com.dinosaur.foodbowl.domain.user.exception.signup;

import com.dinosaur.foodbowl.domain.user.exception.UserErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NicknameDuplicateException extends RuntimeException {

  private final String nickname;
  private final HttpStatus httpStatus;
  private final String message;

  public NicknameDuplicateException(String nickname, UserErrorCode userErrorCode) {
    super(userErrorCode.getMessage());
    this.nickname = nickname;
    this.httpStatus = userErrorCode.getHttpStatus();
    this.message = userErrorCode.getMessage();
  }
}
