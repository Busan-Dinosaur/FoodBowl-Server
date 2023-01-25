package com.dinosaur.foodbowl.global.config.security.jwt;

import static com.dinosaur.foodbowl.global.config.security.jwt.JwtToken.ACCESS_TOKEN;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtToken.REFRESH_TOKEN;

import com.dinosaur.foodbowl.domain.auth.application.CookieService;
import com.dinosaur.foodbowl.domain.auth.application.TokenService;
import com.dinosaur.foodbowl.domain.user.entity.role.Role.RoleType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

  private final JwtTokenProvider jwtTokenProvider;
  private final TokenService tokenService;
  private final CookieService cookieService;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {
    var accessTokenValidation = jwtTokenProvider.tryCheckTokenValid((HttpServletRequest) request,
        ACCESS_TOKEN);

    if (accessTokenValidation.isValid()) {
      Authentication auth = jwtTokenProvider.getAuthentication(accessTokenValidation.getToken());
      SecurityContextHolder.getContext().setAuthentication(auth);
    } else if (accessTokenValidation.getTokenType() == JwtValidationType.EXPIRED) {
      var refreshTokenValidation = jwtTokenProvider.tryCheckTokenValid(
          (HttpServletRequest) request, REFRESH_TOKEN);
      Long userId = jwtTokenProvider.extractUserIdFromPayload(
          jwtTokenProvider.extractToken((HttpServletRequest) request, ACCESS_TOKEN));
      HttpServletResponse httpResponse = (HttpServletResponse) response;

      if (refreshTokenValidation.isValid() && tokenService.validate(userId,
          refreshTokenValidation.getToken())) {
        String accessToken = tokenService.createAccessToken(userId, RoleType.ROLE_회원);
        String refreshToken = tokenService.createRefreshToken(userId);

        Cookie accessCookie = cookieService.generateCookie(ACCESS_TOKEN.getName(), accessToken,
            (int) ACCESS_TOKEN.getValidMilliSecond() / 1000);
        Cookie refreshCookie = cookieService.generateCookie(REFRESH_TOKEN.getName(), refreshToken,
            (int) REFRESH_TOKEN.getValidMilliSecond() / 1000);

        httpResponse.addCookie(accessCookie);
        httpResponse.addCookie(refreshCookie);

        Authentication auth = jwtTokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(auth);
      }
    } else {
      log.info(accessTokenValidation.getTokenType().getMsg());
    }

    filterChain.doFilter(request, response);
  }
}
