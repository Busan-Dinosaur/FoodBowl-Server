package com.dinosaur.foodbowl.domain.user.exception;

import com.dinosaur.foodbowl.global.error.ErrorResponse;
import java.util.stream.Collectors;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
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

  @ExceptionHandler(UserException.class)
  public ResponseEntity<ErrorResponse> userException(UserException e) {
    String errorMessage = getErrorMessage(e.getInvalidValue().toString(), e.getFieldName(),
        e.getMessage());
    return ResponseEntity.status(e.getHttpStatus())
        .body(ErrorResponse.from(errorMessage));
  }

  private static String getErrorMessage(BindException e) {
    BindingResult bindingResult = e.getBindingResult();

    return bindingResult.getFieldErrors()
        .stream()
        .map(fieldError -> getErrorMessage((String) fieldError.getRejectedValue(),
            fieldError.getField(),
            fieldError.getDefaultMessage()))
        .collect(Collectors.joining(", "));
  }

  public static String getErrorMessage(String invalidValue, String errorField,
      String errorMessage) {
    return String.format("[%s] %s: %s", invalidValue, errorField, errorMessage);
  }
}
