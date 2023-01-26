package com.dinosaur.foodbowl.domain.auth.application;

import static com.dinosaur.foodbowl.global.config.security.jwt.JwtToken.REFRESH_TOKEN;

import com.dinosaur.foodbowl.domain.user.entity.role.Role.RoleType;
import com.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenService {

  private final RedisTemplate redisTemplate;
  private final JwtTokenProvider jwtTokenProvider;

  public String createAccessToken(long userId, RoleType roleType) {
    return jwtTokenProvider.createAccessToken(userId, roleType);
  }

  public String createRefreshToken(long userId) {
    String refreshToken = jwtTokenProvider.createRefreshToken();
    redisTemplate.opsForValue()
        .set(String.valueOf(userId), refreshToken, REFRESH_TOKEN.getValidMilliSecond(),
            TimeUnit.MILLISECONDS);
    return refreshToken;
  }

  public void deleteToken(long userId) {
    redisTemplate.delete(String.valueOf(userId));
  }

  public boolean isValid(long userId, String token) {
    String refreshToken = (String) redisTemplate.opsForValue().get(String.valueOf(userId));

    if (refreshToken == null || !refreshToken.equals(token)) {
      return false;
    }

    return true;
  }
}
