package com.dinosaur.foodbowl.domain.user.dto.request;

import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_INTRODUCE_LENGTH;
import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_LOGIN_ID_LENGTH;
import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_NICKNAME_LENGTH;
import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_PASSWORD_LENGTH;

import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignupRequestDto {

  @Length(max = MAX_LOGIN_ID_LENGTH)
  private String loginId;
  @Length(max = MAX_PASSWORD_LENGTH)
  private String password;
  @Length(max = MAX_NICKNAME_LENGTH)
  private String nickname;
  @Length(max = MAX_INTRODUCE_LENGTH)
  private String introduce;
  private MultipartFile thumbnail;

  public User toEntity(Thumbnail thumbnail, PasswordEncoder passwordEncoder) {
    return User.builder()
        .loginId(loginId)
        .password(passwordEncoder.encode(password))
        .nickname(nickname)
        .introduce(introduce)
        .thumbnail(thumbnail)
        .build();
  }
}
