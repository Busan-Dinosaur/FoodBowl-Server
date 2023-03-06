package com.dinosaur.foodbowl.domain.post.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.user.entity.User;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

class PostRepositoryTest extends IntegrationTest {

    @Nested
    class 게시글_조회 {

        @Test
        void 특정_유저의_게시글만_조회한다() {
            User user1 = userTestHelper.builder().build();
            User user2 = userTestHelper.builder().build();
            Post post1 = postTestHelper.builder().user(user1).content("post1").build();
            Post post2 = postTestHelper.builder().user(user2).content("post2").build();

            Pageable pageable = PageRequest.of(0, 18);
            List<Post> posts = postRepository.findThumbnailsByUser(user1, pageable);

            assertThat(posts.size()).isEqualTo(1);
            assertThat(posts.get(0).getUser()).isEqualTo(user1);
            assertThat(posts.get(0).getContent()).isEqualTo(post1.getContent());
        }

        @Test
        void 설정한_페이지와_크기만큼_조회한다() {
            User user = userTestHelper.builder().build();

            for (int i = 0; i < 10; i++) {
                postTestHelper.builder().user(user).content("test" + i).build();
            }

            Pageable pageable = PageRequest.of(1, 3, Sort.by("id").descending());
            List<Post> posts = postRepository.findThumbnailsByUser(user, pageable);

            assertThat(posts.size()).isEqualTo(3);
            assertThat(posts.get(0).getContent()).isEqualTo("test6");
            assertThat(posts.get(1).getContent()).isEqualTo("test5");
            assertThat(posts.get(2).getContent()).isEqualTo("test4");
        }
    }

    @Nested
    class 피드_조회 {

        private User user;

        @BeforeEach
        void setUp() {
            user = userTestHelper.builder().build();
            User user2 = userTestHelper.builder().build();
            User user3 = userTestHelper.builder().build();

            followTestHelper.builder().following(user2).follower(user).build();

            postTestHelper.builder().user(user).content("유저1 포스트1").build();
            postTestHelper.builder().user(user2).content("유저2 포스트1").build();
            postTestHelper.builder().user(user3).content("유저3 포스트1").build();

            postTestHelper.builder().user(user).content("유저1 포스트2").build();
            postTestHelper.builder().user(user2).content("유저2 포스트2").build();
            postTestHelper.builder().user(user3).content("유저3 포스트2").build();

            postTestHelper.builder().user(user).content("유저1 포스트3").build();
            postTestHelper.builder().user(user2).content("유저2 포스트3").build();
            postTestHelper.builder().user(user3).content("유저3 포스트3").build();
        }

        @Test
        void 팔로우하고_있는_유저의_게시글만_조회한다() {
            Pageable pageable = PageRequest.of(0, 3, Sort.by("id").descending());

            List<Post> feed = postRepository.findFeed(user, pageable);

            assertThat(feed).hasSize(3);
            assertThat(feed.get(0).getContent()).isEqualTo("유저2 포스트3");
            assertThat(feed.get(1).getContent()).isEqualTo("유저1 포스트3");
            assertThat(feed.get(2).getContent()).isEqualTo("유저2 포스트2");
        }
    }

    @Nested
    class 특정_유저_제외_모든_유저_게시글_조회 {

        @Test
        void 특정_유저가_작성한_게시글을_불러오지_않는다() {
            //given
            User user1 = userTestHelper.builder().build();
            User user2 = userTestHelper.builder().build();

            postTestHelper.builder().user(user1).build();
            Post post2 = postTestHelper.builder().user(user2).build();
            Post post3 = postTestHelper.builder().user(user2).build();

            Pageable pageable = PageRequest.of(0, 2, Sort.by("id").descending());

            //when
            List<Post> posts = postRepository.findAllByUserNot(user1, pageable);

            //then
            assertThat(posts).hasSize(2);
            assertThat(posts).containsExactly(post3, post2);
        }

        @Test
        void 게시글을_조회한다() {
            //given
            User user1 = userTestHelper.builder().build();
            User user2 = userTestHelper.builder().build();

            postTestHelper.builder().user(user1).build();
            Post post2 = postTestHelper.builder().user(user2).build();
            Post post3 = postTestHelper.builder().user(user2).build();

            Pageable pageable = PageRequest.of(1, 1, Sort.by("id").descending());

            //when
            List<Post> posts = postRepository.findAllByUserNot(user1, pageable);

            //then
            assertThat(posts).hasSize(1);
            assertThat(posts).containsExactly(post2);
        }
    }
}
