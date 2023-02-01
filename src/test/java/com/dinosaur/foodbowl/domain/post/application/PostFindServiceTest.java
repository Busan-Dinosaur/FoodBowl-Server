package com.dinosaur.foodbowl.domain.post.application;

import static com.dinosaur.foodbowl.global.error.ErrorCode.POST_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.global.error.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PostFindServiceTest extends IntegrationTest {

  @Nested
  @DisplayName("게시글 ID로 찾기")
  class FindById {

    @Test
    @DisplayName("해당 ID의 게시글이 존재하는 경우 게시글을 반환한다.")
    void should_returnPost_when_existId() {
      Post savedPost = postTestHelper.builder().build();

      Post findPost = postFindService.findById(savedPost.getId());

      assertThat(savedPost).isEqualTo(findPost);
    }

    @Test
    @DisplayName("해당 ID의 게시글이 존재하지 않는 경우 예외가 발생한다.")
    void should_throwException_when_notExistId() {
      long postId = -999l;

      assertThatThrownBy(() -> postFindService.findById(postId))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining(POST_NOT_FOUND.getMessage());
    }
  }
}
