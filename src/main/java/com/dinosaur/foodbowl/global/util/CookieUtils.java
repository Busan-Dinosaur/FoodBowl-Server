package com.dinosaur.foodbowl.global.util;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtils {

  public Cookie generateCookie(String name, String value, int expiredSeconds) {
    Cookie cookie = new Cookie(name, value);
    cookie.setHttpOnly(true);
    cookie.setMaxAge(expiredSeconds);
    return cookie;
  }
}
