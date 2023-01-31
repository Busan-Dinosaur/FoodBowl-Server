package com.dinosaur.foodbowl.global.util.validator.follow;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {NotMeValidator.class})
public @interface NotMe {

  String message() default "Unable to request yourself";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
