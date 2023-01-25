package com.dinosaur.foodbowl.domain.auth.application;

import static com.dinosaur.foodbowl.global.config.security.jwt.JwtValidationType.EMPTY;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtValidationType.VALID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.global.config.security.dto.TokenValidationDto;
import com.dinosaur.foodbowl.global.error.BusinessException;
import com.dinosaur.foodbowl.global.error.ErrorCode;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class JwtTokenServiceTest extends IntegrationTest {

  @Nested
  @DisplayName("리프레쉬 토큰 검증")
  class Validate {

    private final long userId = 1L;
    private final long TEST_TOKEN_VALID_MILLISECOND = 5 * 1000L;

    @Test
    @DisplayName("토큰 검증에 성공한다.")
    void Should_Success_When_ValidToken() {
      String refreshToken = jwtTokenProvider.createRefreshToken();
      redisTemplate.opsForValue()
          .set(String.valueOf(userId), refreshToken, TEST_TOKEN_VALID_MILLISECOND,
              TimeUnit.MILLISECONDS);
      TokenValidationDto tokenValidationDto = TokenValidationDto.of(true, VALID, refreshToken);

      tokenService.validate(userId, tokenValidationDto);
    }

    @Test
    @DisplayName("유저의 리프레쉬 토큰이 존재하지 않는 경우 예외가 발생한다.")
    void Should_ThrowException_When_RefreshTokenNotExist() {
      TokenValidationDto tokenValidationDto = TokenValidationDto.of(false, EMPTY);

      assertThatThrownBy(() -> tokenService.validate(userId, tokenValidationDto))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining(EMPTY.getMsg());
    }

    @Test
    @DisplayName("유저의 리프레쉬 토큰과 일치하지 않는 경우 예외가 발생한다.")
    void Should_ThrowException_When_RefreshTokenNotMatch() {
      String refreshToken = jwtTokenProvider.createRefreshToken();
      redisTemplate.opsForValue()
          .set(String.valueOf(userId), refreshToken, TEST_TOKEN_VALID_MILLISECOND,
              TimeUnit.MILLISECONDS);
      TokenValidationDto tokenValidationDto = TokenValidationDto.of(true, VALID, "refreshToken");

      assertThatThrownBy(() -> tokenService.validate(userId, tokenValidationDto))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining(ErrorCode.TOKEN_INVALID.getMessage());
    }
  }
}
