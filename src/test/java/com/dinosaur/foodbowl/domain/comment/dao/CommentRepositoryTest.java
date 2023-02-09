package com.dinosaur.foodbowl.domain.comment.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.blame.entity.Blame;
import com.dinosaur.foodbowl.domain.comment.entity.Comment;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.user.entity.User;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CommentRepositoryTest extends IntegrationTest {

  @Nested
  @DisplayName("제한되지 않은 댓글 시간순으로 찾기")
  class FindUnrestrictedComments {

    @Test
    @DisplayName("게시글 댓글을 순차적으로 가져온다.")
    void should_success_when_findUnrestrictedComments() {
      Post oldPost = postTestHelper.builder().build();
      Post newPost = postTestHelper.builder().build();
      Comment oldPostComment = commentTestHelper.builder().post(oldPost).message("test1")
          .build();
      Comment newPostComment = commentTestHelper.builder().post(newPost).message("test2").build();
      Comment oldPostComment2 = commentTestHelper.builder().post(oldPost).message("test3").build();

      List<Comment> comments = commentRepository.findUnrestrictedComments(oldPost);

      assertThat(comments.size()).isEqualTo(2);
      assertThat(comments.get(0).getMessage()).isEqualTo(oldPostComment.getMessage());
      assertThat(comments.get(1).getMessage()).isEqualTo(oldPostComment2.getMessage());
    }

    @Test
    @DisplayName("신고가 5회 이상 존재하는 댓글은 가져오지 않는다.")
    void should_notBring_when_commentsMoreThanFiveReports() {
      Post post = postTestHelper.builder().build();
      User user = userTestHelper.builder().build();
      Comment unrestrictedComment = commentTestHelper.builder().post(post).message("test1").build();
      Comment restrictedComment = commentTestHelper.builder().post(post).message("test2").build();

      unrestrictedComment.report(user);
      for (int i = 0; i < 5; i++) {
        user = userTestHelper.builder().build();
        restrictedComment.report(user);
      }

      em.flush();
      em.clear();

      List<Comment> comments = commentRepository.findUnrestrictedComments(post);

      assertThat(comments.size()).isEqualTo(1);
      assertThat(comments.get(0).getMessage()).isEqualTo(unrestrictedComment.getMessage());
      assertThat(comments.get(0).getBlames().size()).isEqualTo(1);
    }
  }

  @Nested
  @DisplayName("댓글 삭제")
  class DeleteComment {

    @Test
    @DisplayName("댓글을 삭제하면 연관된 신고도 함께 삭제된다.")
    void should_deleteBlames_when_deleteComment() {
      User user = userTestHelper.builder().build();
      Comment comment = commentTestHelper.builder().build();

      comment.report(user);

      em.flush();
      em.clear();

      commentRepository.delete(comment);

      List<Comment> comments = commentRepository.findAll();
      List<Blame> blames = blameRepository.findAll();

      assertThat(comments.size()).isEqualTo(0);
      assertThat(blames.size()).isEqualTo(0);
    }
  }
}
