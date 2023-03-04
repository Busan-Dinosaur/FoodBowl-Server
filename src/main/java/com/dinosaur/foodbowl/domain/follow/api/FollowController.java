package com.dinosaur.foodbowl.domain.follow.api;

import com.dinosaur.foodbowl.domain.follow.application.FollowService;
import com.dinosaur.foodbowl.domain.follow.application.GetFollowService;
import com.dinosaur.foodbowl.domain.follow.dto.FollowerResponseDto;
import com.dinosaur.foodbowl.domain.follow.dto.FollowingResponseDto;
import com.dinosaur.foodbowl.domain.user.application.UserFindService;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.util.resolver.LoginUser;
import com.dinosaur.foodbowl.global.util.validator.follow.NotMe;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/follows")
public class FollowController {

  private final UserFindService userFindService;
  private final FollowService followService;
  private final GetFollowService getFollowService;

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

  @GetMapping("/{userId}/followers")
  public ResponseEntity<List<FollowerResponseDto>> getFollowers(
      @PathVariable("userId") Long userId,
      @PageableDefault(size = 12, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {

    User user = userFindService.findById(userId);

    List<FollowerResponseDto> response = getFollowService.getFollowers(user, pageable);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{userId}/followings")
  public ResponseEntity<List<FollowingResponseDto>> getFollowings(
      @PathVariable("userId") Long userId,
      @PageableDefault(size = 12, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {

    User user = userFindService.findById(userId);

    List<FollowingResponseDto> response = getFollowService.getFollowings(user, pageable);

    return ResponseEntity.ok(response);
  }
}
