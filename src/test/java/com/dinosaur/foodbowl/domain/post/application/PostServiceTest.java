package com.dinosaur.foodbowl.domain.post.application;

import static org.junit.jupiter.api.Assertions.*;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.address.dto.AddressDto;
import com.dinosaur.foodbowl.domain.category.entity.Category.CategoryType;
import com.dinosaur.foodbowl.domain.post.dto.PostCreateRequestDto;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.domain.store.dto.StoreDto;
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
      StoreDto storeDto = generateStoreDto(generateAddressDto());
      List<MultipartFile> photoFiles = List.of(photoTestHelper.getPhotoFile());
      PostCreateRequestDto requestDto = getPostCreateRequestDto(storeDto, photoFiles);

      // when
      Long postId = postService.createPost(user, requestDto);
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
      StoreDto storeDto = generateStoreDto(generateAddressDto());
      PostCreateRequestDto requestDto = getPostCreateRequestDto(storeDto, Collections.emptyList());

      // then
      Assertions.assertThatThrownBy(() -> postService.createPost(user, requestDto))
          .isInstanceOf(IndexOutOfBoundsException.class);
    }
  }

  private PostCreateRequestDto getPostCreateRequestDto(StoreDto storeDto,
      List<MultipartFile> photoFiles) {
    return PostCreateRequestDto.builder()
        .photoFiles(photoFiles)
        .storeDto(storeDto)
        .content("test")
        .categoryId(CategoryType.전체.getId())
        .build();
  }

  private StoreDto generateStoreDto(AddressDto addressDto) {
    return StoreDto.builder()
        .addressDto(addressDto)
        .storeName("test")
        .build();
  }

  private AddressDto generateAddressDto() {
    return AddressDto.builder()
        .addressName("부산광역시 부산대학로 1")
        .region1depthName("부산광역시")
        .region2depthName("금정구")
        .region3depthName("장전동")
        .roadName("부산대학로")
        .mainBuildingNo("1")
        .build();
  }
}