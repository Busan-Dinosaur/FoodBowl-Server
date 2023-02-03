package com.dinosaur.foodbowl.domain.post.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.post.dto.response.PostThumbnailResponseDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import java.util.List;
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
}
