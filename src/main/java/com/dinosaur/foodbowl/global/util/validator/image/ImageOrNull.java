package com.dinosaur.foodbowl.global.util.validator.image;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ImageFileValidator.class})
public @interface ImageOrNull {

  String message() default "Invalid image file";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}