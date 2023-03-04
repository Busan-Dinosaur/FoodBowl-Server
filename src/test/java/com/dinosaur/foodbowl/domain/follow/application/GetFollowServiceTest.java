package com.dinosaur.foodbowl.domain.follow.application;

import static org.assertj.core.api.Assertions.*;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.follow.dto.FollowerResponseDto;
import com.dinosaur.foodbowl.domain.follow.dto.FollowingResponseDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class GetFollowServiceTest extends IntegrationTest {

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

    List<FollowerResponseDto> response = getFollowService.getFollowers(me, pageable);
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
    List<FollowingResponseDto> response = getFollowService.getFollowings(me, pageable);
    List<Long> responseUserIds = response.stream().map(FollowingResponseDto::getUserId).toList();

    assertThat(response.size()).isEqualTo(2);
    assertThat(responseUserIds).contains(following1.getId(), following2.getId());
  }
}