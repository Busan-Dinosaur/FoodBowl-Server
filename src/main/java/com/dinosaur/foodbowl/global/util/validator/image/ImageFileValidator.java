package com.dinosaur.foodbowl.global.util.validator.image;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.springframework.web.multipart.MultipartFile;

public class ImageFileValidator implements ConstraintValidator<ImageOrNull, MultipartFile> {

  @Override
  public void initialize(ImageOrNull constraintAnnotation) {
  }

  @Override
  public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
    if (multipartFile == null) {
      return true;
    }
    if (isNotImageFile(multipartFile)) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate("이미지 파일이 아닙니다.")
          .addConstraintViolation();

      return false;
    }
    return true;
  }

  private boolean isNotImageFile(MultipartFile file) {
    try {
      InputStream originalInputStream = new BufferedInputStream(file.getInputStream());
      return ImageIO.read(originalInputStream) == null;
    } catch (IOException e) {
      return true;
    }
  }
}
