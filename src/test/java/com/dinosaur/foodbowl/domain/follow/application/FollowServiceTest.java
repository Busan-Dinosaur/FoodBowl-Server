package com.dinosaur.foodbowl.domain.follow.application;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.user.UserTestHelper.UserBuilder;
import com.dinosaur.foodbowl.domain.user.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FollowServiceTest extends IntegrationTest {

  @Nested
  @DisplayName("성공 테스트")
  class Success {

    @Test
    @DisplayName("로그인한 유저와 팔로우 할 유저가 존재하면 팔로잉을 성공한다.")
    void shouldSucceedToFollowWhenValidatedUsers() {
      // given
      UserBuilder userBuilder = userTestHelper.builder();
      User me = userBuilder.build();
      User other = userBuilder.build();

      // when
      followService.follow(me, other.getId());
      em.flush();
      em.clear();

      // then
      boolean isFollowed = followRepository.existsByFollowerAndFollowing(me, other);
      Assertions.assertThat(isFollowed).isTrue();
    }

    @Test
    @DisplayName("팔로잉한 유저이면 팔로잉 취소는 성공한다.")
    void shouldSucceedToUnfollowWhenFollowing() {
      // given
      UserBuilder userBuilder = userTestHelper.builder();
      User me = userBuilder.build();
      User other = userBuilder.build();
      followService.follow(me, other.getId());
      em.flush();
      em.clear();
      me = userFindDao.findById(me.getId());
      other = userFindDao.findById(other.getId());

      // when
      followService.unfollow(me, other.getId());
      em.flush();
      em.clear();

      // then
      boolean isFollowed = followRepository.existsByFollowerAndFollowing(me, other);
      Assertions.assertThat(isFollowed).isFalse();
    }
  }
}
