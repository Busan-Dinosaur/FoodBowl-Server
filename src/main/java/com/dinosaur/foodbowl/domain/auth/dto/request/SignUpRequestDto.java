package com.dinosaur.foodbowl.domain.auth.dto.request;

import static com.dinosaur.foodbowl.domain.auth.dto.AuthFieldError.Message;
import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_INTRODUCE_LENGTH;

import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.util.validator.image.ImageOrNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SignUpRequestDto {

  @Pattern(regexp = "^[a-zA-Z0-9_]{4,12}", message = Message.LOGIN_ID_INVALID)
  private String loginId;
  @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$", message = Message.PASSWORD_INVALID)
  private String password;
  @Pattern(regexp = "^[a-zA-Z0-9가-힣]{1,16}", message = Message.NICKNAME_INVALID)
  private String nickname;
  @Length(max = MAX_INTRODUCE_LENGTH)
  private String introduce;
  @ImageOrNull
  private MultipartFile thumbnail;

  @Builder
  private SignUpRequestDto(String loginId, String password, String nickname,
      String introduce, MultipartFile thumbnail) {
    this.loginId = loginId;
    this.password = password;
    this.nickname = nickname;
    this.introduce = introduce;
    this.thumbnail = thumbnail;
  }

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
