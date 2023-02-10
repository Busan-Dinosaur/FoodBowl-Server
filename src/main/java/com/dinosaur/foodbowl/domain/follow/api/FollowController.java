package com.dinosaur.foodbowl.domain.follow.api;

import com.dinosaur.foodbowl.domain.follow.application.FollowService;
import com.dinosaur.foodbowl.domain.user.application.UserFindService;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.util.resolver.LoginUser;
import com.dinosaur.foodbowl.global.util.validator.follow.NotMe;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/follows")
public class FollowController {

  private final FollowService followService;
  private final UserFindService userFindService;

  @PostMapping("/{userId}")
  public ResponseEntity<Void> follow(@PathVariable("userId") @NotMe Long otherId,
      @LoginUser User me) {
    User other = userFindService.findById(otherId);

    followService.follow(me, other);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> unFollow(@PathVariable("userId") @NotMe Long otherId,
      @LoginUser User me) {
    User other = userFindService.findById(otherId);

    followService.unfollow(me, other);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
