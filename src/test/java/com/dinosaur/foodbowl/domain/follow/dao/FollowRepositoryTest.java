package com.dinosaur.foodbowl.domain.follow.dao;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dinosaur.foodbowl.domain.follow.entity.Follow;
import com.dinosaur.foodbowl.domain.user.dao.UserRepository;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.dao.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

class FollowRepositoryTest extends RepositoryTest {

  @Autowired
  UserRepository userRepository;

  @Autowired
  FollowRepository followRepository;

  @Nested
  class NullColumnTest {

    @DisplayName("팔로잉하는 사람이 null이면 예외가 발생한다.")
    @Test
    void followingUserNull() {
      assertThatThrownBy(
          () -> builder().setUpFollower("로그인1", "비밀번호1", "닉네임", "소개")
              .doFollow()
      ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("팔로우하는 사람이 null이면 예외가 발생한다.")
    @Test
    void followerUserNull() {
      assertThatThrownBy(
          () -> builder().setUpFollowing("로그인1", "비밀번호1", "닉네임", "소개")
              .doFollow()
      ).isInstanceOf(DataIntegrityViolationException.class);
    }
  }

  private FollowBuilder builder() {
    return new FollowBuilder();
  }

  private final class FollowBuilder {

    private User following;
    private User follower;
    private Follow follow;

    private User setUpUser(String loginId, String password, String nickname, String introduce) {
      User user = User.builder()
          .loginId(loginId)
          .password(password)
          .nickname(nickname)
          .introduce(introduce)
          .build();
      return userRepository.save(user);
    }

    private FollowBuilder setUpFollowing(
        String loginId, String password, String nickname, String introduce
    ) {
      this.following = setUpUser(loginId, password, nickname, introduce);
      return this;
    }

    private FollowBuilder setUpFollower(
        String loginId, String password, String nickname, String introduce
    ) {
      this.follower = setUpUser(loginId, password, nickname, introduce);
      return this;
    }

    private FollowBuilder doFollow() {
      Follow follow = Follow.builder()
          .following(following)
          .follower(follower)
          .build();
      this.follow = followRepository.save(follow);
      return this;
    }

    private Follow follow() {
      return this.follow;
    }
  }
}