package com.dinosaur.foodbowl.domain.comment;

import static com.dinosaur.foodbowl.domain.comment.entity.Comment.MAX_MESSAGE_LENGTH;

import com.dinosaur.foodbowl.domain.comment.dao.CommentRepository;
import com.dinosaur.foodbowl.domain.comment.entity.Comment;
import com.dinosaur.foodbowl.domain.post.PostTestHelper;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.user.UserTestHelper;
import com.dinosaur.foodbowl.domain.user.entity.User;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentTestHelper {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserTestHelper userTestHelper;

    @Autowired
    private PostTestHelper postTestHelper;

    private String getRandomUUIDLengthWith(int length) {
        String randomString = UUID.randomUUID().toString();
        length = Math.min(length, randomString.length());
        return randomString.substring(0, length);
    }

    public CommentBuilder builder() {
        return new CommentBuilder();
    }

    public final class CommentBuilder {

        private Comment comment;
        private Post post;
        private User user;
        private String message;

        private CommentBuilder() {
        }

        public CommentBuilder comment(Comment comment) {
            this.comment = comment;
            return this;
        }

        public CommentBuilder post(Post post) {
            this.post = post;
            return this;
        }

        public CommentBuilder user(User user) {
            this.user = user;
            return this;
        }

        public CommentBuilder message(String message) {
            this.message = message;
            return this;
        }

        public Comment build() {
            return commentRepository.save(Comment.builder()
                    .comment(comment)
                    .post(post != null ? post : postTestHelper.builder().build())
                    .user(user != null ? user : userTestHelper.builder().build())
                    .message(message != null ?
                            message : getRandomUUIDLengthWith(MAX_MESSAGE_LENGTH)
                    )
                    .build());
        }
    }
}
