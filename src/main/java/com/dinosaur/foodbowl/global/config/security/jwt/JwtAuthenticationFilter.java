package com.dinosaur.foodbowl.global.config.security.jwt;

import static com.dinosaur.foodbowl.global.config.security.jwt.JwtToken.ACCESS_TOKEN;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {
    var tokenValidationDto = jwtTokenProvider.tryCheckTokenValid((HttpServletRequest) request,
        ACCESS_TOKEN);

    if (tokenValidationDto.isValid()) {
      Authentication auth = jwtTokenProvider.getAuthentication(tokenValidationDto.getToken());
      SecurityContextHolder.getContext().setAuthentication(auth);
    } else {
      log.info(tokenValidationDto.getTokenType().getMsg());
    }

    filterChain.doFilter(request, response);
  }
}
