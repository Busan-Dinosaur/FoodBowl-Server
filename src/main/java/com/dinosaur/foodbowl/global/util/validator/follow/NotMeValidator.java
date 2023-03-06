package com.dinosaur.foodbowl.global.util.validator.follow;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class NotMeValidator implements ConstraintValidator<NotMe, Long> {

    @Override
    public void initialize(NotMe constraintAnnotation) {
    }

    @Override
    public boolean isValid(Long userId, ConstraintValidatorContext context) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        long loginUserId = Long.parseLong(authentication.getName());

        if (userId.equals(loginUserId)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("자신에 대한 요청은 불가능합니다.")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
