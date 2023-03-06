package com.dinosaur.foodbowl.domain.comment.application;

import static com.dinosaur.foodbowl.global.error.ErrorCode.COMMENT_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.comment.entity.Comment;
import com.dinosaur.foodbowl.global.error.BusinessException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CommentFindServiceTest extends IntegrationTest {

    @Nested
    class 댓글_찾기 {

        @Test
        void 댓글_ID가_존재하면_댓글을_조회한다() {
            Comment savedComment = commentTestHelper.builder().build();

            Comment findComment = commentFindService.findById(savedComment.getId());

            assertThat(savedComment).isEqualTo(findComment);
        }

        @Test
        void 댓글_ID가_존재하지_않으면_예외가_발생한다() {
            long commentId = -999l;

            assertThatThrownBy(() -> commentFindService.findById(commentId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(COMMENT_NOT_FOUND.getMessage());
        }
    }
}
