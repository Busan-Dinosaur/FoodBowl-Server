package com.dinosaur.foodbowl.domain.comment.application;

import static com.dinosaur.foodbowl.global.error.ErrorCode.COMMENT_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.comment.entity.Comment;
import com.dinosaur.foodbowl.global.error.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CommentFindServiceTest extends IntegrationTest {

  @Nested
  @DisplayName("댓글 ID로 찾기")
  class FindById {

    @Test
    @DisplayName("댓글 ID가 존재한다면 댓글을 반환한다.")
    void should_returnComment_when_IdExist() {
      Comment savedComment = commentTestHelper.builder().build();

      Comment findComment = commentFindService.findById(savedComment.getId());

      assertThat(savedComment).isEqualTo(findComment);
    }

    @Test
    @DisplayName("댓글 ID가 존재하지 않으면 예외가 발생한다.")
    void should_throwException_when_IdNotExist() {
      long commentId = -999l;

      assertThatThrownBy(() -> commentFindService.findById(commentId))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining(COMMENT_NOT_FOUND.getMessage());
    }
  }
}
