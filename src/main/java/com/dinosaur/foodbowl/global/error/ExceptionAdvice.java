package com.dinosaur.foodbowl.global.error;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> validationExceptionHandler(ConstraintViolationException e) {
    String errorMessage = e.getConstraintViolations()
        .stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.joining(", "));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.from(errorMessage));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> methodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.from(e.getMessage()));
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
    String[] layerErrorFields = errorField.split("\\.");
    return String.format("[%s] %s: %s",
        invalidValue, layerErrorFields[layerErrorFields.length - 1], errorMessage);
  }
}
