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
  TOKEN_INVALID("유효하지 않은 토큰입니다.", HttpStatus.BAD_REQUEST),

  /******* Post *******/
  POST_NOT_FOUND("게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  POST_HAS_NOT_IMAGE("게시글의 사진은 반드시 한 장 이상이어야 합니다.", HttpStatus.BAD_REQUEST),
  POST_NOT_WRITER("게시글 작성자가 아닙니다.", HttpStatus.BAD_REQUEST),


  /******* Comment *******/
  COMMENT_NOT_FOUND("댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  COMMENT_NOT_WRITER("댓글 작성자가 아닙니다.", HttpStatus.BAD_REQUEST),
  ;

  private final String message;
  private final HttpStatus httpStatus;

  ErrorCode(String message, HttpStatus httpStatus) {
    this.message = message;
    this.httpStatus = httpStatus;
  }
}
