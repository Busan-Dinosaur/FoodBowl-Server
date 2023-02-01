package com.dinosaur.foodbowl.domain.follow.application;

import com.dinosaur.foodbowl.domain.user.application.UserFindService;
import com.dinosaur.foodbowl.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

  private final UserFindService userFindService;

  @Transactional
  public void follow(User me, Long otherId) {
    User other = userFindService.findById(otherId);

    if (me.isFollowing(other)) {
      return;
    }

    me.follow(other);
  }

  @Transactional
  public void unfollow(User me, Long otherId) {
    User other = userFindService.findById(otherId);

    if (!me.isFollowing(other)) {
      return;
    }

    me.unfollow(other);
  }
}
