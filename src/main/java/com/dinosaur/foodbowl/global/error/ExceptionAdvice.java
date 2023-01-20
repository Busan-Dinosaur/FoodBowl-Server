package com.dinosaur.foodbowl.global.error;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {

  @ExceptionHandler(BindException.class)
  public ResponseEntity<ErrorResponse> bindException(BindException e) {
    String errorMessage = getErrorMessage(e);
    return ResponseEntity.badRequest()
        .body(ErrorResponse.from(errorMessage));
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> businessException(BusinessException e) {
    String errorMessage = getErrorMessage(e.getInvalidValue().toString(), e.getFieldName(),
        e.getMessage());
    return ResponseEntity.status(e.getHttpStatus())
        .body(ErrorResponse.from(errorMessage));
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map validationExceptionHandler(ConstraintViolationException e) {
    List<String> errors = e.getConstraintViolations()
        .stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
    return Collections.singletonMap("errors", errors);
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
