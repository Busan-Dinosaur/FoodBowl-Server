package com.dinosaur.foodbowl.domain.clip.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.clip.dto.response.ClipStatusResponseDto;
import com.dinosaur.foodbowl.domain.clip.entity.Clip;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.user.entity.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ClipServiceTest extends IntegrationTest {

  @Nested
  @DisplayName("클립")
  class ClipTest {

    @Test
    @DisplayName("유저, 게시글에 대한 클립이 존재하지 않으면 ok 반환한다.")
    void should_returnOk_when_clipForUserAndPostNotExist() {
      User user = userTestHelper.builder().build();
      Post post = postTestHelper.builder().build();

      ClipStatusResponseDto response = clipService.clip(user, post.getId());
      Optional<Clip> findClip = clipRepository.findClipByUserAndPost(user, post);

      assertThat(response.getStatus()).isEqualTo("ok");
      assertThat(findClip).isNotEmpty();
    }

    @Test
    @DisplayName("유저, 게시글에 대한 클립이 존재하면 ok 반환한다.")
    void should_returnOk_when_chipForUserAndPostExist() {
      User user = userTestHelper.builder().build();
      Post post = postTestHelper.builder().build();
      Clip clip = clipTestHelper.builder().user(user).post(post).build();

      ClipStatusResponseDto response = clipService.clip(user, post.getId());
      Optional<Clip> findClip = clipRepository.findClipByUserAndPost(user, post);

      assertThat(response.getStatus()).isEqualTo("ok");
      assertThat(findClip).isNotEmpty();
    }
  }

  @Nested
  @DisplayName("언클립")
  class UnclipTest {

    @Test
    @DisplayName("유저, 게시글에 대한 클립이 존재하면 ok 반환한다.")
    void should_returnOk_when_clipForUserAndPostExist() {
      User user = userTestHelper.builder().build();
      Post post = postTestHelper.builder().build();
      Clip clip = clipTestHelper.builder().user(user).post(post).build();

      ClipStatusResponseDto response = clipService.unclip(user, post.getId());
      Optional<Clip> findClip = clipRepository.findClipByUserAndPost(user, post);

      assertThat(response.getStatus()).isEqualTo("ok");
      assertThat(findClip).isEmpty();
    }

    @Test
    @DisplayName("유저, 게시글에 대한 클립이 존재하지 않으면 ok 반환한다.")
    void should_returnOk_when_clipForUserAndPostNotExist() {
      User user = userTestHelper.builder().build();
      Post post = postTestHelper.builder().build();

      ClipStatusResponseDto response = clipService.unclip(user, post.getId());
      Optional<Clip> findClip = clipRepository.findClipByUserAndPost(user, post);

      assertThat(response.getStatus()).isEqualTo("ok");
      assertThat(findClip).isEmpty();
    }
  }
}
