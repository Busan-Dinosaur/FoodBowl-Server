package com.dinosaur.foodbowl.domain.post.application;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.address.dto.AddressRequestDto;
import com.dinosaur.foodbowl.domain.post.dto.PostCreateRequestDto;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.store.dto.StoreRequestDto;
import com.dinosaur.foodbowl.domain.user.entity.User;
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
      User user = userTestHelper.generateUserWithoutThumbnail();
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
  }

  @Nested
  @DisplayName("실패 테스트")
  class Fail {

    @Test
    @DisplayName("사진이 한장도 없으면 게시글 생성은 실패한다.")
    public void should_fail_when_no_file() {
      // given
      User user = userTestHelper.generateUserWithoutThumbnail();
      StoreRequestDto storeRequestDto = postTestHelper.generateStoreDto();
      AddressRequestDto addressRequestDto = postTestHelper.generateAddressDto();
      PostCreateRequestDto requestDto = postTestHelper.getPostCreateRequestDto(storeRequestDto,
          addressRequestDto);

      // then
      Assertions.assertThatThrownBy(
              () -> postService.createPost(user, requestDto, Collections.emptyList()))
          .isInstanceOf(IndexOutOfBoundsException.class);
    }
  }
}