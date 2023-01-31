package com.dinosaur.foodbowl.domain.comment.application;

import static com.dinosaur.foodbowl.global.error.ErrorCode.COMMENT_NOT_WRITER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.comment.dto.request.CommentWriteRequestDto;
import com.dinosaur.foodbowl.domain.comment.entity.Comment;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.error.BusinessException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CommentServiceTest extends IntegrationTest {

  @Nested
  @DisplayName("댓글 작성")
  class WriteComment {

    @Test
    @DisplayName("댓글을 성공적으로 작성한다.")
    void should_success_when_writeComment() {
      User user = userTestHelper.builder().build();
      Post savedPost = postTestHelper.builder().build();
      CommentWriteRequestDto request = CommentWriteRequestDto.builder()
          .postId(savedPost.getId())
          .message("테스트 댓글")
          .build();

      commentService.writeComment(user, request);
      List<Comment> comments = commentRepository.findAll();

      assertThat(comments.size()).isEqualTo(1);
      assertThat(comments.get(0).getUser()).isEqualTo(user);
      assertThat(comments.get(0).getPost()).isEqualTo(savedPost);
      assertThat(comments.get(0).getMessage()).isEqualTo("테스트 댓글");
    }
  }

  @Nested
  @DisplayName("댓글 수정")
  class UpdateComment {

    @Test
    @DisplayName("댓글 작성자가 아닌 경우 예외가 발생한다.")
    void should_throwException_when_commentNotWriter() {
      User user = userTestHelper.builder().build();
      Comment comment = commentTestHelper.builder().build();
      String message = "수정된 댓글";

      assertThatThrownBy(() -> commentService.updateComment(user, comment.getId(), message))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining(COMMENT_NOT_WRITER.getMessage());
    }

    @Test
    @DisplayName("댓글 수정에 성공한다.")
    void should_success_when_updateComment() {
      User user = userTestHelper.builder().build();
      Comment comment = commentTestHelper.builder().user(user).build();
      String message = "수정된 댓글";

      LocalDateTime updatedAt = comment.getUpdatedAt();
      long postId = commentService.updateComment(user, comment.getId(), message);

      em.flush();
      em.clear();

      Comment updatedComment = commentFindDao.findById(comment.getId());

      assertThat(updatedComment.getMessage()).isEqualTo(message);
      assertThat(updatedComment.getPost().getId()).isEqualTo(postId);
      assertThat(updatedComment.getUpdatedAt().compareTo(updatedAt)).isPositive();
    }
  }
}