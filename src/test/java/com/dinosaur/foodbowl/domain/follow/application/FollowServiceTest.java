package com.dinosaur.foodbowl.domain.follow.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_INTRODUCE_LENGTH;
import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_LOGIN_ID_LENGTH;
import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_NICKNAME_LENGTH;
import static com.dinosaur.foodbowl.domain.user.entity.User.MAX_PASSWORD_LENGTH;

import com.dinosaur.foodbowl.domain.follow.dao.FollowRepository;
import com.dinosaur.foodbowl.domain.follow.entity.Follow;
import com.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import com.dinosaur.foodbowl.domain.user.dao.UserRepository;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.util.auth.AuthUtil;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
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
  private UserRepository userRepository;

  @Autowired
  private FollowRepository followRepository;
  @Autowired
  private FollowService followService;

  @Autowired
  private AuthUtil authUtil;

  @Nested
  @DisplayName("성공 테스트")
  class Success {

    @Test
    @DisplayName("로그인한 유저와 팔로우 할 유저가 존재하면 팔로잉을 성공한다.")
    void shouldSucceedToFollowWhenValidatedUsers() {
      // given
      User me = generateUser(null);
      User other = generateUser(null);

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
      User me = generateUser(null);
      User other = generateUser(null);
      followService.follow(me, other.getId());
      em.flush();
      em.clear();
      List<Follow> all1 = followRepository.findAll();
      System.out.println("여기1" + all1);

      // when
      followService.unfollow(me, other.getId());
      em.flush();
      em.clear();
      List<Follow> all = followRepository.findAll();
      System.out.println("여기" + all);
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
      User me = generateUser(null);

      // then
      assertThatThrownBy(() -> followService.follow(me, me.getId())).isInstanceOf(
          IllegalArgumentException.class);

    }

    @Test
    @DisplayName("이미 팔로잉한 유저이면 팔로잉은 실패한다.")
    void shouldFailToFollowWhenAlreadyFollowed() {
      // given
      User me = generateUser(null);
      User other = generateUser(null);
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
      User me = generateUser(null);

      // then
      assertThatThrownBy(() -> followService.unfollow(me, me.getId())).isInstanceOf(
          IllegalArgumentException.class);

    }

    @Test
    @DisplayName("이미 팔로잉한 유저가 아니면 팔로잉 취소는 실패한다.")
    void shouldSucceedToUnfollowWhenFollowing() {
      // given
      User me = generateUser(null);
      User other = generateUser(null);
      em.flush();
      em.clear();

      // then
      assertThatThrownBy(() -> followService.unfollow(me, other.getId())).isInstanceOf(
          IllegalArgumentException.class);
    }

  }

  private User generateUser(Thumbnail thumbnail) {
    User userWithThumbnail = User.builder()
        .loginId(getRandomUUIDLengthWith(MAX_LOGIN_ID_LENGTH))
        .nickname(getRandomUUIDLengthWith(MAX_NICKNAME_LENGTH))
        .password(getRandomUUIDLengthWith(MAX_PASSWORD_LENGTH))
        .introduce(getRandomUUIDLengthWith(MAX_INTRODUCE_LENGTH))
        .thumbnail(thumbnail)
        .build();
    return userRepository.save(userWithThumbnail);
  }

  private static String getRandomUUIDLengthWith(int length) {
    String randomString = UUID.randomUUID()
        .toString();
    length = Math.min(length, randomString.length());
    return randomString.substring(0, length);
  }
}