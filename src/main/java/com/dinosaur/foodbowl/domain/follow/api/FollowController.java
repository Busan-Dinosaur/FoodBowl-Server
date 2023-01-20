package com.dinosaur.foodbowl.domain.follow.api;

import com.dinosaur.foodbowl.domain.follow.application.FollowService;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.util.auth.AuthUtil;
import com.dinosaur.foodbowl.global.util.validator.follow.NotMe;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/follows")
public class FollowController {

  private final AuthUtil authUtil;
  private final FollowService followService;

  @PostMapping("/{userId}")
  public ResponseEntity<Void> follow(@PathVariable("userId") @NotMe Long userId) {
    User me = authUtil.getUserByJWT();

    followService.follow(me, userId);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> unFollow(@PathVariable("userId") @NotMe Long userId) {
    User me = authUtil.getUserByJWT();

    followService.unfollow(me, userId);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

}
