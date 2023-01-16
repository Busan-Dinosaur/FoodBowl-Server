package com.dinosaur.foodbowl.domain.user.api;

import static com.dinosaur.foodbowl.domain.user.entity.role.Role.RoleType.ROLE_회원;
import static com.dinosaur.foodbowl.global.config.security.JwtTokenProvider.ACCESS_TOKEN;
import static com.dinosaur.foodbowl.global.config.security.JwtTokenProvider.DEFAULT_TOKEN_VALID_MILLISECOND;

import com.dinosaur.foodbowl.domain.user.application.DeleteAccountService;
import com.dinosaur.foodbowl.domain.user.application.GetProfileService;
import com.dinosaur.foodbowl.domain.user.application.UpdateProfileService;
import com.dinosaur.foodbowl.domain.user.application.signup.SignUpService;
import com.dinosaur.foodbowl.domain.user.dto.request.SignUpRequestDto;
import com.dinosaur.foodbowl.domain.user.dto.request.UpdateProfileRequestDto;
import com.dinosaur.foodbowl.domain.user.dto.response.ProfileResponseDto;
import com.dinosaur.foodbowl.domain.user.dto.response.SignUpResponseDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.config.security.JwtTokenProvider;
import com.dinosaur.foodbowl.global.util.auth.AuthUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

  private final SignUpService signUpService;
  private final DeleteAccountService deleteAccountService;
  private final UpdateProfileService updateProfileService;
  private final GetProfileService getProfileService;
  private final AuthUtil authUtil;
  private final JwtTokenProvider jwtTokenProvider;

  @PostMapping("/sign-up")
  public ResponseEntity<SignUpResponseDto> signUp(@Valid @ModelAttribute SignUpRequestDto request,
      HttpServletResponse response) {
    SignUpResponseDto signUpResponseDto = signUpService.signUp(request);

    setAccessToken(response, signUpResponseDto);

    return ResponseEntity.created(URI.create("/users/" + signUpResponseDto.getUserId()))
        .body(signUpResponseDto);
  }

  private void setAccessToken(HttpServletResponse response, SignUpResponseDto signUpResponseDto) {
    String accessToken = jwtTokenProvider.createAccessToken(signUpResponseDto.getUserId(), ROLE_회원);
    Cookie cookie = new Cookie(ACCESS_TOKEN, accessToken);
    cookie.setHttpOnly(true);
    cookie.setMaxAge((int) (DEFAULT_TOKEN_VALID_MILLISECOND / 1000));
    response.addCookie(cookie);
  }

  @DeleteMapping
  public ResponseEntity<Void> deleteAccount() {
    User me = authUtil.getUserByJWT();
    deleteAccountService.deleteMySelf(me);
    return ResponseEntity.status(HttpStatus.NO_CONTENT)
        .build();
  }

  @PatchMapping
  public ResponseEntity<Void> updateProfile(
      @ModelAttribute @Valid UpdateProfileRequestDto requestDto) {
    User me = authUtil.getUserByJWT();
    long userId = updateProfileService.updateProfile(me, requestDto);
    return ResponseEntity.status(HttpStatus.NO_CONTENT)
        .location(URI.create("/users/" + userId))
        .build();
  }

  @GetMapping("/{userId}")
  public ResponseEntity<ProfileResponseDto> getProfile(@PathVariable long userId) {
    ProfileResponseDto profile = getProfileService.getProfile(userId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(profile);
  }
}
