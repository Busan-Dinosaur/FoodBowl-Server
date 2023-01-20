package com.dinosaur.foodbowl.domain.auth.application;

import static com.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider.REFRESH_TOKEN_VALID_MILLISECOND;
import static com.dinosaur.foodbowl.global.error.ErrorCode.TOKEN_INVALID;

import com.dinosaur.foodbowl.global.config.security.dto.TokenValidationDto;
import com.dinosaur.foodbowl.global.error.BusinessException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenService {

  private final RedisTemplate redisTemplate;

  public void saveToken(long userId, String token) {
    redisTemplate.opsForValue()
        .set(String.valueOf(userId), token, REFRESH_TOKEN_VALID_MILLISECOND, TimeUnit.MILLISECONDS);
  }

  public void deleteToken(long userId) {
    redisTemplate.delete(userId);
  }

  public void validate(long userId, TokenValidationDto tokenValidationDto) {
    String token = tokenValidationDto.getToken();

    if (!tokenValidationDto.isValid()) {
      throw new BusinessException(token, "token", HttpStatus.BAD_REQUEST,
          tokenValidationDto.getTokenType().getMsg());
    }

    String refreshToken = (String) redisTemplate.opsForValue().get(String.valueOf(userId));

    if (refreshToken == null || !refreshToken.equals(token)) {
      throw new BusinessException(token, "token", TOKEN_INVALID);
    }
  }
}
