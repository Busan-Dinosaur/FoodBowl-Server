package com.dinosaur.foodbowl.domain.auth.application;

import static com.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider.REFRESH_TOKEN_VALID_MILLISECOND;

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

  public void saveToken(long userId, String token) {
    redisTemplate.opsForValue()
        .set(userId, token, REFRESH_TOKEN_VALID_MILLISECOND, TimeUnit.MILLISECONDS);
  }

  public void deleteToken(long userId) {
    redisTemplate.delete(userId);
  }
}
