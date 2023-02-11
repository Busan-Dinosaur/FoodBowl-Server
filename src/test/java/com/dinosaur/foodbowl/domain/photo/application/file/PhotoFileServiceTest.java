package com.dinosaur.foodbowl.domain.photo.application.file;

import static com.dinosaur.foodbowl.domain.photo.application.file.PhotoFileConstants.ROOT_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.photo.entity.Photo;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.global.error.BusinessException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PhotoFileServiceTest extends IntegrationTest {

  @Nested
  @DisplayName("사진 저장 테스트")
  class SaveTest {

    @Test
    @DisplayName("사진 파일이 유효한 경우 저장은 성공해야 한다.")
    void should_saveSuccessfully_when_validMultipartFile() throws IOException {
      Post post = postTestHelper.builder().build();
      Photo result = photoFileService.save(photoTestHelper.getImageFile(), post);

      assertThat(result).isNotNull();
      deleteTestFile(result);
    }

    @Test
    @DisplayName("사진 파일이 유효하지 않으면 BusinessException 이 발생한다.")
    void should_throw_BusinessException_when_InvalidFile() throws IOException {
      Post post = postTestHelper.builder().build();

      assertThatThrownBy(() ->
          photoFileService.save(photoTestHelper.getFakeImageFile(), post))
          .isInstanceOf(BusinessException.class);

      assertThatThrownBy(() ->
          photoFileService.save(null, post))
          .isInstanceOf(BusinessException.class);

      assertThatThrownBy(() ->
          photoFileService.save(photoTestHelper.getTooLongNameImageFile(448), post))
          .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("존재하지 않는 게시글에 사진을 추가하면 BusinessException 이 발생한다.")
    void should_throw_BusinessException_when_post_not_found() {
      Post deleted = postTestHelper.builder().build();
      postRepository.delete(deleted);
      em.flush();
      em.clear();

      assertThatThrownBy(() ->
          photoFileService.save(photoTestHelper.getImageFile(), deleted))
          .isInstanceOf(BusinessException.class);
    }

    private void deleteTestFile(Photo photo) {
      File photoFile = new File(ROOT_PATH + photo.getPath());
      assertThat(photoFile).exists();
      assertThat(photoFile.delete()).isTrue();
    }
  }

  @Nested
  @DisplayName("사진 삭제 테스트")
  class DeleteTest {

    @Test
    @DisplayName("사진 파일이 존재할 경우 삭제는 성공해야 한다.")
    void should_deleteSuccessfully_when_existFile() throws IOException {
      Photo saved = photoFileService.save(photoTestHelper.getImageFile(),
          postTestHelper.builder().build());

      photoFileService.delete(saved);

      assertThat(Files.exists(Path.of(ROOT_PATH + saved.getPath()))).isFalse();
    }
  }
}