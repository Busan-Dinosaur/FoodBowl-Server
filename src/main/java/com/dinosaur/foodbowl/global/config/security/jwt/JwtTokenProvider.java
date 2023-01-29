package com.dinosaur.foodbowl.global.config.security.jwt;

import static com.dinosaur.foodbowl.global.config.security.jwt.JwtConstants.CLAIMS_ROLES;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtConstants.DELIMITER;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtToken.ACCESS_TOKEN;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtToken.REFRESH_TOKEN;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtValidationType.EMPTY;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtValidationType.EXPIRED;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtValidationType.MALFORMED;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtValidationType.UNKNOWN;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtValidationType.UNSUPPORTED;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtValidationType.VALID;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtValidationType.WRONG_FORMAT;
import static com.dinosaur.foodbowl.global.config.security.jwt.JwtValidationType.WRONG_SIGNATURE;

import com.dinosaur.foodbowl.domain.user.entity.Role.RoleType;
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
import java.util.Base64.Decoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  private final Decoder decoder = Base64.getUrlDecoder();
  private final JsonParser jsonParser = new BasicJsonParser();

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
    claims.put(CLAIMS_ROLES.getName(), String.join(DELIMITER.getName(), roles));
    Date now = new Date();
    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + ACCESS_TOKEN.getValidMilliSecond()))
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }

  public String createRefreshToken() {
    Date now = new Date();

    return Jwts.builder()
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + REFRESH_TOKEN.getValidMilliSecond()))
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }

  public Authentication getAuthentication(String token) {
    Claims claims = getClaim(token);
    List<String> roles = getRoles(claims);
    UserDetails userDetails = new JwtUserEntity(claims.getSubject(), roles);
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  private List<String> getRoles(Claims claims) {
    String[] roles = claims.get(CLAIMS_ROLES.getName())
        .toString()
        .split(DELIMITER.getName());
    return List.of(roles);
  }

  private Claims getClaim(String token) {
    return Jwts.parser()
        .setSigningKey(secretKey)
        .parseClaimsJws(token)
        .getBody();
  }

  public TokenValidationDto tryCheckTokenValid(HttpServletRequest req, JwtToken jwtToken) {
    try {
      String token = extractToken(req, jwtToken);
      getClaim(token);
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

  public Object extractPayload(String token, String key) {
    String payloadJWT = token.split("\\.")[1];
    String payload = new String(decoder.decode(payloadJWT));

    Map<String, Object> jsonArray = jsonParser.parseMap(payload);

    if (!jsonArray.containsKey(key)) {
      throw new WrongFormatJwtException();
    }

    return jsonArray.get(key);
  }
}
