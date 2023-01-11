package com.dinosaur.foodbowl.domain.user.exception;

import com.dinosaur.foodbowl.domain.user.exception.signup.LoginIdDuplicateException;
import com.dinosaur.foodbowl.domain.user.exception.signup.NicknameDuplicateException;
import com.dinosaur.foodbowl.global.error.ErrorResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UserExceptionAdvice {

  @ExceptionHandler(BindException.class)
  public ResponseEntity<ErrorResponse> bindException(BindException e) {
    String errorMessage = getErrorMessage(e);
    return ResponseEntity.badRequest()
        .body(ErrorResponse.from(errorMessage));
  }

  @ExceptionHandler(LoginIdDuplicateException.class)
  public ResponseEntity<ErrorResponse> loginIdDuplicateException(LoginIdDuplicateException e) {
    String errorMessage = getErrorMessage(e.getLoginId(), "loginId", e.getMessage());
    return ResponseEntity.badRequest()
        .body(ErrorResponse.from(errorMessage));
  }

  @ExceptionHandler(NicknameDuplicateException.class)
  public ResponseEntity<ErrorResponse> nicknameDuplicateException(NicknameDuplicateException e) {
    String errorMessage = getErrorMessage(e.getNickname(), "nickname", e.getMessage());
    return ResponseEntity.badRequest()
        .body(ErrorResponse.from(errorMessage));
  }

  private static String getErrorMessage(BindException e) {
    BindingResult bindingResult = e.getBindingResult();

    StringBuilder stringBuilder = new StringBuilder();

    for (FieldError fieldError : bindingResult.getFieldErrors()) {
      String errorMessage = getErrorMessage((String) fieldError.getRejectedValue(),
          fieldError.getField(),
          fieldError.getDefaultMessage());
      stringBuilder.append(errorMessage);
      stringBuilder.append(", ");
    }

    if (isExistError(bindingResult)) {
      deleteLastComma(stringBuilder);
    }

    return stringBuilder.toString();
  }

  private static boolean isExistError(BindingResult bindingResult) {
    return !bindingResult.getFieldErrors().isEmpty();
  }

  private static void deleteLastComma(StringBuilder sb) {
    sb.delete(sb.length() - ", ".length(), sb.length());
  }

  public static String getErrorMessage(String invalidValue, String errorField,
      String errorMessage) {
    return String.format("[%s] %s: %s", invalidValue, errorField, errorMessage);
  }
}
