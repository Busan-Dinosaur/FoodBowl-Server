package com.dinosaur.foodbowl.global.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Base64;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

  public static final Long DEFAULT_TOKEN_VALID_MILLISECOND = 60 * 60 * 1000L;
  public static final String TOKEN_TYPE = "Bearer";

  @Value("${spring.jwt.secret}")
  private String secretKey;

  @PostConstruct
  protected void init() {
    secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
  }

  public String createToken(String userPk, String role) {
    return createToken(userPk, role, DEFAULT_TOKEN_VALID_MILLISECOND);
  }

  public String createToken(String userPk, String role, long tokenValidMillisecond) {
    Claims claims = Jwts.claims().setSubject(userPk);
    claims.put("role", role);
    Date now = new Date();
    return TOKEN_TYPE + " " + Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + tokenValidMillisecond))
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }

  public Authentication getAuthentication(String token) {
    Claims claims = getClaim(token);
    String roleString = claims.get("role").toString();
    UserDetails userDetails = new JwtUserEntity(claims.getSubject(), roleString);
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  private Claims getClaim(String token) {
    return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
  }

  public String resolveToken(HttpServletRequest req) {
    String requestHeader = req.getHeader("Authorization");
    if (requestHeader == null || requestHeader.isEmpty()) {
      return null;
    }
    String[] parts = requestHeader.split(" ");
    String type = parts[0];
    if (parts.length != 2 || !type.equals(TOKEN_TYPE)) {
      return null;
    }
    String token = parts[1];
    return token;
  }

  public boolean validateToken(String jwtToken) {
    try {
      Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
      return !claims.getBody().getExpiration().before(new Date());
    } catch (Exception e) {
      return false;
    }
  }
}