package com.dinosaur.foodbowl.global.config.web;

import com.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider;
import com.dinosaur.foodbowl.global.util.resolver.LoginUserIdArgumentResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(new LoginUserIdArgumentResolver(jwtTokenProvider));
  }
}