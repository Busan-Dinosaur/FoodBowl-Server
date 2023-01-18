package com.dinosaur.foodbowl.domain.user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorCode {

  LOGIN_ID_DUPLICATE("이미 존재하는 로그인 아이디입니다.", HttpStatus.CONFLICT),
  NICKNAME_DUPLICATE("이미 존재하는 닉네임입니다.", HttpStatus.CONFLICT),
  USER_NOT_FOUND("유저를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
  ;

  private final String message;
  private final HttpStatus httpStatus;

  UserErrorCode(String message, HttpStatus httpStatus) {
    this.message = message;
    this.httpStatus = httpStatus;
  }
}
