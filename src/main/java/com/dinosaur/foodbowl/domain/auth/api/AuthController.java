package com.dinosaur.foodbowl.domain.auth.api;

import static com.dinosaur.foodbowl.domain.user.entity.role.Role.RoleType.ROLE_회원;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtToken.ACCESS_TOKEN;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtToken.REFRESH_TOKEN;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider.ACCESS_TOKEN_VALID_MILLISECOND;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider.REFRESH_TOKEN_VALID_MILLISECOND;

import com.dinosaur.foodbowl.domain.auth.application.AuthService;
import com.dinosaur.foodbowl.domain.auth.application.TokenService;
import com.dinosaur.foodbowl.domain.auth.dto.request.LoginRequestDto;
import com.dinosaur.foodbowl.domain.auth.dto.request.SignUpRequestDto;
import com.dinosaur.foodbowl.domain.auth.dto.response.SignUpResponseDto;
import com.dinosaur.foodbowl.global.config.security.dto.TokenValidationDto;
import com.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider;
import com.dinosaur.foodbowl.global.util.resolver.LoginUserId;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final TokenService tokenService;
  private final JwtTokenProvider jwtTokenProvider;

  @PostMapping("/sign-up")
  public ResponseEntity<SignUpResponseDto> signUp(@Valid @ModelAttribute SignUpRequestDto request,
      HttpServletResponse response) {
    SignUpResponseDto signUpResponseDto = authService.signUp(request);

    setAccessToken(response, signUpResponseDto.getUserId());
    setRefreshToken(response, signUpResponseDto.getUserId());

    return ResponseEntity.created(URI.create("/users/" + signUpResponseDto.getUserId()))
        .body(signUpResponseDto);
  }

  private void setAccessToken(HttpServletResponse response, long userId) {
    String accessToken = jwtTokenProvider.createAccessToken(userId, ROLE_회원);
    Cookie cookie = generateCookie(ACCESS_TOKEN.getName(), accessToken,
        ACCESS_TOKEN_VALID_MILLISECOND);
    response.addCookie(cookie);
  }

  private void setRefreshToken(HttpServletResponse response, long userId) {
    String refreshToken = jwtTokenProvider.createRefreshToken();
    tokenService.saveToken(userId, refreshToken);
    Cookie cookie = generateCookie(REFRESH_TOKEN.getName(), refreshToken,
        REFRESH_TOKEN_VALID_MILLISECOND);
    response.addCookie(cookie);
  }

  private Cookie generateCookie(String name, String value, long expire) {
    Cookie cookie = new Cookie(name, value);
    cookie.setHttpOnly(true);
    cookie.setMaxAge((int) expire / 1000);
    return cookie;
  }

  @PostMapping("/log-in")
  public ResponseEntity<Void> login(@Valid @RequestBody LoginRequestDto loginRequestDto,
      HttpServletResponse response) {
    long userId = authService.login(loginRequestDto);

    setAccessToken(response, userId);
    setRefreshToken(response, userId);

    return ResponseEntity.ok().build();
  }

  @PostMapping("/log-out")
  public ResponseEntity<Void> logout(@LoginUserId Long userId) {
    tokenService.deleteToken(userId);

    return ResponseEntity.noContent().build();
  }

  @PostMapping("/refresh")
  public ResponseEntity<Void> refresh(@LoginUserId Long userId, HttpServletRequest request,
      HttpServletResponse response) {
    TokenValidationDto tokenValidationDto = jwtTokenProvider.tryCheckTokenValid(request,
        REFRESH_TOKEN);

    tokenService.validate(userId, tokenValidationDto);

    setAccessToken(response, userId);
    setRefreshToken(response, userId);

    return ResponseEntity.noContent().build();
  }
}
