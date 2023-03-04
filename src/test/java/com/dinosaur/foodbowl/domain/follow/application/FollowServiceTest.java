package com.dinosaur.foodbowl.domain.follow.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.follow.dto.FollowerResponseDto;
import com.dinosaur.foodbowl.domain.follow.dto.FollowingResponseDto;
import com.dinosaur.foodbowl.domain.user.UserTestHelper.UserBuilder;
import com.dinosaur.foodbowl.domain.user.entity.User;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
      followService.follow(me, other);
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
      followService.follow(me, other);

      em.flush();
      em.clear();

      me = userFindService.findById(me.getId());
      other = userFindService.findById(other.getId());

      // when
      followService.unfollow(me, other);
      em.flush();
      em.clear();

      // then
      boolean isFollowed = followRepository.existsByFollowerAndFollowing(me, other);
      Assertions.assertThat(isFollowed).isFalse();
    }

    @Test
    @DisplayName("이미 팔로우한 사람을 팔로우하는 경우 팔로우 상태는 유지된다.")
    void shouldNothingWhenFollowAlreadyFollowing() {
      // given
      UserBuilder userBuilder = userTestHelper.builder();
      User me = userBuilder.build();
      User other = userBuilder.build();

      // when
      followService.follow(me, other);
      em.flush();
      em.clear();

      followService.follow(me, other);

      // then
      boolean isFollowed = followRepository.existsByFollowerAndFollowing(me, other);
      Assertions.assertThat(isFollowed).isTrue();
    }

    @Test
    @DisplayName("팔로우 하지 않은 사람을 언팔로우하는 경우 아무일도 일어나지 않는다.")
    void shouldNothingWhenUnfollowNotFollowing() {
      // given
      UserBuilder userBuilder = userTestHelper.builder();
      User me = userBuilder.build();
      User other = userBuilder.build();

      // when
      followService.unfollow(me, other);
      em.flush();
      em.clear();
    }

    @Test
    @DisplayName("유저를 팔로우한 유저 목록 조회를 성공한다.")
    public void getFollowers() {
      User me = userTestHelper.builder().build();
      User follower1 = userTestHelper.builder().build();
      User follower2 = userTestHelper.builder().build();
      User following1 = userTestHelper.builder().build();

      followService.follow(follower1, me);
      followService.follow(follower2, me);
      followService.follow(me, follower1);
      followService.follow(me, following1);

      Pageable pageable = PageRequest.of(0, 5);

      List<FollowerResponseDto> response = followService.getFollowers(me, pageable);
      List<Long> responseUserIds = response.stream().map(FollowerResponseDto::getUserId).toList();

      assertThat(response.size()).isEqualTo(2);
      assertThat(responseUserIds).contains(follower1.getId(), follower2.getId());
    }

    @Test
    @DisplayName("유저가 팔로우한 유저 목록 조회를 성공한다.")
    public void getFollowings() {
      User me = userTestHelper.builder().build();
      User follower1 = userTestHelper.builder().build();
      User following1 = userTestHelper.builder().build();
      User following2 = userTestHelper.builder().build();

      followService.follow(me, following1);
      followService.follow(me, following2);
      followService.follow(follower1, me);
      followService.follow(following1, me);

      Pageable pageable = PageRequest.of(0, 5);
      List<FollowingResponseDto> response = followService.getFollowings(me, pageable);
      List<Long> responseUserIds = response.stream().map(FollowingResponseDto::getUserId).toList();

      assertThat(response.size()).isEqualTo(2);
      assertThat(responseUserIds).contains(following1.getId(), following2.getId());
    }
  }
}
