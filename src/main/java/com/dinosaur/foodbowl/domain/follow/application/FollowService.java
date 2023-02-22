package com.dinosaur.foodbowl.domain.follow.application;

import com.dinosaur.foodbowl.domain.follow.dao.FollowRepository;
import com.dinosaur.foodbowl.domain.follow.entity.Follow;
import com.dinosaur.foodbowl.domain.user.entity.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

  private final FollowRepository followRepository;

  @Transactional
  public void follow(User me, User other) {

    if (followRepository.existsByFollowerAndFollowing(me, other)) {
      return;
    }

    Follow follow = Follow.of(me, other);
    followRepository.save(follow);
  }

  @Transactional
  public void unfollow(User me, User other) {
    Optional<Follow> follow = followRepository.findByFollowerAndFollowing(me, other);

    follow.ifPresent(followRepository::delete);
  }
}
