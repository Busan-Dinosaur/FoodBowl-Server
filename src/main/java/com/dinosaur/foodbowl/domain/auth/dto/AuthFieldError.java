package com.dinosaur.foodbowl.domain.auth.dto;

public enum AuthFieldError {

  LOGIN_ID_INVALID(Message.LOGIN_ID_INVALID),
  PASSWORD_INVALID(Message.PASSWORD_INVALID),
  ;

  private final String message;

  AuthFieldError(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public static class Message {

    public static final String LOGIN_ID_INVALID = "로그인 아이디는 4~12자 영어, 숫자, '_'만 가능합니다.";
    public static final String PASSWORD_INVALID = "비밀번호는 8~20자여야 하고 영어, 숫자가 포함되어야 합니다.";
  }
}
