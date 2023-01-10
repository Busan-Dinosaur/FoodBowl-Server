package com.dinosaur.foodbowl.global.config.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.domain.user.entity.role.Role.RoleType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

@ContextConfiguration
class JwtTokenProviderTest {

  private static final String TEST_SECRET_KEY = "foodbowl_zzang_zzang";
  private static final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();

  @BeforeAll
  static void setupSecretKey() {
    ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", TEST_SECRET_KEY);
  }

  @Test
  void createAccessToken() {
    long userPk = 1;
    RoleType[] roleType = new RoleType[]{RoleType.USER, RoleType.ADMIN};

    String accessToken = jwtTokenProvider.createAccessToken(userPk, roleType);
    accessToken = removePrefix(accessToken);
    Claims claims = Jwts.parser().setSigningKey(TEST_SECRET_KEY)
        .parseClaimsJws(accessToken)
        .getBody();

    long resultUserPK = Long.parseLong(claims.getSubject());
    String roles = claims.get("roles").toString();
    assertThat(resultUserPK).isEqualTo(userPk);
    assertThat(roles).isEqualTo("ROLE_USER,ROLE_ADMIN");
  }

  private String removePrefix(String accessToken) {
    return accessToken.split(" ")[1];
  }

  @Test
  void getAuthentication() {
    /**
     * PK: 1
     * ROLE: USER, ADMIN
     */
    String validToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZXMiOiJST0xFX1VTRVIsUk9MRV9BRE1JTiIsImlhdCI6MTY3MzMwOTg3MSwiZXhwIjoxNjczMzEzNDcxfQ.pSVOJM9LuBVXOmwF5PAmEhSASUE5HeSJZpQ15gGd2o0";

    Authentication authentication = jwtTokenProvider.getAuthentication(validToken);

    assertThat(authentication.isAuthenticated()).isTrue();
    assertThat(authentication.getPrincipal()).isInstanceOf(UserDetails.class);
    List<String> authorities = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());
    assertThat(authorities).containsAll(List.of("ROLE_USER", "ROLE_ADMIN"));
  }
}