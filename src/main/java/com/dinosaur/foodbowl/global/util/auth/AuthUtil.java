package com.dinosaur.foodbowl.global.util.auth;

import com.dinosaur.foodbowl.domain.user.dao.UserFindDao;
import com.dinosaur.foodbowl.domain.user.entity.User;
import jakarta.annotation.PostConstruct;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthUtil {

  private final UserFindDao userFindDao;

  @Value("${spring.jwt.secret}")
  private String secretKey;

  @PostConstruct
  protected void init() {
    secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
  }

  public Long getUserIdByJWT() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return Long.parseLong(authentication.getName());
  }

  public User getUserByJWT() {
    Long userId = getUserIdByJWT();
    return userFindDao.findById(userId);
  }
}