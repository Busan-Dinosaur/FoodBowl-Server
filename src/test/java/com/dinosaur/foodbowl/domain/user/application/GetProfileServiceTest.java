package com.dinosaur.foodbowl.domain.user.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.domain.follow.dao.FollowRepository;
import com.dinosaur.foodbowl.domain.user.UserTestHelper;
import com.dinosaur.foodbowl.domain.user.dto.response.ProfileResponseDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class GetProfileServiceTest {

  @Autowired
  GetProfileService getProfileService;

  @Autowired
  FollowRepository followRepository;

  @Autowired
  UserTestHelper userTestHelper;

  @Autowired
  EntityManager em;

  @AfterAll
  static void deleteAll() {
    UserTestHelper.deleteAllThumbnails();
  }

  @Nested
  @DisplayName("프로필 가져오기")
  class GetProfile {

    @Test
    @DisplayName("내가 2명을 팔로우하고 1명이 팔로잉 할 때 나의 팔로워는 1명, 팔로잉은 2명이다.")
    void should_getProfileExactly_when_follower1following2() {
      User me = userTestHelper.generateUser();
      User userA = userTestHelper.generateUser();
      User userB = userTestHelper.generateUser();

      me.follow(userA);
      me.follow(userB);
      userA.follow(me);

      em.flush();
      em.clear();

      ProfileResponseDto result = getProfileService.getProfile(me.getId());

      assertThat(result.getUserId()).isEqualTo(me.getId());
      assertThat(result.getNickname()).isEqualTo(me.getNickname());
      assertThat(result.getIntroduce()).isEqualTo(me.getIntroduce());
      assertThat(result.getThumbnailURL()).isEqualTo(me.getThumbnailURL().orElseThrow());
      assertThat(result.getFollowerCount()).isEqualTo(1);
      assertThat(result.getFollowingCount()).isEqualTo(2);
    }
  }
}