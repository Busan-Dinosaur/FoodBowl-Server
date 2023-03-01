package com.dinosaur.foodbowl.global.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.IntegrationTest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CookieUtilsTest extends IntegrationTest {

  @Nested
  class 쿠키_생성 {

    @Test
    void 쿠키를_생성한다() {
      String name = "cookieName";
      String value = "cookieValue";
      int expire = 1;

      Cookie cookie = cookieUtils.generateCookie(name, value, expire);

      assertThat(cookie.getName()).isEqualTo(name);
      assertThat(cookie.getMaxAge()).isEqualTo(1);
    }
  }
}
