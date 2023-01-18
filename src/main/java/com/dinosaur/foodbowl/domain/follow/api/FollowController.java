package com.dinosaur.foodbowl.domain.follow.api;

import com.dinosaur.foodbowl.domain.follow.application.FollowService;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.util.auth.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/follows")
public class FollowController {

  private final AuthUtil authUtil;
  private final FollowService followService;

  @PostMapping("/{userId}")
  public ResponseEntity<Void> follow(@PathVariable("userId") Long userId) {
    User me = authUtil.getUserByJWT();

    followService.follow(me, userId);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> unFollow(@PathVariable("userId") Long userId) {
    User me = authUtil.getUserByJWT();

    followService.unfollow(me, userId);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}