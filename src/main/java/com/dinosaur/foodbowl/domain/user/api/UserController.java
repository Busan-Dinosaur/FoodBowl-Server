package com.dinosaur.foodbowl.domain.user.api;

import com.dinosaur.foodbowl.domain.user.application.DeleteAccountService;
import com.dinosaur.foodbowl.domain.user.application.SignUpService;
import com.dinosaur.foodbowl.domain.user.dto.request.SignUpRequestDto;
import com.dinosaur.foodbowl.domain.user.dto.response.SignUpResponseDto;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

  private final SignUpService signUpService;
  private final DeleteAccountService deleteAccountService;

  @PostMapping("/sign-up")
  public ResponseEntity<SignUpResponseDto> signUp(@Valid @ModelAttribute SignUpRequestDto request) {
    return signUpService.signUp(request);
  }

  @DeleteMapping
  public ResponseEntity<Void> deleteAccount() {
    return deleteAccountService.deleteMySelf();
  }
}
