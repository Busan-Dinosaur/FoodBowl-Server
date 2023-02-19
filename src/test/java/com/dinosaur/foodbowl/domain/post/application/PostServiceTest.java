package com.dinosaur.foodbowl.domain.post.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.post.dto.response.PostFeedResponseDto;
import com.dinosaur.foodbowl.domain.post.dto.response.PostThumbnailResponseDto;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.user.entity.User;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

class PostServiceTest extends IntegrationTest {

  @Nested
  @DisplayName("게시글 썸네일 불러오기")
  class GetThumbnails {

    @Test
    @DisplayName("지정한 페이지 설정으로 게시글 썸네일 목록을 불러온다.")
    void should_loadThumbnails_with_pageable_when_getThumbnails() {
      User user = userTestHelper.builder().build();

      for (int i = 0; i < 5; i++) {
        postTestHelper.builder().user(user).content("test" + i).build();
      }

      Pageable pageable = PageRequest.of(1, 2, Sort.by("id").descending());
      List<PostThumbnailResponseDto> response = postService.getThumbnails(user.getId(), pageable);

      assertThat(response.size()).isEqualTo(2);
    }
  }

  @Nested
  @DisplayName("게시글 피드 불러오기")
  class GetFeed {

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
      user = userTestHelper.builder().build();
      User user2 = userTestHelper.builder().build();

      followTestHelper.builder().following(user2).follower(user).build();

      post = postTestHelper.builder().user(user2).content("테스트 게시글").build();

      photoTestHelper.builder().post(post).build();
      photoTestHelper.builder().post(post).build();

      postCategoryTestHelper.builder().post(post).build();
      postCategoryTestHelper.builder().post(post).build();

      clipTestHelper.builder().post(post).build();
      clipTestHelper.builder().post(post).build();

      commentTestHelper.builder().post(post).build();
      commentTestHelper.builder().post(post).build();
    }

    @Test
    @DisplayName("나와 내가 팔로우하고 있는 유저의 게시글만 불러온다.")
    void should_find_onlyPostsMeAndFollowingUser() {
      em.flush();
      em.clear();

      Pageable pageable = PageRequest.of(0, 3, Sort.by("id").descending());

      List<PostFeedResponseDto> response = postService.getFeed(user, pageable);

      assertThat(response).hasSize(1);
      assertThat(response.get(0).getContent()).isEqualTo("테스트 게시글");
      assertThat(response.get(0).getFollowerCount()).isEqualTo(1);
      assertThat(response.get(0).getPhotoPaths()).hasSize(2);
      assertThat(response.get(0).getCategories()).hasSize(2);
      assertThat(response.get(0).getClipCount()).isEqualTo(2);
      assertThat(response.get(0).getCommentCount()).isEqualTo(2);
    }
  }
}
