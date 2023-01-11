package com.dinosaur.foodbowl.domain.user.dto.request;

import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_INTRODUCE_LENGTH;
import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_LOGIN_ID_LENGTH;
import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_NICKNAME_LENGTH;

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
public class SignUpRequestDto {

  public static final String LOGIN_ID_INVALID = "로그인 아이디는 4~12자 영어, 숫자, '_'만 가능합니다.";
  public static final String PASSWORD_INVALID = "비밀번호는 8~20자여야 하고 영어, 숫자가 포함되어야 합니다.";
  public static final String NICKNAME_INVALID = "닉네임은 1~16자 한글, 영어, 숫자만 가능합니다.";

  @Length(max = MAX_LOGIN_ID_LENGTH)
  private String loginId;
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
