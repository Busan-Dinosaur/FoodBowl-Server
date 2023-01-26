package com.dinosaur.foodbowl.domain.auth.api;

import static com.dinosaur.foodbowl.domain.user.entity.role.Role.RoleType.ROLE_회원;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtToken.ACCESS_TOKEN;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtToken.REFRESH_TOKEN;

import com.dinosaur.foodbowl.domain.auth.application.AuthService;
import com.dinosaur.foodbowl.global.util.CookieUtils;
import com.dinosaur.foodbowl.domain.auth.application.TokenService;
import com.dinosaur.foodbowl.domain.auth.dto.request.LoginRequestDto;
import com.dinosaur.foodbowl.domain.auth.dto.request.SignUpRequestDto;
import com.dinosaur.foodbowl.domain.auth.dto.response.SignUpResponseDto;
import com.dinosaur.foodbowl.global.util.resolver.LoginUserId;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final TokenService tokenService;
  private final CookieUtils cookieUtils;

  @GetMapping("/health-check")
  public String healthCheck() {
    return "health-check";
  }

  @PostMapping("/sign-up")
  public ResponseEntity<SignUpResponseDto> signUp(@Valid @ModelAttribute SignUpRequestDto request,
      HttpServletResponse response) {
    SignUpResponseDto signUpResponseDto = authService.signUp(request);

    String accessToken = tokenService.createAccessToken(signUpResponseDto.getUserId(), ROLE_회원);
    String refreshToken = tokenService.createRefreshToken(signUpResponseDto.getUserId());

    Cookie accessCookie = cookieUtils.generateCookie(ACCESS_TOKEN.getName(), accessToken,
        (int) ACCESS_TOKEN.getValidMilliSecond() / 1000);
    Cookie refreshCookie = cookieUtils.generateCookie(REFRESH_TOKEN.getName(), refreshToken,
        (int) REFRESH_TOKEN.getValidMilliSecond() / 1000);

    response.addCookie(accessCookie);
    response.addCookie(refreshCookie);

    return ResponseEntity.created(URI.create("/users/" + signUpResponseDto.getUserId()))
        .body(signUpResponseDto);
  }

  @PostMapping("/log-in")
  public ResponseEntity<Void> login(@Valid @RequestBody LoginRequestDto loginRequestDto,
      HttpServletResponse response) {
    long userId = authService.login(loginRequestDto);

    String accessToken = tokenService.createAccessToken(userId, ROLE_회원);
    String refreshToken = tokenService.createRefreshToken(userId);

    Cookie accessCookie = cookieUtils.generateCookie(ACCESS_TOKEN.getName(), accessToken,
        (int) ACCESS_TOKEN.getValidMilliSecond() / 1000);
    Cookie refreshCookie = cookieUtils.generateCookie(REFRESH_TOKEN.getName(), refreshToken,
        (int) REFRESH_TOKEN.getValidMilliSecond() / 1000);

    response.addCookie(accessCookie);
    response.addCookie(refreshCookie);

    return ResponseEntity.ok().build();
  }

  @PostMapping("/log-out")
  public ResponseEntity<Void> logout(@LoginUserId Long userId) {
    tokenService.deleteToken(userId);

    return ResponseEntity.noContent().build();
  }
}
