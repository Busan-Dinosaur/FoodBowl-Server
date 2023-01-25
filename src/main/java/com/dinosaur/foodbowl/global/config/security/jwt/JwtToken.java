package com.dinosaur.foodbowl.global.config.security.jwt;

import lombok.Getter;

@Getter
public enum JwtToken {

  ACCESS_TOKEN("accessToken"),
  REFRESH_TOKEN("refreshToken");

  private final String name;

  JwtToken(String name) {
    this.name = name;
  }
}
