package com.dinosaur.foodbowl.domain.user.api;

import com.dinosaur.foodbowl.domain.user.application.DeleteAccountService;
import com.dinosaur.foodbowl.domain.user.application.SignupService;
import com.dinosaur.foodbowl.domain.user.dto.request.SignupRequestDto;
import com.dinosaur.foodbowl.domain.user.dto.response.SignupResponseDto;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

  private final SignupService signupService;
  private final DeleteAccountService deleteAccountService;

  @PostMapping("/signup")
  public SignupResponseDto signup(@Valid @ModelAttribute SignupRequestDto request) {
    return signupService.signup(request);
  }

  @Secured("ROLE_USER")
  @DeleteMapping
  public void deleteAccount() {
    deleteAccountService.deleteMySelf();
  }
}
