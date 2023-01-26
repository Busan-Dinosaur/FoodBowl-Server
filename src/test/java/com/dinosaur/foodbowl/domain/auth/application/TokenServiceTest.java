package com.dinosaur.foodbowl.domain.auth.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.user.entity.role.Role.RoleType;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TokenServiceTest extends IntegrationTest {

  private final long userId = 1L;
  private final long TEST_TOKEN_VALID_MILLISECOND = 10 * 1000L;
  private final String testToken = "testToken";

  @Nested
  @DisplayName("AccessToken 생성")
  class CreateAccessToken {

    @Test
    @DisplayName("AccessToken 생성에 성공한다.")
    void should_success_when_createAccessToken() {
      String accessToken = tokenService.createAccessToken(userId, RoleType.ROLE_회원);

      Long result = jwtTokenProvider.extractUserIdFromPayload(accessToken);

      assertThat(result).isEqualTo(userId);
    }
  }

  @Nested
  @DisplayName("RefreshToken 생성")
  class CreateRefreshToken {

    @Test
    @DisplayName("RefreshToken 생성에 성공한다.")
    void should_success_when_createRefreshToken() {
      String refreshToken = tokenService.createRefreshToken(userId);

      String savedToken = (String) redisTemplate.opsForValue().get(String.valueOf(userId));

      assertThat(savedToken).isEqualTo(refreshToken);
    }
  }

  @Nested
  @DisplayName("Token 삭제")
  class DeleteToken {

    @Test
    @DisplayName("Token 존재할 때 토큰 삭제에 성공한다.")
    void should_success_when_tokenExist() {
      redisTemplate.opsForValue()
          .set(String.valueOf(userId), testToken, TEST_TOKEN_VALID_MILLISECOND,
              TimeUnit.MILLISECONDS);

      tokenService.deleteToken(userId);

      String result = (String) redisTemplate.opsForValue().get(String.valueOf(userId));

      assertThat(result).isNull();
    }

    @Test
    @DisplayName("Token 존재하지 않을 때 토큰 삭제에 성공한다.")
    void should_success_when_tokenNotExist() {
      tokenService.deleteToken(userId);

      String result = (String) redisTemplate.opsForValue().get(String.valueOf(userId));

      assertThat(result).isNull();
    }
  }

  @Nested
  @DisplayName("Token 검증")
  class ExistsByKey {

    @Test
    @DisplayName("Token 검증에 성공하면 true 반환한다.")
    void should_returnTrue_when_validateSuccess() {
      redisTemplate.opsForValue()
          .set(String.valueOf(userId), testToken, TEST_TOKEN_VALID_MILLISECOND,
              TimeUnit.MILLISECONDS);

      boolean result = tokenService.isValid(userId, testToken);

      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Key 대한 Token 존재하지 않을 때 false 반환한다.")
    void should_returnFalse_when_keyNotExist() {
      boolean result = tokenService.isValid(2L, testToken);

      assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Token 일치하지 않을 때 false 반환한다.")
    void should_returnFalse_when_tokenNotMatch() {
      redisTemplate.opsForValue()
          .set(String.valueOf(userId), "invalid-token", TEST_TOKEN_VALID_MILLISECOND,
              TimeUnit.MILLISECONDS);

      boolean result = tokenService.isValid(userId, testToken);

      assertThat(result).isFalse();
    }
  }
}
