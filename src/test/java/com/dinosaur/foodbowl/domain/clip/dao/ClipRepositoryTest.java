package com.dinosaur.foodbowl.domain.clip.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.clip.entity.Clip;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

class ClipRepositoryTest extends IntegrationTest {

  @Nested
  @DisplayName("유저, 게시글에 대한 클립이 존재하는지 확인한다.")
  class ExistsClipByUserAndPost {

    @Test
    @DisplayName("유저, 게시글에 대한 클립이 존재하면 true 반환한다.")
    void should_returnTrue_when_clipExistForUserAndPost() {
      User user = userTestHelper.builder().build();
      Post post = postTestHelper.builder().build();
      Clip clip = clipTestHelper.builder().user(user).post(post).build();

      boolean result = clipRepository.existsClipByUserAndPost(user, post);

      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("유저만 일치하면 false 반환한다.")
    void should_returnFalse_when_clipExistForUser() {
      User user = userTestHelper.builder().build();
      Post post = postTestHelper.builder().build();
      Clip clip = clipTestHelper.builder().user(user).build();

      boolean result = clipRepository.existsClipByUserAndPost(user, post);

      assertThat(result).isFalse();
    }

    @Test
    @DisplayName("게시글만 일치하면 false 반환한다.")
    void should_returnFalse_when_clipExistForPost() {
      User user = userTestHelper.builder().build();
      Post post = postTestHelper.builder().build();
      Clip clip = clipTestHelper.builder().post(post).build();

      boolean result = clipRepository.existsClipByUserAndPost(user, post);

      assertThat(result).isFalse();
    }

    @Test
    @DisplayName("일치하는 것이 존재하지 않으면 false 반환한다.")
    void should_returnFalse_when_clipNotExist() {
      User user = userTestHelper.builder().build();
      Post post = postTestHelper.builder().build();
      Clip clip = clipTestHelper.builder().build();

      boolean result = clipRepository.existsClipByUserAndPost(user, post);

      assertThat(result).isFalse();
    }
  }

  @Nested
  @DisplayName("유저, 게시글에 대한 클립을 가져온다.")
  class FindClipByUserAndPost {

    @Test
    @DisplayName("유저, 게시글에 대한 클립이 존재하면 가져온다.")
    void should_clipImport_when_findForUserAndPost() {
      User user = userTestHelper.builder().build();
      Post post = postTestHelper.builder().build();
      Clip clip = clipTestHelper.builder().user(user).post(post).build();

      Optional<Clip> findClip = clipRepository.findClipByUserAndPost(user, post);

      assertThat(findClip).isNotEmpty();
      assertThat(findClip.get().getUser()).isEqualTo(user);
      assertThat(findClip.get().getPost()).isEqualTo(post);
    }

    @Test
    @DisplayName("유저, 게시글에 대한 클립이 존재하지 않으면 가져오지 않는다.")
    void should_clipNotImport_when_findForUserAndPost() {
      User user = userTestHelper.builder().build();
      Post post = postTestHelper.builder().build();
      Clip clip = clipTestHelper.builder().build();

      Optional<Clip> findClip = clipRepository.findClipByUserAndPost(user, post);

      assertThat(findClip).isEmpty();
    }
  }

  @Nested
  @DisplayName("특정 사용자의 클립 목록 조회(paging, fetch join)")
  class FindClipByUser {

    @Test
    @DisplayName("특정 사용자의 클립 목록만 조회한다.")
    void should_get_clips_for_user() {
      //given
      User user1 = userTestHelper.builder().build();
      Post post1 = postTestHelper.builder().build();
      Post post2 = postTestHelper.builder().build();
      Post post3 = postTestHelper.builder().build();
      clipTestHelper.builder().user(user1).post(post1).build();
      Clip clip2 = clipTestHelper.builder().user(user1).post(post2).build();
      Clip clip3 = clipTestHelper.builder().user(user1).post(post3).build();

      User user2 = userTestHelper.builder().build();
      Post post4 = postTestHelper.builder().build();
      postTestHelper.builder().build();
      clipTestHelper.builder().user(user2).post(post4).build();

      Pageable pageable = PageRequest.of(0, 2, Sort.by("id").descending());

      //when
      List<Clip> result = clipRepository.findClipByUser(user1, pageable);

      //then
      assertThat(result).hasSize(2);
      assertThat(result.get(0)).isEqualTo(clip3);
      assertThat(result.get(1)).isEqualTo(clip2);
    }
  }
}
