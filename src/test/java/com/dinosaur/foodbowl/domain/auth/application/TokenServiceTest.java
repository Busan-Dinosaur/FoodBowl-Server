package com.dinosaur.foodbowl.domain.auth.application;

import static com.dinosaur.foodbowl.global.config.security.jwt.JwtConstants.CLAIMS_SUB;
import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.user.entity.Role.RoleType;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TokenServiceTest extends IntegrationTest {

  private final long userId = 1L;
  private final long TEST_TOKEN_VALID_MILLISECOND = 10 * 1000L;
  private final String testToken = "testToken";

  @Nested
  class 엑세스_토큰_생성 {

    @Test
    void 엑세스_토큰_생성에_성공한다() {
      String accessToken = tokenService.createAccessToken(userId, RoleType.ROLE_회원);

      Long result = Long.parseLong(
          jwtTokenProvider.extractPayload(accessToken, CLAIMS_SUB.getName()).toString());

      assertThat(result).isEqualTo(userId);
    }
  }

  @Nested
  class 리프레쉬_토큰_생성 {

    @Test
    void 리프레쉬_토큰_생성에_성공한다() {
      String refreshToken = tokenService.createRefreshToken(userId);

      String savedToken = (String) redisTemplate.opsForValue().get(String.valueOf(userId));

      assertThat(savedToken).isEqualTo(refreshToken);
    }
  }

  @Nested
  class 토큰_삭제 {

    @Test
    void 토큰이_존재한다면_토큰_삭제에_성공한다() {
      redisTemplate.opsForValue()
          .set(String.valueOf(userId), testToken, TEST_TOKEN_VALID_MILLISECOND,
              TimeUnit.MILLISECONDS);

      tokenService.deleteToken(userId);

      String result = (String) redisTemplate.opsForValue().get(String.valueOf(userId));

      assertThat(result).isNull();
    }

    @Test
    void 토큰이_존재하지_않으면_토큰_삭제에_성공한다() {
      tokenService.deleteToken(userId);

      String result = (String) redisTemplate.opsForValue().get(String.valueOf(userId));

      assertThat(result).isNull();
    }
  }

  @Nested
  class 토큰_검증 {

    @Test
    void 토큰_검증에_성공하면_true_반환한다() {
      redisTemplate.opsForValue()
          .set(String.valueOf(userId), testToken, TEST_TOKEN_VALID_MILLISECOND,
              TimeUnit.MILLISECONDS);

      boolean result = tokenService.isValid(userId, testToken);

      assertThat(result).isTrue();
    }

    @Test
    void 토큰이_존재하지_않으면_false_반환한다() {
      boolean result = tokenService.isValid(2L, testToken);

      assertThat(result).isFalse();
    }

    @Test
    void 토큰이_일치하지_않으면_false_반환한다() {
      redisTemplate.opsForValue()
          .set(String.valueOf(userId), "invalid-token", TEST_TOKEN_VALID_MILLISECOND,
              TimeUnit.MILLISECONDS);

      boolean result = tokenService.isValid(userId, testToken);

      assertThat(result).isFalse();
    }
  }
}
