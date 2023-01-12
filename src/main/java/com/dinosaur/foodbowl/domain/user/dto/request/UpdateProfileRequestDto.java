package com.dinosaur.foodbowl.domain.user.dto.request;

import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_INTRODUCE_LENGTH;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateProfileRequestDto {

  @Length(max = MAX_INTRODUCE_LENGTH)
  private String introduce;
  private MultipartFile thumbnail;
}
