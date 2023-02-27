package com.dinosaur.foodbowl.domain.post.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.user.entity.User;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

class PostRepositoryTest extends IntegrationTest {

  @Nested
  @DisplayName("유저 게시글 페이징 검색")
  class FindAllByUserWithPageable {

    @Test
    @DisplayName("해당 유저에 대한 게시글만 가져온다.")
    void should_getOnlyUser_when_findPosts() {
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
    @DisplayName("설정한 페이지와 크기만큼 가져온다.")
    void should_pageAndSize_when_pageAndSizeSet() {
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
  @DisplayName("유저 피드 페이징 검색")
  class FindFeed {

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
    @DisplayName("나와 내가 팔로우하고 있는 유저의 게시글만 불러온다.")
    void should_find_onlyPostsMeAndFollowingUser() {
      Pageable pageable = PageRequest.of(0, 3, Sort.by("id").descending());

      List<Post> feed = postRepository.findFeed(user, pageable);

      assertThat(feed).hasSize(3);
      assertThat(feed.get(0).getContent()).isEqualTo("유저2 포스트3");
      assertThat(feed.get(1).getContent()).isEqualTo("유저1 포스트3");
      assertThat(feed.get(2).getContent()).isEqualTo("유저2 포스트2");
    }
  }

  @Nested
  @DisplayName("특정 유저를 제외한 모든 유저가 작성한 게시글을 조회한다.")
  class FindAllByUserNot {

    @Test
    @DisplayName("특정 유저가 작성한 게시글을 불러오지 않는다.")
    void getPostsExcludeUser() {
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
    @DisplayName("페이징 적용 후 게시글을 불러온다.")
    void getPostsWithPaging() {
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
