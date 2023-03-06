package com.dinosaur.foodbowl.global.config.security.jwt;

import lombok.Getter;

@Getter
public enum JwtConstants {

    CLAIMS_SUB("sub"),
    CLAIMS_ROLES("roles"),
    DELIMITER(",");

    private final String name;

    JwtConstants(String name) {
        this.name = name;
    }
}
