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

  private static final String TEST_SECRET_KEY = "2B4B6250655368566D597133743677397A244326452948404D635166546A576E";
  private static final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();

  @BeforeAll
  static void setupSecretKey() {
    ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", TEST_SECRET_KEY);
  }

  @Test
  void createAccessToken() {
    long userPk = 1;
    RoleType[] roleType = new RoleType[]{RoleType.ROLE_회원, RoleType.ROLE_관리자};

    String accessToken = jwtTokenProvider.createAccessToken(userPk, roleType);
    accessToken = removePrefix(accessToken);
    Claims claims = Jwts.parser().setSigningKey(TEST_SECRET_KEY)
        .parseClaimsJws(accessToken)
        .getBody();

    long resultUserPK = Long.parseLong(claims.getSubject());
    String roles = claims.get("roles").toString();
    assertThat(resultUserPK).isEqualTo(userPk);
    assertThat(roles).isEqualTo("ROLE_회원,ROLE_관리자");
  }

  private String removePrefix(String accessToken) {
    return accessToken.split(" ")[1];
  }

  @Test
  void getAuthentication() {
    /**
     * PK: 1
     * ROLE: 회원, 관리자
     * expired: 2073년 1월 16일
     */
    String validToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZXMiOiJST0xFX-2ajOybkCxST0xFX-q0gOumrOyekCIsImlhdCI6MTY3Mzg0OTAxNSwiZXhwIjoxNjc4MTY5MDE1fQ.jN1AbCdUaYXHaMVPB3rDebkRx335cub44_2hLo5Ne0c";

    Authentication authentication = jwtTokenProvider.getAuthentication(validToken);

    assertThat(authentication.isAuthenticated()).isTrue();
    assertThat(authentication.getPrincipal()).isInstanceOf(UserDetails.class);
    List<String> authorities = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());
    assertThat(authorities).containsAll(List.of("ROLE_회원", "ROLE_관리자"));
  }
}