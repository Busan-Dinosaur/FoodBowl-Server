package com.dinosaur.foodbowl.domain.user.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.user.UserTestHelper.UserBuilder;
import com.dinosaur.foodbowl.domain.user.dto.response.ProfileResponseDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class GetProfileServiceTest extends IntegrationTest {

  private UserBuilder userBuilder;

  @BeforeEach
  void setUp() {
    userBuilder = userTestHelper.builder();
  }

  @Nested
  class 프로필_조회 {

    @Test
    void 프로필_조회_시_팔로잉_숫자가_일치한다() {
      User me = userBuilder.thumbnail(thumbnailTestHelper.generateThumbnail()).build();
      User userA = userBuilder.thumbnail(thumbnailTestHelper.generateThumbnail()).build();
      User userB = userBuilder.thumbnail(thumbnailTestHelper.generateThumbnail()).build();

      me.follow(userA);
      me.follow(userB);
      userA.follow(me);

      em.flush();
      em.clear();

      ProfileResponseDto result = getProfileService.getProfile(me.getId());

      assertThat(result.getUserId()).isEqualTo(me.getId());
      assertThat(result.getNickname()).isEqualTo(me.getNickname().getNickname());
      assertThat(result.getIntroduce()).isEqualTo(me.getIntroduce());
      assertThat(result.getThumbnailURL()).isEqualTo(me.getThumbnailURL().orElseThrow());
      assertThat(result.getFollowerCount()).isEqualTo(1);
      assertThat(result.getFollowingCount()).isEqualTo(2);
    }

    @Test
    void 프로필_조회_시_게시글_숫자가_일치한다() {
      User me = userBuilder.thumbnail(thumbnailTestHelper.generateThumbnail()).build();
      User other = userBuilder.thumbnail(thumbnailTestHelper.generateThumbnail()).build();
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
      assertThat(result.getNickname()).isEqualTo(me.getNickname().getNickname());
      assertThat(result.getIntroduce()).isEqualTo(me.getIntroduce());
      assertThat(result.getThumbnailURL()).isEqualTo(me.getThumbnailURL().orElseThrow());
      assertThat(result.getPostCount()).isEqualTo(myPostCount);
    }
  }
}
