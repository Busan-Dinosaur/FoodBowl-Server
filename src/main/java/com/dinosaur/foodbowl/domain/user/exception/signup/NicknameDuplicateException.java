package com.dinosaur.foodbowl.domain.user.exception.signup;

public class NicknameDuplicateException extends RuntimeException {

  private String nickname;

  public NicknameDuplicateException(String nickname) {
    super(getMessage(nickname));
  }

  public static String getMessage(String nickname) {
    return "닉네임: '" + nickname + "'가 중복됩니다.";
  }
}
