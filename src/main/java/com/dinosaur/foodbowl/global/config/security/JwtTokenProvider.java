package com.dinosaur.foodbowl.global.config.security;

import static com.dinosaur.foodbowl.global.config.security.JwtValidationType.EMPTY;
import static com.dinosaur.foodbowl.global.config.security.JwtValidationType.EXPIRED;
import static com.dinosaur.foodbowl.global.config.security.JwtValidationType.MALFORMED;
import static com.dinosaur.foodbowl.global.config.security.JwtValidationType.UNKNOWN;
import static com.dinosaur.foodbowl.global.config.security.JwtValidationType.UNSUPPORTED;
import static com.dinosaur.foodbowl.global.config.security.JwtValidationType.VALID;
import static com.dinosaur.foodbowl.global.config.security.JwtValidationType.WRONG_FORMAT;
import static com.dinosaur.foodbowl.global.config.security.JwtValidationType.WRONG_SIGNATURE;

import com.dinosaur.foodbowl.domain.user.entity.role.Role.RoleType;
import com.dinosaur.foodbowl.global.config.security.exception.EmptyJwtException;
import com.dinosaur.foodbowl.global.config.security.exception.WrongFormatJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
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

  public String createAccessToken(Long userPk, RoleType... roles) {
    return createAccessToken(String.valueOf(userPk), Arrays.stream(roles)
        .map(RoleType::name)
        .toArray(String[]::new));
  }

  public String createAccessToken(String userPk, String... roles) {
    Claims claims = Jwts.claims().setSubject(userPk);
    claims.put("roles", String.join(",", roles));
    Date now = new Date();
    return TOKEN_TYPE + " " + Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + DEFAULT_TOKEN_VALID_MILLISECOND))
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }

  public Authentication getAuthentication(String token) {
    Claims claims = getClaim(token);
    List<String> roles = getRolesBy(claims);
    UserDetails userDetails = new JwtUserEntity(claims.getSubject(), roles);
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  private static List<String> getRolesBy(Claims claims) {
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

  public TokenValidationDto tryCheckTokenValid(HttpServletRequest req) {
    try {
      String token = resolveToken(req);
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

  public String resolveToken(HttpServletRequest req) {
    String requestHeader = req.getHeader("Authorization");
    if (requestHeader == null || requestHeader.isEmpty()) {
      throw new EmptyJwtException();
    }
    String[] parts = requestHeader.split(" ");
    String type = parts[0];
    if (parts.length != 2 || !type.equals(TOKEN_TYPE)) {
      throw new WrongFormatJwtException();
    }
    return parts[1];
  }
}