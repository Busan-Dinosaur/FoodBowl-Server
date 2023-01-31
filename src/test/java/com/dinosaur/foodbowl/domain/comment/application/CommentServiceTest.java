package com.dinosaur.foodbowl.domain.comment.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.comment.dto.request.CommentWriteRequestDto;
import com.dinosaur.foodbowl.domain.comment.entity.Comment;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.user.entity.User;
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
}
