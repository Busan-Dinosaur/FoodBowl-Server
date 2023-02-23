package com.dinosaur.foodbowl.domain.follow;

import com.dinosaur.foodbowl.domain.follow.dao.FollowRepository;
import com.dinosaur.foodbowl.domain.follow.entity.Follow;
import com.dinosaur.foodbowl.domain.user.UserTestHelper;
import com.dinosaur.foodbowl.domain.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FollowTestHelper {

  @Autowired
  private FollowRepository followRepository;

  @Autowired
  private UserTestHelper userTestHelper;

  public FollowBuilder builder() {
    return new FollowBuilder();
  }

  public final class FollowBuilder {

    private User following;
    private User follower;

    private FollowBuilder() {
    }

    public FollowBuilder following(User user) {
      this.following = user;
      return this;
    }

    public FollowBuilder follower(User user) {
      this.follower = user;
      return this;
    }

    public Follow build() {
      return followRepository.save(Follow.builder()
          .following(following != null ? following : userTestHelper.builder().build())
          .follower(follower != null ? follower : userTestHelper.builder().build())
          .build());
    }
  }
}
