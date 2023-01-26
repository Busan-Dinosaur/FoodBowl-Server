package com.dinosaur.foodbowl.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

  /******* User *******/
  LOGIN_ID_DUPLICATE("이미 존재하는 로그인 아이디입니다.", HttpStatus.CONFLICT),
  NICKNAME_DUPLICATE("이미 존재하는 닉네임입니다.", HttpStatus.CONFLICT),
  USER_NOT_FOUND("유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  PASSWORD_NOT_MATCH("비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
  TYPE_INVALID("유효하지 않은 역할입니다.", HttpStatus.BAD_REQUEST),

  /******* Auth *******/
  TOKEN_INVALID("유효하지 않은 토큰입니다.", HttpStatus.BAD_REQUEST);

  private final String message;
  private final HttpStatus httpStatus;

  ErrorCode(String message, HttpStatus httpStatus) {
    this.message = message;
    this.httpStatus = httpStatus;
  }
}
