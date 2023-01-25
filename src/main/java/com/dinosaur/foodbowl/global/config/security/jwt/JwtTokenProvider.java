package com.dinosaur.foodbowl.global.config.security.jwt;

import static com.dinosaur.foodbowl.global.config.security.jwt.JwtToken.ACCESS_TOKEN;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtValidationType.EMPTY;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtValidationType.EXPIRED;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtValidationType.MALFORMED;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtValidationType.UNKNOWN;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtValidationType.UNSUPPORTED;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtValidationType.VALID;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtValidationType.WRONG_FORMAT;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtValidationType.WRONG_SIGNATURE;

import com.dinosaur.foodbowl.domain.user.entity.role.Role.RoleType;
import com.dinosaur.foodbowl.global.config.security.dto.TokenValidationDto;
import com.dinosaur.foodbowl.global.config.security.exception.EmptyJwtException;
import com.dinosaur.foodbowl.global.config.security.exception.WrongFormatJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

  public static final long ACCESS_TOKEN_VALID_MILLISECOND = 30 * 60 * 1000L;
  public static final long REFRESH_TOKEN_VALID_MILLISECOND = 14 * 24 * 60 * 60 * 1000L;

  @Value("${spring.jwt.secret}")
  private String secretKey;

  @PostConstruct
  protected void init() {
    secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
  }

  public String createAccessToken(Long userPk, RoleType... roles) {
    return createAccessToken(String.valueOf(userPk), Arrays.stream(roles)
        .map(RoleType::name)
        .toArray(String[]::new));
  }

  private String createAccessToken(String userPk, String... roles) {
    Claims claims = Jwts.claims().setSubject(userPk);
    claims.put("roles", String.join(",", roles));
    Date now = new Date();
    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_VALID_MILLISECOND))
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }

  public String createRefreshToken() {
    Date now = new Date();

    return Jwts.builder()
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_VALID_MILLISECOND))
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }

  public Authentication getAuthentication(String token) {
    Claims claims = getClaim(token);
    List<String> roles = getRolesBy(claims);
    UserDetails userDetails = new JwtUserEntity(claims.getSubject(), roles);
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  private List<String> getRolesBy(Claims claims) {
    String[] roles = claims.get("roles")
        .toString()
        .split(",");
    return List.of(roles);
  }

  private Claims getClaim(String token) {
    return Jwts.parser().setSigningKey(secretKey)
        .parseClaimsJws(token)
        .getBody();
  }

  public TokenValidationDto tryCheckTokenValid(HttpServletRequest req, JwtToken jwtToken) {
    try {
      String token = extractToken(req, jwtToken);
      Long.parseLong(Jwts.parser()
          .setSigningKey(secretKey)
          .parseClaimsJws(token)
          .getBody()
          .getSubject());
      return TokenValidationDto.of(true, VALID, token);
    } catch (MalformedJwtException e) {
      return TokenValidationDto.of(false, MALFORMED);
    } catch (ExpiredJwtException e) {
      return TokenValidationDto.of(false, EXPIRED);
    } catch (UnsupportedJwtException e) {
      return TokenValidationDto.of(false, UNSUPPORTED);
    } catch (SignatureException e) {
      return TokenValidationDto.of(false, WRONG_SIGNATURE);
    } catch (EmptyJwtException e) {
      return TokenValidationDto.of(false, EMPTY);
    } catch (WrongFormatJwtException e) {
      return TokenValidationDto.of(false, WRONG_FORMAT);
    } catch (Exception e) {
      return TokenValidationDto.of(false, UNKNOWN);
    }
  }

  public String extractToken(HttpServletRequest req, JwtToken jwtToken) {
    Optional<Cookie> accessToken = Arrays.stream(req.getCookies())
        .filter(cookie -> cookie.getName().equals(jwtToken.getName()))
        .findFirst();
    if (accessToken.isEmpty()) {
      throw new EmptyJwtException();
    }
    return accessToken.get().getValue();
  }

  public Long extractUserId(HttpServletRequest req) {
    String accessToken = extractToken(req, ACCESS_TOKEN);
    Claims claim = getClaim(accessToken);
    return Long.parseLong(claim.getSubject());
  }
}
