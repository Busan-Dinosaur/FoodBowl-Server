package com.dinosaur.foodbowl.domain.post.application;

import static com.dinosaur.foodbowl.global.error.ErrorCode.POST_HAS_NOT_IMAGE;
import static com.dinosaur.foodbowl.global.error.ErrorCode.POST_NOT_WRITER;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.address.dto.requset.AddressRequestDto;
import com.dinosaur.foodbowl.domain.post.dto.request.PostCreateRequestDto;
import com.dinosaur.foodbowl.domain.post.dto.request.PostUpdateRequestDto;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.store.dto.request.StoreRequestDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.error.BusinessException;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

class PostServiceTest extends IntegrationTest {

  @Nested
  @DisplayName("성공 테스트")
  class Success {

    @Test
    @DisplayName("올바른 요청에 대한 게시글 생성은 성공한다.")
    public void should_success_when_valid_request() {
      // given
      User user = userTestHelper.builder().build();
      StoreRequestDto storeRequestDto = postTestHelper.generateStoreDto();
      AddressRequestDto addressRequestDto = postTestHelper.generateAddressDto();
      List<MultipartFile> images = List.of(photoTestHelper.getPhotoFile());
      PostCreateRequestDto requestDto = postTestHelper.getPostCreateRequestDto(storeRequestDto,
          addressRequestDto);

      // when
      Long postId = postService.createPost(user, requestDto, images);
      em.flush();
      em.clear();

      // then
      Post post = postRepository.getReferenceById(postId);
      Assertions.assertThat(post).isNotNull();
    }

    @Test
    @DisplayName("올바른 요청에 대한 게시글 수정은 성공한다.")
    public void should_success_when_valid_update_request() {
      // given
      User user = userTestHelper.builder().build();
      Post before = postTestHelper.builder().content("before").thumbnail(null).user(user)
          .store(null).build();
      StoreRequestDto storeRequestDto = postTestHelper.generateStoreDto();
      AddressRequestDto addressRequestDto = postTestHelper.generateAddressDto();
      List<MultipartFile> images = List.of(photoTestHelper.getPhotoFile());
      PostUpdateRequestDto requestDto = postTestHelper.getPostUpdateRequestDto(
          storeRequestDto, addressRequestDto, List.of(1L, 2L));

      // when
      postService.updatePost(user, before.getId(), requestDto, images);
      em.flush();
      em.clear();

      // then
      Post after = postRepository.getReferenceById(before.getId());
      Assertions.assertThat(after.getContent()).isEqualTo(requestDto.getContent());
      Assertions.assertThat(after.getStore().getStoreName())
          .isEqualTo(requestDto.getStore().getStoreName());
      Assertions.assertThat(after.getStore().getAddress().getAddressName())
          .isEqualTo(requestDto.getAddress().getAddressName());
      // TODO: Photo Service Merge 후 주석 제거
      //  Assertions.assertThat(after.getPhotos().size()).isEqualTo(1);
      //   Assertions.assertThat(after.getThumbnail()).isNotNull();
    }
  }

  @Nested
  @DisplayName("실패 테스트")
  class Fail {

    @Test
    @DisplayName("사진이 한장도 없으면 게시글 수정은 실패한다.")
    public void should_fail_when_no_file() {
      // given
      User user = userTestHelper.builder().build();
      Post before = postTestHelper.builder().content("before").thumbnail(null).user(user)
          .store(null).build();
      PostUpdateRequestDto requestDto = postTestHelper.getValidPostUpdateRequestDto();


      // then
      Assertions.assertThatThrownBy(
              () -> postService.updatePost(user, before.getId(), requestDto, Collections.emptyList()))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining(POST_HAS_NOT_IMAGE.getMessage());
    }

    @Test
    @DisplayName("게시글 수정시 게시글 작성자가 아닌 경우 예외가 발생한다.")
    public void should_throw_BusinessException_when_postNotWriter() {
      // given
      User user = userTestHelper.builder().build();
      User another = userTestHelper.builder().build();
      Post before = postTestHelper.builder().user(user).build();
      PostUpdateRequestDto requestDto = postTestHelper.getValidPostUpdateRequestDto();

      // then
      Assertions.assertThatThrownBy(
              () -> postService.updatePost(another, before.getId(), requestDto,
                  List.of(photoTestHelper.getPhotoFile()))).isInstanceOf(BusinessException.class)
          .hasMessageContaining(POST_NOT_WRITER.getMessage());

    }

    @Test
    @DisplayName("사진이 한장도 없으면 게시글 수정은 실패한다.")
    public void should_fail_when_update_without_file() {
      // given
      User user = userTestHelper.builder().build();
      Post before = postTestHelper.builder().user(user).build();
      PostUpdateRequestDto requestDto = postTestHelper.getValidPostUpdateRequestDto();

      // then
      Assertions.assertThatThrownBy(
              () -> postService.updatePost(user, before.getId(), requestDto, Collections.emptyList()))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining(POST_HAS_NOT_IMAGE.getMessage());
    }
  }
}