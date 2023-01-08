package com.dinosaur.foodbowl.global.config.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
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
    var tokenValidationDto = jwtTokenProvider.tryCheckTokenValid((HttpServletRequest) request);

    if (tokenValidationDto.isValid()) {
      Authentication auth = jwtTokenProvider.getAuthentication(tokenValidationDto.getToken());
      SecurityContextHolder.getContext().setAuthentication(auth);
    } else {
      log.info(tokenValidationDto.getTokenType().getMsg());
    }

    filterChain.doFilter(request, response);
  }
}