package com.dinosaur.foodbowl.domain.post.application;

import static com.dinosaur.foodbowl.global.error.ErrorCode.POST_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.global.error.BusinessException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PostFindServiceTest extends IntegrationTest {

  @Nested
  class 게시글_조회 {

    @Test
    void 게시글_ID가_존재하면_게시글을_조회한다() {
      Post savedPost = postTestHelper.builder().build();

      Post findPost = postFindService.findById(savedPost.getId());

      assertThat(savedPost).isEqualTo(findPost);
    }

    @Test
    void 게시글_ID가_존재하지_않으면_예외가_발생한다() {
      long postId = -999l;

      assertThatThrownBy(() -> postFindService.findById(postId))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining(POST_NOT_FOUND.getMessage());
    }
  }
}
