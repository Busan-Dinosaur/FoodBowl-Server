package com.dinosaur.foodbowl.global.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.IntegrationTest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CookieUtilsTest extends IntegrationTest {

  @Nested
  @DisplayName("쿠키 생성")
  class GenerateCookie {

    @Test
    @DisplayName("쿠키를 생성한다.")
    void should_success_when_generateCookie() {
      String name = "cookieName";
      String value = "cookieValue";
      int expire = 1;

      Cookie cookie = cookieUtils.generateCookie(name, value, expire);

      assertThat(cookie.getName()).isEqualTo(name);
      assertThat(cookie.getMaxAge()).isEqualTo(1);
    }
  }
}
