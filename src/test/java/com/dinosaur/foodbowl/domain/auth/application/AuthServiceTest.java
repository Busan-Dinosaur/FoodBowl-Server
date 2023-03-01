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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AuthServiceTest extends IntegrationTest {

  @Nested
  class 회원가입 {

    @Test
    void 회원가입을_정상적으로_수행한다() {

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
    void 중복되는_아이디가_존재하면_예외가_발생한다() {
      User existUser = userTestHelper.builder().loginId("TestLoginId").build();
      SignUpRequestDto request = SignUpRequestDto.builder()
          .loginId(existUser.getLoginId())
          .build();

      assertThatThrownBy(() -> authService.signUp(request))
          .isInstanceOf(BusinessException.class);
    }

    @Test
    void 중복되는_닉네임이_존재하면_예외가_발생한다() {
      User existUser = userTestHelper.builder().loginId("TestNickname").build();
      SignUpRequestDto request = SignUpRequestDto.builder()
          .nickname(existUser.getNickname())
          .build();

      assertThatThrownBy(() -> authService.signUp(request))
          .isInstanceOf(BusinessException.class);
    }
  }

  @Nested
  class 로그인 {

    @Test
    void 로그인을_성공적으로_수행한다() {
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
    void 로그인_아이디가_존재하지_않으면_예외가_발생한다() {
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
    void 비밀번호가_일치하지_않으면_예외가_발생한다() {
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
  class 닉네임_유효성_및_중복_검사 {

    @Test
    void 닉네임_유효성_및_중복_검사를_통과한다() {
      String nickname = "hello";

      CheckResponseDto checkResponseDto = authService.checkNickname(nickname);

      assertThat(checkResponseDto.isAvailable()).isTrue();
      assertThat(checkResponseDto.getMessage()).isEqualTo("사용 가능한 닉네임입니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "abcde###", "oh-my-zsh", "한글을_사랑합시다", "cant blank",
        "01234567890123456", "         ", "012345678901234567890"})
    void 유효하지_않은_닉네임이라면_유효성_검사에_실패한다(String nickname) {
      CheckResponseDto checkResponseDto = authService.checkNickname(nickname);

      assertThat(checkResponseDto.isAvailable()).isFalse();
      assertThat(checkResponseDto.getMessage()).isEqualTo(Nickname.NICKNAME_INVALID);
    }

    @Test
    void 중복된_닉네임은_중복_검사에_실패한다() {
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
