package com.dinosaur.foodbowl.global.util.validator.image;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ImageFileValidator.class})
public @interface ImageOrNull {

  String message() default "Invalid image file";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}