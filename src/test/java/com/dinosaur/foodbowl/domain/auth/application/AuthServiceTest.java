package com.dinosaur.foodbowl.domain.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.user.UserTestHelper.UserBuilder;
import com.dinosaur.foodbowl.domain.auth.dto.request.SignUpRequestDto;
import com.dinosaur.foodbowl.domain.auth.dto.response.SignUpResponseDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.domain.user.exception.UserException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AuthServiceTest extends IntegrationTest {

  private UserBuilder userBuilder;

  @BeforeEach()
  void setUp() {
    userBuilder = userTestHelper.builder();
  }

  @Nested
  @DisplayName("회원가입 테스트")
  class SignUp {

    @Test
    @DisplayName("회원가입을 정상적으로 수행한다.")
    void success_signUp() {

      SignUpRequestDto request = SignUpRequestDto.builder()
          .loginId("TestLoginId")
          .password("TestPassword")
          .nickname("TestNickname")
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
    void throw_exception_exist_loginId() {
      User existUser = userBuilder.loginId("TestLoginId").build();
      SignUpRequestDto request = SignUpRequestDto.builder()
          .loginId(existUser.getLoginId())
          .build();

      assertThatThrownBy(() -> authService.signUp(request))
          .isInstanceOf(UserException.class);
    }

    @Test
    @DisplayName("중복되는 닉네임이 존재하면 예외가 발생한다.")
    void throw_exception_exist_nickname() {
      User existUser = userBuilder.loginId("TestNickname").build();
      SignUpRequestDto request = SignUpRequestDto.builder()
          .nickname(existUser.getNickname())
          .build();

      assertThatThrownBy(() -> authService.signUp(request))
          .isInstanceOf(UserException.class);
    }
  }
}