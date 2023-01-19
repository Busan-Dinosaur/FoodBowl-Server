package com.dinosaur.foodbowl.global.util.validator.follow;

import com.dinosaur.foodbowl.global.util.auth.AuthUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class IsMeValidator implements ConstraintValidator<NotMe, Long> {

  @Autowired
  private AuthUtil authUtil;

  @Override
  public void initialize(NotMe constraintAnnotation) {
  }

  @Override
  public boolean isValid(Long userId, ConstraintValidatorContext context) {
    if (userId.equals(authUtil.getUserIdByJWT())) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate("자신에 대한 요청은 불가능합니다.")
          .addConstraintViolation();
      return false;
    }
    return true;
  }
}
