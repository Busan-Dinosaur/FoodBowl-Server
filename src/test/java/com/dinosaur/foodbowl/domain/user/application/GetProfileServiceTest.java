package com.dinosaur.foodbowl.domain.user.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.domain.follow.dao.FollowRepository;
import com.dinosaur.foodbowl.domain.post.PostTestHelper;
import com.dinosaur.foodbowl.domain.user.UserTestHelper;
import com.dinosaur.foodbowl.domain.user.dto.response.ProfileResponseDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.util.thumbnail.ThumbnailTestHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
  PostTestHelper postTestHelper;

  @PersistenceContext
  EntityManager em;

  @AfterAll
  static void deleteAll() {
    ThumbnailTestHelper.deleteAllThumbnails();
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

    @Test
    @DisplayName("내가 쓴 게시글이 10개일 때 postCount는 10이어야 한다.")
    void should_getPostCountExactly_when_myPostIs10() {
      User me = userTestHelper.generateUser();
      User other = userTestHelper.generateUser();
      int myPostCount = 10;

      postTestHelper.builder().user(other).build();
      for (int i = 0; i < myPostCount; i++) {
        postTestHelper.builder().user(me).build();
      }
      postTestHelper.builder().user(other).build();

      em.flush();
      em.clear();

      ProfileResponseDto result = getProfileService.getProfile(me.getId());

      assertThat(result.getUserId()).isEqualTo(me.getId());
      assertThat(result.getNickname()).isEqualTo(me.getNickname());
      assertThat(result.getIntroduce()).isEqualTo(me.getIntroduce());
      assertThat(result.getThumbnailURL()).isEqualTo(me.getThumbnailURL().orElseThrow());
      assertThat(result.getPostCount()).isEqualTo(myPostCount);
    }
  }
}