package com.dinosaur.foodbowl.domain.auth.application;

import static com.dinosaur.foodbowl.global.error.ErrorCode.NICKNAME_DUPLICATE;
import static com.dinosaur.foodbowl.global.error.ErrorCode.PASSWORD_NOT_MATCH;
import static com.dinosaur.foodbowl.global.error.ErrorCode.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.auth.dto.request.LoginRequestDto;
import com.dinosaur.foodbowl.domain.auth.dto.request.SignUpRequestDto;
import com.dinosaur.foodbowl.domain.auth.dto.response.CheckResponseDto;
import com.dinosaur.foodbowl.domain.auth.dto.response.SignUpResponseDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.domain.user.entity.embedded.Nickname;
import com.dinosaur.foodbowl.global.error.BusinessException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AuthServiceTest extends IntegrationTest {

  @Nested
  @DisplayName("회원가입 테스트")
  class SignUp {

    @Test
    @DisplayName("회원가입을 정상적으로 수행한다.")
    void should_success_when_signUp() {

      SignUpRequestDto request = SignUpRequestDto.builder()
          .loginId("TestLoginId")
          .password("TestPassword")
          .nickname(Nickname.from("TestNickname"))
          .build();

      SignUpResponseDto response = authService.signUp(request);
      em.flush();
      em.clear();

      Optional<User> findUser = userRepository.findById(response.getUserId());
      assertThat(findUser).isNotEmpty();
      assertThat(findUser.get().getLoginId()).isEqualTo(response.getLoginId());
      assertThat(findUser.get().getNickname()).isEqualTo(response.getNickname());
    }

    @Test
    @DisplayName("중복되는 아이디가 존재하면 예외가 발생한다.")
    void should_throwException_when_existDuplicateLoginId() {
      User existUser = userTestHelper.builder().loginId("TestLoginId").build();
      SignUpRequestDto request = SignUpRequestDto.builder()
          .loginId(existUser.getLoginId())
          .build();

      assertThatThrownBy(() -> authService.signUp(request))
          .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("중복되는 닉네임이 존재하면 예외가 발생한다.")
    void should_throwException_when_existDuplicateNickname() {
      User existUser = userTestHelper.builder().loginId("TestNickname").build();
      SignUpRequestDto request = SignUpRequestDto.builder()
          .nickname(existUser.getNickname())
          .build();

      assertThatThrownBy(() -> authService.signUp(request))
          .isInstanceOf(BusinessException.class);
    }
  }

  @Nested
  @DisplayName("로그인 테스트")
  class Login {

    @Test
    @DisplayName("로그인을 성공적으로 수행한다.")
    void should_success_when_login() {
      String loginId = "testLoginId";
      String password = "testPassword";
      String encodePassword = passwordEncoder.encode(password);
      User user = userTestHelper.builder().loginId(loginId).password(encodePassword).build();

      LoginRequestDto request = LoginRequestDto.builder()
          .loginId(loginId)
          .password(password)
          .build();

      long userId = authService.login(request);

      assertThat(user.getId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("로그인 아이디 유저가 존재하지 않으면 예외가 발생한다.")
    void should_throwException_when_loginIdNotExist() {
      String loginId = "testLoginId";
      String password = "testPassword";

      LoginRequestDto request = LoginRequestDto.builder()
          .loginId(loginId)
          .password(password)
          .build();

      assertThatThrownBy(() -> authService.login(request))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining(USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 예외가 발생한다.")
    void should_throwException_when_passwordNotMatch() {
      String loginId = "testLoginId";
      String password = "testPassword";
      String encodePassword = passwordEncoder.encode(password);
      userTestHelper.builder().loginId(loginId).password(encodePassword).build();

      LoginRequestDto request = LoginRequestDto.builder()
          .loginId(loginId)
          .password("wrongPassword")
          .build();

      assertThatThrownBy(() -> authService.login(request))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining(PASSWORD_NOT_MATCH.getMessage());
    }
  }

  @Nested
  @DisplayName("닉네임 유효성 및 중복 검사")
  class CheckNickname {

    @Test
    @DisplayName("닉네임 유효성 및 중복 검사에 통과한다.")
    void should_success_when_checkNickname() {
      String nickname = "hello";

      CheckResponseDto checkResponseDto = authService.checkNickname(nickname);

      assertThat(checkResponseDto.isAvailable()).isTrue();
      assertThat(checkResponseDto.getMessage()).isEqualTo("사용 가능한 닉네임입니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "abcde###", "oh-my-zsh", "한글을_사랑합시다", "cant blank",
        "01234567890123456", "         ", "012345678901234567890"})
    @DisplayName("닉네임 유효성 검사에 실패한다.")
    void should_fail_when_nicknameIsNotValid(String nickname) {
      CheckResponseDto checkResponseDto = authService.checkNickname(nickname);

      assertThat(checkResponseDto.isAvailable()).isFalse();
      assertThat(checkResponseDto.getMessage()).isEqualTo(Nickname.NICKNAME_INVALID);
    }

    @Test
    @DisplayName("닉네임 중복 검사에 실패한다.")
    void should_fail_when_nicknameIsDuplicate() {
      String nickname = "hello";
      userTestHelper.builder()
          .nickname(nickname)
          .build();

      CheckResponseDto checkResponseDto = authService.checkNickname(nickname);

      assertThat(checkResponseDto.isAvailable()).isFalse();
      assertThat(checkResponseDto.getMessage()).isEqualTo(NICKNAME_DUPLICATE.getMessage());
    }
  }
}
