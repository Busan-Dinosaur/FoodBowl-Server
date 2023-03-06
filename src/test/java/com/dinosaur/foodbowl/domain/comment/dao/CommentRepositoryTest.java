package com.dinosaur.foodbowl.domain.comment.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.blame.entity.Blame;
import com.dinosaur.foodbowl.domain.comment.entity.Comment;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.user.entity.User;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CommentRepositoryTest extends IntegrationTest {

    @Nested
    class 제한되지_않은_댓글_시간순_조회 {

        @Test
        void 댓글을_시간순으로_조회한다() {
            Post oldPost = postTestHelper.builder().build();
            Post newPost = postTestHelper.builder().build();
            Comment oldPostComment = commentTestHelper.builder()
                    .post(oldPost)
                    .message("test1")
                    .build();
            Comment newPostComment = commentTestHelper.builder()
                    .post(newPost)
                    .message("test2")
                    .build();
            Comment oldPostComment2 = commentTestHelper.builder()
                    .post(oldPost)
                    .message("test3")
                    .build();

            List<Comment> comments = commentRepository.findUnrestrictedComments(oldPost);

            assertThat(comments.size()).isEqualTo(2);
            assertThat(comments.get(0).getMessage()).isEqualTo(oldPostComment.getMessage());
            assertThat(comments.get(1).getMessage()).isEqualTo(oldPostComment2.getMessage());
        }

        @Test
        void 신고가_5회_이상_존재하는_댓글은_조회하지_않는다() {
            Post post = postTestHelper.builder().build();
            User user = userTestHelper.builder().build();
            Comment unrestrictedComment = commentTestHelper.builder()
                    .post(post)
                    .message("test1")
                    .build();
            Comment restrictedComment = commentTestHelper.builder()
                    .post(post)
                    .message("test2")
                    .build();

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
    class 댓글_삭제 {

        @Test
        void 댓글을_삭제하면_연관된_신고도_함께_삭제된다() {
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
