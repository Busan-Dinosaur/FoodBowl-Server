package com.dinosaur.foodbowl.global.config.security.dto;

import com.dinosaur.foodbowl.global.config.security.jwt.JwtValidationType;
import lombok.Getter;

@Getter
public class TokenValidationDto {

    private final boolean isValid;
    private final JwtValidationType tokenType;
    private final String token;

    private TokenValidationDto(boolean isValid, JwtValidationType tokenType, String token) {
        this.isValid = isValid;
        this.tokenType = tokenType;
        this.token = token;
    }

    public static TokenValidationDto of(boolean isValid, JwtValidationType tokenType, String token) {
        return new TokenValidationDto(isValid, tokenType, token);
    }

    public static TokenValidationDto of(boolean isValid, JwtValidationType tokenType) {
        return new TokenValidationDto(isValid, tokenType, null);
    }
}
