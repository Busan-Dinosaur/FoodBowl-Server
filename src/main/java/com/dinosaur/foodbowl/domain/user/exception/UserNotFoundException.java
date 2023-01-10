package com.dinosaur.foodbowl.domain.user.exception;

public class UserNotFoundException extends RuntimeException {

  private long userId;

  public UserNotFoundException(long userId) {
    super("ID: '" + userId + "'에 해당하는 유저를 찾을 수 없습니다.");
  }

  public UserNotFoundException(long userId, Throwable cause) {
    super("ID: '" + userId + "'에 해당하는 유저를 찾을 수 없습니다.", cause);
  }
}
