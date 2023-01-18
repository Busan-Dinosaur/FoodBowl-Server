package com.dinosaur.foodbowl.domain.follow.application;

import com.dinosaur.foodbowl.domain.follow.dao.FollowRepository;
import com.dinosaur.foodbowl.domain.user.dao.UserRepository;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.util.auth.AuthUtil;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

  private final UserRepository userRepository;
  private final FollowRepository followRepository;
  private final AuthUtil authUtil;

  @Transactional
  public void follow(User me, Long userId) {
    checkMe(me, userId);
    User other = userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
    checkAlreadyFollowed(me, other);
    me.follow(other);
  }

  @Transactional
  public void unfollow(User me, Long userId) {
    checkMe(me, userId);
    User other = userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
    checkNotFollowed(me, other);
    followRepository.deleteFollowByFollowerAndFollowing(me, other);
  }

  private void checkMe(User me, Long userId) {
    if (Objects.equals(me.getId(), userId)) {
      throw new IllegalArgumentException();
    }
  }

  private void checkAlreadyFollowed(User me, User other) {
    if (followRepository.existsByFollowerAndFollowing(me, other)) {
      throw new IllegalArgumentException();
    }
  }

  private void checkNotFollowed(User me, User other) {
    if (!followRepository.existsByFollowerAndFollowing(me, other)) {
      throw new IllegalArgumentException();
    }
  }

}