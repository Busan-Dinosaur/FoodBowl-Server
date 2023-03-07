package com.dinosaur.foodbowl.domain.clip.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.clip.dto.response.ClipPostThumbnailResponse;
import com.dinosaur.foodbowl.domain.clip.dto.response.ClipStatusResponseDto;
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

class ClipServiceTest extends IntegrationTest {

    @Nested
    class 클립 {

        @Test
        void 유저_게시글에_대한_클립이_존재하지_않으면_ok_반환한다() {
            User user = userTestHelper.builder().build();
            Post post = postTestHelper.builder().build();

            ClipStatusResponseDto response = clipService.clip(user, post.getId());
            Optional<Clip> findClip = clipRepository.findClipByUserAndPost(user, post);

            assertThat(response.getStatus()).isEqualTo("ok");
            assertThat(findClip).isNotEmpty();
        }

        @Test
        void 유저_게시글에_대한_클립이_존재하면_ok_반환한다() {
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
    class 클립_취소 {

        @Test
        void 유저_게시글에_대한_클립이_존재하면_ok_반환한다() {
            User user = userTestHelper.builder().build();
            Post post = postTestHelper.builder().build();
            Clip clip = clipTestHelper.builder().user(user).post(post).build();

            ClipStatusResponseDto response = clipService.unclip(user, post.getId());
            Optional<Clip> findClip = clipRepository.findClipByUserAndPost(user, post);

            assertThat(response.getStatus()).isEqualTo("ok");
            assertThat(findClip).isEmpty();
        }

        @Test
        void 유저_게시글에_대한_클립이_존재하지_않으면_ok_반환한다() {
            User user = userTestHelper.builder().build();
            Post post = postTestHelper.builder().build();

            ClipStatusResponseDto response = clipService.unclip(user, post.getId());
            Optional<Clip> findClip = clipRepository.findClipByUserAndPost(user, post);

            assertThat(response.getStatus()).isEqualTo("ok");
            assertThat(findClip).isEmpty();
        }
    }

    @Nested
    class 특정_사용자_클립_게시글_썸네일_목록_조회 {

        @Test
        void 특정_사용자_클립_썸네일_경로_목록을_조회한다() {
            //given
            User user = userTestHelper.builder().build();
            Post post = postTestHelper.builder().build();
            Clip clip = clipTestHelper.builder().user(user).post(post).build();

            Pageable pageable = PageRequest.of(0, 100, Sort.by("id").descending());

            //when
            List<ClipPostThumbnailResponse> result = clipService.getClipPostThumbnails(user, pageable);

            //then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).clipId()).isEqualTo(clip.getId());
            assertThat(result.get(0).thumbnailPath()).isEqualTo(post.getThumbnail().getPath());
        }
    }
}
