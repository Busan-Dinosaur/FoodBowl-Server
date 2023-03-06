package com.dinosaur.foodbowl.global.config.security.jwt;

import lombok.Getter;

@Getter
public enum JwtToken {

    ACCESS_TOKEN("accessToken", 30 * 60 * 1000L),
    REFRESH_TOKEN("refreshToken", 14 * 24 * 60 * 60 * 1000L);

    private final String name;
    private final long validMilliSecond;

    JwtToken(String name, long validMilliSecond) {
        this.name = name;
        this.validMilliSecond = validMilliSecond;
    }
}
