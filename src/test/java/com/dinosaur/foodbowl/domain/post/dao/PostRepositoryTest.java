package com.dinosaur.foodbowl.domain.post.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.user.entity.User;
import java.util.List;
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
}
