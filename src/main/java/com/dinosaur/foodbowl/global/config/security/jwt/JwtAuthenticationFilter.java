package com.dinosaur.foodbowl.global.config.security.jwt;

import static com.dinosaur.foodbowl.global.config.security.jwt.JwtConstants.CLAIMS_ROLES;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtConstants.CLAIMS_SUB;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtConstants.DELIMITER;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtToken.ACCESS_TOKEN;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtToken.REFRESH_TOKEN;

import com.dinosaur.foodbowl.domain.auth.application.TokenService;
import com.dinosaur.foodbowl.domain.user.entity.role.Role.RoleType;
import com.dinosaur.foodbowl.global.util.CookieUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
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
  private final CookieUtils cookieUtils;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    var accessTokenValidation = jwtTokenProvider.tryCheckTokenValid(httpRequest, ACCESS_TOKEN);

    if (accessTokenValidation.isValid()) {
      setAuth(accessTokenValidation.getToken());
    } else if (accessTokenValidation.getTokenType() == JwtValidationType.EXPIRED) {
      var refreshTokenValidation = jwtTokenProvider.tryCheckTokenValid(httpRequest, REFRESH_TOKEN);
      String accessToken = jwtTokenProvider.extractToken(httpRequest, ACCESS_TOKEN);
      Long userId = Long.parseLong(
          jwtTokenProvider.extractPayload(accessToken, CLAIMS_SUB.getName()).toString());
      RoleType[] roles = getRoleTypes(
          jwtTokenProvider.extractPayload(accessToken, CLAIMS_ROLES.getName()).toString());

      if (refreshTokenValidation.isValid() && tokenService.isValid(userId,
          refreshTokenValidation.getToken())) {
        String renewedAccessToken = tokenService.createAccessToken(userId, roles);
        String renewedRefreshToken = tokenService.createRefreshToken(userId);

        Cookie accessCookie = cookieUtils.generateCookie(ACCESS_TOKEN.getName(), renewedAccessToken,
            (int) ACCESS_TOKEN.getValidMilliSecond() / 1000);
        Cookie refreshCookie = cookieUtils.generateCookie(REFRESH_TOKEN.getName(),
            renewedRefreshToken, (int) REFRESH_TOKEN.getValidMilliSecond() / 1000);

        httpResponse.addCookie(accessCookie);
        httpResponse.addCookie(refreshCookie);

        setAuth(renewedAccessToken);
      }
    } else {
      log.info(accessTokenValidation.getTokenType().getMsg());
    }

    filterChain.doFilter(request, response);
  }

  private void setAuth(String token) {
    Authentication auth = jwtTokenProvider.getAuthentication(token);
    SecurityContextHolder.getContext().setAuthentication(auth);
  }

  private RoleType[] getRoleTypes(String jsonRoles) {
    return Arrays.stream(jsonRoles.split(DELIMITER.getName()))
        .map(RoleType::from)
        .toArray(RoleType[]::new);
  }
}
