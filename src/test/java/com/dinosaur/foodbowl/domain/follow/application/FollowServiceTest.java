package com.dinosaur.foodbowl.domain.follow.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.dinosaur.foodbowl.domain.follow.dao.FollowRepository;
import com.dinosaur.foodbowl.domain.user.UserTestHelper;
import com.dinosaur.foodbowl.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@Transactional
class FollowServiceTest {

  @Autowired
  EntityManager em;
  @Autowired
  private FollowRepository followRepository;
  @Autowired
  private FollowService followService;
  @Autowired
  private UserTestHelper userTestHelper;

  @Nested
  @DisplayName("성공 테스트")
  class Success {

    @Test
    @DisplayName("로그인한 유저와 팔로우 할 유저가 존재하면 팔로잉을 성공한다.")
    void shouldSucceedToFollowWhenValidatedUsers() {
      // given
      User me = userTestHelper.generateUserWithoutThumbnail();
      User other = userTestHelper.generateUserWithoutThumbnail();

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
      User me = userTestHelper.generateUserWithoutThumbnail();
      User other = userTestHelper.generateUserWithoutThumbnail();
      followService.follow(me, other.getId());
      em.flush();
      em.clear();

      // when
      followService.unfollow(me, other.getId());

      // then
      boolean isFollowed = followRepository.existsByFollowerAndFollowing(me, other);
      Assertions.assertThat(isFollowed).isFalse();
    }
  }

  @Nested
  @DisplayName("실패 테스트")
  class Fail {

    @Test
    @DisplayName("팔로잉 대상이 자신이면 실패한다.")
    void shouldFailToFollowWhenOneself() {
      // given
      User me = userTestHelper.generateUserWithoutThumbnail();

      // then
      assertThatThrownBy(() -> followService.follow(me, me.getId())).isInstanceOf(
          IllegalArgumentException.class);

    }

    @Test
    @DisplayName("이미 팔로잉한 유저이면 팔로잉은 실패한다.")
    void shouldFailToFollowWhenAlreadyFollowed() {
      // given
      User me = userTestHelper.generateUserWithoutThumbnail();
      User other = userTestHelper.generateUserWithoutThumbnail();
      followService.follow(me, other.getId());
      em.flush();
      em.clear();

      // then
      assertThatThrownBy(() -> followService.follow(me, other.getId())).isInstanceOf(
          IllegalArgumentException.class);

    }

    @Test
    @DisplayName("팔로잉 취소 대상이 자신이면 실패한다.")
    void shouldFailToUnfollowWhenOneself() {
      // given
      User me = userTestHelper.generateUserWithoutThumbnail();

      // then
      assertThatThrownBy(() -> followService.unfollow(me, me.getId())).isInstanceOf(
          IllegalArgumentException.class);

    }

    @Test
    @DisplayName("이미 팔로잉한 유저가 아니면 팔로잉 취소는 실패한다.")
    void shouldSucceedToUnfollowWhenFollowing() {
      // given
      User me = userTestHelper.generateUserWithoutThumbnail();
      User other = userTestHelper.generateUserWithoutThumbnail();
      em.flush();
      em.clear();

      // then
      assertThatThrownBy(() -> followService.unfollow(me, other.getId())).isInstanceOf(
          IllegalArgumentException.class);
    }

  }
}