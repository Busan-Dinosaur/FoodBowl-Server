package com.dinosaur.foodbowl.domain.comment.application;

import static com.dinosaur.foodbowl.global.error.ErrorCode.COMMENT_NOT_WRITER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.comment.dto.request.CommentWriteRequestDto;
import com.dinosaur.foodbowl.domain.comment.dto.response.CommentResponseDto;
import com.dinosaur.foodbowl.domain.comment.entity.Comment;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.error.BusinessException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CommentServiceTest extends IntegrationTest {

  @Nested
  class 댓글_작성 {

    @Test
    void 댓글을_성공적으로_작성한다() {
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
  class 댓글_수정 {

    @Test
    void 댓글_작성자가_아니라면_예외가_발생한다() {
      User user = userTestHelper.builder().build();
      Comment comment = commentTestHelper.builder().build();
      String message = "수정된 댓글";

      assertThatThrownBy(() -> commentService.updateComment(user, comment.getId(), message))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining(COMMENT_NOT_WRITER.getMessage());
    }

    @Test
    void 댓글_수정에_성공한다() {
      User user = userTestHelper.builder().build();
      Comment comment = commentTestHelper.builder().user(user).build();
      String message = "수정된 댓글";

      LocalDateTime updatedAt = comment.getUpdatedAt();
      long postId = commentService.updateComment(user, comment.getId(), message);

      em.flush();
      em.clear();

      Comment updatedComment = commentFindService.findById(comment.getId());

      assertThat(updatedComment.getMessage()).isEqualTo(message);
      assertThat(updatedComment.getPost().getId()).isEqualTo(postId);
    }
  }

  @Nested
  class 댓글_삭제 {

    @Test
    void 댓글_삭제에_성공한다() {
      User user = userTestHelper.builder().build();
      Comment comment = commentTestHelper.builder().user(user).build();

      commentService.deleteComment(user, comment.getId());

      em.flush();
      em.clear();

      Optional<Comment> deletedComment = commentRepository.findById(comment.getId());
      assertThat(deletedComment).isEmpty();
    }

    @Test
    void 댓글_작성자가_아니라면_예외가_발생한다() {
      User user = userTestHelper.builder().build();
      Comment comment = commentTestHelper.builder().build();

      assertThatThrownBy(() -> commentService.deleteComment(user, comment.getId()))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining(COMMENT_NOT_WRITER.getMessage());
    }
  }

  @Nested
  class 제한되지_않은_댓글_시간순_조회 {

    @Test
    void 댓글_조회에_성공한다() {
      User user = userTestHelper.builder().build();
      Post post = postTestHelper.builder().build();
      Comment comment1 = commentTestHelper.builder().user(user).post(post).message("test1").build();
      Comment comment2 = commentTestHelper.builder().user(user).post(post).message("test2").build();

      List<CommentResponseDto> comments = commentService.getComments(post.getId());

      assertThat(comments.size()).isEqualTo(2);
      assertThat(comments.get(0).getNickname()).isEqualTo(user.getNickname().getNickname());
      assertThat(comments.get(0).getUserThumbnailPath()).isEqualTo(
          user.getThumbnailURL().orElseGet(() -> null));
      assertThat(comments.get(0).getMessage()).isEqualTo(comment1.getMessage());
      assertThat(comments.get(0).getCreatedAt()).isEqualTo(comment1.getCreatedAt());
      assertThat(comments.get(1).getNickname()).isEqualTo(user.getNickname().getNickname());
      assertThat(comments.get(1).getUserThumbnailPath()).isEqualTo(
          user.getThumbnailURL().orElseGet(() -> null));
      assertThat(comments.get(1).getMessage()).isEqualTo(comment2.getMessage());
      assertThat(comments.get(1).getCreatedAt()).isEqualTo(comment2.getCreatedAt());
    }
  }
}
