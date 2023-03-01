package com.dinosaur.foodbowl.domain.clip.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.clip.entity.Clip;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

class ClipRepositoryTest extends IntegrationTest {

  @Nested
  class 유저_게시글에_대한_클립_존재_확인 {

    @Test
    void 유저_게시글에_대한_클립이_존재하면_true_반환한다() {
      User user = userTestHelper.builder().build();
      Post post = postTestHelper.builder().build();
      Clip clip = clipTestHelper.builder().user(user).post(post).build();

      boolean result = clipRepository.existsClipByUserAndPost(user, post);

      assertThat(result).isTrue();
    }

    @Test
    void 유저만_일치하면_false_반환한다() {
      User user = userTestHelper.builder().build();
      Post post = postTestHelper.builder().build();
      Clip clip = clipTestHelper.builder().user(user).build();

      boolean result = clipRepository.existsClipByUserAndPost(user, post);

      assertThat(result).isFalse();
    }

    @Test
    void 게시글만_일치하면_false_반환한다() {
      User user = userTestHelper.builder().build();
      Post post = postTestHelper.builder().build();
      Clip clip = clipTestHelper.builder().post(post).build();

      boolean result = clipRepository.existsClipByUserAndPost(user, post);

      assertThat(result).isFalse();
    }

    @Test
    void 일치하는_것이_존재하지_않으면_false_반환한다() {
      User user = userTestHelper.builder().build();
      Post post = postTestHelper.builder().build();
      Clip clip = clipTestHelper.builder().build();

      boolean result = clipRepository.existsClipByUserAndPost(user, post);

      assertThat(result).isFalse();
    }
  }

  @Nested
  class 유저_게시글_클립_조회 {

    @Test
    void 유저_게시글에_대한_클립이_존재하면_조회한다() {
      User user = userTestHelper.builder().build();
      Post post = postTestHelper.builder().build();
      Clip clip = clipTestHelper.builder().user(user).post(post).build();

      Optional<Clip> findClip = clipRepository.findClipByUserAndPost(user, post);

      assertThat(findClip).isNotEmpty();
      assertThat(findClip.get().getUser()).isEqualTo(user);
      assertThat(findClip.get().getPost()).isEqualTo(post);
    }

    @Test
    void 유저_게시글에_대한_클립이_존재하지_않으면_조회하지_않는다() {
      User user = userTestHelper.builder().build();
      Post post = postTestHelper.builder().build();
      Clip clip = clipTestHelper.builder().build();

      Optional<Clip> findClip = clipRepository.findClipByUserAndPost(user, post);

      assertThat(findClip).isEmpty();
    }
  }

  @Nested
  class 특정_사용자_클립_목록_조회 {

    @Test
    void 특정_사용자_클립_목록만_조회한다() {
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
